package com.wanjia.service.impl;

import com.wanjia.dao.SpecialtyBookedInfoMapper;
import com.wanjia.dao.UserSpecialtyOrderMapper;
import com.wanjia.entity.order.UserSpecialtyOrder;
import com.wanjia.entity.order.book.SpecialtyBookedInfo;
import com.wanjia.exceptions.ElasticSearchException;
import com.wanjia.exceptions.MySqlException;
import com.wanjia.exceptions.OrderGenerateException;
import com.wanjia.exceptions.ResourceLockFailException;
import com.wanjia.service.SpecialtyOrderService;
import com.wanjia.utils.*;
import com.wanjia.vo.cart.ShopCartSpecialtyVo;
import com.wanjia.vo.speciality.SpecialtyVo;
import org.apache.log4j.Logger;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 特产资源锁定和订单服务 service
 * Created by blake on 2016/10/10.
 */
@Service("specialtyOrderService")
public class SpecialtyOrderServiceImpl implements SpecialtyOrderService {

	private Logger logger = Logger.getLogger(SpecialtyOrderServiceImpl.class) ;

	@Autowired
	SpecialtyBookedInfoMapper specialtyBookedInfoMapper ;
	@Autowired
	ExceptionLoggerServiceImpl exceptionLoggerService ;

	@Autowired
	ElasticSearchClient elasticSearchClient ;

	@Autowired
	UserSpecialtyOrderMapper userSpecialtyOrderMapper ;

	public  int lockSpecialtyResource(List<ShopCartSpecialtyVo> shopCartSpecialtyVoList /*, List<OrderShopCartProductErrorInfo> orderShopCartProductErrorInfoList*/) throws ResourceLockFailException{

		for(ShopCartSpecialtyVo shopCartSpecialtyVo : shopCartSpecialtyVoList){

			/*OrderShopCartProductErrorInfo orderShopCartProductErrorInfo = new OrderShopCartProductErrorInfo(shopCartSpecialtyVo,3);
			orderShopCartProductErrorInfoList.add(orderShopCartProductErrorInfo) ;*/

			try {
				int lockResult = lockSpecialtyResourceInternal(shopCartSpecialtyVo);
		       /* if(lockResult == 0){
			        orderShopCartProductErrorInfo.setMessage(ResourceLockState.Error);
			        orderShopCartProductErrorInfo.setCode(-1);
			        return 0 ;
		        }else{
			        orderShopCartProductErrorInfo.setMessage(ResourceLockState.Success);
			        orderShopCartProductErrorInfo.setCode(1);
		        }*/
			}catch (MySqlException e){
				throw new ResourceLockFailException("query mysql error",e);
			}catch (ResourceLockFailException e2){
				throw e2;
			}
		}
		return 1 ;

	}


	private int lockSpecialtyResourceInternal(ShopCartSpecialtyVo specialtyVo) throws MySqlException{

		int flag = 1 ;

		//获得特产的预订情况
		SpecialtyBookedInfo specialtyBookedInfo =  getSpecialtyBookedInfo(specialtyVo) ;



		long totalNumber = specialtyBookedInfo.getSpecialNumber() ;
		//新预订的数量加上已经被预订的数量就是要新锁定的资源的总量
		int newBookNumber = specialtyVo.getBookNumber() + specialtyBookedInfo.getBookedNumber();

		if(newBookNumber > totalNumber && totalNumber != -1){
			throw new ResourceLockFailException("lock resource fail no sufficient resources",specialtyVo.getShopId(),specialtyVo.getSpecialtyId(),3) ;
		}

		Map lockResoureMap = generateSpecialtyResourceLockParam(specialtyBookedInfo,newBookNumber);

		flag = specialtyBookedInfoMapper.lockSpecialtyResource(lockResoureMap);
		if(flag == 0){
			lockSpecialtyResourceInternal(specialtyVo) ;
		}

		return flag ;
	}

	private SpecialtyBookedInfo getSpecialtyBookedInfo(ShopCartSpecialtyVo specialtyVo) throws MySqlException{

		SpecialtyBookedInfo specialtyBookedInfo = null ;
		Map paramMap  = new HashMap();
		long shopId = specialtyVo.getShopId() ;
		long specialtyId  = specialtyVo.getSpecialtyId() ;

		paramMap.put("shopId",shopId);
		paramMap.put("specialtyId",specialtyId);
		//获得特产的预订情况
		specialtyBookedInfo =  specialtyBookedInfoMapper.selectByShopIdAndSpecialtyId(paramMap) ;

		return specialtyBookedInfo ;

	}


	private Map generateSpecialtyResourceLockParam(SpecialtyBookedInfo specialtyBookedInfo,int bookNumber){

		Map lockResoureMap = new HashMap() ;

		lockResoureMap.put("version",specialtyBookedInfo.getVersion());
		lockResoureMap.put("id",specialtyBookedInfo.getId());
		lockResoureMap.put("newVersion",System.currentTimeMillis());
		lockResoureMap.put("bookedNumber",bookNumber) ;

		return lockResoureMap ;

	}

	/*public void rollBackSpecialyResource(OrderShopCartProductErrorInfo orderShopCartProductErrorInfo){

		ShopCartSpecialtyVo shopCartSpecialtyVo = (ShopCartSpecialtyVo) orderShopCartProductErrorInfo.getShopCartBaseVo() ;
		SpecialtyBookedInfo  specialtyBookedInfo = getSpecialtyBookedInfo(shopCartSpecialtyVo) ;
		try {
			rollBackSpecialtyResourceInternal(shopCartSpecialtyVo,specialtyBookedInfo);
		} catch (Exception e) {
			//todo 回滚异常处理
			Date bookDate = DateUtil.parseDateStrToDate(shopCartSpecialtyVo.getBookDate()) ;
			exceptionLoggerService.addResourceLockErrorInfo(JsonUtil.toJsonString(shopCartSpecialtyVo),bookDate,(byte)2,3,e.getLocalizedMessage());
			logger.error("rollback specialty resource error",e);

		}

	}*/


	/*private void rollBackSpecialtyResourceInternal(ShopCartSpecialtyVo specialtyVo ,SpecialtyBookedInfo specialtyBookedInfo)throws  Exception{

		int bookNumber = specialtyBookedInfo.getBookedNumber() - specialtyVo.getBookNumber() ;
		Map rollBackSpecialtyParam = generateSpecialtyResourceLockParam(specialtyBookedInfo,bookNumber) ;
		int flag = specialtyBookedInfoMapper.lockSpecialtyResource(rollBackSpecialtyParam);
		if(flag == 0){
			rollBackSpecialtyResourceInternal(specialtyVo,specialtyBookedInfo) ;
		}
	}*/


	/**
	 * 购物车中特产转换成订单
	 * @param shopCartSpecialtyVo
	 * @param pid
	 */
	@Override
	public double shopCartSpecialtyToOrder(ShopCartSpecialtyVo shopCartSpecialtyVo,String pid) throws OrderGenerateException{


		double totalPrice = 0 ;
		long shopId = shopCartSpecialtyVo.getShopId() ;
		long userId = shopCartSpecialtyVo.getUserId() ;
		String bookDate = shopCartSpecialtyVo.getBookDate();
		int bookNumber = shopCartSpecialtyVo.getBookNumber() ;
		long specialtyId = shopCartSpecialtyVo.getSpecialtyId() ;

		String esId = shopId+"_"+specialtyId ;
		StringBuilder specialtyIdBuffer = new StringBuilder() ;
		specialtyIdBuffer.append("specialty").append("_").append(userId).append("_").append(shopId)
				         .append("_").append(specialtyId).append("_").append(System.currentTimeMillis());

		try {
			Map<String,Object> entityMap = elasticSearchClient.getEntityById(ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_INDEX,ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_TYPE,esId);
			double  price = Double.parseDouble(entityMap.get("specialtyPrice").toString());
			UserSpecialtyOrder userSpecialtyOrder = new UserSpecialtyOrder() ;
			userSpecialtyOrder.setShopId(shopId);
			userSpecialtyOrder.setIsComment(false);
			userSpecialtyOrder.setIsRemark((byte)0);
			userSpecialtyOrder.setpOrderId(pid);
			userSpecialtyOrder.setSpecialtyId(specialtyId);
			userSpecialtyOrder.setOrderId(specialtyIdBuffer.toString());
			userSpecialtyOrder.setPrice(price);
			userSpecialtyOrder.setCreateDate(new Date());
			userSpecialtyOrder.setBookDate(DateUtil.parseDateStrToDate(bookDate));
			userSpecialtyOrder.setBookNumber(bookNumber);
			int result = userSpecialtyOrderMapper.insert(userSpecialtyOrder);
			if(result == 1){
				totalPrice = price* bookNumber ;
				return totalPrice ;
			}else{
				throw new OrderGenerateException("generate order error",shopCartSpecialtyVo) ;
			}
		} catch (ElasticSearchException e) {
			logger.error("query es error",e);
			throw new OrderGenerateException("es error",e) ;
		}catch (MySqlException e){
			logger.error("mysql error",e);
			throw new OrderGenerateException("mysql error",e) ;
		}catch (Exception e){
			logger.error("inner error",e);
			throw new OrderGenerateException("inner error",e) ;
		}
	}


	@Override
	//如果es 失败把数据存放在redis中
	public void updateSpecialtyBookInEs(ShopCartSpecialtyVo shopCartSpecialtyVo, int operate) throws OrderGenerateException{

		long shopId = shopCartSpecialtyVo.getShopId() ;
		long specialtyId = shopCartSpecialtyVo.getSpecialtyId() ;
		int bookNumber = shopCartSpecialtyVo.getBookNumber() ;
		StringBuilder idPrefix = new StringBuilder();
		idPrefix.append(shopId).append("_").append(specialtyId);

		GetRequest getRequest = new GetRequest(ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_INDEX,ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_TYPE,idPrefix.toString());
		try {
			GetResponse getResponse = elasticSearchClient.getRequestExecute(getRequest) ;
			SpecialtyVo specialtyVo =  (SpecialtyVo)JsonUtil.toObject(getResponse.getSourceAsString(),SpecialtyVo.class);
			int specialtyNumber = specialtyVo.getSpecialtyNumber() ;

			UpdateRequest updateRequest = new UpdateRequest(ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_INDEX,ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_TYPE,idPrefix.toString());
			updateRequest.version(getRequest.version());
			if(operate ==1 ){
				updateRequest.doc("specialtyNumber",specialtyNumber-bookNumber);
			}else{
				updateRequest.doc("specialtyNumber",specialtyNumber+bookNumber);
			}
			elasticSearchClient.executeUpdateRequest(updateRequest);
		} catch (ElasticSearchException e) {
			logger.error("es phase es error",e);
			//todo   这个es的异常需要后续处理
		}catch (VersionConflictEngineException e){
			updateSpecialtyBookInEs(shopCartSpecialtyVo,operate);
		}

	}







}

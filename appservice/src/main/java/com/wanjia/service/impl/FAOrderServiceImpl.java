package com.wanjia.service.impl;

import com.wanjia.dao.UserSpecialtyOrderMapper;
import com.wanjia.entity.order.UserSpecialtyOrder;
import com.wanjia.exceptions.*;
import com.wanjia.service.FAOrderService;
import com.wanjia.service.ShopInfoService;
import com.wanjia.utils.DateUtil;
import com.wanjia.utils.ESIndexAndTypeConstant;
import com.wanjia.utils.ElasticSearchClient;
import com.wanjia.vo.cart.ShopCartTravelFamilyActivityVo;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 农家特色游资源锁定和订单 service
 * Created by blake on 2016/10/10.
 */

@Service("fAOrderService")
public class FAOrderServiceImpl implements FAOrderService {

	private Logger logger = Logger.getLogger(FAOrderServiceImpl.class) ;


	@Autowired
	ElasticSearchClient elasticSearchClient;

	@Autowired
	ExceptionLoggerServiceImpl exceptionLoggerService ;

	@Autowired
	ShopInfoService shopInfoService;

	@Autowired
	UserSpecialtyOrderMapper userSpecialtyOrderMapper;


	public int lockFAResource(List<ShopCartTravelFamilyActivityVo> shopCartTravelFamilyActivityVos /*, List<OrderShopCartProductErrorInfo> orderShopCartProductErrorInfos*/) throws ResourceLockFailException{

		for(ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo : shopCartTravelFamilyActivityVos){
			//todo remove code
		/*	OrderShopCartProductErrorInfo orderShopCartProductErrorInfo = new OrderShopCartProductErrorInfo(shopCartTravelFamilyActivityVo,5);
			orderShopCartProductErrorInfos.add(orderShopCartProductErrorInfo) ;*/

			try {
				int lockResult = lockFAResourceInternal(shopCartTravelFamilyActivityVo);
				/*if(lockResult == 0 ){
					orderShopCartProductErrorInfo.setMessage(ResourceLockState.Error);
					orderShopCartProductErrorInfo.setCode(-1);
					return 0 ;
				}
				orderShopCartProductErrorInfo.setMessage(ResourceLockState.Success);
				orderShopCartProductErrorInfo.setCode(1);*/
			} catch (ElasticSearchException e) {
				/*orderShopCartProductErrorInfo.setMessage(ResourceLockState.Error);
				orderShopCartProductErrorInfo.setCode(-1);*/
				logger.error("query es error",e);
				throw new ResourceLockFailException("query es error",e) ;
				//return 0 ;
			}catch (ResourceLockFailException e2){
				throw e2 ;
			}
		}

		return 1 ;
	}

	public int lockFAResourceInternal(ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo) throws ElasticSearchException,ResourceLockFailException{

		long shopId = shopCartTravelFamilyActivityVo.getShopId() ;
		long familyActivityId = shopCartTravelFamilyActivityVo.getFamilyActivityId() ;

		int allowBookMaxNumber = getFMMaxBookNumber(shopId,familyActivityId);
		int bookNumber = shopCartTravelFamilyActivityVo.getBookNumber() ;

		if(bookNumber > allowBookMaxNumber){
			throw new ResourceLockFailException("lock resource fail , no sufficient resources",shopCartTravelFamilyActivityVo.getShopId(),shopCartTravelFamilyActivityVo.getFamilyActivityId(),5);
			//return 0;
		}
		return 1 ;
	}

	//获得农家自助游允许的最大预订数
	private int getFMMaxBookNumber(long shopId, long familyActivityId) throws ElasticSearchException {

		int number = 0;
		String id = shopId + "_" + familyActivityId;
		QueryBuilder queryBuilder = QueryBuilders.termQuery("_id", id);
		List<String> fields = new ArrayList<String>();
		fields.add("maxBookNumber");
		Map<String, Object> fieldValueMap = null;
		try {
			fieldValueMap = elasticSearchClient.queryUniqueColumnSpecificField(queryBuilder, null, fields, ESIndexAndTypeConstant.SHOP_TRAVEL_FAMILYACTIVITY_INDEX, ESIndexAndTypeConstant.SHOP_TRAVEL_FAMILYACTIVITY_TYPE);
		} catch (Exception e) {
			throw new ElasticSearchException("query es error", e);
		}
		number = (Integer) fieldValueMap.get("maxBookNumber");

		return number;

	}

	/**
	 * 购物车农家特色游产品转换成订单
	 * @param shopCartTravelFamilyActivityVo
	 * @param pid
	 */
	public double shopCartFAToOrder(ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo ,String pid) throws OrderGenerateException{

		double totalPrice = 0 ;

		try {
			long shopId = shopCartTravelFamilyActivityVo.getShopId() ;
			long userId = shopCartTravelFamilyActivityVo.getUserId();
			long faId = shopCartTravelFamilyActivityVo.getFamilyActivityId() ;
			int bookNumber = shopCartTravelFamilyActivityVo.getBookNumber() ;
			String faDate = shopCartTravelFamilyActivityVo.getFamilyActivityDate();

			String key = "shop_travel_familyactivity_price_"+shopId+"_"+faId ;
			double price = shopInfoService.getTravelPrice(key, DateUtil.parseDateToLongValue(faDate));

			StringBuilder idBuilder = new StringBuilder();
			idBuilder.append("fa").append("_").append(userId).append("_").append(shopId)
					    .append("_").append(faId).append("_").append(System.currentTimeMillis());

			UserSpecialtyOrder userSpecialtyOrder = new UserSpecialtyOrder();
			userSpecialtyOrder.setOrderId(idBuilder.toString());
			userSpecialtyOrder.setpOrderId(pid);
			userSpecialtyOrder.setShopId(shopId);
			userSpecialtyOrder.setSpecialtyId(faId);
			userSpecialtyOrder.setBookDate(DateUtil.parseDateStrToDate(faDate));
			userSpecialtyOrder.setCreateDate(new Date());
			userSpecialtyOrder.setPrice(price);
			userSpecialtyOrder.setBookNumber(bookNumber);

			int affectNumber = userSpecialtyOrderMapper.insert(userSpecialtyOrder);
			if(affectNumber == 0){
				throw new OrderGenerateException("order generate error",shopCartTravelFamilyActivityVo) ;
			}else{
				totalPrice = price * bookNumber ;
				return totalPrice ;
			}
		}  catch (ParseException e) {
			logger.error("parse date error",e);
			throw new OrderGenerateException("parse error",e) ;

		}catch (RedisException e){
			logger.error("redis error",e);
			throw new OrderGenerateException("redis error",e) ;
		}catch (MySqlException e){
			logger.error("mysql error",e);
			throw new OrderGenerateException("mysql error",e) ;
		}


	}

}

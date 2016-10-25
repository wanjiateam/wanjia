package com.wanjia.service.impl;

import com.wanjia.dao.UserTicketOrderMapper;
import com.wanjia.entity.order.UserTicketOrder;
import com.wanjia.exceptions.*;
import com.wanjia.service.ShopInfoService;
import com.wanjia.service.TicketOrderService;
import com.wanjia.utils.DateUtil;
import com.wanjia.utils.ESIndexAndTypeConstant;
import com.wanjia.utils.ElasticSearchClient;
import com.wanjia.vo.cart.ShopCartTravelTicketVo;
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
 * 门票资源锁定和门票订单 服务相关的service
 * Created by blake on 2016/10/10.
 */
@Service("ticketOrderService")
public class TicketOrderServiceImpl implements TicketOrderService {


	private Logger logger = Logger.getLogger(TicketOrderServiceImpl.class) ;


	@Autowired
	ElasticSearchClient elasticSearchClient;

	@Autowired
	ExceptionLoggerServiceImpl exceptionLoggerService ;

	@Autowired
	ShopInfoService shopInfoService ;

	@Autowired
	UserTicketOrderMapper userTicketOrderMapper;


	public int lockTicketResource(List<ShopCartTravelTicketVo> shopCartTravelTicketVoList/*, List<OrderShopCartProductErrorInfo> orderShopCartProductErrorInfoList*/) throws ResourceLockFailException {

		for(ShopCartTravelTicketVo shopCartTravelTicketVo : shopCartTravelTicketVoList){
			//todo
			/*OrderShopCartProductErrorInfo orderShopCartProductErrorInfo = new OrderShopCartProductErrorInfo(shopCartTravelTicketVo,4);
			orderShopCartProductErrorInfoList.add(orderShopCartProductErrorInfo);*/

			try {
				int ticketResourceLockResult = lockTicketResourceInternal(shopCartTravelTicketVo);
				/*if(ticketResourceLockResult == 0){
					orderShopCartProductErrorInfo.setCode(-1);
					orderShopCartProductErrorInfo.setMessage(ResourceLockState.Error);
					return 0;
				}*/
			} catch (ElasticSearchException e) {
				/*orderShopCartProductErrorInfo.setCode(-1);
				orderShopCartProductErrorInfo.setMessage(ResourceLockState.Error);*/
				logger.error("query es error",e);
				throw new ResourceLockFailException("query es error",e);
				//return 0 ;
			}catch (ResourceLockFailException e2){
				throw e2 ;
			}
			/*orderShopCartProductErrorInfo.setCode(1);
			orderShopCartProductErrorInfo.setMessage(ResourceLockState.Success);*/
		}

		return 1 ;

	}


	public int lockTicketResourceInternal(ShopCartTravelTicketVo shopCartTravelTicketVo) throws ElasticSearchException{

		long shopId = shopCartTravelTicketVo.getShopId();
		long ticketId = shopCartTravelTicketVo.getTicketId() ;
		int maxAllowBookNumber = getTicketMaxBookNumber(shopId,ticketId);
		int  bookNumber = shopCartTravelTicketVo.getBookNumber() ;
		if(bookNumber > maxAllowBookNumber){
			throw new ResourceLockFailException("lock resource error,no sufficient resources",shopCartTravelTicketVo.getShopId(),shopCartTravelTicketVo.getTicketId(),4) ;
			/*return  0 ;*/
		}

		return 1 ;
	}



	//获得门票允许的最大预订数
	private int getTicketMaxBookNumber(long shopId, long ticketId) throws ElasticSearchException {

		int number = 0;
		String id = shopId + "_" + ticketId;
		QueryBuilder queryBuilder = QueryBuilders.termQuery("_id", id);
		List<String> fields = new ArrayList<String>();
		fields.add("maxBookNumber");
		Map<String, Object> fieldValueMap = null;
		try {
			fieldValueMap = elasticSearchClient.queryUniqueColumnSpecificField(queryBuilder, null, fields, ESIndexAndTypeConstant.SHOP_TRAVEL_TICKET_INDEX, ESIndexAndTypeConstant.SHOP_TRAVEL_TICKET_TYPE);
		} catch (Exception e) {
			throw new ElasticSearchException("query es error", e);
		}
		number = (Integer) fieldValueMap.get("maxBookNumber");

		return number;

	}

	/**
	 * 购物车中的门票信息转换成订单
	 * @param shopCartTravelTicketVo
	 * @param pid
	 * @throws OrderGenerateException
	 */

	@Override
	public double shopCartTicketToOrder(ShopCartTravelTicketVo shopCartTravelTicketVo,String pid) throws OrderGenerateException {


		double totalPrice = 0 ;
		try {
			long shopId = shopCartTravelTicketVo.getShopId() ;
			long ticketId = shopCartTravelTicketVo.getTicketId() ;
			long userId = shopCartTravelTicketVo.getUserId() ;
			long bookNumber = shopCartTravelTicketVo.getBookNumber() ;
			String ticketDate = shopCartTravelTicketVo.getTicketDate() ;
			String key = "shop_travel_ticket_price_"+shopId+"_"+ticketId ;

			double price = shopInfoService.getTravelPrice(key, DateUtil.parseDateToLongValue(ticketDate)) ;

			StringBuilder ticketIdBuilder = new StringBuilder();
			ticketIdBuilder.append("ticket").append("_").append(userId).append("_").append(shopId).append("_").append(ticketId).append("_")
					             .append(System.currentTimeMillis());

			UserTicketOrder userTicketOrder = new UserTicketOrder() ;
			userTicketOrder.setOrderId(ticketIdBuilder.toString());
			userTicketOrder.setpOrderId(pid);
			userTicketOrder.setCreateDate(new Date());
			userTicketOrder.setTourDate(DateUtil.parseDateStrToDate(ticketDate));
			userTicketOrder.setShopId(shopId);
			userTicketOrder.setTicketId(ticketId);
			userTicketOrder.setPrice(price);
			userTicketOrder.setBookNumer((int)bookNumber);

			int affectNumber = userTicketOrderMapper.insert(userTicketOrder);
			if(affectNumber == 0){
				throw new OrderGenerateException("order generate error",shopCartTravelTicketVo) ;
			}else{
				totalPrice = price * bookNumber ;
				return  totalPrice ;
			}

		} catch (ParseException e) {
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

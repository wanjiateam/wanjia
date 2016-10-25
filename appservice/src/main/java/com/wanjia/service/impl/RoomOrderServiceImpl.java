package com.wanjia.service.impl;

import com.wanjia.dao.RoomBookedInfoMapper;
import com.wanjia.dao.UserLiveOrderMapper;
import com.wanjia.entity.order.UserLiveOrder;
import com.wanjia.entity.order.book.RoomBookedInfo;
import com.wanjia.exceptions.*;
import com.wanjia.service.RoomOrderService;
import com.wanjia.service.ShopInfoService;
import com.wanjia.utils.*;
import com.wanjia.vo.cart.OrderShopCartProductErrorInfo;
import com.wanjia.vo.cart.ShopCartRoomVo;
import com.wanjia.vo.live.RoomBookVo;
import org.apache.log4j.Logger;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * Created by blake on 2016/10/10.
 */
@Service("roomOrderService")
public class RoomOrderServiceImpl implements RoomOrderService {

	private Logger logger = Logger.getLogger(RoomOrderServiceImpl.class) ;



	@Autowired
	RoomBookedInfoMapper roomBookedInfoMapper;

	@Autowired
	ElasticSearchClient elasticSearchClient;

	@Autowired
	ExceptionLoggerServiceImpl exceptionLoggerService ;

	@Autowired
	UserLiveOrderMapper userLiveOrderMapper ;


	@Autowired
	ShopInfoService shopInfoService;
	/**
	 * 锁定住的资源，按照购物车中住的产品 分别去锁定资源，如果有锁定失败的直接返回
	 *todo 修改这一部分逻辑
	 * @param roomVoList
	 */
	public int lockRoomResource(List<ShopCartRoomVo> roomVoList) throws ResourceLockFailException {

		for (ShopCartRoomVo roomVo : roomVoList) {
			//先去获得自己预订量是不是大于最大的可预订数量
			String startDate = roomVo.getStartDate();
			String endDate = roomVo.getEndDate();

			List<Date> dateList = DateUtil.dateList(startDate, endDate);

			if (dateList != null) {
				for (Date liveDate : dateList) {
					//查询房间预订动态表的参数
					try {
						int lockResult = lockRoomResourceInternal(roomVo,liveDate);
					} catch (ElasticSearchException e) {
						logger.error("query es error",e);
						throw new ResourceLockFailException("query es error",e);

					}catch (MySqlException e2){
						logger.error("query redis error",e2);
						throw new ResourceLockFailException("query redis error",e2) ;
					}catch (ResourceLockFailException e3){
						throw e3 ;
					}

				}
			}
		}

		return 1;
	}


	private int lockRoomResourceInternal(ShopCartRoomVo roomVo,Date liveDate) throws ElasticSearchException,MySqlException,ResourceLockFailException{

		int flag = 1 ;
		Map lockRoomResourceParams = checkRoomBookedInfo(roomVo ,liveDate);

		if(lockRoomResourceParams != null){

			int updateResult = updateRoomNumber(lockRoomResourceParams);

			if (updateResult != 1) {
				//如果version 不同表示可能是因为别的竞争资源导致更新失败，那么这个时候需要继续去lock 资源
				lockRoomResourceInternal(roomVo,liveDate) ;
			} else {
				//锁定资源成功
				flag = 1 ;
			}
		}else{
			throw new ResourceLockFailException("resource lock fail exception",roomVo.getShopId(),roomVo.getRoomId(),1);
		}
		return flag ;

	}





	private int getRoomNumber(long shopId, long roomId) throws ElasticSearchException {

		int number = 0;
		String id = shopId + "_" + roomId;
		QueryBuilder queryBuilder = QueryBuilders.termQuery("_id", id);
		List<String> fields = new ArrayList<String>();
		fields.add("roomNumber");
		Map<String, Object> fieldValueMap = null;
		try {
			fieldValueMap = elasticSearchClient.queryUniqueColumnSpecificField(queryBuilder, null, fields, ESIndexAndTypeConstant.SHOP_ROOM_INDEX, ESIndexAndTypeConstant.SHOP_ROOM_TYPE);
		} catch (Exception e) {
			throw new ElasticSearchException("query es error", e);
		}
		number = (Integer) fieldValueMap.get("roomNumber");

		return number;

	}

	//这个预订信息是在数据库中查询
	private Map checkRoomBookedInfo(ShopCartRoomVo roomVo , Date liveDate) throws ElasticSearchException,MySqlException {

		long shopId = roomVo.getShopId();
		long roomId = roomVo.getRoomId() ;


		RoomBookedInfo roomBookedInfo = getRoomBookedInfo(roomVo,liveDate) ;
		Map map = new HashMap();
		//表示已经被预订的数量
		int bookedNumber = 0;
		long version = 0l;
		//数据库中bookId是自动增长的，如果在更新的时候发现数据库中不存在对应的预订信息 则插入一条预订信息到DB id为1 这样可以避免并发的问题
		long bookId = 1l;
		long newVersion = System.currentTimeMillis();
		//如果预订信息在数据库中为空 则需要插入一条数据到数据库
		if (roomBookedInfo == null) {
			int number = getRoomNumber(shopId, roomId);
			RoomBookedInfo roomBookedRecord = new RoomBookedInfo();
			roomBookedRecord.setRoomId(roomId);
			roomBookedRecord.setShopId(shopId);
			roomBookedRecord.setVersion(version);
			roomBookedRecord.setBookDate(liveDate);
			roomBookedRecord.setBookNumber(0);
			roomBookedRecord.setNumber(number);
			roomBookedRecord.setBookId(bookId);
			roomBookedInfoMapper.insert(roomBookedRecord);

		} else {
			bookedNumber = roomBookedInfo.getBookNumber()+roomVo.getBookNumber();
			int totalNumber = roomBookedInfo.getNumber() ;
			if(bookedNumber>totalNumber){
				return  null ;
			}
		}

		map = generateLockRoomResourceParameter(roomBookedInfo,roomVo,bookedNumber) ;

		return map ;
	}

	private RoomBookedInfo getRoomBookedInfo(ShopCartRoomVo roomVo , Date liveDate) throws MySqlException{

		RoomBookedInfo roomBookedInfo = null ;
		long shopId = roomVo.getShopId();
		long roomId = roomVo.getRoomId() ;
		Map queryBookedRecordParamaters = new HashMap();
		queryBookedRecordParamaters.put("shopId",shopId);
		queryBookedRecordParamaters.put("roomId",roomId) ;
		queryBookedRecordParamaters.put("bookDate",liveDate);

		try {
			roomBookedInfo = roomBookedInfoMapper.selectByShopIdRoomIdAndDate(queryBookedRecordParamaters);
		} catch (Exception e) {
			throw new MySqlException("mysql inner exception",e) ;
		}

		return roomBookedInfo ;
	}


	private int updateRoomNumber(Map paramters) throws MySqlException{
		int updateResult = 0 ;
		try{
			updateResult = roomBookedInfoMapper.updateRoomNumberById(paramters);
		} catch (Exception e) {
			throw new MySqlException("mysql inner exception",e) ;
		}
		return updateResult;
	}


	private void roomResourceRollBack(OrderShopCartProductErrorInfo orderShopCartProductErrorInfo){

		ShopCartRoomVo shopCartRoomVo = (ShopCartRoomVo) orderShopCartProductErrorInfo.getShopCartBaseVo() ;
		String startDate = shopCartRoomVo.getStartDate() ;
		String endDate = shopCartRoomVo.getEndDate() ;

		List<Date> dateList = DateUtil.dateList(startDate,endDate);
		for(Date liveDate : dateList){
			try {
				rollBackRoomResourceInternal(shopCartRoomVo,liveDate);

			} catch (Exception e) {
				//todo 如果异常了  需要把 异常的信息保存到数据库 这样用人工的方式处理
				try {
					exceptionLoggerService.addResourceLockErrorInfo(JsonUtil.toJsonString(shopCartRoomVo),liveDate,(byte)2,1,e.getLocalizedMessage());
				} catch (MySqlException e1) {
					logger.error("roll back resource error",e1);
				}
				logger.error("rollback room resource error",e);
			}

		}
	}


	private  int rollBackRoomResourceInternal(ShopCartRoomVo shopCartRoomVo,Date liveDate) throws  MySqlException,ElasticSearchException{


		RoomBookedInfo roomBookedInfo = getRoomBookedInfo(shopCartRoomVo,liveDate) ;
		//回滚的数量为已经预定的数量减去用户预订的数量
		int rollBackNumber = roomBookedInfo.getBookNumber() - shopCartRoomVo.getBookNumber() ;

		Map lockRoomResourceParam =generateLockRoomResourceParameter(roomBookedInfo,shopCartRoomVo,rollBackNumber);

		int updateResult = updateRoomNumber(lockRoomResourceParam) ;
		if(updateResult == 0){
			//回滚失败 可能的原因为 并发更新导致的version 版本不一致
			lockRoomResourceInternal(shopCartRoomVo, liveDate) ;
		}

		return 1 ;
	}

	private Map generateLockRoomResourceParameter(RoomBookedInfo roomBookedInfo,ShopCartRoomVo shopCartRoomVo,int bookedNumber){

		Map lockRoomResourceParam = new HashMap();
		long bookId = roomBookedInfo.getBookId() ;
		long version = roomBookedInfo.getVersion() ;
		lockRoomResourceParam.put("bookId",bookId);
		lockRoomResourceParam.put("oldVersion",version) ;
		lockRoomResourceParam.put("newVersion",System.currentTimeMillis());
		lockRoomResourceParam.put("bookedNumber",bookedNumber) ;
		return lockRoomResourceParam ;

	}



	@Override
	public double  shopCarRoomVoToRoomOrder(ShopCartRoomVo shopCartRoomVo,String pid) throws OrderGenerateException{

		try {

			long shopId = shopCartRoomVo.getShopId() ;
			long roomId = shopCartRoomVo.getRoomId() ;
			String startDate = shopCartRoomVo.getStartDate() ;
			long startDateMills = DateUtil.parseDateToLongValue(startDate);
			String endDate = shopCartRoomVo.getEndDate() ;
			long endDateMills = DateUtil.parseDateToLongValue(startDate) ;

			int bookNumber = shopCartRoomVo.getBookNumber() ;
			double price = shopInfoService.getRoomTotalPriceDuringDateRange(shopId,roomId,startDateMills,endDateMills) ;

			StringBuffer liveId = new StringBuffer();

			liveId.append("live").append("_").append(shopCartRoomVo.getUserId()).append("_").append(shopId).append("_").append(roomId).append("_").append(System.currentTimeMillis());

			UserLiveOrder userLiveOrder = new UserLiveOrder(liveId.toString(),pid,shopId,(int)roomId);
			userLiveOrder.setBookNumer((short)bookNumber);
			userLiveOrder.setPrice(price);
			userLiveOrder.setCreateDate(new Date());
			userLiveOrder.setStartDate(DateUtil.parseDateStrToDate(startDate));
			userLiveOrder.setEndDate(DateUtil.parseDateStrToDate(endDate));
			int affectNumber = userLiveOrderMapper.insert(userLiveOrder) ;
			//记录下每个订单生成的状态
			if(affectNumber == 1){
				return price*bookNumber ;
			}else{
				throw new OrderGenerateException("generate room order error",shopCartRoomVo) ;
			}

		} catch (ParseException e) {
			logger.error("parse date error ---",e);
			throw new OrderGenerateException("parse date error",e) ;

		} catch (RedisException e) {
			logger.error("query room price from redis error--",e);
			throw new OrderGenerateException("query redis error",e) ;
		}catch (MySqlException e){
			logger.error("convert shopCartRoom To liveOrder error,may be a db error",e);
			throw new OrderGenerateException("query mysql error",e) ;
		}catch (Exception e){
			logger.error("inner exception",e);
			throw new OrderGenerateException("inner error",e) ;
		}

	}


	@Override
	//如果es 失败把数据存放在redis中
	public void updateRoomBookInfoInEs(ShopCartRoomVo shopCartRoomVo, int operate) throws OrderGenerateException{

		long shopId = shopCartRoomVo.getShopId() ;
		long roomId = shopCartRoomVo.getRoomId() ;
		int bookNumber = shopCartRoomVo.getBookNumber() ;
		String endDate = shopCartRoomVo.getStartDate();
		String startDate = shopCartRoomVo.getEndDate();
		StringBuilder idPrefix = new StringBuilder();
		idPrefix.append(shopId).append("_").append(roomId).append("_") ;
		try {
			List<String> dateList = DateUtil.getDateList(DateUtil.parseDateToLongValue(startDate),DateUtil.parseDateToLongValue(endDate));
            for(String date : dateList){
	            String id =  idPrefix.toString()+date ;
	            updateRoomBookInfoInESInternal(id,shopCartRoomVo.getBookNumber(),operate);
            }
		} catch (ParseException e) {
			 throw new OrderGenerateException("es phase  parse exception",e) ;
		} catch (OrderGenerateException e) {
			throw e;

		}

	}

	/**
	 * 修改es中房间预订的具体信息
	 * @param id
	 * @param changeNumber
	 * @param operate 1代表 es中房间预订的数量 要减去被预定的数量！
	 *                   2表示es中房间预订的数量需要加上预订的数量 这种情况可能出现在修改多个es中的预订信息但是部分成功 部分失败 需要
	 *                把前面成功的修改回滚
	 */
	private void updateRoomBookInfoInESInternal(String id,int changeNumber,int operate) throws OrderGenerateException{


		try {
			GetRequest getRequest = new GetRequest(ESIndexAndTypeConstant.SHOP_ROOM_BOOK_INDEX,ESIndexAndTypeConstant.SHOP_ROOM_BOOK_TYPE,id) ;
			GetResponse getResponse = elasticSearchClient.getRequestExecute(getRequest);
			RoomBookVo bookVo = (RoomBookVo) JsonUtil.toObject(getResponse.getSourceAsString(), RoomBookVo.class) ;
			int number = 0 ;
			int esBookedNumber = bookVo.getBookRoomNumber() ;
			if(operate == 1){
				number = esBookedNumber - changeNumber ;
			}else if(operate == 2){
				number = esBookedNumber + changeNumber ;
			}
			UpdateRequest updateRequest = new UpdateRequest(ESIndexAndTypeConstant.SHOP_ROOM_BOOK_INDEX,ESIndexAndTypeConstant.SHOP_ROOM_BOOK_TYPE,id);

			updateRequest.doc("bookRoomNumber",number) ;
			updateRequest.version(getRequest.version()) ;
			elasticSearchClient.executeUpdateRequest(updateRequest);
		} catch (ElasticSearchException e) {
			logger.error("es error",e);
			//todo   这个es的异常需要后续处理
		}catch (VersionConflictEngineException e){
			updateRoomBookInfoInESInternal(id,changeNumber,operate);
		}
	}

}

package com.wanjia.service.impl;

import com.wanjia.dao.CourseBookedInfoMapper;
import com.wanjia.dao.UserMealOrderMapper;
import com.wanjia.dao.UserOrderMealCourseMapper;
import com.wanjia.entity.order.UserMealOrder;
import com.wanjia.entity.order.UserOrderMealCourse;
import com.wanjia.entity.order.book.CourseBookedInfo;
import com.wanjia.exceptions.ElasticSearchException;
import com.wanjia.exceptions.MySqlException;
import com.wanjia.exceptions.OrderGenerateException;
import com.wanjia.exceptions.ResourceLockFailException;
import com.wanjia.service.MealOrderService;
import com.wanjia.utils.*;
import com.wanjia.vo.cart.OrderShopCartProductErrorInfo;
import com.wanjia.vo.cart.ShopCartCourseVo;
import com.wanjia.vo.cart.ShopCartMealVo;
import com.wanjia.vo.restaurant.CourseVo;
import org.apache.log4j.Logger;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 餐饮订单和餐饮资源锁定service
 * Created by blake on 2016/10/10.
 */

@Service("mealOrderService")
public class MealOrderServiceImpl implements MealOrderService {

	private Logger logger = Logger.getLogger(RoomOrderServiceImpl.class) ;

	@Autowired
	ElasticSearchClient elasticSearchClient;

	@Autowired
	ExceptionLoggerServiceImpl exceptionLoggerService ;

	@Autowired
	CourseBookedInfoMapper courseBookedInfoMapper ;

	@Autowired
	UserMealOrderMapper userMealOrderMapper;

	@Autowired
	UserOrderMealCourseMapper userOrderMealCourseMapper ;

	public int lockMealResource(List<ShopCartMealVo> mealVoList/*, List<OrderShopCartProductErrorInfo> orderShopCartProductErrorInfos*/) throws ResourceLockFailException {

		for(ShopCartMealVo mealVo : mealVoList){
			String mealDateStr = mealVo.getBookDate() ;
			Date mealDate = DateUtil.parseDateStrToDate(mealDateStr) ;

			List<ShopCartCourseVo> shopCartCourseVoList = mealVo.getCourseVoList() ;
			if(shopCartCourseVoList == null || shopCartCourseVoList.size() == 0){
				continue;
			}
			//去锁定菜品的资源
			for(ShopCartCourseVo courseVo : shopCartCourseVoList){
				//todo
				/*OrderShopCartProductErrorInfo orderShopCartProductErrorInfo = new OrderShopCartProductErrorInfo(courseVo,2) ;*/
				try {
					int flag = lockMealCourseInternal(courseVo,mealDate);
					/*if(flag == 0){
						orderShopCartProductErrorInfo.setCode(-1);
						orderShopCartProductErrorInfo.setMessage(ResourceLockState.Error);
						return  0 ;
					}
					orderShopCartProductErrorInfo.setCode(1);
					orderShopCartProductErrorInfo.setMessage(ResourceLockState.Success);
					orderShopCartProductErrorInfo.setAttachment(mealVo);*/
				} catch (ElasticSearchException e) {
					/*orderShopCartProductErrorInfo.setCode(-1);
					orderShopCartProductErrorInfo.setMessage(ResourceLockState.Error);*/
					logger.error("query error",e);
					throw new ResourceLockFailException("query es error",e);
					//return  0 ;
				}catch (MySqlException e2){
					throw new ResourceLockFailException("query mysql error",e2);

				}catch (ResourceLockFailException e3){
					throw e3;
				}
			}
		}


		return 1 ;
	}


	private int lockMealCourseInternal(ShopCartCourseVo shopCartCourseVo,Date bookDate) throws ElasticSearchException,MySqlException,ResourceLockFailException{

		//先获得菜品的预订情况
		Map lockCourseMap = checkCourseBookedInfo(shopCartCourseVo,bookDate);
		if(lockCourseMap ==null){
			throw new ResourceLockFailException("lock resource fail,no sufficient resources",shopCartCourseVo.getShopId(),shopCartCourseVo.getCourseId(),2) ;
			// return 0 ;
		}

		int affectNumber = courseBookedInfoMapper.updateCourseBookedNumber(lockCourseMap) ;

		if(affectNumber == 0 ){
			//表示更新的时候出现了版本冲突 继续更新
			lockMealCourseInternal(shopCartCourseVo,bookDate);
		}

		return 1 ;
	}





	//获得菜品的预订情况
	private Map checkCourseBookedInfo(ShopCartCourseVo shopCartCourseVo, Date bookDate) throws ElasticSearchException ,MySqlException{

		Map map  = null;
		long shopId = shopCartCourseVo.getShopId();
		long courseId = shopCartCourseVo.getCourseId() ;
		//获取菜品预订的信息
		CourseBookedInfo courseBookedInfo = getCourseBookedInfo(shopCartCourseVo,bookDate) ;
		//表示已经被预订的数量
		int bookedNumber = 0;
		long version = 0l;
		long bookId = 1l;
		//如果预订信息在数据库中为空 则需要插入一条数据到数据库
		if (courseBookedInfo == null) {

			int number = getCourseNumber(shopId, courseId);
			courseBookedInfo = new CourseBookedInfo();
			courseBookedInfo.setShopId(shopId);
			courseBookedInfo.setCourseId(courseId);
			courseBookedInfo.setVersion(version);
			courseBookedInfo.setBookDate(bookDate);
			courseBookedInfo.setNumber(number);
			courseBookedInfo.setBookId(bookId);
			//需要判断插入是不是成功 如果失败说明有同时有别的操作在进行
			courseBookedInfoMapper.insert(courseBookedInfo);
		}

		bookedNumber = courseBookedInfo.getBookedNumber() ;
		int totalNumber = courseBookedInfo.getNumber() ;
		int newTotalBookedNumber = shopCartCourseVo.getBookNumber() + bookedNumber;
		if(newTotalBookedNumber > totalNumber){
			return null ;
		}

		map = generateCourseResourceLockParam(courseBookedInfo,newTotalBookedNumber) ;
		return map ;
	}


	private Map generateCourseResourceLockParam(CourseBookedInfo courseBookedInfo,int bookNumber){
		Map map = new HashMap() ;
		map.put("version",courseBookedInfo.getVersion()) ;
		map.put("bookId",courseBookedInfo.getBookId());
		map.put("bookedNumber",bookNumber) ;
		map.put("newVersion",System.currentTimeMillis()) ;
		return map ;
	}

	private CourseBookedInfo getCourseBookedInfo(ShopCartCourseVo shopCartCourseVo, Date bookDate) throws MySqlException{

		CourseBookedInfo courseBookedInfo = null ;
		long shopId = shopCartCourseVo.getShopId();
		long courseId = shopCartCourseVo.getCourseId() ;
		Map paramaters  = new HashMap();
		paramaters.put("shopId",shopId);
		paramaters.put("courseId",courseId);
		paramaters.put("bookDate",bookDate) ;
		courseBookedInfo =  courseBookedInfoMapper.selectByShopIdCourseIdAndDate(paramaters) ;

		return courseBookedInfo ;
	}


	private int getCourseNumber(long shopId, long courseId) throws ElasticSearchException {

		int number = 0;
		String id = shopId + "_" + courseId;
		QueryBuilder queryBuilder = QueryBuilders.termQuery("_id", id);
		List<String> fields = new ArrayList<String>();
		fields.add("courseNumber");
		Map<String, Object> fieldValueMap = null;
		try {
			fieldValueMap = elasticSearchClient.queryUniqueColumnSpecificField(queryBuilder, null, fields, ESIndexAndTypeConstant.SHOP_COURSE_INDEX, ESIndexAndTypeConstant.SHOP_COURSE_TYPE);
		} catch (Exception e) {
			throw new ElasticSearchException("query es error", e);
		}
		number = (Integer) fieldValueMap.get("courseNumber");

		return number;

	}



	//回滚菜品资源
	private void rollBackCourseResource(OrderShopCartProductErrorInfo orderShopCartProductErrorInfo) {

		ShopCartCourseVo shopCartCourseVo = (ShopCartCourseVo)orderShopCartProductErrorInfo.getShopCartBaseVo() ;
		ShopCartMealVo mealVo = (ShopCartMealVo) orderShopCartProductErrorInfo.getAttachment() ;
		String dateStr = mealVo.getBookDate() ;
		Date date = DateUtil.parseDateStrToDate(dateStr) ;
		try {
			rollBackCourseResourceInternal(shopCartCourseVo,date);
		} catch (Exception e) {
			//todo 回滚异常处理
			//todo 如果异常了  需要把 异常的信息保存到数据库 这样用人工的方式处理
			try {
				exceptionLoggerService.addResourceLockErrorInfo(JsonUtil.toJsonString(shopCartCourseVo),date,(byte)2,1,e.getLocalizedMessage());
			} catch (MySqlException e1) {
				logger.error("roll back resource error",e1);
			}
			logger.error("rollback room resource error",e);
		}
	}


	public void rollBackCourseResourceInternal(ShopCartCourseVo shopCartCourseVo ,Date courseDate) throws  Exception{

		CourseBookedInfo courseBookedInfo = getCourseBookedInfo(shopCartCourseVo,courseDate) ;

		int bookNumber = courseBookedInfo.getBookedNumber() - shopCartCourseVo.getBookNumber() ;
		Map  rollBackCourseResourceParam = generateCourseResourceLockParam(courseBookedInfo,bookNumber) ;

		int affectNumber = courseBookedInfoMapper.updateCourseBookedNumber(rollBackCourseResourceParam) ;

		if(affectNumber == 0 ){
			//表示更新的时候出现了版本冲突 继续更新
			rollBackCourseResourceInternal(shopCartCourseVo,courseDate);
		}
	}


	/**
	 * 生成餐饮订单
	 * @param shopCartMealVo
	 * @param pid
	 * @return
	 */
	@Override
	public double shopCartMealToOrder(ShopCartMealVo shopCartMealVo,String pid) throws OrderGenerateException{

		try {

			getCourseBaseInfo(shopCartMealVo);
			String bookDate = shopCartMealVo.getBookDate();
			int mealType = shopCartMealVo.getMealType();
			long shopId = shopCartMealVo.getShopId();
			long userId = shopCartMealVo.getUserId();
			StringBuilder mealId = new StringBuilder();
			mealId.append("meal").append("_").append(userId).append("_").append(shopId).append("_").append(System.currentTimeMillis());

			double totalPrice = 0;
			List<ShopCartCourseVo> shopCartCourseVoList = shopCartMealVo.getCourseVoList();
			List<UserOrderMealCourse> userOrderMealCourseList = new ArrayList<UserOrderMealCourse>();


			for (ShopCartCourseVo shopCartCourseVo : shopCartCourseVoList) {

				totalPrice += shopCartCourseVo.getPrice() * shopCartCourseVo.getBookNumber();

				UserOrderMealCourse courseOrder = new UserOrderMealCourse();
				courseOrder.setCourseId(shopCartCourseVo.getCourseId());
				courseOrder.setCourseNumber(shopCartCourseVo.getBookNumber());
				courseOrder.setPrice(shopCartCourseVo.getPrice());
				courseOrder.setfOrderId(mealId.toString());
				userOrderMealCourseList.add(courseOrder);
			}


			UserMealOrder userMealOrder = new UserMealOrder(mealId.toString(), pid, shopId);
			userMealOrder.setPrice(totalPrice);
			userMealOrder.setMealDate(DateUtil.parseDateStrToDate(bookDate));
			userMealOrder.setCreateDate(new Date());
			userMealOrder.setMealType((byte) mealType);


			//插入meal 订单信息到数据库
			int mealInsertResult = userMealOrderMapper.insert(userMealOrder);
			if (mealInsertResult != 1) {
				throw new OrderGenerateException("meal order generate exception") ;
			}
			//插入菜品信息到数据库
			int courseInsertSize = userOrderMealCourseMapper.batchInsert(userOrderMealCourseList);
			if (courseInsertSize == userOrderMealCourseList.size()) {
				return totalPrice;
			} else {
				throw new OrderGenerateException("add course order detail info exception") ;
			}
		}catch (ElasticSearchException e){
           throw new OrderGenerateException("es error",e) ;

		}catch (MySqlException e){
			throw new OrderGenerateException("mysql error",e) ;

		}catch (OrderGenerateException e){
			throw e ;
		}catch (Exception e){
           throw new OrderGenerateException("inner error",e) ;
		}

	}


	private void getCourseBaseInfo(ShopCartMealVo mealVo) throws ElasticSearchException{

		Map<Long,ShopCartCourseVo> shopCartCourseVoMap = new HashMap<Long,ShopCartCourseVo>() ;
		String index = ESIndexAndTypeConstant.SHOP_COURSE_INDEX;
		String type = ESIndexAndTypeConstant.SHOP_COURSE_TYPE;

		List<ShopCartCourseVo> shopCartCourseVoList = mealVo.getCourseVoList() ;
		List<String> courseIds = new ArrayList<String>() ;

		for(ShopCartCourseVo courseVo : shopCartCourseVoList){
			long shopId = courseVo.getShopId() ;
			long courseId = courseVo.getCourseId() ;
			courseIds.add(shopId+"_"+courseId);
			shopCartCourseVoMap.put(courseId,courseVo) ;
		}
		QueryBuilder queryBuilder = QueryBuilders.termsQuery("_id", courseIds);

		List<CourseVo> courseVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder, null, index, type, CourseVo.class);

		for(CourseVo courseVo : courseVos){
			long shopId = courseVo.getShopId() ;
			long courseId = courseVo.getCourseId() ;
			ShopCartCourseVo shopCartCourseVo = (ShopCartCourseVo) shopCartCourseVoMap.get(courseId) ;
			shopCartCourseVo.setShopName(courseVo.getShopName());
			shopCartCourseVo.setCourseName(courseVo.getCourseName());
			shopCartCourseVo.setPrice(courseVo.getCoursePrice());
		}
	}


	/**
	 * 修改es中的菜品预订信息
	 * @param shopCartMealVo
	 * @param operate
	 */
	@Override
	public void updateCourseBookInfoInEs(ShopCartMealVo shopCartMealVo,int operate){
		long shopId = shopCartMealVo.getShopId();
		String bookDate = shopCartMealVo.getBookDate() ;
		List<ShopCartCourseVo>  shopCartCourseVoList = shopCartMealVo.getCourseVoList() ;
		for(ShopCartCourseVo shopCartCourseVo : shopCartCourseVoList){
			updateCourseBookInfoInternal(shopCartCourseVo,bookDate,shopId,operate);
		}


	}

	private void updateCourseBookInfoInternal(ShopCartCourseVo shopCartCourseVo ,String date,long shopId,int operate){
		long courseId = shopCartCourseVo.getCourseId() ;
		String id = shopId+"_"+courseId+"_"+date ;
		int bookNumber = shopCartCourseVo.getBookNumber() ;
		GetRequest getRequest = new GetRequest(ESIndexAndTypeConstant.SHOP_COURSE_BOOK_INDEX,ESIndexAndTypeConstant.SHOP_COURSE_BOOK_TYPE,id);
		try {
			GetResponse getResponse = elasticSearchClient.getRequestExecute(getRequest) ;
			CourseBookedInfo courseBookedInfo = (CourseBookedInfo)JsonUtil.toObject(getResponse.getSourceAsString(),CourseBookedInfo.class) ;
			int bookedNumber = courseBookedInfo.getBookedNumber() ;

			UpdateRequest updateRequest = new UpdateRequest(ESIndexAndTypeConstant.SHOP_COURSE_BOOK_INDEX,ESIndexAndTypeConstant.SHOP_COURSE_BOOK_TYPE,id);
			updateRequest.version(getRequest.version());
			if(operate == 1){
				updateRequest.doc("bookedNumber",bookedNumber-bookNumber);
			}else if(operate == 2){
				updateRequest.doc("bookedNumber",bookedNumber+bookNumber);
			}
			elasticSearchClient.executeUpdateRequest(updateRequest);
		} catch (ElasticSearchException e) {
			logger.error("es phase es error",e);
			//todo 这个es的异常需要后续处理
		}catch (VersionConflictEngineException e){
			updateCourseBookInfoInternal(shopCartCourseVo,date,shopId,operate);
		}


	}

}


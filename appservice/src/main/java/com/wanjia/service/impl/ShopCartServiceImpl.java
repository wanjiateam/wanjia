package com.wanjia.service.impl;

import com.wanjia.exceptions.ElasticSearchException;
import com.wanjia.exceptions.RedisException;
import com.wanjia.service.ShopCartService;
import com.wanjia.service.ShopInfoService;
import com.wanjia.utils.*;
import com.wanjia.vo.cart.*;
import com.wanjia.vo.live.RoomVo;
import com.wanjia.vo.restaurant.CourseBookVo;
import com.wanjia.vo.restaurant.CourseVo;
import com.wanjia.vo.speciality.SpecialtyVo;
import com.wanjia.vo.travel.FamilyActivityVo;
import com.wanjia.vo.travel.GuideVo;
import com.wanjia.vo.travel.TicketVo;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 购物车服务类
 * Created by blake on 2016/8/3.
 */
@Service("shopCartService")
public class ShopCartServiceImpl implements ShopCartService {

	private static Logger logger = Logger.getLogger(ShopCartServiceImpl.class);

	//购物车在redis中key的前缀
	public static final  String shopCartKeyPrefix = "shopCart_";

	//购物车中住的ash key的前缀
	public static final String roomKeyPrefix = "room_";
	//购物车中餐饮hash key的前缀
	public static final String mealKeyPrefix = "meal_";
	//购物车中特产hash key的前缀
	public static final String specialtyKeyPrefix = "specialty_";
	//购物车中门票hash key的前缀
	public static final String ticketKeyPrefix = "ticket_";
	//购物车中导游hash key的前缀
	public static final String guideKeyPrefix = "guide_";
	//购物车中农家特色游hash key的前缀
	public static final String fmKeyPrefix = "familyActivity_";

	//redis 存放住的离线产品key的前缀
	public static final String OFFLINE_LIVE = "offline_live_";
	//redis 存放菜品的离线产品key的前缀
	public static final String OFFLINE_COURSE = "offline_COURSE_";
	//redis 存放特产的离线产品key的前缀
	public static final String OFFLINE_SPECIALTY = "offline_specialty_";
	//redis 存放门票的离线产品key的前缀
	public static final String OFFLINE_TICKET = "offline_ticket_";
	//redis 存放导游的离线产品key的前缀
	public static final String OFFLINE_GUIDE = "offline_guide_";
	//redis 存放农家特色游的离线产品key的前缀
	public static final String OFFLINE_FA = "offline_fa_";

	public static final int NEGATIVE_PRODUCT_NUMBER_ERROR_CODE = -2 ;


	@Autowired
	RedisClient redisClient;

	@Autowired
	ShopInfoService shopInfoService;

	@Autowired
	ElasticSearchClient elasticSearchClient;

	private String generateRedisCartKey(long userId) {
		return shopCartKeyPrefix + userId;
	}

	/**
	 * 用户添加住房到购物车
	 *
	 * @param shopCartRoomVo
	 */
	@Override
	public int addRoomToShopCart(ShopCartRoomVo shopCartRoomVo) throws Exception {

		//返回的标识 1 成功   2用户预订数超过允许的最大预订数,再次添加商品到购物车失败（针对的情况是用户多次添加一个商品到购物车）
		int returnFlag = 1;
		//所有的住房的hash key 都是 以room_开头  加上入住日期，离店日期和房间id
		String key = generateRedisCartKey(shopCartRoomVo.getUserId());
		long shopId = shopCartRoomVo.getShopId();
		try {

			String roomField = roomKeyPrefix + shopId + "_" + shopCartRoomVo.getStartDate() + "_" + shopCartRoomVo.getEndDate() + "_" + shopCartRoomVo.getRoomId();
			//判断同一个人同一个时间段有没有重复预订一个房间
			boolean isExist = redisClient.isHashFieldExist(key, roomField);
			if (isExist) {
				String roomVoStr = redisClient.getHashFieldValue(key, roomField);
				ShopCartRoomVo oldShopCartRoomVo = (ShopCartRoomVo) JsonUtil.toObject(roomVoStr, ShopCartRoomVo.class);
				int oldBookNumber = oldShopCartRoomVo.getBookNumber();
				int newBookNumber = shopCartRoomVo.getBookNumber();
				if(newBookNumber < 0){
					//减产品的数量 如果所剩的数量为0 直接把这个数据删除
					if(oldBookNumber+newBookNumber <= 0){
						redisClient.delHashKey(key,roomField);
						return 1  ;
					}else{
						shopCartRoomVo.setBookNumber(newBookNumber + oldBookNumber);
						returnFlag = (int) redisClient.setHashValue(key, roomField, JsonUtil.toJsonString(shopCartRoomVo));
					}
				}else{
					//获得目前最大的可预订数
					int allowBookNumber = shopInfoService.getRoomAllowBookNumberDuringDateRange(shopId, shopCartRoomVo.getRoomId(), shopCartRoomVo.getStartDate(), shopCartRoomVo.getEndDate());
					if (newBookNumber > (allowBookNumber - oldBookNumber)) {
						returnFlag = -1;
					} else {
						shopCartRoomVo.setBookNumber(newBookNumber + oldBookNumber);
						returnFlag = (int) redisClient.setHashValue(key, roomField, JsonUtil.toJsonString(shopCartRoomVo));
					}
				}

			} else {
				if(shopCartRoomVo.getBookNumber() < 0){
					return NEGATIVE_PRODUCT_NUMBER_ERROR_CODE ;
				}else{
					returnFlag = (int) redisClient.setHashValue(key, roomField, JsonUtil.toJsonString(shopCartRoomVo));
				}
			}
		} catch (Exception e) {
			logger.error("query redis error", e);
			throw e;
		}

		return returnFlag;
	}


	/**
	 * 修改房间的数量 前端应该控制 数量修改的限制
	 *
	 * @param userId
	 * @param shopId
	 * @param roomId
	 * @param changeNumber
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	@Override
	public long changeRoomNumber(long userId, long shopId, long roomId, int changeNumber, String startDate, String endDate) throws Exception {
		long affectNumber = 0;
		String key = generateRedisCartKey(userId);
		String roomField = roomKeyPrefix + shopId + "_" + startDate + "_" + endDate + "_" + roomId;
		if (changeNumber == 0) {
			affectNumber = redisClient.delHashKey(key, roomField);
		} else {
			String value = redisClient.getHashFieldValue(key, roomField);
			if (value == null) {
				throw new RuntimeException("specify product does not exist in shopCart");
			}
			ShopCartRoomVo roomVo = (ShopCartRoomVo) JsonUtil.toObject(value, ShopCartRoomVo.class);
			roomVo.setBookNumber(changeNumber);
			affectNumber = redisClient.setHashValue(key, roomField, JsonUtil.toJsonString(roomVo));
		}
		return affectNumber;
	}

	/**
	 * 用户添加菜品到购物车,
	 * 餐饮的数据存储分为两个部分，shopCart 中存放各个 meal的的信息，具体的菜品的信息存放在另一个hash结构中
	 * @param shopCartMealVo
	 * @throws Exception
	 */
	@Override
	public List<ShopCartCourseVo> addMealToShopCart(ShopCartMealVo shopCartMealVo) throws Exception {

		//用于记录所有超过最大预定数的菜品和预订数非法的菜品
		List<ShopCartCourseVo> errorCourseVos = new ArrayList<ShopCartCourseVo>();
		//用于合并新添加的和已经存在的菜品信息
		List<ShopCartCourseVo> mergeShopCartCourseVoList = new ArrayList<ShopCartCourseVo>();
		//所有的菜单的key 都是以meal开头 然后加上预定一起和mealType
		long userId = shopCartMealVo.getUserId() ;
		long shopId = shopCartMealVo.getShopId();

		String key = generateRedisCartKey(userId);
		String field = mealKeyPrefix +userId+"_"+shopId + "_" + shopCartMealVo.getBookDate() + "_" + shopCartMealVo.getMealType();

		copyMealShopBaseInfoToCourse(shopCartMealVo);

		//添加菜品到购物车
		// step1：检查餐饮信息是不是已经存在
		boolean hashFieldExist = redisClient.isHashFieldExist(key, field);
		//记录下所有不合法的菜品id
		List<Integer> invalidIds = new ArrayList<Integer>();
		//删除的菜品id
		List<Integer> delIds = new ArrayList<Integer>();

		if (hashFieldExist) {

			//获得指定菜品的相关信息
			Map<String,String>  courseInfoMap = redisClient.getAllHashValue(field) ;
			if(!courseInfoMap.isEmpty()){
				Map<Long,ShopCartCourseVo> shopCartNewCourseVoMap = new HashMap<Long,ShopCartCourseVo>();
				List<String> oldShopCartCourseVoBookIds = new ArrayList<String>();
				Collection<String> shopCartCourseVoStrs = courseInfoMap.values() ;
				for(String shopCartCourseVoStr : shopCartCourseVoStrs){
					ShopCartCourseVo courseVo = (ShopCartCourseVo) JsonUtil.toObject(shopCartCourseVoStr,ShopCartCourseVo.class) ;
					long courseId = courseVo.getCourseId() ;
					String id = shopId + "_" + courseId + "_" + shopCartMealVo.getBookDate() ;
					oldShopCartCourseVoBookIds.add(id) ;
					shopCartNewCourseVoMap.put(courseId,courseVo) ;
				}

				//存放购物车中所有商品目前的可预订数
				Map<Long, Integer> oldCourseAllowBookNumber = new HashMap<Long, Integer>();
				//获得所有courseId指定的course信息
				List<CourseBookVo> courseBookVos = getCourseBookedVosByIds(oldShopCartCourseVoBookIds);
				//把每个菜品 已经预定的情况加入map中 供查询使用
				for (CourseBookVo courseBookVo : courseBookVos) {
					int allowBookNumber = courseBookVo.getTotalNumber() - courseBookVo.getBookedNumber();
					oldCourseAllowBookNumber.put(courseBookVo.getCourseId(), allowBookNumber);
				}

				//对于新添加到购物车中的数据与已经存在的数据做比较
				List<ShopCartCourseVo> newShopCartCourseVoList = shopCartMealVo.getCourseVoList();
				for (int i = 0; i< newShopCartCourseVoList.size() ; i++) {
					ShopCartCourseVo newVo = newShopCartCourseVoList.get(i) ;
					long id = newVo.getCourseId();
					ShopCartCourseVo vo = shopCartNewCourseVoMap.get(id);
					if (vo != null) {
						int oldBookNumber = vo.getBookNumber();
						int newBookNumber = newVo.getBookNumber();
						if(newBookNumber < 0){
							int number = oldBookNumber +newBookNumber ;
							if(number <=0){
								redisClient.delHashKey(field,String.valueOf(newVo.getCourseId()));
							}
							newVo.setBookNumber(number);


						}else{
							Integer allowBookNumber = oldCourseAllowBookNumber.get(id);
							if (allowBookNumber == null) {
								allowBookNumber = getCourseNum(shopId + "_" + vo.getCourseId());
							}
							if (allowBookNumber == -1) {
								newVo.setBookNumber(oldBookNumber + newBookNumber);
							} else {
								//如果购物车中的产品的总量大于可预定的数量，记录下来返回给客户端
								if (newBookNumber + oldBookNumber > allowBookNumber) {
									newVo.setAllowBookNumber(allowBookNumber);
									newVo.setBookNumber(newBookNumber + oldBookNumber);
									errorCourseVos.add(newVo);
									invalidIds.add(i) ;
								} else {
									newVo.setBookNumber(oldBookNumber + newBookNumber);
								}
							}
						}
					}
				}
			}
		}
		//把具体菜品的信息加入购物车
		addCourseInfoToShopCart(field,shopCartMealVo,invalidIds,errorCourseVos,hashFieldExist) ;

		return errorCourseVos;


	}


	private void addCourseInfoToShopCart(String key ,ShopCartMealVo shopCartMealVo,List<Integer> inValidIds,List<ShopCartCourseVo> errorCourseVos,boolean hashFieldExist ) throws Exception{
		List<ShopCartCourseVo> courseVoList = shopCartMealVo.getCourseVoList();
		if(courseVoList != null && !courseVoList.isEmpty()){
			Map<String,String> value = new HashMap<String,String>();
			for(int i=0 ; i< courseVoList.size() ; i++){
				ShopCartCourseVo courseVo  = courseVoList.get(i);
				if(courseVo.getBookNumber() <=0 || inValidIds.contains(i)){
					errorCourseVos.add(courseVo);
				}else{
					String k = String.valueOf(courseVo.getCourseId()) ;
					String v = JsonUtil.toJsonString(courseVo) ;
					value.put(k,v);
				}

			}
			if(!value.isEmpty()){
				if(!hashFieldExist){
					redisClient.setHashValue(generateRedisCartKey(courseVoList.get(0).getUserId()), key,JsonUtil.toJsonString(shopCartMealVo));
				}
				redisClient.setMutilHashValue(key,value);
			}
		}

	}

	private List<CourseBookVo> getCourseBookedVosByIds(Collection<String> ids) throws Exception {
		QueryBuilder queryBuilder = QueryBuilders.termsQuery("_id", ids);
		return elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder, null, ESIndexAndTypeConstant.SHOP_COURSE_BOOK_INDEX, ESIndexAndTypeConstant.SHOP_COURSE_BOOK_TYPE, CourseBookVo.class);
	}


	private int getCourseNum(String id) throws ElasticSearchException{
		Integer value = null;
		Map<String, Object> result = elasticSearchClient.getEntityById(ESIndexAndTypeConstant.SHOP_COURSE_INDEX, ESIndexAndTypeConstant.SHOP_COURSE_TYPE, id);
		value = (Integer) result.get("courseNumber");
		//如果为空 表示es中不存在这个数据，那么数据直接设置成0
		if (value == null) {
			value = 0;
		}
		return value;
	}

	private void copyMealShopBaseInfoToCourse(ShopCartMealVo mealVo) {
		List<ShopCartCourseVo> courseVoList = mealVo.getCourseVoList();
		if (!courseVoList.isEmpty()) {
			long userId = mealVo.getUserId();
			long shopId = mealVo.getShopId();
			for (ShopCartCourseVo courseVo : courseVoList) {
				courseVo.setShopId(shopId);
				courseVo.setUserId(userId);
			}
		}
	}

	/**
	 * 删除一顿餐饮 分为两步 1 删除meal 信息 2 删除全部course信息
	 *
	 * @param userId
	 * @param shopId
	 * @param bookDate
	 * @param mealType
	 * @return
	 * @throws Exception
	 */
	@Override
	public int delMeal(long userId, long shopId, String bookDate, int mealType) throws RedisException {

		long affectNumber = 0;
		String key = generateRedisCartKey(userId);
		String field = mealKeyPrefix +userId+"_"+ shopId + "_" + bookDate + "_" + mealType;
		//1 删除meal 信息
		affectNumber = redisClient.delHashKey(key, field);
		//2 删除全部course信息
		affectNumber = redisClient.delKey(field) ;

		return (int) affectNumber;
	}

	@Override
	public void cleanShopCartByUserId(long userId) throws RedisException{
		redisClient.delKey(generateRedisCartKey(userId)) ;
	}

	/**
	 * 修改购物车中的菜品的数量
	 *
	 * @param userId
	 * @param shopId
	 * @param bookDate
	 * @param mealType
	 * @param courseId
	 * @param changeNumber
	 * @return
	 * @throws Exception
	 */
	@Override
	public int changeCourseNumber(long userId, long shopId, String bookDate, int mealType, long courseId, int changeNumber) throws Exception {

		int code = 0;
		String key = mealKeyPrefix +userId+"_"+ shopId + "_" + bookDate + "_" + mealType;
		String field = String.valueOf(courseId) ;
		String courseJson = redisClient.getHashFieldValue(key, field);
		if (courseJson != null) {
			if (changeNumber == 0) {
				code =  (int) redisClient.delHashKey(key,field);
				//判断 菜品的所有信息是不是 全部删除了，如果是 把餐饮的信息也删除
				Set<String> keys = redisClient.getAllHashKeys(key);
				if(key.length() == 0){
					String shopCartKey = generateRedisCartKey(userId) ;
					redisClient.delHashKey(shopCartKey,key) ;
				}
			} else {
				ShopCartCourseVo courseVo = (ShopCartCourseVo) JsonUtil.toObject(courseJson, ShopCartCourseVo.class);
				courseVo.setBookNumber(changeNumber);
				code = (int) redisClient.setHashValue(key, field, JsonUtil.toJsonString(courseVo));
			}
		} else {
			throw new RuntimeException("specify product does not exist in shopCart");
		}

		return code;
	}

	/**
	 * 用户添加特产到redis
	 *
	 * @param shopCartSpecialtyVo
	 * @throws Exception
	 */
	@Override
	public int addSpecialtyToShopCart(ShopCartSpecialtyVo shopCartSpecialtyVo) throws Exception {
		int flag = 1;

		String key = generateRedisCartKey(shopCartSpecialtyVo.getUserId());
		long shopId = shopCartSpecialtyVo.getShopId();
		//所有的特产在redis中的hash key 以shopId_+specialty_开头加上特产的id
		String field = specialtyKeyPrefix + shopId + "_" + shopCartSpecialtyVo.getSpecialtyId();
		boolean isSpecialtyExist = redisClient.isHashFieldExist(key, field);
		int newBookNumber = shopCartSpecialtyVo.getBookNumber();

		if (isSpecialtyExist) {
			String specialtyVoStr = redisClient.getHashFieldValue(key, field);
			ShopCartSpecialtyVo oldShopCartSpecialtyVo = (ShopCartSpecialtyVo) JsonUtil.toObject(specialtyVoStr, ShopCartSpecialtyVo.class);
			String id = shopId + "_" + shopCartSpecialtyVo.getSpecialtyId();

			int oldBookNumber = oldShopCartSpecialtyVo.getBookNumber();

			if(newBookNumber < 0){
				//如果用户删除产品的数量变成0了直接删除
				int number = newBookNumber+oldBookNumber ;
				if( number <=0){
					redisClient.delHashKey(key,field);
					return 1 ;
				}else{
					shopCartSpecialtyVo.setBookNumber(number);
				}
			}else{
				//获得特产的数量
				int allowNumber = getSpecialtyNum(id);
				if (allowNumber == -1) {
					shopCartSpecialtyVo.setBookNumber(oldBookNumber + newBookNumber);
				} else {
					int totalBookNumber = oldBookNumber + newBookNumber;
					if (totalBookNumber > allowNumber) {
						flag = -1;
						return flag;
					} else {
						shopCartSpecialtyVo.setBookNumber(totalBookNumber);
					}
				}
			}

		}else{
			if(newBookNumber <= 0){
				return NEGATIVE_PRODUCT_NUMBER_ERROR_CODE ;
			}
		}

		flag = (int) redisClient.setHashValue(key, field, JsonUtil.toJsonString(shopCartSpecialtyVo));

		return flag;
	}

	/**
	 * 获得特产的数量
	 *
	 * @param id
	 * @return
	 */
	private int getSpecialtyNum(String id) throws ElasticSearchException{
		Integer value = null;
		Map<String, Object> result = elasticSearchClient.getEntityById(ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_INDEX, ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_TYPE, id);
		value = (Integer) result.get("specialtyNumber");
		//如果为空 表示es中不存在这个数据，那么数据直接设置成0
		if (value == null) {
			value = 0;
		}
		return value;
	}

	/**
	 * @param userId
	 * @param shopId
	 * @return
	 */
	@Override
	public int deleteAllSpecialtyByShopId(long userId, long shopId) throws  RedisException{

		int affectNumber = 0;
		List<String> delKeys = null;
		String key = generateRedisCartKey(userId);
		String field = specialtyKeyPrefix + shopId + "_";
		Set<String> allKeys = redisClient.getAllHashKeys(key);
		if (!allKeys.isEmpty()) {
			delKeys = new ArrayList<String>();
			for (String hkey : allKeys) {
				if (hkey.startsWith(field)) {
					delKeys.add(hkey);
				}
			}
		}

		if (delKeys != null && !delKeys.isEmpty()) {
			String[] keys = new String[delKeys.size()];
			for (int i = 0; i < delKeys.size(); i++) {
				keys[i] = delKeys.get(i);
			}
			affectNumber = (int) redisClient.delHashKey(key, keys);
		}

		return affectNumber;
	}


	/**
	 * 删除指定店家指定的特产
	 *
	 * @param shopId
	 * @param userId
	 * @param specialtyId
	 * @return
	 */
	@Override
	public int updateSpecialtyNumber(long shopId, long userId, long specialtyId, int changeNumber) throws RedisException{
		int affectNumber = 0;
		String key = generateRedisCartKey(userId);
		String field = specialtyKeyPrefix + shopId + "_" + specialtyId;
		if (changeNumber == 0) {
			affectNumber = (int) redisClient.delHashKey(key, field);
		} else {
			String value = redisClient.getHashFieldValue(key, field);
			if (value != null) {
				ShopCartSpecialtyVo specialtyVo = (ShopCartSpecialtyVo) JsonUtil.toObject(value, ShopCartSpecialtyVo.class);
				specialtyVo.setBookNumber(changeNumber);
				affectNumber = (int) redisClient.setHashValue(key, field, JsonUtil.toJsonString(specialtyVo));
			} else {
				throw new RuntimeException("specify product does not exist in shopCart");
			}

		}
		return affectNumber;
	}

	/**
	 * 添加门票到购物车
	 *
	 * @param shopCartTravelTicketVo
	 * @throws Exception
	 */
	@Override
	public int addTravelTicketToShopCart(ShopCartTravelTicketVo shopCartTravelTicketVo) throws Exception {

		int flag = 1;

		String key = generateRedisCartKey(shopCartTravelTicketVo.getUserId());
		long shopId = shopCartTravelTicketVo.getShopId();
		//field 是有ticket id 和日期组成
		String field = ticketKeyPrefix + shopId + "_" + shopCartTravelTicketVo.getTicketDate() + "_" + shopCartTravelTicketVo.getTicketId();
		//先检查用户之前是不是买过同一票种相同日期的票
		boolean isFieldExist = redisClient.isHashFieldExist(key, field);
		int newBookNumber = shopCartTravelTicketVo.getBookNumber();

		if (isFieldExist) {
			String jsonValue = redisClient.getHashFieldValue(key, field);
			ShopCartTravelTicketVo oldTicketVo = (ShopCartTravelTicketVo) JsonUtil.toObject(jsonValue, ShopCartTravelTicketVo.class);
			String id = shopId + "_" + shopCartTravelTicketVo.getTicketId();
			int oldBookNumber = oldTicketVo.getBookNumber();

			if(newBookNumber < 0 ){
				int number =  newBookNumber+oldBookNumber ;
				//预订的数量加上原来的数量小于0 直接删除
				if(number <= 0 ){
					redisClient.delHashKey(key,field);
					return 1 ;
				}else{
					shopCartTravelTicketVo.setBookNumber(number);
				}
			}else{
				int maxBookNumber = getTicketMaxNumber(id);
				int totalBookNumber = oldBookNumber + newBookNumber;
				if (totalBookNumber > maxBookNumber) {
					flag = -1;
					return flag;
				} else {
					shopCartTravelTicketVo.setBookNumber(totalBookNumber);
				}
			}

		}else {
			if(newBookNumber <= 0){
				return  NEGATIVE_PRODUCT_NUMBER_ERROR_CODE ;
			}
		}

		flag = (int) redisClient.setHashValue(key, field, JsonUtil.toJsonString(shopCartTravelTicketVo));

		return flag;
	}

	private int getTicketMaxNumber(String id) throws ElasticSearchException{
		Integer value = null;
		Map<String, Object> result = elasticSearchClient.getEntityById(ESIndexAndTypeConstant.SHOP_TRAVEL_TICKET_INDEX, ESIndexAndTypeConstant.SHOP_TRAVEL_TICKET_TYPE, id);
		value = (Integer) result.get("maxBookNumber");
		//如果为空 表示es中不存在这个数据，那么数据直接设置成0
		if (value == null) {
			value = 0;
		}
		return value;
	}

	/**
	 * 添加导游到购物车
	 *
	 * @param shopCartTravelGuideVo
	 * @throws Exception
	 */
	@Override
	public int addTravelGuideToShopCart(ShopCartTravelGuideVo shopCartTravelGuideVo) throws Exception {

		int affectNumber = 0;
		//对于导游 对于同一天同一个用户只能添加一个
		String key = generateRedisCartKey(shopCartTravelGuideVo.getUserId());
		long shopId = shopCartTravelGuideVo.getShopId();
		//field 是有农家特色游得id和日期组成
		String field = guideKeyPrefix + shopId + "_" + shopCartTravelGuideVo.getGuideDate() + "_" + shopCartTravelGuideVo.getGuideId();
		boolean isExist = redisClient.isHashFieldExist(key, field);
		if (isExist) {
			return -1;
		}
		affectNumber = (int) redisClient.setHashValue(key, field, JsonUtil.toJsonString(shopCartTravelGuideVo));
		return affectNumber;

	}


	/**
	 * 用户添加农家特色游信息到购物车
	 *
	 * @param shopCartTravelFamilyActivityVo
	 * @return
	 * @throws Exception
	 */
	@Override
	public int addTravelFamilyActivityToShopCart(ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo) throws Exception {
		int affectNumber = 0;

		String key = generateRedisCartKey(shopCartTravelFamilyActivityVo.getUserId());
		long shopId = shopCartTravelFamilyActivityVo.getShopId();
		//field 是有农家特色游得id和日期组成
		String field = fmKeyPrefix + shopId + "_" + shopCartTravelFamilyActivityVo.getFamilyActivityDate() + "_" + shopCartTravelFamilyActivityVo.getFamilyActivityId();
		//step1:检查 用户是不是已经下过相同日期和相同特色游的订单
		boolean isExist = redisClient.isHashFieldExist(key, field);
		int newBookNumber = shopCartTravelFamilyActivityVo.getBookNumber();

		if (isExist) {
			String jsonValue = redisClient.getHashFieldValue(key, field);
			ShopCartTravelFamilyActivityVo oldVo = (ShopCartTravelFamilyActivityVo) JsonUtil.toObject(jsonValue, ShopCartTravelFamilyActivityVo.class);
			int oldBoolNumber = oldVo.getBookNumber();
            if(newBookNumber <0 ){
	            int number = newBookNumber+oldBoolNumber ;
	            //产品的数量为0 直接删除
	            if(number <=0){
		            redisClient.delHashKey(key,field) ;
		            return 1 ;
	            }else{
		            shopCartTravelFamilyActivityVo.setBookNumber(number);
	            }
            }else{
	            String id = shopId + "_" + shopCartTravelFamilyActivityVo.getFamilyActivityId();
	            int maxBookNumber = getFamilyActivityMaxNumber(id);
	            int totalBookNumber = oldBoolNumber + newBookNumber;
	            if (totalBookNumber > maxBookNumber) {
		            return -1;
	            } else {
		            shopCartTravelFamilyActivityVo.setBookNumber(totalBookNumber);
	            }
            }
		}else{
			if(newBookNumber < 0){
				return NEGATIVE_PRODUCT_NUMBER_ERROR_CODE ;
			}
		}

		affectNumber = (int) redisClient.setHashValue(key, field, JsonUtil.toJsonString(shopCartTravelFamilyActivityVo));

		return affectNumber;
	}


	private int getFamilyActivityMaxNumber(String id) throws ElasticSearchException{
		Integer value = null;
		Map<String, Object> result = elasticSearchClient.getEntityById(ESIndexAndTypeConstant.SHOP_TRAVEL_FAMILYACTIVITY_INDEX, ESIndexAndTypeConstant.SHOP_TRAVEL_FAMILYACTIVITY_TYPE, id);
		value = (Integer) result.get("maxBookNumber");
		//如果为空 表示es中不存在这个数据，那么数据直接设置成0
		if (value == null) {
			value = 0;
		}
		return value;
	}


	/**
	 * 删除 门票，导游，农家特色游的产品，更改 门票  农家特色游的数量 ，导游的数量不能修改
	 *
	 * @param shopId
	 * @param userId
	 * @param id
	 * @param type
	 * @param changeNumber
	 * @return
	 */
	@Override
	public int changeTravelNumber(long shopId, long userId, long id, int type, int changeNumber, String bookDate) throws Exception {

		int affectNumber = 0;
		String key = generateRedisCartKey(userId);
		String field = null;

		switch (type) {
			case 1:
				field = ticketKeyPrefix + shopId + "_" + bookDate + "_" + id;
				break;
			case 2:
				field = guideKeyPrefix + shopId + "_" + bookDate + "_" + id;
				break;
			case 3:
				field = fmKeyPrefix + shopId + "_" + bookDate + "_" + id;
				break;

		}
		if (field == null) {
			throw new RuntimeException("no type=" + type + "  exit..");
		}

		if (changeNumber == 0) {
			affectNumber = (int) redisClient.delHashKey(key, field);
		} else {
			String value = redisClient.getHashFieldValue(key, field);
			Object obj = null;

			if (value != null) {
				switch (type) {
					case 1:
						ShopCartTravelTicketVo ticketVo = (ShopCartTravelTicketVo) JsonUtil.toObject(value, ShopCartTravelTicketVo.class);
						ticketVo.setBookNumber(changeNumber);
						obj = ticketVo;
						break;
					case 3:
						ShopCartTravelFamilyActivityVo familyActivityVo = (ShopCartTravelFamilyActivityVo) JsonUtil.toObject(value, ShopCartTravelFamilyActivityVo.class);
						familyActivityVo.setBookNumber(changeNumber);
						obj = familyActivityVo;
						break;
				}
			} else {
				throw new RuntimeException("specify product does not exist in shopCart");
			}

			if (obj != null) {
				affectNumber = (int) redisClient.setHashValue(key, field, JsonUtil.toJsonString(obj));
			}
		}

		return affectNumber;
	}

	@Override
	public Map<Long, ShopCartProductContainerVo> getShopCartInfo(long userId) throws Exception {

		String key = "shopCart_" + userId;

		//获得用户添加到购物车的所有产品
		Map<String, String> values = redisClient.getAllHashValue(key);
		if (!values.isEmpty()) {
			//如果购物车中有数据，生产装载购物车中所有产品的对象 ShopCartProductContainerVo
			return convertShopCartStringToEntity(values);

		}
		return null;
	}

	//获得购物车中的所有产品，并且去检查对应的产品店家是不是已经下架
	private Map<Long, ShopCartProductContainerVo> convertShopCartStringToEntity(Map<String, String> values) throws Exception{

		Map<Long, ShopCartProductContainerVo> shopCartCategory = new HashMap<Long, ShopCartProductContainerVo>();

		Map<String, List<ShopCartBaseVo>> roomVoMap = new HashMap<String, List<ShopCartBaseVo>>();
		//Map<String, Map<String, ShopCartCourseVo>> mealVoMap = new HashMap<String, Map<String, ShopCartCourseVo>>();
		List<ShopCartMealVo> mealVoList = new ArrayList<ShopCartMealVo>();
		Map<String, List<ShopCartBaseVo>> specialtyVoMap = new HashMap<String, List<ShopCartBaseVo>>();
		Map<String, List<ShopCartBaseVo>> ticketVoMap = new HashMap<String, List<ShopCartBaseVo>>();
		Map<String, List<ShopCartBaseVo>> guideVoMap = new HashMap<String, List<ShopCartBaseVo>>();
		Map<String, List<ShopCartBaseVo>> familyActivityVoMap = new HashMap<String, List<ShopCartBaseVo>>();

		Map<Long,String> shopNameMap = new HashMap<Long,String>();


		Set<Map.Entry<String, String>> entities = values.entrySet();

		for (Map.Entry<String, String> entry : entities) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key.startsWith(roomKeyPrefix)) {

				ShopCartRoomVo roomVo = (ShopCartRoomVo) JsonUtil.toObject(value, ShopCartRoomVo.class);
				long shopId = roomVo.getShopId();
				String id = shopId + "_" + roomVo.getRoomId();
				ShopCartProductContainerVo container = getContainerVo(shopCartCategory, shopId);
				container.addRoomVo(roomVo);

				addShopBaseProductToTmpMap(roomVoMap,roomVo,id);

			} else if (key.startsWith(mealKeyPrefix)) {

				ShopCartMealVo mealVo = (ShopCartMealVo) JsonUtil.toObject(value, ShopCartMealVo.class);
				Map<String,String> courseMapValues = redisClient.getAllHashValue(key) ;
				if(courseMapValues.isEmpty()){
					continue;
				}

				Collection<String> shopCartCourseVoStrs = courseMapValues.values() ;
				List<ShopCartCourseVo> shopCartCourseVoList = new ArrayList<ShopCartCourseVo>();
				for(String shopCartCourseVoStr : shopCartCourseVoStrs){
					ShopCartCourseVo shopCartCourseVo = (ShopCartCourseVo)JsonUtil.toObject(shopCartCourseVoStr,ShopCartCourseVo.class) ;
					shopCartCourseVoList.add(shopCartCourseVo) ;
				}
				mealVo.setCourseVoList(shopCartCourseVoList);

				long shopId = mealVo.getShopId();
				ShopCartProductContainerVo container = getContainerVo(shopCartCategory, shopId);
				//把购物车中每个菜品都添加到map中
				List<ShopCartCourseVo> courseVoList = mealVo.getCourseVoList();
				container.addMealVo(mealVo);
				mealVoList.add(mealVo) ;


			} else if (key.startsWith(specialtyKeyPrefix)) {

				ShopCartSpecialtyVo specialtyVo = (ShopCartSpecialtyVo) JsonUtil.toObject(value, ShopCartSpecialtyVo.class);
				long shopId = specialtyVo.getShopId();
				ShopCartProductContainerVo container = getContainerVo(shopCartCategory, shopId);
				String id = shopId + "_" + specialtyVo.getSpecialtyId();
				container.addSpecialtyVo(specialtyVo);

				addShopBaseProductToTmpMap(specialtyVoMap,specialtyVo,id);

			} else if (key.startsWith(ticketKeyPrefix)) {

				ShopCartTravelTicketVo ticketVo = (ShopCartTravelTicketVo) JsonUtil.toObject(value, ShopCartTravelTicketVo.class);
				long shopId = ticketVo.getShopId();
				ShopCartProductContainerVo container = getContainerVo(shopCartCategory, shopId);
				String id = shopId + "_" + ticketVo.getTicketId();
				container.addTicketVo(ticketVo);
				addShopBaseProductToTmpMap(ticketVoMap,ticketVo,id);


			} else if (key.startsWith(guideKeyPrefix)) {

				ShopCartTravelGuideVo guideVo = (ShopCartTravelGuideVo) JsonUtil.toObject(value, ShopCartTravelGuideVo.class);
				long shopId = guideVo.getShopId();
				ShopCartProductContainerVo container = getContainerVo(shopCartCategory, shopId);
				String id = shopId + "_" + guideVo.getGuideId();
				container.addGuideVo(guideVo);
				addShopBaseProductToTmpMap(guideVoMap,guideVo,id);


			} else if (key.startsWith(fmKeyPrefix)) {

				ShopCartTravelFamilyActivityVo fmVo = (ShopCartTravelFamilyActivityVo) JsonUtil.toObject(value, ShopCartTravelFamilyActivityVo.class);
				long shopId = fmVo.getShopId();
				ShopCartProductContainerVo container = getContainerVo(shopCartCategory, shopId);
				String id = shopId + "_" + fmVo.getFamilyActivityId();
				container.addFamilyActivityVo(fmVo);

				addShopBaseProductToTmpMap(familyActivityVoMap,fmVo,id);

			}
		}

		//所有的下架信息都保存在redis中 ，（在redis 失败的情况下可以去查es或者mysql，备份的方案暂时不考虑）

		if (!shopCartCategory.isEmpty()) {

			//step one 获得所有的店家id
			Set<Long> ids = shopCartCategory.keySet();

			Set<String> roomOfflineIds = getOfflineIds(ids, OFFLINE_LIVE);
			Set<String> courseOfflineIds = getOfflineIds(ids, OFFLINE_COURSE);
			Set<String> specialtyOfflineIds = getOfflineIds(ids, OFFLINE_SPECIALTY);
			Set<String> ticketOfflineIds = getOfflineIds(ids, OFFLINE_TICKET);
			Set<String> guideOfflineIds = getOfflineIds(ids, OFFLINE_GUIDE);
			Set<String> faOfflineIds = getOfflineIds(ids, OFFLINE_FA);

			if (!roomVoMap.isEmpty()) {
				//获得产品的具体信息 对于房间而言就是 店家的名字 ，房型的名字。。。
				Set<String> roomIds = roomVoMap.keySet();
				getProductBasicInfoForShopCartRoom(roomVoMap,1);


				//给container加上店家的名字
				for(long shopId : ids){
						ShopCartProductContainerVo containerVo =  shopCartCategory.get(shopId);
						List<ShopCartRoomVo> roomVoList = containerVo.getRoomVoList();
					   if(roomVoList != null && !roomVoList.isEmpty()){
						   containerVo.setShopName(roomVoList.get(0).getShopName());
					   }
					}

				if (roomOfflineIds != null && !roomOfflineIds.isEmpty()) {
					findNonExistProduct(roomOfflineIds, roomVoMap);
				}

			}

            if(!mealVoList.isEmpty()){
	            getMealVosBasicInfo(mealVoList);

	            //存放所有店家菜品的信息
	            Map<String,List<ShopCartBaseVo>> shopCartBaseVoMap = new HashMap<String,List<ShopCartBaseVo>>() ;
	            //获得全部菜品的总价
	            for(long shopId : ids){
		            ShopCartProductContainerVo containerVo =  shopCartCategory.get(shopId);
		            List<ShopCartMealVo> mealVos = containerVo.getMealVoList();
		            if(mealVos != null && !mealVos.isEmpty()){
			            for(ShopCartMealVo mealVo : mealVos){
				            List<ShopCartCourseVo> courseVoList =  mealVo.getCourseVoList() ;
				            double totalPrice =0;
				            boolean setMealShopName = false ;
				            for(ShopCartCourseVo courseVo : courseVoList){
					            long sId = courseVo.getShopId() ;
					            long cId = courseVo.getCourseId() ;
					            String id = sId+"_"+cId;
					            List<ShopCartBaseVo> shopCartBaseVoList = shopCartBaseVoMap.get(id) ;
					            if(shopCartBaseVoList == null){
						            shopCartBaseVoList = new ArrayList<ShopCartBaseVo>() ;
						            shopCartBaseVoMap.put(id,shopCartBaseVoList) ;
					            }
					            shopCartBaseVoList.add(courseVo);
					            totalPrice += courseVo.getPrice()*courseVo.getBookNumber() ;
					            if(!setMealShopName){
						            //给餐饮加上店家的名字
						            mealVo.setShopName(courseVo.getShopName());
						            setMealShopName = true ;
						            //给container加上店家的名字
						            containerVo.setShopName(courseVo.getShopName());
					            }
				            }
				            mealVo.setTotalPrice(totalPrice);
			            }
		            }
	            }
	            if (courseOfflineIds != null && !courseOfflineIds.isEmpty()) {
		            findNonExistProduct(courseOfflineIds, shopCartBaseVoMap);
	            }

            }


			if(!specialtyVoMap.isEmpty()){
				getProductBasicInfoForShopCartRoom(specialtyVoMap,3);

				//给container加上店家的名字
				for(long shopId : ids){
					ShopCartProductContainerVo containerVo =  shopCartCategory.get(shopId);
					List<ShopCartSpecialtyVo> specialtyVos = containerVo.getSpecialtyVoList();
					if(specialtyVos != null && !specialtyVos.isEmpty()){
						containerVo.setShopName(specialtyVos.get(0).getShopName());
					}
				}

				if (specialtyOfflineIds != null && !specialtyOfflineIds.isEmpty()) {
					findNonExistProduct(specialtyOfflineIds, specialtyVoMap);
				}
			}


			if(!ticketVoMap.isEmpty()){
				getProductBasicInfoForShopCartRoom(ticketVoMap,4);

				//给container加上店家的名字
				for(long shopId : ids){
					ShopCartProductContainerVo containerVo =  shopCartCategory.get(shopId);
					List<ShopCartTravelTicketVo> ticketVos = containerVo.getTravelContainerVo().getTicketVoList();
					if(ticketVos != null && !ticketVos.isEmpty()){
						containerVo.setShopName(ticketVos.get(0).getShopName());
					}
				}

				if (ticketOfflineIds != null && !ticketOfflineIds.isEmpty()) {
					findNonExistProduct(ticketOfflineIds, ticketVoMap);
				}
			}

			if(!guideVoMap.isEmpty()){
				getProductBasicInfoForShopCartRoom(guideVoMap,5);

				//给container加上店家的名字
				for(long shopId : ids){
					ShopCartProductContainerVo containerVo =  shopCartCategory.get(shopId);
					List<ShopCartTravelGuideVo> guideVoList = containerVo.getTravelContainerVo().getGuideVoList();
					if(guideVoList != null && !guideVoList.isEmpty()){
						containerVo.setShopName(guideVoList.get(0).getShopName());
					}
				}

				if (guideOfflineIds != null && !guideOfflineIds.isEmpty()) {
					findNonExistProduct(guideOfflineIds, guideVoMap);
				}
			}


			if(!familyActivityVoMap.isEmpty()){
				getProductBasicInfoForShopCartRoom(familyActivityVoMap,6);

				//给container加上店家的名字
				for(long shopId : ids){
					ShopCartProductContainerVo containerVo =  shopCartCategory.get(shopId);
					List<ShopCartTravelFamilyActivityVo> familyActivityVos = containerVo.getTravelContainerVo().getFamilyActivityVoList();
					if(familyActivityVos != null && !familyActivityVos.isEmpty()){
						containerVo.setShopName(familyActivityVos.get(0).getShopName());
					}
				}

				if (faOfflineIds != null && !faOfflineIds.isEmpty() && !familyActivityVoMap.isEmpty()) {
					findNonExistProduct(faOfflineIds, familyActivityVoMap);
				}
			}

		}

		return shopCartCategory;
	}

	public void findNonExistProduct(Set<String> ids, Map<String, List<ShopCartBaseVo>> shopCartBaseVoMap) {
		for (String id : ids) {
			List<ShopCartBaseVo> shopCartBaseVos = shopCartBaseVoMap.get(id);
			if(shopCartBaseVos != null && !shopCartBaseVos.isEmpty()){
				for(ShopCartBaseVo baseVo : shopCartBaseVos){
					baseVo.setExist(false);
				}
			}

		}
	}


	/**
	 * 添加不同的产品到临时的map中留给 获取购物车产品基本信息时 使用
	 * @param shopCartBaseVoMap
	 * @param baseVo
	 */
	private void addShopBaseProductToTmpMap(Map<String, List<ShopCartBaseVo>> shopCartBaseVoMap,ShopCartBaseVo baseVo,String id){
		List<ShopCartBaseVo> shopCartBaseVoList = shopCartBaseVoMap.get(id) ;
		if(shopCartBaseVoList == null){
			shopCartBaseVoList = new ArrayList<ShopCartBaseVo>();
			shopCartBaseVoMap.put(id,shopCartBaseVoList) ;
		}
		shopCartBaseVoList.add(baseVo);
	}


	public void getMealVosBasicInfo(List<ShopCartMealVo> mealVoList) throws Exception{
		for(ShopCartMealVo mealVo : mealVoList){
			getMealVoBasicInfo(mealVo);
		}
	}

	private void getMealVoBasicInfo(ShopCartMealVo mealVo)  throws Exception{

		Map<Long,ShopCartCourseVo> shopCartCourseVoMap = new HashMap<Long,ShopCartCourseVo>() ;
		String index = ESIndexAndTypeConstant.SHOP_COURSE_INDEX;
		String type = ESIndexAndTypeConstant.SHOP_COURSE_TYPE;
		Class clazz = CourseVo.class;

			List<ShopCartCourseVo> shopCartCourseVoList = mealVo.getCourseVoList() ;
			List<String> courseIds = new ArrayList<String>() ;
			List<String> courseBookedIds = new ArrayList<String>() ;

			for(ShopCartCourseVo courseVo : shopCartCourseVoList){
				long shopId = courseVo.getShopId() ;
				long courseId = courseVo.getCourseId() ;
				String bookDate = mealVo.getBookDate() ;
				courseIds.add(shopId+"_"+courseId);
				courseBookedIds.add(shopId+"_"+courseId+"_"+bookDate);
				shopCartCourseVoMap.put(courseId,courseVo) ;
			}
			QueryBuilder queryBuilder = QueryBuilders.termsQuery("_id", courseIds);

			List<CourseVo> courseVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder, null, index, type, clazz);
			List<CourseBookVo> courseBookVos = getCourseBookedVosByIds(courseBookedIds) ;
			//记录每一个菜品的预订情况 ，提供给下面按照id查询使用
			Map<Long,Integer> courseBookedNumberMap = new HashMap<Long,Integer>() ;
			for(CourseBookVo courseBookVo : courseBookVos){
				courseBookedNumberMap.put(courseBookVo.getCourseId(),courseBookVo.getBookedNumber()) ;
			}
			for(CourseVo courseVo : courseVos){
				long shopId = courseVo.getShopId() ;
				long courseId = courseVo.getCourseId() ;
				String id = shopId+"_"+courseId ;
				ShopCartCourseVo shopCartCourseVo = (ShopCartCourseVo) shopCartCourseVoMap.get(courseId) ;
				shopCartCourseVo.setShopName(courseVo.getShopName());
				shopCartCourseVo.setCourseName(courseVo.getCourseName());
				shopCartCourseVo.setPrice(courseVo.getCoursePrice());
				Integer bookedNum = courseBookedNumberMap.get(courseId) ;
				if(bookedNum != null){
					shopCartCourseVo.setAllowBookNumber(courseVo.getCourseNumber() - bookedNum);
				}else{
					shopCartCourseVo.setAllowBookNumber(courseVo.getCourseNumber());
				}
			}

	}






	/**
	 * 获得住游产的基本信息 包括可预订的数量情况
	 * @param productVoMap
	 * @throws Exception
	 */
	private void getProductBasicInfoForShopCartRoom(Map<String, List<ShopCartBaseVo>> productVoMap, int productType) throws Exception {

		Set<String> ids = productVoMap.keySet();
		QueryBuilder queryBuilder = QueryBuilders.termsQuery("_id", ids);

		String index = "";
		String type = "";
		Class clazz = null;
		if(productType == 1){
			index = ESIndexAndTypeConstant.SHOP_ROOM_INDEX;
			type = ESIndexAndTypeConstant.SHOP_ROOM_TYPE;
			clazz = RoomVo.class;
			List<RoomVo> roomVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder, null, index, type, clazz);
			for (RoomVo roomVo : roomVos) {
				long shopId = roomVo.getShopId();
				long roomId = roomVo.getRoomId();
				String id = shopId + "_" + roomId;
				List<ShopCartBaseVo> shopCartBaseVoList =  productVoMap.get(id);
				for(ShopCartBaseVo baseVo : shopCartBaseVoList){
					ShopCartRoomVo shopCartRoomVo = (ShopCartRoomVo) baseVo ;
					String startDateStr = shopCartRoomVo.getStartDate();
					String endDateStr = shopCartRoomVo.getEndDate();

					long startDate = DateUtil.parseDateToLongValue(startDateStr);
					long endDate = DateUtil.parseDateToLongValue(endDateStr);

					shopCartRoomVo.setShopName(roomVo.getShopName());
					shopCartRoomVo.setRoomName(roomVo.getRoomName());
					shopCartRoomVo.setRoomType(roomVo.getRoomType());
					double price = shopInfoService.getRoomTotalPriceDuringDateRange(shopId, roomId, startDate, endDate);
					shopCartRoomVo.setPrice(price);
					int allowBookNumber = shopInfoService.getRoomAllowBookNumberDuringDateRange(shopId, roomId, startDateStr, endDateStr);
					shopCartRoomVo.setAllowBookNumber(allowBookNumber);
				}

			}
		}else if(productType == 3){
			index = ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_INDEX;
			type = ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_TYPE;
			clazz = SpecialtyVo.class;
			List<SpecialtyVo> specialtyVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder, null, index, type, clazz);
            for(SpecialtyVo specialtyVo : specialtyVos){
	            long shopId = specialtyVo.getShopId() ;
	            long specialtyId = specialtyVo.getSpecialtyId() ;
	            String id = shopId+"_"+specialtyId ;
	            List<ShopCartBaseVo> shopCartBaseVoList =  productVoMap.get(id);
	            for(ShopCartBaseVo baseVo : shopCartBaseVoList){
		            ShopCartSpecialtyVo shopCartSpecialtyVo = (ShopCartSpecialtyVo) baseVo ;
		            shopCartSpecialtyVo.setShopName(specialtyVo.getShopName());
		            shopCartSpecialtyVo.setSpecialtyName(specialtyVo.getSpecialtyName());
		            shopCartSpecialtyVo.setPrice(specialtyVo.getSpecialtyPrice());
		            shopCartSpecialtyVo.setAllowBookNumber(specialtyVo.getSpecialtyNumber());
	            }
            }
		}else if(productType == 4){
			index = ESIndexAndTypeConstant.SHOP_TRAVEL_TICKET_INDEX;
			type = ESIndexAndTypeConstant.SHOP_TRAVEL_TICKET_TYPE;
			clazz = TicketVo.class;
			List<TicketVo> ticketVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder, null, index, type, clazz);
			for(TicketVo ticketVo : ticketVos){
				long shopId = ticketVo.getShopId() ;
				long ticketId = ticketVo.getTicketId();
				String id = shopId+"_"+ticketId ;

				List<ShopCartBaseVo> shopCartBaseVoList =  productVoMap.get(id);

				for(ShopCartBaseVo baseVo : shopCartBaseVoList) {
					ShopCartTravelTicketVo shopCartTravelTicketVo = (ShopCartTravelTicketVo) baseVo;
					shopCartTravelTicketVo.setShopName(ticketVo.getShopName());
					shopCartTravelTicketVo.setTicketType(ticketVo.getTicketType());
					shopCartTravelTicketVo.setPrice(ticketVo.getTicketPrice());
					shopCartTravelTicketVo.setTicketName(ticketVo.getTicketName());
					shopCartTravelTicketVo.setMaxBookNumber(ticketVo.getMaxBookNumber());
				}
			}
		}else if(productType == 5){
			index = ESIndexAndTypeConstant.SHOP_TRAVEL_GUIDE_INDEX ;
			type = ESIndexAndTypeConstant.SHOP_TRAVEL_GUIDE_TYPE ;
			clazz = GuideVo.class;
			List<GuideVo> guideVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder, null, index, type, clazz);
			for(GuideVo guideVo : guideVos){
				long shopId = guideVo.getShopId() ;
				long guideId = guideVo.getGuideId();
				String id = shopId+"_"+guideId ;

				List<ShopCartBaseVo> shopCartBaseVoList =  productVoMap.get(id);
				for(ShopCartBaseVo baseVo : shopCartBaseVoList) {
					ShopCartTravelGuideVo shopCartTravelGuideVo = (ShopCartTravelGuideVo) baseVo;
					shopCartTravelGuideVo.setShopName(guideVo.getShopName());
					shopCartTravelGuideVo.setPrice(guideVo.getTourGuardPrice());
				}
			}
		}else if(productType == 6){
			index = ESIndexAndTypeConstant.SHOP_TRAVEL_FAMILYACTIVITY_INDEX;
			type = ESIndexAndTypeConstant.SHOP_TRAVEL_FAMILYACTIVITY_TYPE;
			clazz = FamilyActivityVo.class;
			List<FamilyActivityVo> familyActivityVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder, null, index, type, clazz);
			for(FamilyActivityVo familyActivityVo : familyActivityVos){
				long shopId = familyActivityVo.getShopId() ;
				long guideId = familyActivityVo.getFamilyActiveId() ;
				String id = shopId+"_"+guideId ;

				List<ShopCartBaseVo> shopCartBaseVoList =  productVoMap.get(id);
				for(ShopCartBaseVo baseVo : shopCartBaseVoList) {
					ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo = (ShopCartTravelFamilyActivityVo) baseVo;
					shopCartTravelFamilyActivityVo.setShopName(familyActivityVo.getShopName());
					shopCartTravelFamilyActivityVo.setPrice(familyActivityVo.getFamilyActivityPrice());
					shopCartTravelFamilyActivityVo.setMaxBookNumber(familyActivityVo.getMaxBookNumber());
					shopCartTravelFamilyActivityVo.setFamilyActivityName(familyActivityVo.getFamilyActivityName());
				}
			}

		}


	}



	//获得在redis中的不同种类菜品的下线列表key 为 prefix_shopid
	public Set<String> getOfflineIds(Set<Long> shopIds, String prefix) {

		Set<String> idContainer = null;

		for (long shopId : shopIds) {
			String key = prefix + shopId;
			Set<String> ids = null;
			try {
				ids = redisClient.getAllHashKeys(key);
			} catch (RedisException e) {
				//todo
				e.printStackTrace();
			}
			if (!ids.isEmpty()) {
				if (idContainer == null) {
					idContainer = new HashSet<String>();
				}
				idContainer.addAll(ids);
			}
		}
		return idContainer;
	}


	private ShopCartProductContainerVo getContainerVo(Map<Long, ShopCartProductContainerVo> shopCartCategory, long shopId) {

		ShopCartProductContainerVo containerVo = shopCartCategory.get(shopId);

		if (containerVo == null) {
			containerVo = new ShopCartProductContainerVo(shopId);
			shopCartCategory.put(shopId, containerVo);
		}

		return containerVo;

	}

	/**
	 * 获得用户在指定店家 指定时间的餐饮信息
	 * @param shopId
	 * @param userId
	 * @param bookDate
	 * @param mealType
	 * @return
	 */
	@Override
	public  ShopCartMealVo getUserMealInfo(long shopId, long userId,String bookDate,int mealType) throws Exception{
		String key = mealKeyPrefix +userId+"_"+shopId + "_" + bookDate + "_" + mealType;
		Map<String,String> courseVosMap = redisClient.getAllHashValue(key) ;
		if(!courseVosMap.isEmpty()){
			Collection<String> values = courseVosMap.values() ;
			ShopCartMealVo mealVo = new ShopCartMealVo() ;
			mealVo.setShopId(shopId);
			mealVo.setUserId(userId);
			mealVo.setBookDate(bookDate);
			mealVo.setMealType(mealType);
			List<ShopCartCourseVo> shopCartCourseVoList = new ArrayList<ShopCartCourseVo>();
			for(String value : values){
				ShopCartCourseVo shopCartCourseVo = (ShopCartCourseVo)JsonUtil.toObject(value,ShopCartCourseVo.class) ;
				shopCartCourseVoList.add(shopCartCourseVo) ;
			}
			mealVo.setCourseVoList(shopCartCourseVoList);
			getMealVoBasicInfo(mealVo);

			double  totalPrice = 0 ;
			for(ShopCartCourseVo shopCartCourseVo : shopCartCourseVoList){
				totalPrice+=shopCartCourseVo.getPrice() ;
			}
			mealVo.setTotalPrice(totalPrice);
			return mealVo ;
		}
		return null;
	}

	/**
	 * 获得购物车中产品的数量
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Override
	public int getShopCartProductCount(long userId) throws Exception {

		String key = generateRedisCartKey(userId) ;
		Set<String> keys = redisClient.getAllHashKeys(key);

		return keys.size();
	}

	/**
	 * 获得某一餐次的菜品的数量
	 * @param shopId
	 * @param userId
	 * @param bookDate
	 * @param mealType
	 * @return
	 * @throws Exception
	 */
	@Override
	public int getMealCourseCount(long shopId, long userId, String bookDate, int mealType) throws Exception {
		String key = mealKeyPrefix +userId+"_"+shopId + "_" + bookDate + "_" + mealType;
		Set<String> keys = redisClient.getAllHashKeys(key);
		return  keys.size() ;
	}


	/**
	 * 获得同一店家 同一房型 同一入住和离店时间的用户预订的房间数量
	 * @param shopId
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @param roomId
	 * @return
	 */
	@Override
	public int getRoomCount(long shopId, long userId, String startDate, String endDate, long roomId) throws Exception{

		int  roomCount = 0 ;
		String key = generateRedisCartKey(userId) ;

		boolean exist = redisClient.checkIfKeyExist(key);
		if(exist){
			String fieldKey = roomKeyPrefix+shopId+"_"+startDate+"_"+endDate+"_"+roomId ;
			String  shopCartRoomJson = redisClient.getHashFieldValue(key,fieldKey) ;
			if(shopCartRoomJson != null && !shopCartRoomJson.equals("")){
				ShopCartRoomVo shopCartRoomVo = (ShopCartRoomVo) JsonUtil.toObject(shopCartRoomJson,ShopCartRoomVo.class) ;
				roomCount = shopCartRoomVo.getBookNumber() ;
			}
		}
		return roomCount;
	}

	/**
	 * 获得用户购物车中的同一特产的数量
	 * @param shopId
	 * @param userId
	 * @param specialtyId
	 * @return
	 * @throws Exception
	 */
	@Override
	public int getSpecialtyCount(long shopId, long userId, long specialtyId) throws Exception {

		int specialtyCount = 0 ;
		String key = generateRedisCartKey(userId);
		String fieldKey = specialtyKeyPrefix+shopId + "_" +specialtyId;

		boolean exist = redisClient.isHashFieldExist(key,fieldKey) ;

		if(exist){
			String  shopCartSpecialtyJson = redisClient.getHashFieldValue(key,fieldKey);
			ShopCartSpecialtyVo shopCartSpecialtyVo = (ShopCartSpecialtyVo) JsonUtil.toObject(shopCartSpecialtyJson,ShopCartSpecialtyVo.class) ;
			specialtyCount = shopCartSpecialtyVo.getBookNumber() ;

		}

		return specialtyCount;
	}


	/**
	 * 获得用户某一日期 购物车中门票的数量
	 * @param shopId
	 * @param userId
	 * @param ticketId
	 * @param tourDate
	 * @return
	 * @throws Exception
	 */
	@Override
	public int getTravelTicketCount(long shopId, long userId, long ticketId, String tourDate) throws Exception {

		int  ticketCount = 0 ;
		String key = generateRedisCartKey(userId) ;
		String fieldKey = ticketKeyPrefix + shopId + "_" + tourDate + "_" + ticketId;
		boolean exist = redisClient.isHashFieldExist(key,fieldKey) ;
		if(exist){
			String shopCartTravelTicketJson = redisClient.getHashFieldValue(key,fieldKey);
			ShopCartTravelTicketVo shopCartTravelTicketVo = (ShopCartTravelTicketVo) JsonUtil.toObject(shopCartTravelTicketJson,ShopCartTravelTicketVo.class);
			ticketCount = shopCartTravelTicketVo.getBookNumber() ;
		}
		return ticketCount;
	}


	/**
	 * 获得用户购物车中同一店家相同游玩时间的农家特色游的数量
	 * @param shopId
	 * @param userId
	 * @param fmId
	 * @param tourDate
	 * @return
	 * @throws Exception
	 */
	@Override
	public int getTravelFACount(long shopId, long userId, long fmId, String tourDate) throws Exception {

		int faCount = 0 ;
		String key = generateRedisCartKey(userId) ;
		String fieldKey = fmKeyPrefix + shopId + "_" + tourDate + "_" +fmId;
		boolean exist = redisClient.isHashFieldExist(key,fieldKey);
		if(exist){
			String shopCartTravelFAJson = redisClient.getHashFieldValue(key,fieldKey);
			ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo = (ShopCartTravelFamilyActivityVo)JsonUtil.toObject(shopCartTravelFAJson,ShopCartTravelFamilyActivityVo.class);
			faCount = shopCartTravelFamilyActivityVo.getBookNumber() ;
		}
		return faCount;
	}



	public void cleanShopCartByUserId(){}
}

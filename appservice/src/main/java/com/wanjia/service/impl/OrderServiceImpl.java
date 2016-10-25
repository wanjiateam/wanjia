package com.wanjia.service.impl;

import com.wanjia.dao.*;
import com.wanjia.entity.order.UserBaseOrder;
import com.wanjia.enumpackage.OrderState;
import com.wanjia.exceptions.*;
import com.wanjia.service.*;
import com.wanjia.utils.*;
import com.wanjia.vo.cart.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 订单处理 service
 * Created by blake on 2016/8/18.
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {


	private static final Logger logger  = Logger.getLogger(OrderServiceImpl.class) ;
	@Autowired
	RedisClient redisClient;



	@Autowired
	ElasticSearchClient elasticSearchClient;

	@Autowired
	ResourceLockErrorRecordMapper resourceLockErrorRecordMapper ;

	@Autowired
	UserBaseOrderMapper userBaseOrderMapper ;




	@Autowired
	RoomOrderService roomOrderService ;

	@Autowired
	MealOrderService mealOrderService ;

	@Autowired
	SpecialtyOrderService specialtyOrderService ;

	@Autowired
	TicketOrderService ticketOrderService ;

	@Autowired
	FAOrderService fAOrderService ;

	@Autowired
	ShopCartService shopCartService;
	/**
	 * 把购物车中的数据转化为订单
	 * @param userId
	 * @return
	 *
	 */

	public /*List<OrderShopCartProductErrorInfo> */ void lockResource(long userId,Map<Long,ShopCartProductContainerVo> shopCartProductContainerVoMap) {

		String key = ShopCartServiceImpl.shopCartKeyPrefix + userId;
		Map<String, String> mapValues = null;
		try {
			mapValues = redisClient.getAllHashValue(key);
		} catch (RedisException e) {
			logger.error("query redis error",e);
			throw new ResourceLockFailException("redis error",e) ;
		}

		//List<OrderShopCartProductErrorInfo> orderShopCartProductErrorInfos = null ;
		//住房的购物车信息
		List<ShopCartRoomVo> shopCartRoomVoList = null;
		//餐饮的购物车信息
		List<ShopCartMealVo> shopCartMealVoList = null;
		//特产的购物车信息
		List<ShopCartSpecialtyVo> shopCartSpecialtyVoList = null;
		//门票的购物车信息
		List<ShopCartTravelTicketVo> shopCartTravelTicketVoList = null;
		//农家特色游的购物车信息
		List<ShopCartTravelFamilyActivityVo> shopCartTravelFamilyActivityVoList = null;
		//导游的购物车信息
		List<ShopCartTravelGuideVo> shopCartTravelGuideVoList = null;

		//如果购物车不为空 去锁定资源
		if (!mapValues.isEmpty()) {
			Set<Map.Entry<String, String>> entries = mapValues.entrySet();
			for (Map.Entry<String, String> entry : entries) {
				String pkey = entry.getKey();
				String pvalue = entry.getValue();
				if (pkey.startsWith(ShopCartServiceImpl.roomKeyPrefix)) {

					if (shopCartRoomVoList == null) {
						shopCartRoomVoList = new ArrayList<ShopCartRoomVo>();
					}

					ShopCartRoomVo shopCartRoomVo = (ShopCartRoomVo) JsonUtil.toObject(pvalue, ShopCartRoomVo.class) ;
					long shopId = shopCartRoomVo.getShopId() ;

					ShopCartProductContainerVo shopCartProductContainerVo = getShopCartProductToContainer(shopId,shopCartProductContainerVoMap);

					shopCartProductContainerVo.addRoomVo(shopCartRoomVo);

					shopCartRoomVoList.add(shopCartRoomVo);

				} else if (pkey.startsWith(ShopCartServiceImpl.specialtyKeyPrefix)) {

					if (shopCartSpecialtyVoList == null) {
						shopCartSpecialtyVoList = new ArrayList<ShopCartSpecialtyVo>();
					}

					ShopCartSpecialtyVo shopCartSpecialtyVo = (ShopCartSpecialtyVo) JsonUtil.toObject(pvalue, ShopCartSpecialtyVo.class);
					ShopCartProductContainerVo shopCartProductContainerVo = getShopCartProductToContainer(shopCartSpecialtyVo.getShopId(),shopCartProductContainerVoMap);
					shopCartProductContainerVo.addSpecialtyVo(shopCartSpecialtyVo);

					shopCartSpecialtyVoList.add(shopCartSpecialtyVo);

				} else if (pkey.startsWith(ShopCartServiceImpl.mealKeyPrefix)) {

					ShopCartMealVo mealVo = (ShopCartMealVo) JsonUtil.toObject(pvalue, ShopCartMealVo.class);
					Map<String, String> courseMap = null;
					try {
						courseMap = redisClient.getAllHashValue(pkey);
					} catch (RedisException e) {
						throw new ResourceLockFailException("redis error",e) ;
					}
					if (!courseMap.isEmpty()) {

						if (shopCartMealVoList == null) {
							shopCartMealVoList = new ArrayList<ShopCartMealVo>();
						}
						List<ShopCartCourseVo> courseVoList = new ArrayList<ShopCartCourseVo>();
						Set<Map.Entry<String, String>> courseEntries = courseMap.entrySet();
						for (Map.Entry<String, String> courseEntry : courseEntries) {
							String courseValue = courseEntry.getValue();
							courseVoList.add((ShopCartCourseVo) JsonUtil.toObject(courseValue, ShopCartCourseVo.class));
						}
						mealVo.setCourseVoList(courseVoList);
						ShopCartProductContainerVo shopCartProductContainerVo = getShopCartProductToContainer(mealVo.getShopId(),shopCartProductContainerVoMap);
						shopCartProductContainerVo.addMealVo(mealVo);

						shopCartMealVoList.add(mealVo);
					}
				} else if (pkey.startsWith(ShopCartServiceImpl.ticketKeyPrefix)) {
					if (shopCartTravelTicketVoList == null) {
						shopCartTravelTicketVoList = new ArrayList<ShopCartTravelTicketVo>();
					}
					ShopCartTravelTicketVo shopCartTravelTicketVo = (ShopCartTravelTicketVo) JsonUtil.toObject(pvalue, ShopCartTravelTicketVo.class) ;
					ShopCartProductContainerVo shopCartProductContainerVo = getShopCartProductToContainer(shopCartTravelTicketVo.getShopId(),shopCartProductContainerVoMap);
					shopCartProductContainerVo.addTicketVo(shopCartTravelTicketVo);

					shopCartTravelTicketVoList.add(shopCartTravelTicketVo);

				} else if (pkey.startsWith(ShopCartServiceImpl.guideKeyPrefix)) {

					if (shopCartTravelGuideVoList == null) {
						shopCartTravelGuideVoList = new ArrayList<ShopCartTravelGuideVo>();
					}
					ShopCartTravelGuideVo shopCartTravelGuideVo = (ShopCartTravelGuideVo) JsonUtil.toObject(pvalue, ShopCartTravelGuideVo.class) ;
					ShopCartProductContainerVo shopCartProductContainerVo = getShopCartProductToContainer(shopCartTravelGuideVo.getShopId(),shopCartProductContainerVoMap);
					shopCartProductContainerVo.addGuideVo(shopCartTravelGuideVo);

					shopCartTravelGuideVoList.add(shopCartTravelGuideVo);

				} else if (pkey.startsWith(ShopCartServiceImpl.fmKeyPrefix)) {
					if (shopCartTravelFamilyActivityVoList == null) {
						shopCartTravelFamilyActivityVoList = new ArrayList<ShopCartTravelFamilyActivityVo>();
					}
					ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo = (ShopCartTravelFamilyActivityVo) JsonUtil.toObject(pvalue, ShopCartTravelFamilyActivityVo.class) ;
					ShopCartProductContainerVo shopCartProductContainerVo = getShopCartProductToContainer(shopCartTravelFamilyActivityVo.getShopId(),shopCartProductContainerVoMap);
					shopCartProductContainerVo.addFamilyActivityVo(shopCartTravelFamilyActivityVo);

					shopCartTravelFamilyActivityVoList.add(shopCartTravelFamilyActivityVo);
				}

			}

		/*	//记录已经有锁定相应资源的产品信息
			 orderShopCartProductErrorInfos = new ArrayList<OrderShopCartProductErrorInfo>();*/


			// 获得购物车中的产品数据后 去数据库中锁定相应的产品
			if(shopCartRoomVoList != null){
				//1 锁定住房数据
				roomOrderService.lockRoomResource(shopCartRoomVoList/*, orderShopCartProductErrorInfos*/);
				/*int roomResourceBookedResult = lockRoomResource(shopCartRoomVoList, orderShopCartProductErrorInfos);
				if (roomResourceBookedResult == 0) {
					//重新回退已经锁定的数据
					resourceLockRollBack(orderShopCartProductErrorInfos);
				}*/
			}


			//2 锁定餐饮信息
			if(shopCartMealVoList != null){
				int mealResourceBookedResult = mealOrderService.lockMealResource(shopCartMealVoList/*,orderShopCartProductErrorInfos*/) ;
				/*if(mealResourceBookedResult == 0){
					//表示有锁定资源出错的情况 需要回滚所有已经锁定的资源
					resourceLockRollBack(orderShopCartProductErrorInfos);

				}*/
			}



			//锁定特产资源
			if(shopCartSpecialtyVoList != null){
				int specialtyResourceLockResult = specialtyOrderService.lockSpecialtyResource(shopCartSpecialtyVoList/*,orderShopCartProductErrorInfos*/);
				/*if(specialtyResourceLockResult == 0){
					//回滚已经锁定的资源
					resourceLockRollBack(orderShopCartProductErrorInfos);
				}*/
			}


			//锁定门票资源
			if(shopCartTravelTicketVoList != null){
				int  ticketResourceLockResult = ticketOrderService.lockTicketResource(shopCartTravelTicketVoList/*,orderShopCartProductErrorInfos*/);
				/*if(ticketResourceLockResult == 0){
					//回滚已经锁定的资源
					resourceLockRollBack(orderShopCartProductErrorInfos);
				}*/
			}


			//锁定农家特色游资源
			if(shopCartTravelFamilyActivityVoList != null){
				int fmResourceLockResult = fAOrderService.lockFAResource(shopCartTravelFamilyActivityVoList/*,orderShopCartProductErrorInfos*/);
				/*if(fmResourceLockResult == 0){
					//回滚已经锁定的资源
					resourceLockRollBack(orderShopCartProductErrorInfos);
				}*/
			}
		}
		//return  orderShopCartProductErrorInfos ;
	}


	 private ShopCartProductContainerVo getShopCartProductToContainer(long shopId,Map<Long,ShopCartProductContainerVo> shopCartProductContainerVoMap){

		ShopCartProductContainerVo shopCartProductContainerVo = shopCartProductContainerVoMap.get(shopId) ;
		if(shopCartProductContainerVo == null){
			shopCartProductContainerVo = new ShopCartProductContainerVo(shopId) ;
			shopCartProductContainerVoMap.put(shopId,shopCartProductContainerVo) ;
		}
		return shopCartProductContainerVo ;
	}


	/*
	//对于回滚的OrderShopCartProductErrorInfoList 中的数据 除了最后一条 是锁定资源失败的 别的都是成功的
	// 所以要把除了最后一条以外的所有数据都rollback掉 只有 1住2食3产 需要回滚资源 4 门票 5 农家特色游不需要特意去回滚 因为4,5只是去检查预订的数量是不是超过了最大
	//允许的预订数量
	public void resourceLockRollBack(List<OrderShopCartProductErrorInfo> orderShopCartProductErrorInfoList){

		if(orderShopCartProductErrorInfoList.size()==1){
			return ;
		}

		for(int i = 0 ;  i < orderShopCartProductErrorInfoList.size()-1 ; i++){

			OrderShopCartProductErrorInfo orderShopCartProductErrorInfo =  orderShopCartProductErrorInfoList.get(i);
			int type = orderShopCartProductErrorInfo.getType() ;

			switch (type){
				case 1 :roomResourceRollBack(orderShopCartProductErrorInfo) ; break;
				case 2 :rollBackCourseResource(orderShopCartProductErrorInfo);break;
				case 3 :rollBackSpecialyResource(orderShopCartProductErrorInfo); break;
			}
		}
	}*/

	//把购物车中的数据生成相应的订单， 分成4步  1：锁定资源 2 生成订单 3修改 es中的预订数量信息 4清空购物车
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@Override
	public void shopCartToOrder(long userId) throws OrderGenerateException,ResourceLockFailException {

		//锁定资源
		Map<Long,ShopCartProductContainerVo> shopCartProductContainerVoMap = new HashMap<Long,ShopCartProductContainerVo>() ;
		lockResource(userId,shopCartProductContainerVoMap);
		//根据shopCartProductContainerVoMap 中的数据 生产订单
		String orderId = userId+"_"+System.currentTimeMillis() ;

		//订单总的价格
		double totalPrice = 0 ;

		//记录所有的住房信息
		List<ShopCartRoomVo> shopCartRoomVos = null ;
		//记录搜索的餐饮信息
		List<ShopCartMealVo> shopCartMealVos = null ;
		//记录所有的特产信息
		List<ShopCartSpecialtyVo> specialtyVos = null ;
		//记录所有的门票信息
		List<ShopCartTravelTicketVo> shopCartTravelTicketVos = null ;
		//记录所有的农家特色游信息
		List<ShopCartTravelFamilyActivityVo> shopCartTravelFamilyActivityVos = null ;



		//遍历每一个产品 并调用相应的生成订单服务
		Set<Map.Entry<Long,ShopCartProductContainerVo>> entries = shopCartProductContainerVoMap.entrySet() ;
		StringBuilder shopIds = new StringBuilder() ;
		for(Map.Entry<Long,ShopCartProductContainerVo> entry : entries){
			shopIds.append(entry.getKey()).append(",");
			ShopCartProductContainerVo shopCartProductContainerVo = entry.getValue() ;

			//购物中中的住房信息 生成订单
			List<ShopCartRoomVo> shopCartRoomVoList = shopCartProductContainerVo.getRoomVoList() ;
			if(shopCartRoomVoList.size() > 0 ){
				if (shopCartRoomVos == null) {
					shopCartRoomVos = new ArrayList<ShopCartRoomVo>(shopCartRoomVoList.size());
				}
				//住的预订信息全部放在一起 留给后面修改es中的预订数量使用
				shopCartRoomVos.addAll(shopCartRoomVoList);

				totalPrice += generateRoomOrder(shopCartRoomVoList,orderId);
			}

			//购物车中的菜品信息生成订单
			List<ShopCartMealVo>  shopCartMealVoList = shopCartProductContainerVo.getMealVoList() ;
			if(shopCartMealVoList.size() > 0){
				if(shopCartMealVos == null){
					shopCartMealVos = new ArrayList<ShopCartMealVo>(shopCartMealVoList.size()) ;
				}
				//餐饮的预订信息全部放在一起 留给后面修改es中的预订数量使用
				shopCartMealVos.addAll(shopCartMealVoList);

				totalPrice += generateMealOrder(shopCartMealVoList,orderId) ;
			}
			//购物车中的特产信息生成订单
			List<ShopCartSpecialtyVo> specialtyVoList = shopCartProductContainerVo.getSpecialtyVoList() ;
			if(specialtyVoList.size() > 0){
				if(specialtyVos == null ){
					specialtyVos = new ArrayList<ShopCartSpecialtyVo>(specialtyVoList.size()) ;
				}
				//特产的预订信息全部放在一起 留给后面修改es中的预订数量使用
				specialtyVos.addAll(specialtyVoList);

				totalPrice += generateSpecialtyOrder(specialtyVoList,orderId) ;
			}

			//购物车中的门票信息生成订单
			List<ShopCartTravelTicketVo> shopCartTravelTicketVoList = shopCartProductContainerVo.getTravelContainerVo().getTicketVoList() ;
			if(shopCartTravelTicketVoList.size() > 0){
				if(shopCartTravelTicketVos == null){
					shopCartTravelTicketVos = new ArrayList<ShopCartTravelTicketVo>(shopCartTravelTicketVoList.size());
				}

				//门票的预订信息全部放在一起 留给后面修改es中的预订数量使用
				shopCartTravelTicketVos.addAll(shopCartTravelTicketVoList);

				totalPrice += generateTicketOrder(shopCartTravelTicketVoList,orderId) ;
			}

			//购物车中的农家特色游信息生成订单、
			List<ShopCartTravelFamilyActivityVo> shopCartTravelFamilyActivityVoList = shopCartProductContainerVo.getTravelContainerVo().getFamilyActivityVoList();

			if(shopCartTravelFamilyActivityVoList.size() > 0){
				if(shopCartTravelFamilyActivityVos == null ){
					shopCartTravelFamilyActivityVos = new ArrayList<ShopCartTravelFamilyActivityVo>(shopCartTravelFamilyActivityVoList.size()) ;
				}

				//农家特色游的预订信息全部放在一起 留给后面修改es中的预订数量使用
				shopCartTravelFamilyActivityVos.addAll(shopCartTravelFamilyActivityVoList);

				totalPrice += generatFAOrder(shopCartTravelFamilyActivityVoList,orderId) ;
			}
		}

		// 生成父订单
		generateParentOrder(orderId,userId,shopIds.substring(0,shopIds.length()-1),totalPrice);

		//修改es中的资源预订信息
        if(shopCartRoomVos != null){
	        modifyRoomBookedNumberInEs(shopCartRoomVos);
        }
		if(shopCartMealVos != null){
			modifyCourseBookedNumberInEs(shopCartMealVos);
		}
		if(specialtyVos != null){
			modifySpecialtyBookedNumberInEs(specialtyVos);
		}

		//清空购物车
		try {
			//清空除了餐饮以外的所有产品
			shopCartService.cleanShopCartByUserId(userId);
			if(shopCartMealVos != null){
				//清空餐饮信息
				for(ShopCartMealVo shopCartMealVo : shopCartMealVos){
					shopCartService.delMeal(userId,shopCartMealVo.getShopId(),shopCartMealVo.getBookDate(),shopCartMealVo.getMealType());
				}
			}
		} catch (RedisException e) {
			logger.error("clean redis error",e);
		}


	}


	private void generateParentOrder(String orderId,long userId ,String shopIds,double totalPrice) throws OrderGenerateException{


		UserBaseOrder userBaseOrder = new UserBaseOrder();
		userBaseOrder.setOrderId(orderId);
		userBaseOrder.setCreateTime(new Date());
		userBaseOrder.setUserId(userId);
		userBaseOrder.setTotalMoney(totalPrice);
		userBaseOrder.setShopIds(shopIds);
		userBaseOrder.setOrderState((byte) OrderState.ORDER_NO_PAY.getState());
		try {
			int affectNumber = userBaseOrderMapper.insert(userBaseOrder) ;
			if(affectNumber == 0){
				throw new OrderGenerateException("order generate Error");
			}
		} catch (MySqlException e) {
			logger.error("mysql error",e);
			throw new OrderGenerateException("mysql error",e);
		}
	}


	/**
	 * 住订单的生成
	 * @param shopCartRoomVos
	 * @param pid
	 * @return
	 * @throws OrderGenerateException
	 */
	public double generateRoomOrder(List<ShopCartRoomVo> shopCartRoomVos,String pid)throws OrderGenerateException{
		double totalRoomPrice = 0 ;
		for(ShopCartRoomVo shopCartRoomVo : shopCartRoomVos){
			totalRoomPrice += roomOrderService.shopCarRoomVoToRoomOrder(shopCartRoomVo,pid) ;
		}
		return totalRoomPrice ;
	}




	//餐饮订单的生成
	public double generateMealOrder(List<ShopCartMealVo> shopCartMealVos,String pid)throws OrderGenerateException{
		double totalMealPrice = 0 ;
		for(ShopCartMealVo shopCartMealVo : shopCartMealVos){
			totalMealPrice += mealOrderService.shopCartMealToOrder(shopCartMealVo,pid) ;
		}
		return totalMealPrice ;
	}


	//特产订单的生成
	public double generateSpecialtyOrder(List<ShopCartSpecialtyVo> shopCartSpecialtyVos,String pid) throws OrderGenerateException{
		double specialtyPrice = 0 ;
		for(ShopCartSpecialtyVo shopCartSpecialtyVo : shopCartSpecialtyVos){
			specialtyPrice += specialtyOrderService.shopCartSpecialtyToOrder(shopCartSpecialtyVo,pid);
		}
		return specialtyPrice ;
	}


	//门票订单的生成
	public double generateTicketOrder(List<ShopCartTravelTicketVo> shopCartTravelTicketVos,String pid)throws OrderGenerateException{
		double ticketTotalPrice = 0 ;
		for(ShopCartTravelTicketVo shopCartTravelTicketVo : shopCartTravelTicketVos){
			ticketTotalPrice += ticketOrderService.shopCartTicketToOrder(shopCartTravelTicketVo,pid);
		}
		return ticketTotalPrice ;
	}


	//农家特色游订单的生成
	public double generatFAOrder(List<ShopCartTravelFamilyActivityVo> shopCartTravelFamilyActivityVos,String pid)throws OrderGenerateException{
		double faTotalPrice = 0 ;
		for(ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo : shopCartTravelFamilyActivityVos){
			faTotalPrice += fAOrderService.shopCartFAToOrder(shopCartTravelFamilyActivityVo,pid);
		}
		return faTotalPrice ;
	}


	//当订单生成成功 修改es中住房的预订信息
	public void modifyRoomBookedNumberInEs(List<ShopCartRoomVo> shopCartRoomVoList){
		for(ShopCartRoomVo shopCartRoomVo : shopCartRoomVoList){
			roomOrderService.updateRoomBookInfoInEs(shopCartRoomVo,1);
		}
	}

	//当订单生成成功 修改es中菜品的预订信息
	public void modifyCourseBookedNumberInEs(List<ShopCartMealVo> shopCartMealVoList){
		for(ShopCartMealVo shopCartMealVo : shopCartMealVoList){
			mealOrderService.updateCourseBookInfoInEs(shopCartMealVo,1);
		}
	}


	//当订单生成成功 修改es中特产的预订信息
	public void modifySpecialtyBookedNumberInEs(List<ShopCartSpecialtyVo> shopCartSpecialtyVoList){
		for(ShopCartSpecialtyVo shopCartSpecialtyVo : shopCartSpecialtyVoList){
			specialtyOrderService.updateSpecialtyBookInEs(shopCartSpecialtyVo,1);
		}
	}


	@Override
	public void buyRoomDirect(ShopCartRoomVo shopCartRoomVo) throws OrderGenerateException, ResourceLockFailException {

		List<ShopCartRoomVo> shopCartRoomVoList = new ArrayList<ShopCartRoomVo>();
		shopCartRoomVoList.add(shopCartRoomVo) ;
		long userId = shopCartRoomVo.getUserId() ;
		String orderId = userId+"_"+System.currentTimeMillis() ;
		//锁定房间资源
		roomOrderService.lockRoomResource(shopCartRoomVoList);
		//生成住房订单
		double totalPrice = roomOrderService.shopCarRoomVoToRoomOrder(shopCartRoomVo,orderId);
		//生成父订单
		generateParentOrder(orderId,userId,String.valueOf(shopCartRoomVo.getShopId()),totalPrice);

		//修改es中的房屋预订数量
		roomOrderService.updateRoomBookInfoInEs(shopCartRoomVo,1);

	}

	@Override
	public void buyMealDirect(ShopCartMealVo shopCartMealVo) throws OrderGenerateException, ResourceLockFailException {
		List<ShopCartMealVo> shopCartMealVoList = new ArrayList<ShopCartMealVo>();
		long userId = shopCartMealVo.getUserId();
		long shopId = shopCartMealVo.getShopId();
		String orderId = userId+"_"+System.currentTimeMillis() ;
		//锁定菜品资源
		mealOrderService.lockMealResource(shopCartMealVoList);
		//生成菜品订单
		double totalPrice= mealOrderService.shopCartMealToOrder(shopCartMealVo,orderId);
		//生成父订单
		generateParentOrder(orderId,userId,String.valueOf(shopId),totalPrice);
		//修改es中菜品的数量
		mealOrderService.updateCourseBookInfoInEs(shopCartMealVo,1);
	}

	@Override
	public void buySpecialtyDirect(ShopCartSpecialtyVo shopCartSpecialtyVo) throws OrderGenerateException, ResourceLockFailException {
		long userId = shopCartSpecialtyVo.getUserId();
		long shopId = shopCartSpecialtyVo.getShopId();
		String orderId = userId+"_"+System.currentTimeMillis();
		List<ShopCartSpecialtyVo> specialtyVoList = new ArrayList<ShopCartSpecialtyVo>();
		specialtyVoList.add(shopCartSpecialtyVo) ;
		specialtyOrderService.lockSpecialtyResource(specialtyVoList) ;
		double totalPrice = specialtyOrderService.shopCartSpecialtyToOrder(shopCartSpecialtyVo,orderId);
		specialtyOrderService.updateSpecialtyBookInEs(shopCartSpecialtyVo,1);
		generateParentOrder(orderId,userId,String.valueOf(shopId),totalPrice);

	}

	@Override
	public void buyTicketDirect(ShopCartTravelTicketVo shopCartTravelTicketVo) throws OrderGenerateException, ResourceLockFailException {
        long userId = shopCartTravelTicketVo.getUserId();
		long shopId = shopCartTravelTicketVo.getShopId();
		String orderId = userId+"_"+System.currentTimeMillis();
		List<ShopCartTravelTicketVo> shopCartTravelTicketVoList = new ArrayList<ShopCartTravelTicketVo>();
		shopCartTravelTicketVoList.add(shopCartTravelTicketVo);
		ticketOrderService.lockTicketResource(shopCartTravelTicketVoList);
		double totalPrice = ticketOrderService.shopCartTicketToOrder(shopCartTravelTicketVo,orderId);
		generateParentOrder(orderId,userId,String.valueOf(shopId),totalPrice);
	}

	@Override
	public void buyFADirect(ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo) throws OrderGenerateException, ResourceLockFailException {

		long userId = shopCartTravelFamilyActivityVo.getUserId();
		long shopId = shopCartTravelFamilyActivityVo.getShopId() ;
		String orderId = userId+"_"+System.currentTimeMillis() ;
		List<ShopCartTravelFamilyActivityVo>  shopCartTravelFamilyActivityVoList = new ArrayList<ShopCartTravelFamilyActivityVo>();
		shopCartTravelFamilyActivityVoList.add(shopCartTravelFamilyActivityVo);
		fAOrderService.lockFAResource(shopCartTravelFamilyActivityVoList);
		double totalPrice = fAOrderService.shopCartFAToOrder(shopCartTravelFamilyActivityVo,orderId);
		generateParentOrder(orderId,userId,String.valueOf(shopId),totalPrice);
	}
}

package com.wanjia.controller;

import com.wanjia.jsonVo.JsonWrapperVo;
import com.wanjia.service.ShopCartService;
import com.wanjia.utils.JsonReturnBody;
import com.wanjia.utils.JsonUtil;
import com.wanjia.vo.cart.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by blake on 2016/8/3.
 */

@Controller
@RequestMapping("/shopCart")
public class ShopCartController {

      private static  Logger logger = Logger.getLogger(ShopCartController.class);
	  private static final int  ERROT_CODE = -9 ;
    /**
     * 添加房间到购物车 添加之前判断当天用户是不是已经预定过同一个房间 如果是把修改购物车中该房间的数量
     *
     *@param shopCartRoomVo
     * @param totalPrice
     * @return  flag //返回的标识 1 成功   -1 用户预订数超过允许的最大预订数,再次添加商品到购物车失败（针对的情况是用户多次添加一个商品到购物车）
     */

    @Autowired
    ShopCartService shopCartService ;



    @RequestMapping(value = "addRoom", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addRoomToCart(@RequestBody ShopCartRoomVo shopCartRoomVo ) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        try {
                int  flag = shopCartService.addRoomToShopCart(shopCartRoomVo);
                generateJsonReturnBodyInfo(jsonReturnBody,flag);
        } catch (Exception e) {
            jsonReturnBody.setCode(ERROT_CODE);
            jsonReturnBody.setMessage("error： "+e.getMessage());
            logger.error("error--",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody) ;
    }


	/**
	 * 修改用户预定房间的数量
	 * @param userId
	 * @param shopId
	 * @param roomId
	 * @param changeNumber  当为0 是表示删除这个房间
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "changeRoomNumber", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String changeRoomNumber(long userId,long shopId,long roomId,int changeNumber , String startDate,String endDate ) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			long  flag = shopCartService.changeRoomNumber(userId,shopId,roomId,changeNumber,startDate,endDate);
			jsonReturnBody.setCode(1);
			jsonReturnBody.setMessage("success");
		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error："+e.getMessage());
			logger.error("error：",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}

    /**
     * 添加中餐或者午餐到购物车
     * @param shopCartMealVo
     * @return exceedCourseIds 存放加入购物车中的单个菜品的数量超过其限制的菜品的id
     */
    @RequestMapping(value = "addMeal", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addCourseToCart(@RequestBody ShopCartMealVo shopCartMealVo) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        try {
                List<ShopCartCourseVo> shopCartCourseVos = shopCartService.addMealToShopCart(shopCartMealVo);

                if(shopCartCourseVos.size()==0){
                    jsonReturnBody.setMessage("success");
                    jsonReturnBody.setCode(1);
                }else{
	                shopCartMealVo.setCourseVoList(shopCartCourseVos);
                    jsonReturnBody.setMessage(shopCartMealVo);
                    jsonReturnBody.setCode(2);
                }

        } catch (Exception e) {
            jsonReturnBody.setCode(ERROT_CODE);
            jsonReturnBody.setMessage("error："+e.getMessage());
            logger.error("error：",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody) ;
    }

	/**
	 * 删除一顿餐饮
	 * @param userId
	 * @param shopId
	 * @param bookDate
	 * @param mealType
	 * @return
	 */
	@RequestMapping(value = "deleteMeal", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String delMeal(long userId,long shopId,String bookDate,int mealType) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			int code = shopCartService.delMeal(userId,shopId,bookDate,mealType) ;
			jsonReturnBody.setCode(code);
			if (code == 1){
				jsonReturnBody.setMessage("success");
			}else if(code == 0){
				jsonReturnBody.setMessage("update number fail");
			}
		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error："+e.getMessage());
			logger.error("error：",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}


	/**
	 * 更新一个菜品的数量
	 * @param userId
	 * @param shopId
	 * @param bookDate
	 * @param mealType
	 * @return
	 */
	@RequestMapping(value = "updateCourseNumber", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String updateCourseNumber(long userId,long shopId,String bookDate,int mealType,long courseId,int changeNumber) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			int code = shopCartService.changeCourseNumber(userId,shopId,bookDate,mealType,courseId,changeNumber) ;
			jsonReturnBody.setCode(1);
			jsonReturnBody.setMessage("success");
		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error："+e.getMessage());
			logger.error("error：",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}


    /**
     * 添加特产到购物车
     * @param shopCartSpecialtyVo
     * @return flag 1 表添加成功 2 表示再次添加的数量大于可预订的数量
     */
    @RequestMapping(value = "addSpecialty", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addSpecialtyToCart(@RequestBody ShopCartSpecialtyVo shopCartSpecialtyVo) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        try {
            int flag = shopCartService.addSpecialtyToShopCart(shopCartSpecialtyVo);
            generateJsonReturnBodyInfo(jsonReturnBody,flag);
        } catch (Exception e) {
            jsonReturnBody.setCode(ERROT_CODE);
            jsonReturnBody.setMessage("error："+e.getMessage());
            logger.error("error：",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody) ;
    }


	/**
	 * 删除指定店家所有的特产
	 * @param shopId
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "deleteAllSpecialty", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String deleteAllSpecialty(long shopId,long userId) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			int deleteNumber = shopCartService.deleteAllSpecialtyByShopId(userId,shopId);
			jsonReturnBody.setCode(deleteNumber);
			if(deleteNumber == 0){
				jsonReturnBody.setMessage("delete all specialty fail");
			}else{
				jsonReturnBody.setMessage("delete "+deleteNumber+" course success");
			}
		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error："+e.getMessage());
			logger.error("error：",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}


	/**
	 * 删除指定店家指定的特产
	 * @param shopId
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "changeSpecialtyNumber", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String changeSpecialtyNumber(long shopId,long userId,long specialtyId,int changeNumber ) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			int affectNumber = shopCartService.updateSpecialtyNumber(shopId,userId,specialtyId,changeNumber) ;
			jsonReturnBody.setCode(1);
			jsonReturnBody.setMessage("update  specialty success ");
		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error："+e.getMessage());
			logger.error("error：",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}

    /**
     * 添加门票到购物车
     * @param shopCartTravelTicketVo
     * @return
     */
    @RequestMapping(value = "addTravelTicket", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addTravelTicketToCart(@RequestBody ShopCartTravelTicketVo shopCartTravelTicketVo) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        try {
            int flag = shopCartService.addTravelTicketToShopCart(shopCartTravelTicketVo);
            generateJsonReturnBodyInfo(jsonReturnBody,flag);
        } catch (Exception e) {
            jsonReturnBody.setCode(ERROT_CODE);
            jsonReturnBody.setMessage("error："+e.getMessage());
            logger.error("error：",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody) ;
    }


    /**
     * 添加导游到购物车
     * @param shopCartTravelGuideVo
     * @return
     */
    @RequestMapping(value = "addTravelGuide", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addTravelGuideToCart(@RequestBody ShopCartTravelGuideVo shopCartTravelGuideVo) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        try {
            int flag = shopCartService.addTravelGuideToShopCart(shopCartTravelGuideVo);
            generateJsonReturnBodyInfo(jsonReturnBody,flag);
        } catch (Exception e) {
            jsonReturnBody.setCode(ERROT_CODE);
            jsonReturnBody.setMessage("error："+e.getMessage());
            logger.error("error：",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody) ;
    }


    /**
     * 添加农家特色游到购物车
     * @param shopCartTravelFamilyActivityVo
     * @return
     */
    @RequestMapping(value = "addTravelFamilyActivity", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addTravelFamilyActivityToCart(@RequestBody ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        try {
            int flag = shopCartService.addTravelFamilyActivityToShopCart(shopCartTravelFamilyActivityVo);
            generateJsonReturnBodyInfo(jsonReturnBody,flag);
        } catch (Exception e) {
            jsonReturnBody.setCode(ERROT_CODE);
            jsonReturnBody.setMessage("error："+e.getMessage());
            logger.error("error：",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody) ;
    }

	/**
	 * 删除 门票，导游，农家特色游的产品，更改 门票  农家特色游的数量 ，导游的数量不能修改
	 * @param shopId
	 * @param userId
	 * @param id
	 * @param type 1 门票 2 导游 3 农家特色游
	 * @return
	 */
	@RequestMapping(value = "changeTravelNumber", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String changeTravelNumber(long shopId,long userId,long id,int type,int changeNumber,String bookDate) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			int flag = shopCartService.changeTravelNumber(shopId,userId,id,type ,changeNumber,bookDate);
			jsonReturnBody.setCode(1);
			jsonReturnBody.setMessage("update success....");
		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error--"+e.getMessage());
			logger.error("error--",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}


    private void generateJsonReturnBodyInfo(JsonReturnBody jsonReturnBody,int code){
        jsonReturnBody.setCode(code);
        if(code == 1){
            //加入到购物车成功
            jsonReturnBody.setMessage("success");
        }else if(code == -1){
            //超过最大可预订的数量
            jsonReturnBody.setMessage("exceed max  allow book number");
        }
    }

    @RequestMapping(value = "getShopCartInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopCartInfo(long userId){

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        try {
            Map<Long,ShopCartProductContainerVo> shopCartProductContainerVoMap = shopCartService.getShopCartInfo(userId) ;
            if(shopCartProductContainerVoMap != null){
                jsonReturnBody.setCode(1);
                JsonWrapperVo<ShopCartProductContainerVo> containerJsonVo = new JsonWrapperVo<ShopCartProductContainerVo>() ;
	            containerJsonVo.addCollection(shopCartProductContainerVoMap.values() );
                jsonReturnBody.setMessage(containerJsonVo);
            }else{
                jsonReturnBody.setCode(-1);
                jsonReturnBody.setMessage("user shop cart is empty");
            }

        } catch (Exception e) {
            jsonReturnBody.setCode(ERROT_CODE);
            jsonReturnBody.setMessage("error："+e.getMessage());
            logger.error("query data  from shop cart error：",e);
        }

        return JsonUtil.toJsonString(jsonReturnBody) ;
    }


	@RequestMapping(value = "getMealInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getMealInfo(long shopId,long userId,String bookDate,int mealType) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			ShopCartMealVo mealVo = shopCartService.getUserMealInfo(shopId,userId,bookDate,mealType);
			if(mealVo !=null ){
				jsonReturnBody.setCode(1);
				jsonReturnBody.setMessage(mealVo);
			}else{
				jsonReturnBody.setCode(-1);
				jsonReturnBody.setMessage("no  meal info in shop cart");
			}

		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error--"+e.getMessage());
			logger.error("error--",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}


	/**
	 * 获得购物车中产品的数量
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "getShopCartProductCount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getShopCartProductCount(long userId) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			int size  = shopCartService.getShopCartProductCount(userId) ;
			if(size != 0  ){
				jsonReturnBody.setCode(1);
				jsonReturnBody.setMessage(size);
			}else{
				jsonReturnBody.setCode(-1);
				jsonReturnBody.setMessage("no product in shopCart");
			}

		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error--"+e.getMessage());
			logger.error("error--",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}



	@RequestMapping(value = "getMealCourseCount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getMealCourseCount(long shopId,long userId,String bookDate,int mealType) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			int size  = shopCartService.getMealCourseCount(shopId,userId,bookDate,mealType);
			if(size != 0  ){
				jsonReturnBody.setCode(1);
				jsonReturnBody.setMessage(size);
			}else{
				jsonReturnBody.setCode(-1);
				jsonReturnBody.setMessage("no product in shopCart");
			}

		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error--"+e.getMessage());
			logger.error("error--",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}


	/**
	 * 获得用户预订同一房型 同一入店离店时间的房间数量
	 * @param shopId
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @param roomId
	 * @return
	 */
	@RequestMapping(value = "getRoomCount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getRoomCount(long shopId,long userId,String startDate,String endDate,long roomId) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			int roomCount  = shopCartService.getRoomCount(shopId,userId,startDate,endDate,roomId);
			jsonReturnBody.setCode(1);
			jsonReturnBody.setMessage(roomCount);
		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error--"+e.getMessage());
			logger.error("error--",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}


	/**
	 * 获得购物车中用户预订的同一特产的数量
	 * @param shopId
	 * @param userId
	 * @param specialtyId
	 * @return
	 */
	@RequestMapping(value = "getSpecialtyCount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getSpecialtyCount(long shopId,long userId,long specialtyId) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			int specialtyCount  = shopCartService.getSpecialtyCount(shopId,userId,specialtyId) ;
			jsonReturnBody.setCode(1);
			jsonReturnBody.setMessage(specialtyCount);
		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error--"+e.getMessage());
			logger.error("error--",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}


	/**
	 *获得用户购物车中同一门票同一游玩日期的门票数量
	 * @param shopId
	 * @param userId
	 * @param ticketId
	 * @param tourDate
	 * @return
	 */
	@RequestMapping(value = "getTravelTicketCount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getTravelTicketCount(long shopId,long userId,long ticketId,String tourDate) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			int specialtyCount  = shopCartService.getTravelTicketCount(shopId,userId,ticketId,tourDate) ;
			jsonReturnBody.setCode(1);
			jsonReturnBody.setMessage(specialtyCount);
		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error--"+e.getMessage());
			logger.error("error--",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}


	/**
	 *获得用户购物车中同一店家指定时间 购物车中农家特色游的数量
	 * @param shopId
	 * @param userId
	 * @param fmId
	 * @param tourDate
	 * @return
	 */
	@RequestMapping(value = "getTravelFACount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getTravelFACount(long shopId,long userId,long fmId,String tourDate) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		try {
			int specialtyCount  = shopCartService.getTravelFACount(shopId,userId, fmId, tourDate) ;
			jsonReturnBody.setCode(1);
			jsonReturnBody.setMessage(specialtyCount);
		} catch (Exception e) {
			jsonReturnBody.setCode(ERROT_CODE);
			jsonReturnBody.setMessage("error--"+e.getMessage());
			logger.error("error--",e);
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}


}

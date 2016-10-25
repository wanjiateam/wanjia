package com.wanjia.controller;

import com.wanjia.enumpackage.ResourceLockState;
import com.wanjia.exceptions.OrderGenerateException;
import com.wanjia.exceptions.ResourceLockFailException;
import com.wanjia.service.OrderService;
import com.wanjia.utils.JsonReturnBody;
import com.wanjia.utils.JsonUtil;
import com.wanjia.utils.RedisClient;
import com.wanjia.vo.cart.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单处理 controller
 * Created by blake on 2016/9/2.
 */
@Controller
@RequestMapping("/order")
public class OrderController {

	private static final Logger logger = Logger.getLogger(OrderController.class) ;

	@Autowired
	OrderService orderService ;


	@RequestMapping(value = "/{userId}/shopCartGenerateOrder", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String convertShopCartToOrder(@PathVariable("userId") long userId) {

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		jsonReturnBody.setType("shopCart generateOrder");
		try {
			//锁定购物车中的资源
		    orderService.shopCartToOrder(userId);
			jsonReturnBody.setCode(1);


		} catch (ResourceLockFailException e) {
			logger.error("lock resource error",e);
			if(e.getResourceLockFailEntity() != null){
				jsonReturnBody.setMessage("resource lock fail : "+e.getResourceLockFailEntity());
				jsonReturnBody.setCode(0);
			}else{
				jsonReturnBody.setMessage("inner error :"+e.getMessage());
				jsonReturnBody.setCode(-1);
			}
		}catch (OrderGenerateException e){
			jsonReturnBody.setCode(-2);
			jsonReturnBody.setMessage("order error :"+e.getMessage());
		}
		return JsonUtil.toJsonString(jsonReturnBody) ;
	}


	@RequestMapping(value = "/buyRoom", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String buyRoomDirect(@RequestBody ShopCartRoomVo shopCartRoomVo){

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		jsonReturnBody.setType("buyRoom");
		try {
			orderService.buyRoomDirect(shopCartRoomVo);
			jsonReturnBody.setMessage("success");
			jsonReturnBody.setCode(1);
		} catch (OrderGenerateException e) {
			jsonReturnBody.setCode(-1);
			jsonReturnBody.setMessage("order generate error:"+e.getMessage());
			logger.error("order generate error",e);
		} catch (ResourceLockFailException e) {
			jsonReturnBody.setCode(-2);
			jsonReturnBody.setMessage("resource lock fail:"+e.getMessage());
			logger.error("resource lock fail",e);
		}

			return  JsonUtil.toJsonString(jsonReturnBody);

		}

	@RequestMapping(value = "/buyMeal", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String buyMealDirect(@RequestBody ShopCartMealVo shopCartMealVo){

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		jsonReturnBody.setType("buyMeal");
		try {
			orderService.buyMealDirect(shopCartMealVo);
			jsonReturnBody.setMessage("success");
			jsonReturnBody.setCode(1);
		} catch (OrderGenerateException e) {
			jsonReturnBody.setCode(-1);
			jsonReturnBody.setMessage("order generate error:"+e.getMessage());
			logger.error("order generate error",e);
		} catch (ResourceLockFailException e) {
			jsonReturnBody.setCode(-2);
			jsonReturnBody.setMessage("resource lock fail:"+e.getMessage());
			logger.error("resource lock fail",e);
		}

		return  JsonUtil.toJsonString(jsonReturnBody);

	}

	@RequestMapping(value = "/buySpecialty", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String buySpecialtyDirect(@RequestBody ShopCartSpecialtyVo shopCartSpecialtyVo){

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		jsonReturnBody.setType("buySpecialty");
		try {
			orderService.buySpecialtyDirect(shopCartSpecialtyVo);
			jsonReturnBody.setMessage("success");
			jsonReturnBody.setCode(1);
		} catch (OrderGenerateException e) {
			jsonReturnBody.setCode(-1);
			jsonReturnBody.setMessage("order generate error:"+e.getMessage());
			logger.error("order generate error",e);
		} catch (ResourceLockFailException e) {
			jsonReturnBody.setCode(-2);
			jsonReturnBody.setMessage("resource lock fail:"+e.getMessage());
			logger.error("resource lock fail",e);
		}

		return  JsonUtil.toJsonString(jsonReturnBody);

	}


	@RequestMapping(value = "/buyTicket", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String buyTicketDirect(@RequestBody ShopCartTravelTicketVo shopCartTravelTicketVo){

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		jsonReturnBody.setType("buyTicket");
		try {
			orderService.buyTicketDirect(shopCartTravelTicketVo);
			jsonReturnBody.setMessage("success");
			jsonReturnBody.setCode(1);
		} catch (OrderGenerateException e) {
			jsonReturnBody.setCode(-1);
			jsonReturnBody.setMessage("order generate error:"+e.getMessage());
			logger.error("order generate error",e);
		} catch (ResourceLockFailException e) {
			jsonReturnBody.setCode(-2);
			jsonReturnBody.setMessage("resource lock fail:"+e.getMessage());
			logger.error("resource lock fail",e);
		}

		return  JsonUtil.toJsonString(jsonReturnBody);
	}


	@RequestMapping(value = "/buyFA", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String buyFA(@RequestBody ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo){

		JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
		jsonReturnBody.setType("buyFA");
		try {
			orderService.buyFADirect(shopCartTravelFamilyActivityVo);
			jsonReturnBody.setMessage("success");
			jsonReturnBody.setCode(1);
		} catch (OrderGenerateException e) {
			jsonReturnBody.setCode(-1);
			jsonReturnBody.setMessage("order generate error:"+e.getMessage());
			logger.error("order generate error",e);
		} catch (ResourceLockFailException e) {
			jsonReturnBody.setCode(-2);
			jsonReturnBody.setMessage("resource lock fail:"+e.getMessage());
			logger.error("resource lock fail",e);
		}

		return  JsonUtil.toJsonString(jsonReturnBody);
	}

}

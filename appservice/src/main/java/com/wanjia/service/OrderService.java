package com.wanjia.service;

import com.wanjia.exceptions.OrderGenerateException;
import com.wanjia.exceptions.ResourceLockFailException;
import com.wanjia.vo.cart.*;

import java.util.List;
import java.util.Map;

/**
 * 用户订单 service
 * Created by blake on 2016/8/18.
 */
public interface OrderService {

	public void shopCartToOrder(long userId) throws OrderGenerateException,ResourceLockFailException;
    public void buyRoomDirect(ShopCartRoomVo shopCartRoomVo) throws OrderGenerateException,ResourceLockFailException ;
	public void buyMealDirect(ShopCartMealVo shopCartMealVo) throws OrderGenerateException,ResourceLockFailException ;
	public void buySpecialtyDirect(ShopCartSpecialtyVo shopCartSpecialtyVo) throws OrderGenerateException,ResourceLockFailException ;
	public void buyTicketDirect(ShopCartTravelTicketVo shopCartTravelTicketVo) throws OrderGenerateException,ResourceLockFailException ;
	public void buyFADirect(ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo) throws OrderGenerateException,ResourceLockFailException ;


}

package com.wanjia.service;

import com.wanjia.exceptions.OrderGenerateException;
import com.wanjia.exceptions.ResourceLockFailException;
import com.wanjia.vo.cart.OrderShopCartProductErrorInfo;
import com.wanjia.vo.cart.ShopCartTravelTicketVo;

import java.util.List;

/**
 * Created by blake on 2016/10/10.
 */
public interface TicketOrderService {

	public int lockTicketResource(List<ShopCartTravelTicketVo> shopCartTravelTicketVoList/*, List<OrderShopCartProductErrorInfo> orderShopCartProductErrorInfoList*/) throws ResourceLockFailException ;
	public double shopCartTicketToOrder(ShopCartTravelTicketVo shopCartTravelTicketVo,String pid) throws OrderGenerateException ;
}

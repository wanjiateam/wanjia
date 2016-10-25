package com.wanjia.service;

import com.wanjia.exceptions.OrderGenerateException;
import com.wanjia.exceptions.ResourceLockFailException;
import com.wanjia.vo.cart.OrderShopCartProductErrorInfo;
import com.wanjia.vo.cart.ShopCartTravelFamilyActivityVo;

import java.util.List;

/**
 * Created by blake on 2016/10/10.
 */
public interface FAOrderService {
	public int lockFAResource(List<ShopCartTravelFamilyActivityVo> shopCartTravelFamilyActivityVos /*, List<OrderShopCartProductErrorInfo> orderShopCartProductErrorInfos*/) throws ResourceLockFailException ;
	public double shopCartFAToOrder(ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo ,String pid) throws OrderGenerateException;
}

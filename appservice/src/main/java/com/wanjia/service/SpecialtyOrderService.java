package com.wanjia.service;

import com.wanjia.exceptions.OrderGenerateException;
import com.wanjia.exceptions.ResourceLockFailException;
import com.wanjia.utils.OrderStateInfo;
import com.wanjia.vo.cart.OrderShopCartProductErrorInfo;
import com.wanjia.vo.cart.ShopCartSpecialtyVo;

import java.util.List;

/**
 * Created by blake on 2016/10/10.
 */
public interface SpecialtyOrderService {
	public  int lockSpecialtyResource(List<ShopCartSpecialtyVo> shopCartSpecialtyVoList /*, List<OrderShopCartProductErrorInfo> orderShopCartProductErrorInfoList*/) throws ResourceLockFailException ;
	public double shopCartSpecialtyToOrder(ShopCartSpecialtyVo shopCartSpecialtyVo,String pid) throws OrderGenerateException;
	public void updateSpecialtyBookInEs(ShopCartSpecialtyVo shopCartSpecialtyVo, int operate) throws OrderGenerateException ;
}

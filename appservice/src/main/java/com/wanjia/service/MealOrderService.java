package com.wanjia.service;

import com.wanjia.exceptions.OrderGenerateException;
import com.wanjia.exceptions.ResourceLockFailException;
import com.wanjia.utils.OrderStateInfo;
import com.wanjia.vo.cart.OrderShopCartProductErrorInfo;
import com.wanjia.vo.cart.ShopCartMealVo;

import java.util.List;

/**
 * Created by blake on 2016/10/10.
 */
public interface MealOrderService {
	public int lockMealResource(List<ShopCartMealVo> mealVoList/*, List<OrderShopCartProductErrorInfo> orderShopCartProductErrorInfos*/) throws ResourceLockFailException ;
	public double shopCartMealToOrder(ShopCartMealVo shopCartMealVo,String pid) throws OrderGenerateException ;

	public void updateCourseBookInfoInEs(ShopCartMealVo shopCartMealVo,int operate) ;
}

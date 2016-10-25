package com.wanjia.service;

import com.wanjia.exceptions.OrderGenerateException;
import com.wanjia.exceptions.ResourceLockFailException;
import com.wanjia.utils.OrderStateInfo;
import com.wanjia.vo.cart.OrderShopCartProductErrorInfo;
import com.wanjia.vo.cart.ShopCartRoomVo;

import java.util.List;

/**
 * 处理room 订单的相关信息
 * Created by blake on 2016/10/10.
 */
public interface RoomOrderService {

	  public int lockRoomResource(List<ShopCartRoomVo> roomVoList/*, List<OrderShopCartProductErrorInfo> orderShopCartProductErrorInfos*/) throws ResourceLockFailException ;
	  public double  shopCarRoomVoToRoomOrder(ShopCartRoomVo shopCartRoomVo,String pid) throws OrderGenerateException ;
	  public void updateRoomBookInfoInEs(ShopCartRoomVo shopCartRoomVo, int operate) throws OrderGenerateException ;
}

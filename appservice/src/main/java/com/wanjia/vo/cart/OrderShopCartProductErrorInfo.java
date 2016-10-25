package com.wanjia.vo.cart;

import com.wanjia.enumpackage.ResourceLockState;

/**
 * 记录购物车中的产品在生成订单的时候可能遇到的问题
 * Created by blake on 2016/9/6.
 */
public class OrderShopCartProductErrorInfo {

	private ShopCartBaseVo shopCartBaseVo ;
	private ResourceLockState message ;
	// 1成功 2失败
	private int code ;
	//1住 2食 3特产 4 门票 5农家特色游
	private int type ;

	private Object attachment ;

	public OrderShopCartProductErrorInfo(ShopCartBaseVo shopCartBaseVo, ResourceLockState lockState, int code ,int type) {
		this.shopCartBaseVo = shopCartBaseVo;
		this.message = lockState;
		this.code = code ;
		this.type = type;
	}

	public OrderShopCartProductErrorInfo(ShopCartBaseVo shopCartBaseVo ,int type) {
		this.shopCartBaseVo = shopCartBaseVo;
		this.type = type;
	}

	public ShopCartBaseVo getShopCartBaseVo() {
		return shopCartBaseVo;
	}

	public void setShopCartBaseVo(ShopCartBaseVo shopCartBaseVo) {
		this.shopCartBaseVo = shopCartBaseVo;
	}

	public ResourceLockState getMessage() {
		return message;
	}

	public void setMessage(ResourceLockState message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}
}

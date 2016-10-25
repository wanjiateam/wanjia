package com.wanjia.exceptions;

import com.wanjia.vo.cart.ShopCartBaseVo;

/**
 * Created by blake on 2016/10/10.
 */
public class OrderGenerateException extends RuntimeException {

	private  ShopCartBaseVo shopCartBaseVo ;

	public OrderGenerateException(String msg, ShopCartBaseVo shopCartBaseVo){
		super(msg);
		this.shopCartBaseVo = shopCartBaseVo ;
	}

	public OrderGenerateException(String msg,Exception e){
		super(msg,e);
	}

	public OrderGenerateException(String msg){
		super(msg);
	}



}

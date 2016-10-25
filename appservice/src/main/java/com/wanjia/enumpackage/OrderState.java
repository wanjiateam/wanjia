package com.wanjia.enumpackage;

/**
 * Created by blake on 2016/10/11.
 */
public enum OrderState{

	//成功生成订单，未支付
	ORDER_NO_PAY(0),
	//已支付
	ORDER_PAIED(1),
	//已过期
	ORDER_EXPIRED(2),
	//退款申请中，等待店家确认
	ORDER_CANCEL(3),
	//店家确认退款未到账，
	ORDER_CANCEL_NO_PAY(4),
	// 退款已到账
	ORDER_CANCEL_PAIED(5);


	private int state ;

	OrderState(int state){
		this.state = state ;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}


}

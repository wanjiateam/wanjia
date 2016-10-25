package com.wanjia.utils;

import java.util.Random;

/**
 * Created by blake on 2016/9/26.
 */
public class OrderIdGenerator {

	private static int seed = 100000;
	static Random random = new Random() ;
	public static String generateOrderId(){

		synchronized (OrderIdGenerator.class){
			StringBuffer sb = new StringBuffer() ;
			sb.append(String.valueOf(System.currentTimeMillis())).append("_");
			sb.append(random.nextInt(seed)) ;
			return sb.toString();
		}
	}
}

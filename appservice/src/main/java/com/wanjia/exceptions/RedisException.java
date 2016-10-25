package com.wanjia.exceptions;

/**
 * Created by blake on 2016/9/26.
 */
public class RedisException extends Exception {

	public RedisException(String message ,Exception e){
		super(message,e);
	}
}

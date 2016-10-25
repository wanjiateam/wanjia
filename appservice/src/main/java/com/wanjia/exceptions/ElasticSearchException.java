package com.wanjia.exceptions;

/**
 * Created by blake on 2016/9/7.
 */
public class ElasticSearchException extends   Exception{

	public ElasticSearchException(String message ,Exception e){
		super(message,e);
	}

}

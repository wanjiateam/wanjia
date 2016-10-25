package com.wanjia.exceptions;

/**
 * Created by blake on 2016/10/9.
 */
public class MySqlException extends Exception {

	public MySqlException(String msg,Exception e){
		super(msg,e);
	}
}

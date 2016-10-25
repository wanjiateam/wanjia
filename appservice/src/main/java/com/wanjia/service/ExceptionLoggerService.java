package com.wanjia.service;

import com.wanjia.exceptions.MySqlException;

import java.util.Date;

/**
 * Created by blake on 2016/10/10.
 */
public interface ExceptionLoggerService {
	public void addResourceLockErrorInfo(String objJson, Date bookDate, byte lockType, int productType, String exceptionInfo) throws MySqlException ;
}

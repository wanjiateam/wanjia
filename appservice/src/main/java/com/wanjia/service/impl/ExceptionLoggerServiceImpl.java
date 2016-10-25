package com.wanjia.service.impl;

import com.wanjia.dao.ResourceLockErrorRecordMapper;
import com.wanjia.entity.order.book.ResourceLockErrorRecord;
import com.wanjia.exceptions.MySqlException;
import com.wanjia.service.ExceptionLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 记录在订单和锁定资源过程中出现的错误到数据库
 * Created by blake on 2016/10/10.
 */
@Service("exceptionLoggerService")
public class ExceptionLoggerServiceImpl implements ExceptionLoggerService{

	@Autowired
	ResourceLockErrorRecordMapper resourceLockErrorRecordMapper ;

	//记录锁定资源错误日志到数据库 资源锁定错误信息表
	public void addResourceLockErrorInfo(String objJson, Date bookDate, byte lockType, int productType, String exceptionInfo) throws MySqlException {
		ResourceLockErrorRecord resourceLockErrorRecord = new ResourceLockErrorRecord() ;
		resourceLockErrorRecord.setBookDate(bookDate);
		resourceLockErrorRecord.setInsertDate(new Date());
		resourceLockErrorRecord.setLockType(lockType);
		resourceLockErrorRecord.setProductType(productType);
		resourceLockErrorRecord.setObjectJson(objJson);
		resourceLockErrorRecord.setExceptionInfo(exceptionInfo);
		resourceLockErrorRecordMapper.insert(resourceLockErrorRecord) ;
	}
}

package com.wanjia.dao;

import com.wanjia.entity.order.book.ResourceLockErrorRecord;
import com.wanjia.exceptions.MySqlException;

public interface ResourceLockErrorRecordMapper {

    int deleteByPrimaryKey(Long errorId) throws MySqlException;

    int insert(ResourceLockErrorRecord record) throws MySqlException;

    int insertSelective(ResourceLockErrorRecord record) throws MySqlException;

    ResourceLockErrorRecord selectByPrimaryKey(Long errorId) throws MySqlException;

    int updateByPrimaryKeySelective(ResourceLockErrorRecord record) throws MySqlException;

    int updateByPrimaryKey(ResourceLockErrorRecord record) throws MySqlException;
}
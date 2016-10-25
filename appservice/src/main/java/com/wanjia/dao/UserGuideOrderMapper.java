package com.wanjia.dao;

import com.wanjia.entity.order.UserGuideOrder;
import com.wanjia.exceptions.MySqlException;

public interface UserGuideOrderMapper {

    int deleteByPrimaryKey(String orderId) throws MySqlException;

    int insert(UserGuideOrder record) throws MySqlException;

    int insertSelective(UserGuideOrder record) throws MySqlException;

    UserGuideOrder selectByPrimaryKey(String orderId) throws MySqlException;

    int updateByPrimaryKeySelective(UserGuideOrder record) throws MySqlException;

    int updateByPrimaryKey(UserGuideOrder record) throws MySqlException;
}
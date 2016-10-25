package com.wanjia.dao;

import com.wanjia.entity.order.UserBaseOrder;
import com.wanjia.exceptions.MySqlException;

public interface UserBaseOrderMapper {

    int deleteByPrimaryKey(String orderId) throws MySqlException;

    int insert(UserBaseOrder record) throws MySqlException;

    int insertSelective(UserBaseOrder record) throws MySqlException;

    UserBaseOrder selectByPrimaryKey(String orderId) throws MySqlException;

    int updateByPrimaryKeySelective(UserBaseOrder record) throws MySqlException;

    int updateByPrimaryKey(UserBaseOrder record) throws MySqlException;
}
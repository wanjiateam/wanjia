package com.wanjia.dao;

import com.wanjia.entity.order.UserMealOrder;
import com.wanjia.exceptions.MySqlException;

public interface UserMealOrderMapper {

    int deleteByPrimaryKey(String orderId) throws MySqlException;

    int insert(UserMealOrder record) throws MySqlException;

    int insertSelective(UserMealOrder record) throws MySqlException;

    UserMealOrder selectByPrimaryKey(String orderId) throws MySqlException;

    int updateByPrimaryKeySelective(UserMealOrder record) throws MySqlException;

    int updateByPrimaryKey(UserMealOrder record) throws MySqlException;
}
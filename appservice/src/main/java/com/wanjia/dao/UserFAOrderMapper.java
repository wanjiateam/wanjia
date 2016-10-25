package com.wanjia.dao;

import com.wanjia.entity.order.UserFAOrder;
import com.wanjia.exceptions.MySqlException;

public interface UserFAOrderMapper {
    int deleteByPrimaryKey(String orderId) throws MySqlException;

    int insert(UserFAOrder record) throws MySqlException;

    int insertSelective(UserFAOrder record) throws MySqlException;

    UserFAOrder selectByPrimaryKey(String orderId) throws MySqlException;

    int updateByPrimaryKeySelective(UserFAOrder record) throws MySqlException;

    int updateByPrimaryKey(UserFAOrder record) throws MySqlException;
}
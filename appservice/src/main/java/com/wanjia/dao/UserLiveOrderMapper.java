package com.wanjia.dao;

import com.wanjia.entity.order.UserLiveOrder;
import com.wanjia.exceptions.MySqlException;

public interface UserLiveOrderMapper {

    int deleteByPrimaryKey(String orderId) throws MySqlException;

    int insert(UserLiveOrder record) throws MySqlException;

    int insertSelective(UserLiveOrder record) throws MySqlException;

    UserLiveOrder selectByPrimaryKey(String orderId) throws MySqlException;

    int updateByPrimaryKeySelective(UserLiveOrder record) throws MySqlException;

    int updateByPrimaryKey(UserLiveOrder record) throws MySqlException;
}
package com.wanjia.dao;

import com.wanjia.entity.order.UserTicketOrder;
import com.wanjia.exceptions.MySqlException;

public interface UserTicketOrderMapper {

    int deleteByPrimaryKey(String orderId) throws MySqlException;

    int insert(UserTicketOrder record) throws MySqlException;

    int insertSelective(UserTicketOrder record) throws MySqlException;

    UserTicketOrder selectByPrimaryKey(String orderId) throws MySqlException;

    int updateByPrimaryKeySelective(UserTicketOrder record) throws MySqlException;

    int updateByPrimaryKey(UserTicketOrder record) throws MySqlException;
}
package com.wanjia.dao;

import com.wanjia.entity.order.UserSpecialtyOrder;
import com.wanjia.exceptions.MySqlException;

public interface UserSpecialtyOrderMapper {
    int deleteByPrimaryKey(String orderId);

    int insert(UserSpecialtyOrder record) throws MySqlException;

    int insertSelective(UserSpecialtyOrder record)throws MySqlException;

    UserSpecialtyOrder selectByPrimaryKey(String orderId) throws MySqlException;

    int updateByPrimaryKeySelective(UserSpecialtyOrder record) throws MySqlException;

    int updateByPrimaryKey(UserSpecialtyOrder record) throws MySqlException;
}
package com.wanjia.dao;

import com.wanjia.entity.UserInfo;
import com.wanjia.exceptions.MySqlException;

import java.util.Map;

public interface UserInfoMapper {

    int deleteByPrimaryKey(Long uid) throws MySqlException;

    int insert(UserInfo record) throws MySqlException;

    int insertSelective(UserInfo record) throws MySqlException;

    UserInfo selectByPrimaryKey(Long uid) throws MySqlException;

    int updateByPrimaryKeySelective(UserInfo record) throws MySqlException;

    int updateByPrimaryKey(UserInfo record) throws MySqlException;

    int checkIfPhoneNumberExist(String phoneNumber) throws MySqlException;

    int checkIfUserExist(Map map) throws MySqlException;

    int userLogin(Map map)  throws MySqlException;

    void updateUserPassword(Map map) throws MySqlException ;

}
package com.wanjia.dao;

import com.wanjia.entity.UserInfo;

import java.util.Map;

public interface UserInfoMapper {
    int deleteByPrimaryKey(Long uid);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Long uid);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
    int checkIfPhoneNumberExist(String phoneNumber);
    int checkIfUserExist(Map map);
    int userLogin(Map map );
    void updateUserPassword(Map map);

}
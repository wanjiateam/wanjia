package com.wanjia.dao;

import com.wanjia.entity.UserInfo;

public interface UserInfoMapper {
    int deleteByPrimaryKey(Long uid);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Long uid);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
    int checkIfPhoneNumberExist(String phoneNumber);
    int checkIfUserExist(String token,int type);
    int userLogin(String token,String passwd,int type );

}
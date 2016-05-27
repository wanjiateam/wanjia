package com.wanjia.service;

import com.wanjia.entity.UserInfo;

/**
 * Created by hsb11289 on 2016/5/23.
 */
public interface UserService {

    public int addUser(UserInfo userInfo,String smsCode);
    public int checkIfUserExist(String token,int type);
    public int userLogin(String token ,String passwd,int type );
    public int sendVerifyCode(String phoneNumber,int expireSeconds,byte isUserExist);
    public int checkSmsCode(String phoneNumber,String smsCode);
    public int findPassword(String phoneNumber,String smsCode,String newPassword);
    public int loginByToken(String phoneNumber,int expireDays);

}

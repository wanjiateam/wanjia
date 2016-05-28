package com.wanjia.service;

import com.wanjia.entity.UserInfo;
import com.wanjia.utils.UserReturnJson;

/**
 * Created by hsb11289 on 2016/5/23.
 */
public interface UserService {

    public UserReturnJson addUser(UserInfo userInfo, String smsCode, String deviceId, int expireDays);
    public int checkIfUserExist(String token,int type);
    public void userLogin(String token ,String passwd,int type,String deviceId,int expireDays,UserReturnJson userReturnJson);
    public int sendVerifyCode(String phoneNumber,int expireSeconds,byte isUserExist);
    public int checkSmsCode(String phoneNumber,String smsCode);
    public UserReturnJson findPassword(String phoneNumber,String smsCode,String newPassword,String deviceId,int expireDays);
    public int loginByToken(String token,int expireDays);

}

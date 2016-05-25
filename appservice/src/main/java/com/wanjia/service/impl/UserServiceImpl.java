package com.wanjia.service.impl;

import com.wanjia.dao.UserInfoMapper;
import com.wanjia.entity.UserInfo;
import com.wanjia.service.UserService;
import com.wanjia.utils.RedisClient;
import com.wanjia.utils.SmsClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by hsb11289 on 2016/5/23.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    Logger logger = Logger.getLogger(UserServiceImpl.class);
    Random random = new Random();
    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    RedisClient redisClient ;

    @Autowired
    SmsClient smsClient ;

    public int  addUser(UserInfo userInfo) {

        int count =  userInfoMapper.checkIfPhoneNumberExist(userInfo.getPhonenumber());
        if(count != 0 ){
            return 0 ;
        }else{
            userInfoMapper.insert(userInfo);
            return 1 ;
        }

    }

    public int checkIfUserExist(String token, int type) {
        Map map = new HashMap();
        map.put("type",type);
        map.put("token",token) ;
        return userInfoMapper.checkIfUserExist(map);
    }

    public int userLogin(String token, String passwd, int type){
        Map map  = new HashMap();
        map.put("token",token);
        map.put("passwd",passwd);
        map.put("type",type);
       return  userInfoMapper.userLogin(map) ;
    }

    public int sendVerifyCode(String phoneNumber, int expireSeconds) {

        try {
            String verifyCode = generateVerifyCode() ;
            smsClient.sendCode(verifyCode,phoneNumber);
            redisClient.setKeyValue(phoneNumber,verifyCode,expireSeconds);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }



    public String generateVerifyCode(){
        StringBuilder code = new StringBuilder() ;
        for (int i=0 ; i<4 ;i ++){
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}

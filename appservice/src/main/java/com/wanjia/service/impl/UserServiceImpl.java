package com.wanjia.service.impl;

import com.wanjia.dao.UserInfoMapper;
import com.wanjia.entity.UserInfo;
import com.wanjia.service.UserService;
import com.wanjia.utils.MessageClient;
import com.wanjia.utils.RedisClient;
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
    MessageClient messageClient ;

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

        int sendFlag = 0 ;
        try {
            String verifyCode = generateVerifyCode() ;
            int sendCode = messageClient.sendCode(verifyCode,phoneNumber);
            if(sendCode==1){
                redisClient.setKeyValue(phoneNumber,verifyCode,expireSeconds);
                sendFlag =1 ;
            }
        } catch (Exception e) {
            logger.error("send verifycode error",e);
        }
        return sendFlag;
    }



    public String generateVerifyCode(){
        StringBuilder code = new StringBuilder() ;
        for (int i=0 ; i<4 ;i ++){
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     *
     * @param phoneNumber
     * @param smsCode
     * @return 0 验证码过期或者不存在 1验证成功 2验证码错误
     */
    @Override
    public int checkSmsCode(String phoneNumber, String smsCode) {
        int flag = 0 ;
        String value =  redisClient.getValueByKey(phoneNumber);
        if(value == null ){
            flag = 0;
        }else if(value.equals(smsCode)){
            flag = 1 ;
        }else{
            flag = 2 ;
        }
        return flag;
    }
}

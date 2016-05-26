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

    /**
     *
     * @param userInfo
     * @param smsCode
     * @return 0 表示用户号码已经存在 1 添加用户成功  2 表示验证码过期或者不存在 3验证码错误
     */
    public int  addUser(UserInfo userInfo,String smsCode) {

        //第一步验证验证码是不是正确
        int smsFlag = checkSmsCode(userInfo.getPhonenumber(),smsCode);
        if(smsFlag == 1){

            int count =  userInfoMapper.checkIfPhoneNumberExist(userInfo.getPhonenumber());
            if(count != 0 ){
                return 0 ;
            }else{
                userInfoMapper.insert(userInfo);
                return 1 ;
            }
        }else if (smsFlag == 0){
            return 2 ;
        }else if(smsFlag == 2){
            return 3 ;
        }

        return smsFlag ;


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

    /**
     *
     * @param phoneNumber
     * @param expireSeconds
     * @param isUserExist 用户是不是已经存在，注册时是0 ，找回密码时是1
     * @return 0 表示发送失败 1 表示发送成功  2表示用户不存在，用户找回密码时候有效
     */
    public int sendVerifyCode(String phoneNumber, int expireSeconds,byte isUserExist) {

        int sendFlag = 0 ;
        try {
            if (isUserExist == 0){
                String verifyCode = generateVerifyCode() ;
                redisClient.setKeyValue(phoneNumber,verifyCode,expireSeconds);
                sendFlag = messageClient.sendCode(verifyCode,phoneNumber);
                sendFlag =1 ;
            }else if(isUserExist == 1){
                int count =  userInfoMapper.checkIfPhoneNumberExist(phoneNumber);
                if(count != 0 ){
                    String verifyCode = generateVerifyCode() ;
                    redisClient.setKeyValue(phoneNumber,verifyCode,expireSeconds);
                    sendFlag = messageClient.sendCode(verifyCode,phoneNumber);
                    sendFlag =1 ;
                }else {
                    sendFlag = 2 ;
                }
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

    /**
     *
     * @param phoneNumber
     * @param smsCode
     * @param newPassword
     * @return 0 验证码错误 1修改成功 2修改密码失败
     */
    public int findPassword(String phoneNumber, String smsCode,String newPassword) {

        int retcode = checkSmsCode(phoneNumber,smsCode);
        if(retcode == 1){
            Map map = new HashMap();
            map.put("phoneNumber",phoneNumber);
            map.put("passwd",newPassword);
            try {
                userInfoMapper.updateUserPassword(map);
            } catch (Exception e) {
                logger.error("update passwd error",e);
                retcode = 2 ;
            }
        }else {
            return  0 ;
        }
        return retcode ;
    }
}

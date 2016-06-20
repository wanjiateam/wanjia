package com.wanjia.service.impl;

import com.wanjia.dao.UserInfoMapper;
import com.wanjia.entity.UserInfo;
import com.wanjia.service.UserService;
import com.wanjia.utils.MessageClient;
import com.wanjia.utils.RedisClient;
import com.wanjia.utils.UserReturnJson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

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

    @Autowired
    BASE64Encoder encoder ;

    @Autowired
    BASE64Decoder decoder ;


    /**
     *
     * @param userInfo
     * @param smsCode
     * @return 0 表示用户号码已经存在 1 添加用户成功  2 表示验证码过期或者不存在 3验证码错误
     */
    public UserReturnJson addUser(UserInfo userInfo, String smsCode, String deviceId, int expireDays) {

        UserReturnJson userReturnJson = new UserReturnJson() ;
        userReturnJson.setType("addUser");
        //第一步验证验证码是不是正确
        int smsFlag = checkSmsCode(userInfo.getPhonenumber(),smsCode);
        if(smsFlag == 1){

            int count =  userInfoMapper.checkIfPhoneNumberExist(userInfo.getPhonenumber());
            //把token存到redis中

            StringBuilder token = new StringBuilder();
            token.append(deviceId).append("_").append(1).append("_").append(userInfo.getPhonenumber());
            String encodeToken = encoder.encode(token.toString().getBytes()) ;
            this.storeTokenInRedis(encodeToken,"1_"+userInfo.getPhonenumber(),expireDays);
            if(count != 0 ){
                userReturnJson.setCode(0);
            }else{
                userInfoMapper.insertSelective(userInfo);
                userReturnJson.setCode(1);
                userReturnJson.setToken(encodeToken);
                userReturnJson.setMessage("add user success");
            }
        }else if (smsFlag == 0){
            userReturnJson.setCode(2);
            userReturnJson.setMessage("user alreay exist");
        }else if(smsFlag == 2){
            userReturnJson.setCode(3);
            userReturnJson.setMessage("sms code wrong");
        }

        return userReturnJson ;


    }

    /**
     * 检查用户是否存在
     * @param token
     * @param type
     * @return 0 表示不存在
     */
    public int checkIfUserExist(String token, int type) {
        Map map = new HashMap();
        map.put("type",type);
        map.put("token",token) ;
        return userInfoMapper.checkIfUserExist(map);
    }

    /**
     *
     * @param token
     * @param passwd
     * @param type
     * @param deviceId
     * @param expireDays
     * @return 0 登陆失败 1 登陆成功
     */
    public void userLogin(String token, String passwd, int type,String deviceId,int expireDays,UserReturnJson userReturnJson){
        int flag = 0 ;
        Map map  = new HashMap();
        map.put("token",token);
        map.put("passwd",passwd);
        int retcode = userInfoMapper.userLogin(map) ;
        if(retcode != 0 ){
            userReturnJson.setCode(1);
            flag = 1 ;
            StringBuilder sb = new StringBuilder();
            sb.append(deviceId).append("_").append(0).append("_").append(token);
            String encodeToken = encoder.encode(token.toString().getBytes()) ;
            this.storeTokenInRedis(encodeToken,type+"_"+token,expireDays);
            userReturnJson.setToken(token);
            userReturnJson.setMessage("success");
        }else{
            userReturnJson.setCode(2);
            userReturnJson.setMessage("passwd error");
        }
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
                redisClient.setKeyValueWithTimeOut(phoneNumber,verifyCode,expireSeconds);
                sendFlag = messageClient.sendCode(verifyCode,phoneNumber);
                sendFlag =1 ;
            }else if(isUserExist == 1){
                int count =  userInfoMapper.checkIfPhoneNumberExist(phoneNumber);
                if(count != 0 ){
                    String verifyCode = generateVerifyCode() ;
                    redisClient.setKeyValueWithTimeOut(phoneNumber,verifyCode,expireSeconds);
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
    public UserReturnJson findPassword(String phoneNumber, String smsCode,String newPassword,String deviceId,int expireDays) {

        UserReturnJson userReturnJson = new UserReturnJson() ;
        userReturnJson.setType("findPassword");

        int retcode = checkSmsCode(phoneNumber,smsCode);
        if(retcode == 1){
            Map map = new HashMap();
            map.put("phoneNumber",phoneNumber);
            map.put("passwd",newPassword);
            try {
                userInfoMapper.updateUserPassword(map);
                userReturnJson.setCode(1);
                userReturnJson.setMessage("update passwd success");
            } catch (Exception e) {
                logger.error("update passwd error",e);
                userReturnJson.setCode(2);
                userReturnJson.setMessage("update passwd error");
            }
        }else {
            userReturnJson.setCode(0);
            userReturnJson.setMessage("sms code error");
        }
        return userReturnJson ;
    }

    /**
     *
     * @param token
     * @param expireDays
     * @return 0 表示登陆失败 1 表示登陆成功
     */
    public int loginByToken(String token, int expireDays) {

        int flag = 0 ;
        try {
            boolean isExist = redisClient.checkIfKeyExist(token) ;
            flag = 1 ;
            if(isExist){
                int seconds = expireDays*24*60*60 ;
                redisClient.setKeyValueWithTimeOut(token,"1",seconds);
            }
        } catch (Exception e) {
            logger.error("loginByToken error",e);
        }
        return flag;
    }

    public void storeTokenInRedis(String token,String value ,int expireDays){
        int seconds = expireDays*24*60*60 ;
        try {
            redisClient.setKeyValueWithTimeOut(token,value ,seconds);
        } catch (Exception e) {
           logger.error("set value to redis  error",e);
        }
    }



}

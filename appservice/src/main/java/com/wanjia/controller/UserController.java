package com.wanjia.controller;

import com.wanjia.entity.UserInfo;
import com.wanjia.service.UserService;
import com.wanjia.utils.JsonUtil;
import com.wanjia.utils.UserReturnJson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by blake on 2016/5/23.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService ;



    /**
     *用户注册
     * @param phoneNumber
     * @param passwd
     * @return
     */

    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addUser(String phoneNumber,String passwd ,String smsCode,String deviceId,int expireDays){
        UserInfo userInfo = new UserInfo();
        userInfo.setPhonenumber(phoneNumber);
        userInfo.setPasswd(passwd);
        UserReturnJson message  =  userService.addUser(userInfo,smsCode,deviceId,expireDays);
        return JsonUtil.toJsonString(message);
    }

    /**
     *用户密码登录登录
     * @param token
     * @param passwd
     * @param type 0 代表邮箱 1代表手机
     * @return 0 代表email或者手机号码不存在 1表示成功 2表示密码错误
     */
    @RequestMapping(value = "login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String userLogin(String token,String passwd,int type,String deviceId,int expireDays){

        UserReturnJson userReturnJson = new UserReturnJson();
        userReturnJson.setType("userLogin");

        int returncode =  userService.checkIfUserExist(token,type);

        if(returncode == 0){
            userReturnJson.setCode(returncode);
            if(type==0){
                userReturnJson.setMessage("email does not exist");
            }else{
                userReturnJson.setMessage("phone does not exist");
            }
        }else{
              userService.userLogin(token, passwd, type,deviceId,expireDays,userReturnJson);
        }

        return JsonUtil.toJsonString(userReturnJson);
    }

    /**
     *
     * @param token
     * @param expireDays
     * @return 0 表示登陆失败 1 表示登陆成功
     */
    @RequestMapping(value = "loginByToken", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String userLoginByToken(String token,int expireDays){

        UserReturnJson userReturnJson = new UserReturnJson();
        userReturnJson.setType("userLoginByToken");

        int returncode =  userService.loginByToken(token,expireDays) ;
        userReturnJson.setToken(token);
        userReturnJson.setCode(returncode);
        if (returncode == 0){
            userReturnJson.setMessage("login fail");
        }else if(returncode ==1){
            userReturnJson.setMessage("success");
        }

        return JsonUtil.toJsonString(userReturnJson);
    }

    /**
     *
     * @param phoneNumber
     * @param expireSeconds
     * @param isUserExist 0 添加用户 1 找回密码
     * @return 0表示发送失败 1 表示发送成功 2表示用户不存在（在找回密码时候有效）
     */
    @RequestMapping(value = "sendSmsCode", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String sendVerifyCode(String phoneNumber,int expireSeconds,byte isUserExist){

        UserReturnJson message = new UserReturnJson();
        message.setType("sendVerifyCode");
        int returncode =  userService.sendVerifyCode(phoneNumber,expireSeconds,isUserExist);
        message.setCode(returncode);
        if(returncode==1){
            message.setMessage("success");
        }else if(returncode == 0){
            message.setMessage("send code failed");
        }else if (returncode == 2){
            message.setMessage("user does not exist");
        }
        return JsonUtil.toJsonString(message);
    }

    /**
     *
     * @param phoneNumber
     * @param smsCode
     * @return  0 验证码过期或者不存在 1验证成功 2验证码错误
     */
    @RequestMapping(value = "checkSmsCode", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String checkSmsCode(String phoneNumber,String smsCode){

        UserReturnJson message = new UserReturnJson();
        message.setType("checkSmsCode");
        int returncode =  userService.checkSmsCode(phoneNumber,smsCode);
        message.setCode(returncode);
        if(returncode==0){
            message.setMessage("sms expire or not exist");
        }else if(returncode == 1){
            message.setMessage("success");
        }else if(returncode == 2){
            message.setMessage("sms code wrong");
        }
        return JsonUtil.toJsonString(message);
    }

    /**
     *
     * @param phoneNumber
     * @param smsCode
     * @param newPassword
     * @return 0 验证码错误 1修改成功 2修改密码失败
     */
    @RequestMapping(value = "findPasswd", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String findPassword(String phoneNumber ,String smsCode, @RequestParam("newPassword") String newPassword,String deviceId,int expireDays){

        UserReturnJson userReturnJson = userService.findPassword(phoneNumber,smsCode,newPassword,deviceId,expireDays) ;
        return JsonUtil.toJsonString(userReturnJson);
    }
}

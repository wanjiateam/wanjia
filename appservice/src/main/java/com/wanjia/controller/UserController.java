package com.wanjia.controller;

import com.wanjia.auth.AuthPassport;
import com.wanjia.entity.UserInfo;
import com.wanjia.service.UserService;
import com.wanjia.utils.JsonUtil;
import com.wanjia.utils.ReturnMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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

    @RequestMapping(value = "hello",method = RequestMethod.GET)
    public String printWelcome(ModelMap model){
        model.addAttribute("message","hello world");
        return "hello" ;
    }

    /**
     *用户注册
     * @param phoneNumber
     * @param passwd
     * @return returncode 0 表示手机号已经被注册 1表示注册成功 2 表示验证码过期或者不存在 3验证码错误
     */

    @AuthPassport
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addUser(String phoneNumber,String passwd ,String smsCode){

        UserInfo userInfo = new UserInfo();
        userInfo.setPhonenumber(phoneNumber);
        userInfo.setPasswd(passwd);
        int returncode =  userService.addUser(userInfo,smsCode);
        ReturnMessage message = new ReturnMessage();
        message.setType("addUser");
        message.setCode(returncode);
        if(returncode==1){
            message.setMessage("add user success");
        }else if(returncode == 0){
            message.setMessage("user alreay exist");
        }else if(returncode == 2){
            message.setMessage("sms code expire");
        }else if(returncode == 3){
            message.setMessage("sms code wrong");
        }
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
    public String userLogin(String token,String passwd,int type){

        ReturnMessage message = new ReturnMessage();
        message.setType("userLogin");
        int returncode =  userService.checkIfUserExist(token,type);
        if(returncode == 0){
            if(type==0){
                message.setMessage("email does not exist");
            }else{
                message.setMessage("phone does not exist");
            }
        }else{
          returncode = userService.userLogin(token, passwd, type);
            if(returncode != 0){
                message.setCode(1);
                message.setMessage("success");
            }else {
                message.setCode(2);
                message.setMessage("passwd error");
            }
        }

        return JsonUtil.toJsonString(message);
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

        ReturnMessage message = new ReturnMessage();
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

        ReturnMessage message = new ReturnMessage();
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
    public String findPassword(String phoneNumber ,String smsCode, @RequestParam("newPassword") String newPassword){

        int returncode = userService.findPassword(phoneNumber,smsCode,newPassword) ;

        ReturnMessage message = new ReturnMessage();
        message.setType("findPasswd");
        message.setCode(returncode);

        if(returncode==0){
            message.setMessage("sms code error");
        }else if(returncode == 1){
            message.setMessage("success");
        }else if(returncode == 2){
            message.setMessage("update passwd error");
        }
        return JsonUtil.toJsonString(message);
    }
}

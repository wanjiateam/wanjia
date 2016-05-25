package com.wanjia.controller;

import com.wanjia.entity.UserInfo;
import com.wanjia.service.UserService;
import com.wanjia.utils.JsonUtil;
import com.wanjia.utils.ReturnMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
     * @return returncode 0 表示手机号已经被注册 1表示注册成功
     */

    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addUser(String phoneNumber,String passwd ){

        UserInfo userInfo = new UserInfo();
        userInfo.setPhonenumber(phoneNumber);
        userInfo.setPasswd(passwd);
        int returncode =  userService.addUser(userInfo);
        ReturnMessage message = new ReturnMessage();
        message.setType("addUser");
        message.setCode(returncode);
        if(returncode==1){
            message.setMessage("add user success");
        }else if(returncode == 0){
            message.setMessage("user alreay exist");
        }

        return JsonUtil.toJsonString(message);
    }

    /**
     *用户密码登录登录
     * @param token
     * @param passwd
     * @param type 0 代表邮箱 1代表手机
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String userLogin(String token,String passwd,int type){

        ReturnMessage message = new ReturnMessage();
        message.setType("userLogin");
        int returncode =  userService.checkIfUserExist(token,type);
        if(returncode == 0){
            message.setCode(-1);
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
                message.setCode(-1);
                message.setMessage("passwd error");
            }
        }
        return JsonUtil.toJsonString(message);
    }

    @RequestMapping(value = "generateVerifyCode", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String verifyCode(String phoneNumber,int expireSeconds){

        ReturnMessage message = new ReturnMessage();
        message.setType("verifyCode");
        int returncode =  userService.sendVerifyCode(phoneNumber,expireSeconds);
        /*if(returncode == 0){
            message.setCode(-1);
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
                message.setCode(-1);
                message.setMessage("passwd error");
            }
        }*/
        return JsonUtil.toJsonString(message);
    }

}

package com.wanjia.controller;

import com.wanjia.entity.UserInfo;
import com.wanjia.service.UserService;
import com.wanjia.utils.JsonUtil;
import com.wanjia.utils.ReturnMessage;
import org.springframework.beans.factory.annotation.Autowired;
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



    @RequestMapping(value = "add", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addUser(String phoneNumber,String passwd ){

        UserInfo userInfo = new UserInfo();
        userInfo.setPhonenumber(phoneNumber);
        userInfo.setPasswd(passwd);
        int returncode =  userService.addUser(userInfo);
        ReturnMessage message = new ReturnMessage();
        message.setType("addUser");
        message.setCode(returncode);

        return JsonUtil.toJsonString(message);
    }

    @RequestMapping(value = "login", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
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

}

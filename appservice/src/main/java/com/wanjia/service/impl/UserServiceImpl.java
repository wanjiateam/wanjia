package com.wanjia.service.impl;

import com.wanjia.dao.UserInfoMapper;
import com.wanjia.entity.UserInfo;
import com.wanjia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hsb11289 on 2016/5/23.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

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
        return userInfoMapper.checkIfUserExist(token,type);
    }

    public int userLogin(String token, String passwd, int type){
       return  userInfoMapper.userLogin(token,passwd,type) ;
    }
}

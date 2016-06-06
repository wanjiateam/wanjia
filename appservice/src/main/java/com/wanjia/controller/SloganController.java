package com.wanjia.controller;

import com.wanjia.service.SloganService;
import com.wanjia.utils.JsonReturnBody;
import com.wanjia.utils.JsonUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

/**
 * Created by hsb11289 on 2016/6/6.
 */
@Controller
@RequestMapping("/slogan")
public class SloganController {

   private static Logger logger = Logger.getLogger(SloganController.class) ;
    @Autowired
    SloganService sloganService ;

    @RequestMapping(value = "getSloganPicUrl", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getSloganPicUrl(){

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        Set<String> picsets = null;
        try {
            picsets = sloganService.getSloganInfos("slogan");
            if(picsets.size()!=0){
                jsonReturnBody.setCode(1);
                jsonReturnBody.setMessage(picsets);
                jsonReturnBody.setType("getSloganPicUrl");
            }else{
                jsonReturnBody.setCode(0);
                jsonReturnBody.setMessage("no pic urls in redis");
                jsonReturnBody.setType("getSloganPicUrl");
            }
        } catch (Exception e) {
            logger.error("get slogan pic error",e);
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("redis exception:"+e.getMessage());
            jsonReturnBody.setType("getSloganPicUrl");
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }














}

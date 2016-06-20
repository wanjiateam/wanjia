package com.wanjia.controller;

import com.wanjia.service.ResortService;
import com.wanjia.utils.JsonReturnBody;
import com.wanjia.utils.JsonUtil;
import com.wanjia.vo.ResortDestinationVo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by blake on 2016/6/12.
 */

@Controller
@RequestMapping("/resort")
public class ResortInfoController {

    Logger logger = Logger.getLogger(ResortInfoController.class);

    @Autowired
    ResortService resortService ;

    @RequestMapping(value = "getResortNameAndPinYin", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAllResortNameAndPinyin(){

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        jsonReturnBody.setType("getResortNameAndPinYin");
        try {
           ResortDestinationVo resortDestinationVo = resortService.getAllResortNameAndPinYin() ;
            if(resortDestinationVo.getDestinations().size() != 0 ){
                jsonReturnBody.setCode(1);
                jsonReturnBody.setMessage(resortDestinationVo);
            }else{
                jsonReturnBody.setCode(2);
                jsonReturnBody.setMessage("get a empty resort info");
            }

        } catch (Exception e) {
            logger.error("get resort info error",e);
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage(e.getMessage());
        }

        return JsonUtil.toJsonString(jsonReturnBody);
    }

    @RequestMapping(value = "getHotResort", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getHotResort(){

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        jsonReturnBody.setType("getHotResort");
        try {
            ResortDestinationVo hotDestinations = resortService.getHotDestination() ;
            if(hotDestinations.getDestinations().size() != 0 ){
                jsonReturnBody.setCode(1);
                jsonReturnBody.setMessage(hotDestinations);
            }else{
                jsonReturnBody.setCode(2);
                jsonReturnBody.setMessage("get a empty hot resort info");
            }

        } catch (Exception e) {
            logger.error("get hot resort info error",e);
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage(e.getMessage());
        }

        return JsonUtil.toJsonString(jsonReturnBody);
    }




}

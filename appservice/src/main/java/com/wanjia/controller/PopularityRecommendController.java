package com.wanjia.controller;

import com.wanjia.entity.PopularityRecommendEntity;
import com.wanjia.service.PopularityRecommendService;
import com.wanjia.utils.JsonReturnBody;
import com.wanjia.utils.JsonUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by hsb11289 on 2016/6/7.
 */
@Controller
@RequestMapping("/popular")
public class PopularityRecommendController {

    Logger logger = Logger.getLogger(PopularityRecommendController.class) ;
    @Autowired
    PopularityRecommendService popularityRecommendService ;

    @RequestMapping(value = "getPopularList", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getSloganPicUrl(){

        JsonReturnBody returnBody = new JsonReturnBody() ;
        List<PopularityRecommendEntity> poppopularity = null;
        try {
            poppopularity = popularityRecommendService.getPopularityRecommendList();
            if(poppopularity.size() >0){
                returnBody.setCode(1);
                returnBody.setType("getPopularList");
                returnBody.setMessage(poppopularity);
            }else{
                returnBody.setCode(0);
                returnBody.setType("getPopularList");
                returnBody.setMessage("empty popularity  in redis");
            }
        } catch (Exception e) {
            logger.error("redis exception",e);
            returnBody.setCode(0);
            returnBody.setType("getPopularList");
            returnBody.setMessage("connect redis error");
        }
        return JsonUtil.toJsonString(returnBody);
    }




}

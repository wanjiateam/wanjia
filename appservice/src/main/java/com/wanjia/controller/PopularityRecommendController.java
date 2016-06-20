package com.wanjia.controller;

import com.wanjia.entity.PopularityRecommendEntity;
import com.wanjia.service.PopularityRecommendService;
import com.wanjia.utils.JsonReturnBody;
import com.wanjia.utils.JsonUtil;
import com.wanjia.vo.PopularityEntityVo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String getPopularPicUrl(){


        Map<String,List<PopularityRecommendEntity>> container ;
        JsonReturnBody returnBody = new JsonReturnBody() ;
        List<PopularityRecommendEntity> popopularity = null;
        try {
            popopularity = popularityRecommendService.getPopularityRecommendList();
            if(popopularity.size() >0){
                returnBody.setCode(1);
                returnBody.setType("getPopularList");
                container = new HashMap<String,List<PopularityRecommendEntity>>() ;
                for(PopularityRecommendEntity entity :popopularity){
                    List<PopularityRecommendEntity> shops = container.get(entity.getResortName());
                    if(shops == null ){
                        shops = new ArrayList<PopularityRecommendEntity>() ;
                        container.put(entity.getResortName(),shops) ;
                    }
                    shops.add(entity);
                }

                PopularityEntityVo vo = new PopularityEntityVo();

                for(Map.Entry<String,List<PopularityRecommendEntity>> pentity : container.entrySet() ){
                    PopularityEntityVo.PopularityShopCollection collection = vo.new PopularityShopCollection();
                    collection.setResortname(pentity.getKey());
                    collection.setResortId(pentity.getValue().get(0).getResortId());
                    collection.setEntitys(pentity.getValue());
                    vo.addShopCollection(collection);
                }

                returnBody.setMessage(vo);
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

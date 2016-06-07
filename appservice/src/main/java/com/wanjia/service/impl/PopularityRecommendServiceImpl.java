package com.wanjia.service.impl;

import com.wanjia.entity.PopularityRecommendEntity;
import com.wanjia.service.PopularityRecommendService;
import com.wanjia.utils.JsonUtil;
import com.wanjia.utils.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by hsb11289 on 2016/6/7.
 */
@Service("popularityRecommendService")
public class PopularityRecommendServiceImpl implements PopularityRecommendService {

    @Autowired
    RedisClient redisClient ;

    @Override
    public List<PopularityRecommendEntity> getPopularityRecommendList() throws Exception{

        List<PopularityRecommendEntity> popularities = new ArrayList<PopularityRecommendEntity>() ;
        Set<String> sets = redisClient.getSortedSet("popularity") ;
        if(sets.size() > 0){
            for(String popularity : sets){
                popularities.add((PopularityRecommendEntity)JsonUtil.toObject(popularity,PopularityRecommendEntity.class));
            }
        }
        return popularities;
    }
}

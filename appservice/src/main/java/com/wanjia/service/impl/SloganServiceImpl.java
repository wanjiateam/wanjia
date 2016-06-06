package com.wanjia.service.impl;

import com.wanjia.service.SloganService;
import com.wanjia.utils.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by hsb11289 on 2016/6/6.
 */
@Service("sloganService")
public class SloganServiceImpl implements SloganService {

    @Autowired
    RedisClient redisClient ;

    @Override
    public Set<String> getSloganInfos(String key) throws Exception{
        return redisClient.getSortedSet(key);
    }
}

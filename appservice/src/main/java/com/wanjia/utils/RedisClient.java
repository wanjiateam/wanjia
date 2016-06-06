package com.wanjia.utils;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * Created by hsb11289 on 2016/5/25.
 */
public class RedisClient {

    private  Jedis jedis ;

    private String redisIp;
    private int redisPort ;

    public RedisClient(String redisIp, int redisPort) {
        this.redisIp = redisIp;
        this.redisPort = redisPort;
        jedis = new Jedis(redisIp,redisPort);
    }

    public void setKeyValue(String key,String value,int expireSecond){
        jedis.setex(key,expireSecond,value) ;
    }

    public String getValueByKey(String key){
        return jedis.get(key) ;
    }


    public boolean checkIfKeyExist(String key) throws Exception{
       return  jedis.exists(key) ;
    }

    public Set<String> getSortedSet(String key){
        return   jedis.zrevrange(key,0,-1) ;
    }



}

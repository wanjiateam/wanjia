package com.wanjia.utils;

import redis.clients.jedis.Jedis;

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
        jedis.set(key,value);
        jedis.expire(key,expireSecond);
    }

    public String getValueByKey(String key,String value){
        return jedis.get(key) ;
    }



}

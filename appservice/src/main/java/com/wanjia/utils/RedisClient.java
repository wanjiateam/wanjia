package com.wanjia.utils;

import redis.clients.jedis.Jedis;

import java.util.Map;
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

    public void setKeyValueWithTimeOut(String key, String value, int expireSecond){
        jedis.setex(key,expireSecond,value) ;
    }

    public void setKeyValue(String key, String value){
        jedis.set(key,value) ;
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

    public long setSortedSet(String key, Map<String,Double> values){
        return   jedis.zadd(key,values);
    }

    // hash data structure
    public Map<String,String> getAllHashValue(String key){
        Map<String,String> result = jedis.hgetAll(key) ;
        return result;
    }
    public String getHashFieldValue(String key,String field){
        return jedis.hget(key,field) ;
    }


    public long setHashValue(String key,String field,String value){
        long result = jedis.hset(key,field,value) ;
        return result ;

    }

    public boolean isHashFieldExist(String key,String field){
        boolean result = jedis.hexists(key,field) ;
        return result ;
    }


}

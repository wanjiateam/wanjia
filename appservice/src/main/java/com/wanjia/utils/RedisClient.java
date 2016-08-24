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
    private static int expireSeconds = 60*60*24*7 ;

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
        String value = jedis.get(key) ;
        return value ;
    }


    public boolean checkIfKeyExist(String key) throws Exception{
        boolean exist =   jedis.exists(key) ;
        return exist ;
    }

    public Set<String> getSortedSet(String key){
        Set<String> values = jedis.zrevrange(key,0,-1);
        return   values ;
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
         String value = jedis.hget(key,field) ;
        return value;
    }


    public long setHashValue(String key,String field,String value){
        long result = jedis.hset(key,field,value) ;
        jedis.expire(key,expireSeconds) ;
        return result ;

    }

    public boolean isHashFieldExist(String key,String field){
        boolean result = jedis.hexists(key,field) ;
        return result ;
    }

    //Set 不会为null
    public Set<String> getAllHashKeys(String key){
        Set<String> keys = jedis.hkeys(key);
        return keys ;
    }

    public long delHashKey(String key,String... field){
         long affectNumber  = jedis.hdel(key,field);
         return  affectNumber ;
    }






}

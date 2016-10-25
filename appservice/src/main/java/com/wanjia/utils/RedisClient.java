package com.wanjia.utils;

import com.wanjia.exceptions.RedisException;
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

    public RedisClient(String redisIp, int redisPort) throws RedisException{
        try {
            this.redisIp = redisIp;
            this.redisPort = redisPort;
            jedis = new Jedis(redisIp,redisPort);
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
    }

    public void setKeyValueWithTimeOut(String key, String value, int expireSecond) throws RedisException{
        try {
            jedis.setex(key,expireSecond,value) ;
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
    }

    public void setKeyValue(String key, String value) throws RedisException{
        try {
            jedis.set(key,value) ;
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
    }

    public String getValueByKey(String key) throws RedisException{
        String value = null ;
        try {
            value = jedis.get(key) ;
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
        return value ;
    }


    public boolean checkIfKeyExist(String key) throws RedisException{
        boolean exist = false;
        try {
            exist = jedis.exists(key);
        } catch (Exception e) {
            throw new RedisException("redis exception",e);        }
        return exist ;
    }

    public Set<String> getSortedSet(String key) throws RedisException{
        Set<String> values = null;
        try {
            values = jedis.zrevrange(key,0,-1);
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
        return   values ;
    }

    public long setSortedSet(String key, Map<String,Double> values) throws RedisException{
        try {
            return   jedis.zadd(key,values);
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
    }

    // hash data structure
    public Map<String,String> getAllHashValue(String key) throws RedisException{
        Map<String,String> result = null;
        try {
            result = jedis.hgetAll(key);
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
        return result;
    }
    public String getHashFieldValue(String key,String field) throws RedisException{
        String value = null;
        try {
            value = jedis.hget(key,field);
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
        return value;
    }


    public long setHashValue(String key,String field,String value) throws RedisException{
        long result = 0;
        try {
            result = jedis.hset(key,field,value);
            jedis.expire(key,expireSeconds) ;
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }

        return result ;

    }

    public boolean isHashFieldExist(String key,String field) throws RedisException{
        boolean result = false;
        try {
            result = jedis.hexists(key,field);
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
        return result ;
    }

    //Set 不会为null
    public Set<String> getAllHashKeys(String key) throws RedisException{
        Set<String> keys = null;
        try {
            keys = jedis.hkeys(key);
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
        return keys ;
    }

    public long delHashKey(String key,String... field) throws RedisException{
        long affectNumber  = 0;
        try {
            affectNumber = jedis.hdel(key,field);
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
        return  affectNumber ;
    }

    public void setMutilHashValue(String key,Map<String,String> value) throws RedisException{
        try {
            jedis.hmset(key,value) ;
            jedis.expire(key,expireSeconds) ;
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
    }

    public long delKey(String key) throws RedisException{
        long affectNumber = 0;
        try {
            affectNumber = jedis.del(key);
        } catch (Exception e) {
            throw new RedisException("redis exception",e);
        }
        return affectNumber ;
    }







}

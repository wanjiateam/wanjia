package com.wanjia.utils;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


public class JsonUtil {

    public static Gson gson = new Gson() ;

    public static String toJsonString(Object o){

        return gson.toJson(o);
    }




    public static Object toObject(String json, Class clazz){

        Map<String,String> map = new HashMap<String,String>() ;
        gson.fromJson(json,map.getClass()) ;
        return gson.fromJson(json,clazz) ;
    }



}

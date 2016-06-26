package com.wanjia.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class JsonUtil {

    public static Gson gson = new Gson() ;

    public static String toJsonString(Object o){

        return gson.toJson(o);
    }




    public static Object toObject(String json, Class clazz){
        return gson.fromJson(json,clazz) ;
    }

    public static Object toList(String json, Type type){
        return gson.fromJson(json,type) ;
    }



}

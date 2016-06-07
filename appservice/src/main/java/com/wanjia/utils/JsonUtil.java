package com.wanjia.utils;

import com.google.gson.Gson;


public class JsonUtil {

    public static Gson gson = new Gson() ;

    public static String toJsonString(Object o){

        return gson.toJson(o);
    }




    public static Object toObject(String json, Class clazz){

        return gson.fromJson(json,clazz) ;
    }



}

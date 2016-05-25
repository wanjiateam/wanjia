package com.wanjia.utils;

import com.google.gson.Gson;

/**
 * Created by blake on 2016/5/24.
 */
public class JsonUtil {

    public static Gson gson = new Gson() ;

    public static String toJsonString(Object o){

        return gson.toJson(o);
    }



}

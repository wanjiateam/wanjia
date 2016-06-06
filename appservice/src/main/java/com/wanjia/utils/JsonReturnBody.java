package com.wanjia.utils;

/**
 * Created by hsb11289 on 2016/6/6.
 */
public class JsonReturnBody {
    private int code ;
    private Object message ;
    private String type ;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public Object getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }
}

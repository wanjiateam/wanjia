package com.wanjia.vo;

/**
 * Created by blake on 2016/6/25.
 */
public class ShopProductLogoVo {

    private long shopId ;
    private int picType ;
    private String picName ;
    private String picUrl ;
    private String picDescribe ;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public int getPicType() {
        return picType;
    }

    public void setPicType(int picType) {
        this.picType = picType;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicDescrible() {
        return picDescribe;
    }

    public void setPicDescrible(String picDescribe) {
        this.picDescribe = picDescribe;
    }
}

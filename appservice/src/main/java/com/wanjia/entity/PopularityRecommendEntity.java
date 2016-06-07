package com.wanjia.entity;

/**
 * Created by hsb11289 on 2016/6/7.
 */
public class PopularityRecommendEntity{

    //店家的id
    private int shopId ;
    //店家的店名
    private String shopName ;
    //店家店铺的url
    private String shopUrl ;
    //好评数
    private int goodNum ;
    //推荐数
    private int recommendNum ;
    //产品的类型 1：住 2：食 3：游 4：产
    private int pruductType ;
    //图片的url地址用做首页展示
    private String picUrl ;


    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    public int getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(int goodNum) {
        this.goodNum = goodNum;
    }

    public int getRecommendNum() {
        return recommendNum;
    }

    public void setRecommendNum(int recommendNum) {
        this.recommendNum = recommendNum;
    }

    public int getPruductType() {
        return pruductType;
    }

    public void setPruductType(int pruductType) {
        this.pruductType = pruductType;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}

package com.wanjia.vo.travel;

/**
 * Created by blake on 2016/7/17.
 */
public class GuideVo {
    private long guideId ;
    private long resortId ;
    private String describe ;
    private String  comments ;
    private int  carService ;
    private int tourGuideService ;
    private long shopId ;
    private String shopName ;
    private  String picUrl ;
    private double tourGuardPrice ;
    private int guideNumber ;
    private int allowBookNumber ;

    public long getGuideId() {
        return guideId;
    }

    public void setGuideId(long guideId) {
        this.guideId = guideId;
    }


    public long getResortId() {
        return resortId;
    }

    public void setResortId(long resortId) {
        this.resortId = resortId;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getCarService() {
        return carService;
    }

    public void setCarService(int carService) {
        this.carService = carService;
    }

    public int getTourGuideService() {
        return tourGuideService;
    }

    public void setTourGuideService(int tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public double getTourGuardPrice() {
        return tourGuardPrice;
    }

    public void setTourGuardPrice(double tourGuardPrice) {
        this.tourGuardPrice = tourGuardPrice;
    }

    public int getGuideNumber() {
        return guideNumber;
    }

    public void setGuideNumber(int guideNumber) {
        this.guideNumber = guideNumber;
    }

    public int getAllowBookNumber() {
        return allowBookNumber;
    }

    public void setAllowBookNumber(int allowBookNumber) {
        this.allowBookNumber = allowBookNumber;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}

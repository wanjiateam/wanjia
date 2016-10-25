package com.wanjia.vo.travel;

/**
 * Created by blake on 2016/7/17.
 */
public class FamilyActivityVo {

    private long familyActiveId;
    private double familyActivityPrice ;
    private String  familyActivityName ;
    private String familyActivityComment ;
    private int tourTimeElapse ;
    private  int personNumMin ;
    private int personNumMax ;
    private long shopId ;
    private  String picUrl ;
    private  int maxBookNumber ;
    private String shopName ;

    public long getFamilyActiveId() {
        return familyActiveId;
    }

    public void setFamilyActiveId(long familyActiveId) {
        this.familyActiveId = familyActiveId;
    }

    public double getFamilyActivityPrice() {
        return familyActivityPrice;
    }

    public void setFamilyActivityPrice(double familyActivityPrice) {
        this.familyActivityPrice = familyActivityPrice;
    }

    public String getFamilyActivityName() {
        return familyActivityName;
    }

    public void setFamilyActivityName(String familyActivityName) {
        this.familyActivityName = familyActivityName;
    }

    public String getFamilyActivityComment() {
        return familyActivityComment;
    }

    public void setFamilyActivityComment(String familyActivityComment) {
        this.familyActivityComment = familyActivityComment;
    }


    public int getTourTimeElapse() {
        return tourTimeElapse;
    }

    public void setTourTimeElapse(int tourTimeElapse) {
        this.tourTimeElapse = tourTimeElapse;
    }

    public int getPersonNumMin() {
        return personNumMin;
    }

    public void setPersonNumMin(int personNumMin) {
        this.personNumMin = personNumMin;
    }

    public int getPersonNumMax() {
        return personNumMax;
    }

    public void setPersonNumMax(int personNumMax) {
        this.personNumMax = personNumMax;
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

    public int getMaxBookNumber() {
        return maxBookNumber;
    }

    public void setMaxBookNumber(int maxBookNumber) {
        this.maxBookNumber = maxBookNumber;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}

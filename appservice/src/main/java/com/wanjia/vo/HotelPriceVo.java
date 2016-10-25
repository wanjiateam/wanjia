package com.wanjia.vo;

import java.util.Date;

/**
 * Created by blake on 2016/6/29.
 */
public class HotelPriceVo {

    private long resortId ;
    private long roomId ;
    private long shopId ;
    private String priceDate ;
    private long priceDateLongValue ;
    private double roomPrice ;
    private String shopName ;
    private long landmarkId;


    public long getResortId() {
        return resortId;
    }

    public void setResortId(long resortId) {
        this.resortId = resortId;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(String priceDate) {
        this.priceDate = priceDate;
    }

    public long getPriceDateLongValue() {
        return priceDateLongValue;
    }

    public void setPriceDateLongValue(long priceDateLongValue) {
        this.priceDateLongValue = priceDateLongValue;
    }

    public double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(double roomPrice) {
        this.roomPrice = roomPrice;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public long getLandmarkId() {
        return landmarkId;
    }

    public void setLandmarkId(long landmarkId) {
        this.landmarkId = landmarkId;
    }
}

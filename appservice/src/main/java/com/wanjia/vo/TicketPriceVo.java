package com.wanjia.vo;

/**
 * Created by blake on 2016/7/2.
 */
public class TicketPriceVo {

    private long resortId ;
    private long shopId ;
    private long ticketId ;
    private double price ;
    private long priceDateLongValue ;
    private String priceDate ;
    private String shopName ;
    private long landmarkId ;


    public long getResortId() {
        return resortId;
    }

    public void setResortId(long resortId) {
        this.resortId = resortId;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getPriceDateLongValue() {
        return priceDateLongValue;
    }

    public void setPriceDateLongValue(long priceDateLongValue) {
        this.priceDateLongValue = priceDateLongValue;
    }

    public String getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(String priceDate) {
        this.priceDate = priceDate;
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

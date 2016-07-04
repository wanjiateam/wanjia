package com.wanjia.vo;

import java.util.Date;

/**
 * Created by hsb11289 on 2016/6/28.
 */
public class ShopHotelListVo extends ShopListBaseVo {

    private long roomId ;

    private double cheapestPrice ;

    private String cheapestDate ;

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public double getCheapestPrice() {
        return cheapestPrice;
    }

    public void setCheapestPrice(double cheapestPrice) {
        this.cheapestPrice = cheapestPrice;
    }

    public String getCheapestDate() {
        return cheapestDate;
    }

    public void setCheapestDate(String cheapestDate) {
        this.cheapestDate = cheapestDate;
    }
}

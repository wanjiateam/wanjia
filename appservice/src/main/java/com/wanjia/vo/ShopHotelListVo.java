package com.wanjia.vo;

import java.util.Date;

/** 店家住房的vo信息
 * Created by hsb11289 on 2016/6/28.
 */
public class ShopHotelListVo extends ShopListBaseVo implements Comparable<ShopHotelListVo> {

    private long roomId ;

    private double cheapestPrice ;


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

    @Override
    public int compareTo(ShopHotelListVo o) {
        double result = o.getCheapestPrice() - this.cheapestPrice ;
        if(result >0){
            return -1 ;
        }else if(result <0){
            return 1 ;
        }else {
            return 0 ;
        }
    }
}

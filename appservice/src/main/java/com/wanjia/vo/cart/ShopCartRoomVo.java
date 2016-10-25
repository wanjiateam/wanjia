package com.wanjia.vo.cart;

import com.wanjia.vo.restaurant.ShopCoursePictureVo;

/**
 * 代表redis购物车中房间的实体类
 * Created by blake on 2016/8/3.
 */
public class ShopCartRoomVo extends ShopCartBaseVo{

    private long roomId ;
    private String roomName ;
    private String roomType ;
    private  int bookNumber ;
    private String startDate ;
    private String endDate ;
    //客户端把最大可预订的数量传到服务端 用于判断用于预订的数量是不是超过了最大的数量
    private int allowBookNumber ;



    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getAllowBookNumber() {
        return allowBookNumber;
    }

    public void setAllowBookNumber(int allowBookNumber) {
        this.allowBookNumber = allowBookNumber;
    }
}

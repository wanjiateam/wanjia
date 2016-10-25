package com.wanjia.vo.live;

import java.util.List;

/**
 * 房型vo 存储了某一房型的相关信息
 * Created by blake on 2016/7/14.
 */
public class RoomVo {

    private long roomId;
    private long shopId ;
    private String roomName ;
    private String roomType ;
    private String  roomSquare ;
    private int isWindow ;
    private String bedDescribe ;
    private int roomCapacity ;
    private String roomFloor ;
    private String roomDescribe ;
    private String picUrl ;
    private int price ;
    private int roomNumber ;
    private List<RoomBookVo> roomBookVoList ;
    private int allowBookNumber ;
    private String shopName ;

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

    public String getRoomSquare() {
        return roomSquare;
    }

    public void setRoomSquare(String roomSquare) {
        this.roomSquare = roomSquare;
    }

    public int getIsWindow() {
        return isWindow;
    }

    public void setIsWindow(int isWindow) {
        this.isWindow = isWindow;
    }

    public String getBedDescribe() {
        return bedDescribe;
    }

    public void setBedDescribe(String bedDescribe) {
        this.bedDescribe = bedDescribe;
    }

    public int getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(int roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    public String getRoomFloor() {
        return roomFloor;
    }

    public void setRoomFloor(String roomFloor) {
        this.roomFloor = roomFloor;
    }

    public String getRoomDescribe() {
        return roomDescribe;
    }

    public void setRoomDescribe(String roomDescribe) {
        this.roomDescribe = roomDescribe;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public List<RoomBookVo> getRoomBookVoList() {
        return roomBookVoList;
    }

    public void setRoomBookVoList(List<RoomBookVo> roomBookVoList) {
        this.roomBookVoList = roomBookVoList;
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

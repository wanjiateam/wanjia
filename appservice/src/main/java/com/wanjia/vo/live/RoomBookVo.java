package com.wanjia.vo.live;

/**
 * 店家房间预订情况的实体
 * Created by blake on 2016/7/14.
 */
public class RoomBookVo {

    private long shopId;
    private long roomId ;
    private String bookDate;
    private long bookDateLongValue ;
    private int bookRoomNumber;
    private int totalRoomNumber;



    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getBookDate() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }

    public long getBookDateLongValue() {
        return bookDateLongValue;
    }

    public void setBookDateLongValue(long bookDateLongValue) {
        this.bookDateLongValue = bookDateLongValue;
    }

    public int getBookRoomNumber() {
        return bookRoomNumber;
    }

    public void setBookRoomNumber(int bookRoomNumber) {
        this.bookRoomNumber = bookRoomNumber;
    }

    public int getTotalRoomNumber() {
        return totalRoomNumber;
    }

    public void setTotalRoomNumber(int totalRoomNumber) {
        this.totalRoomNumber = totalRoomNumber;
    }
}

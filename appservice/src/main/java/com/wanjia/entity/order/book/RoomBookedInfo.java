package com.wanjia.entity.order.book;

import java.util.Date;

public class RoomBookedInfo {
    //数据库中的主键
    private Long bookId;

    private Long roomId;

    private Long shopId;

    private Date bookDate;

    private Integer bookNumber;

    private Integer number;

    private Long version;

    public RoomBookedInfo(Long bookId, Long roomId, Long shopId, Date bookDate, Integer bookNumber, Integer number, Long version) {
        this.bookId = bookId;
        this.roomId = roomId;
        this.shopId = shopId;
        this.bookDate = bookDate;
        this.bookNumber = bookNumber;
        this.number = number;
        this.version = version;
    }

    public RoomBookedInfo() {
        super();
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Date getBookDate() {
        return bookDate;
    }

    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    public Integer getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(Integer bookNumber) {
        this.bookNumber = bookNumber;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
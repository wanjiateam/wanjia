package com.wanjia.entity.order;

import java.util.Date;

public class UserLiveOrder {
    private String orderId;

    private String pOrderId;

    private Long shopId;

    private Integer roomId;

    private Short bookNumer;

    private Double price;

    private Date createDate;

    private Date startDate;

    private Date endDate;

    private Byte isRemark;

    private Boolean isComment;

    private Boolean orderState;

    public UserLiveOrder(String orderId, String pOrderId, Long shopId, Integer roomId) {
        this.orderId = orderId;
        this.pOrderId = pOrderId;
        this.shopId = shopId;
        this.roomId = roomId;
    }


    public UserLiveOrder(String orderId, String pOrderId, Long shopId, Integer roomId, Short bookNumer, Double price, Date createDate, Date startDate, Date endDate, Byte isRemark, Boolean isComment, Boolean orderState) {
        this.orderId = orderId;
        this.pOrderId = pOrderId;
        this.shopId = shopId;
        this.roomId = roomId;
        this.bookNumer = bookNumer;
        this.price = price;
        this.createDate = createDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isRemark = isRemark;
        this.isComment = isComment;
        this.orderState = orderState;
    }

    public UserLiveOrder() {
        super();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getpOrderId() {
        return pOrderId;
    }

    public void setpOrderId(String pOrderId) {
        this.pOrderId = pOrderId == null ? null : pOrderId.trim();
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Short getBookNumer() {
        return bookNumer;
    }

    public void setBookNumer(Short bookNumer) {
        this.bookNumer = bookNumer;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Byte getIsRemark() {
        return isRemark;
    }

    public void setIsRemark(Byte isRemark) {
        this.isRemark = isRemark;
    }

    public Boolean getIsComment() {
        return isComment;
    }

    public void setIsComment(Boolean isComment) {
        this.isComment = isComment;
    }

    public Boolean getOrderState() {
        return orderState;
    }

    public void setOrderState(Boolean orderState) {
        this.orderState = orderState;
    }
}
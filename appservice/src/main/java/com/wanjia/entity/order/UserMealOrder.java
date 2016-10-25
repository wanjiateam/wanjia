package com.wanjia.entity.order;

import java.util.Date;

public class UserMealOrder {
    private String orderId;

    private String pOrderId;

    private Long shopId;

    private Double price;

    private Byte mealType;

    private Date mealDate;

    private Date createDate;

    private Boolean isComment;

    private Byte isRemark;

    private Boolean orderState;

    public UserMealOrder(String orderId, String pOrderId, Long shopId) {
        this.orderId = orderId;
        this.pOrderId = pOrderId;
        this.shopId = shopId;
    }

    public UserMealOrder(String orderId, String pOrderId, Long shopId, Double price, Byte mealType, Date mealDate, Date createDate, Boolean isComment, Byte isRemark, Boolean orderState) {
        this.orderId = orderId;
        this.pOrderId = pOrderId;
        this.shopId = shopId;
        this.price = price;
        this.mealType = mealType;
        this.mealDate = mealDate;
        this.createDate = createDate;
        this.isComment = isComment;
        this.isRemark = isRemark;
        this.orderState = orderState;
    }

    public UserMealOrder() {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Byte getMealType() {
        return mealType;
    }

    public void setMealType(Byte mealType) {
        this.mealType = mealType;
    }

    public Date getMealDate() {
        return mealDate;
    }

    public void setMealDate(Date mealDate) {
        this.mealDate = mealDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Boolean getIsComment() {
        return isComment;
    }

    public void setIsComment(Boolean isComment) {
        this.isComment = isComment;
    }

    public Byte getIsRemark() {
        return isRemark;
    }

    public void setIsRemark(Byte isRemark) {
        this.isRemark = isRemark;
    }

    public Boolean getOrderState() {
        return orderState;
    }

    public void setOrderState(Boolean orderState) {
        this.orderState = orderState;
    }
}
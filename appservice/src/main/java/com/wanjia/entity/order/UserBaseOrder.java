package com.wanjia.entity.order;

import java.util.Date;

public class UserBaseOrder {

    private String orderId;

    private Long userId;

    private Double totalMoney;

    private Date createTime;

    private String shopIds;

    private Byte orderState;

    public UserBaseOrder(String orderId, Long userId, Double totalMoney, Date createTime, String shopIds, Byte orderState) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalMoney = totalMoney;
        this.createTime = createTime;
        this.shopIds = shopIds;
        this.orderState = orderState;
    }

    public UserBaseOrder() {
        super();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getShopIds() {
        return shopIds;
    }

    public void setShopIds(String shopIds) {
        this.shopIds = shopIds == null ? null : shopIds.trim();
    }

    public Byte getOrderState() {
        return orderState;
    }

    public void setOrderState(Byte orderState) {
        this.orderState = orderState;
    }
}
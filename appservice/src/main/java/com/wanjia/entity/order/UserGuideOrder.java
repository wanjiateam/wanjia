package com.wanjia.entity.order;

import java.util.Date;

public class UserGuideOrder {
    private String orderId;

    private String pOrderId;

    private Long shopid;

    private Double price;

    private Date createDate;

    private Date startDate;

    private Date endDate;

    private Byte isRemark;

    private Boolean isComment;

    public UserGuideOrder(String orderId, String pOrderId, Long shopid, Double price, Date createDate, Date startDate, Date endDate, Byte isRemark, Boolean isComment) {
        this.orderId = orderId;
        this.pOrderId = pOrderId;
        this.shopid = shopid;
        this.price = price;
        this.createDate = createDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isRemark = isRemark;
        this.isComment = isComment;
    }

    public UserGuideOrder() {
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

    public Long getShopid() {
        return shopid;
    }

    public void setShopid(Long shopid) {
        this.shopid = shopid;
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
}
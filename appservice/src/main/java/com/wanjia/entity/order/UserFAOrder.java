package com.wanjia.entity.order;

import java.util.Date;

public class UserFAOrder {
    private String orderId;

    private String pOrderId;

    private Long fId;

    private Long shopId;

    private Double price;

    private Integer bookNumber;

    private Date createDate;

    private Date tourDate;

    private Boolean isComment;

    private Byte isRemark;

    public UserFAOrder(String orderId, String pOrderId, Long fId, Long shopId, Double price, Integer bookNumber, Date createDate, Date tourDate, Boolean isComment, Byte isRemark) {
        this.orderId = orderId;
        this.pOrderId = pOrderId;
        this.fId = fId;
        this.shopId = shopId;
        this.price = price;
        this.bookNumber = bookNumber;
        this.createDate = createDate;
        this.tourDate = tourDate;
        this.isComment = isComment;
        this.isRemark = isRemark;
    }

    public UserFAOrder() {
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

    public Long getfId() {
        return fId;
    }

    public void setfId(Long fId) {
        this.fId = fId;
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

    public Integer getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(Integer bookNumber) {
        this.bookNumber = bookNumber;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getTourDate() {
        return tourDate;
    }

    public void setTourDate(Date tourDate) {
        this.tourDate = tourDate;
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
}
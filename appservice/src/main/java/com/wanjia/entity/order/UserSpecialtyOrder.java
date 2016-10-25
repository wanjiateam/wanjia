package com.wanjia.entity.order;

import java.util.Date;

public class UserSpecialtyOrder {
    private String orderId;

    private String pOrderId;

    private Long shopId;

    private Long specialtyId;

    private Double price;

    private Date createDate;

    private Date bookDate;

    private Boolean isComment;

    private Byte isRemark;

    private Integer bookNumber;

    public UserSpecialtyOrder(String orderId, String pOrderId, Long shopId, Long specialtyId, Double price, Date createDate, Date bookDate, Boolean isComment, Byte isRemark, Integer bookNumber) {
        this.orderId = orderId;
        this.pOrderId = pOrderId;
        this.shopId = shopId;
        this.specialtyId = specialtyId;
        this.price = price;
        this.createDate = createDate;
        this.bookDate = bookDate;
        this.isComment = isComment;
        this.isRemark = isRemark;
        this.bookNumber = bookNumber;
    }

    public UserSpecialtyOrder() {
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

    public Long getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Long specialtyId) {
        this.specialtyId = specialtyId;
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

    public Date getBookDate() {
        return bookDate;
    }

    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
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

    public Integer getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(Integer bookNumber) {
        this.bookNumber = bookNumber;
    }
}
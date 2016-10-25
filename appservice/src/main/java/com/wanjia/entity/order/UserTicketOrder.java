package com.wanjia.entity.order;

import java.util.Date;

public class UserTicketOrder {
    private String orderId;

    private String pOrderId;

    private Long shopId;

    private Long ticketId;

    private Integer bookNumer;

    private Double price;

    private Date createDate;

    private Date tourDate;

    private Boolean isComment;

    private Byte isRemark;

    public UserTicketOrder(String orderId, String pOrderId, Long shopId, Long ticketId, Integer bookNumer, Double price, Date createDate, Date tourDate, Boolean isComment, Byte isRemark) {
        this.orderId = orderId;
        this.pOrderId = pOrderId;
        this.shopId = shopId;
        this.ticketId = ticketId;
        this.bookNumer = bookNumer;
        this.price = price;
        this.createDate = createDate;
        this.tourDate = tourDate;
        this.isComment = isComment;
        this.isRemark = isRemark;
    }

    public UserTicketOrder() {
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

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getBookNumer() {
        return bookNumer;
    }

    public void setBookNumer(Integer bookNumer) {
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
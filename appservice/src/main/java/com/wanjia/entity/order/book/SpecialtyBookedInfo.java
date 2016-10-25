package com.wanjia.entity.order.book;

import java.util.Date;

public class SpecialtyBookedInfo {

    private Long id;

    private Integer specialtyId;

    private Long shopId;

    private Double specialtyPrice;

    private String specialtyName;

    private String specialtyComment;

    private String specialtyPictureUrl;

    private Date createDate;

    private Date updateDate;

    private Long specialNumber;

    private Integer specialUnit;

    private Integer bookedNumber;

    private Long version;

    public SpecialtyBookedInfo(Long id, Integer specialtyId, Long shopId, Double specialtyPrice, String specialtyName, String specialtyComment, String specialtyPictureUrl, Date createDate, Date updateDate, Long specialNumber, Integer specialUnit, Integer bookedNumber, Long version) {
        this.id = id;
        this.specialtyId = specialtyId;
        this.shopId = shopId;
        this.specialtyPrice = specialtyPrice;
        this.specialtyName = specialtyName;
        this.specialtyComment = specialtyComment;
        this.specialtyPictureUrl = specialtyPictureUrl;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.specialNumber = specialNumber;
        this.specialUnit = specialUnit;
        this.bookedNumber = bookedNumber;
        this.version = version;
    }

    public SpecialtyBookedInfo() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Integer specialtyId) {
        this.specialtyId = specialtyId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Double getSpecialtyPrice() {
        return specialtyPrice;
    }

    public void setSpecialtyPrice(Double specialtyPrice) {
        this.specialtyPrice = specialtyPrice;
    }

    public String getSpecialtyName() {
        return specialtyName;
    }

    public void setSpecialtyName(String specialtyName) {
        this.specialtyName = specialtyName == null ? null : specialtyName.trim();
    }

    public String getSpecialtyComment() {
        return specialtyComment;
    }

    public void setSpecialtyComment(String specialtyComment) {
        this.specialtyComment = specialtyComment == null ? null : specialtyComment.trim();
    }

    public String getSpecialtyPictureUrl() {
        return specialtyPictureUrl;
    }

    public void setSpecialtyPictureUrl(String specialtyPictureUrl) {
        this.specialtyPictureUrl = specialtyPictureUrl == null ? null : specialtyPictureUrl.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getSpecialNumber() {
        return specialNumber;
    }

    public void setSpecialNumber(Long specialNumber) {
        this.specialNumber = specialNumber;
    }

    public Integer getSpecialUnit() {
        return specialUnit;
    }

    public void setSpecialUnit(Integer specialUnit) {
        this.specialUnit = specialUnit;
    }

    public Integer getBookedNumber() {
        return bookedNumber;
    }

    public void setBookedNumber(Integer bookedNumber) {
        this.bookedNumber = bookedNumber;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
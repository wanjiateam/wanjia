package com.wanjia.entity.order.book;

import java.util.Date;

public class ResourceLockErrorRecord {
    private Long errorId;

    private String objectJson;

    private Integer productType;

    private Date bookDate;

    private Date insertDate;

    private Byte lockType;

    private String exceptionInfo;

    public ResourceLockErrorRecord(Long errorId, String objectJson, Integer productType, Date bookDate, Date insertDate, Byte lockType, String exceptionInfo) {
        this.errorId = errorId;
        this.objectJson = objectJson;
        this.productType = productType;
        this.bookDate = bookDate;
        this.insertDate = insertDate;
        this.lockType = lockType;
        this.exceptionInfo = exceptionInfo;
    }

    public ResourceLockErrorRecord() {
        super();
    }

    public Long getErrorId() {
        return errorId;
    }

    public void setErrorId(Long errorId) {
        this.errorId = errorId;
    }

    public String getObjectJson() {
        return objectJson;
    }

    public void setObjectJson(String objectJson) {
        this.objectJson = objectJson == null ? null : objectJson.trim();
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Date getBookDate() {
        return bookDate;
    }

    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public Byte getLockType() {
        return lockType;
    }

    public void setLockType(Byte lockType) {
        this.lockType = lockType;
    }

    public String getExceptionInfo() {
        return exceptionInfo;
    }

    public void setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo == null ? null : exceptionInfo.trim();
    }
}
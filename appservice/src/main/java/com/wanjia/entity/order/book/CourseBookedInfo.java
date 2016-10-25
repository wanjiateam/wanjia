package com.wanjia.entity.order.book;

import java.util.Date;

public class CourseBookedInfo {
    private Long bookId;

    private Long courseId;

    private Long shopId;

    private Date bookDate;

    private Integer bookedNumber;

    private Integer number;

    private Long version;

    public CourseBookedInfo(Long bookId, Long courseId, Long shopId, Date bookDate, Integer bookedNumber, Integer number, Long version) {
        this.bookId = bookId;
        this.courseId = courseId;
        this.shopId = shopId;
        this.bookDate = bookDate;
        this.bookedNumber = bookedNumber;
        this.number = number;
        this.version = version;
    }

    public CourseBookedInfo() {
        super();
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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

    public Integer getBookedNumber() {
        return bookedNumber;
    }

    public void setBookedNumber(Integer bookedNumber) {
        this.bookedNumber = bookedNumber;
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
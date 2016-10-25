package com.wanjia.vo.restaurant;

/**
 * 菜品预定信息的实体类
 * Created by blake on 2016/7/14.
 */
public class CourseBookVo {

    private long shopId ;
    private long courseId ;
    private String buyDate ;
    private long buyDateLongValue ;
    private int bookedNumber ;
    private int totalNumber ;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public long getBuyDateLongValue() {
        return buyDateLongValue;
    }

    public void setBuyDateLongValue(long buyDateLongValue) {
        this.buyDateLongValue = buyDateLongValue;
    }

    public int getBookedNumber() {
        return bookedNumber;
    }

    public void setBookedNumber(int bookedNumber) {
        this.bookedNumber = bookedNumber;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }
}

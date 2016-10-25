package com.wanjia.vo.cart;

/**
 * 购物车中的菜品的实体类
 * Created by blake on 2016/8/3.
 */
public class ShopCartCourseVo extends  ShopCartBaseVo{

    private long courseId ;
    private String courseName ;
    private int bookNumber ;
    //客户端提供的表示最大的可预订数量
    private int allowBookNumber ;

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public int getAllowBookNumber() {
        return allowBookNumber;
    }

    public void setAllowBookNumber(int allowBookNumber) {
        this.allowBookNumber = allowBookNumber;
    }

}

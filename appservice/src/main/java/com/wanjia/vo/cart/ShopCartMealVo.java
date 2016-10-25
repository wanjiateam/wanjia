package com.wanjia.vo.cart;

import java.util.List;

/**
 * Created by blake on 2016/8/4.
 */
public class ShopCartMealVo  {

    private String bookDate ;
    private int mealType ;
    private double totalPrice ;
    private long shopId ;
    private long userId ;
    private String shopName ;
    private List<ShopCartCourseVo> courseVoList ;

    public String getBookDate() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }

    public int getMealType() {
        return mealType;
    }

    public void setMealType(int mealType) {
        this.mealType = mealType;
    }

    public List<ShopCartCourseVo> getCourseVoList() {
        return courseVoList;
    }

    public void setCourseVoList(List<ShopCartCourseVo> courseVoList) {
        this.courseVoList = courseVoList;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}

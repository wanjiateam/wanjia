package com.wanjia.vo;

/**
 * Created by hsb11289 on 2016/6/28.
 */
public class ShopFoodListVo extends ShopListBaseVo{

    private int foodTotalNum ;
    private double foodAveragePrice ;

    public int getFoodTotalNum() {
        return foodTotalNum;
    }

    public void setFoodTotalNum(int foodTotalNum) {
        this.foodTotalNum = foodTotalNum;
    }

    public double getFoodAveragePrice() {
        return foodAveragePrice;
    }

    public void setFoodAveragePrice(double foodAveragePrice) {
        this.foodAveragePrice = foodAveragePrice;
    }
}

package com.wanjia.vo.cart;

/**
 * 购物车的基础类
 * Created by blake on 2016/8/3.
 */
public class ShopCartBaseVo {

    protected long userId ;
    protected long shopId ;
    protected String shopName ;
    protected double price ;
    protected boolean exist = true ;


    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }
}

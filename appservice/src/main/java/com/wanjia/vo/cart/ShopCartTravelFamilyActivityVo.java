package com.wanjia.vo.cart;

/**
 * 农家特色游的购物车vo 数据
 * Created by blake on 2016/8/11.
 */
public class ShopCartTravelFamilyActivityVo extends ShopCartBaseVo {

    private long familyActivityId ;
    private String familyActivityName ;
    private String familyActivityDate ;
    private int maxBookNumber ;
    private int bookNumber ;

    public long getFamilyActivityId() {
        return familyActivityId;
    }

    public void setFamilyActivityId(long familyActivityId) {
        this.familyActivityId = familyActivityId;
    }

    public String getFamilyActivityName() {
        return familyActivityName;
    }

    public void setFamilyActivityName(String familyActivityName) {
        this.familyActivityName = familyActivityName;
    }

    public String getFamilyActivityDate() {
        return familyActivityDate;
    }

    public void setFamilyActivityDate(String familyActivityDate) {
        this.familyActivityDate = familyActivityDate;
    }

    public int getMaxBookNumber() {
        return maxBookNumber;
    }

    public void setMaxBookNumber(int maxBookNumber) {
        this.maxBookNumber = maxBookNumber;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }
}

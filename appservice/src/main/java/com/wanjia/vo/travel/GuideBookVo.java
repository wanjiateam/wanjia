package com.wanjia.vo.travel;

/**
 * Created by blake on 2016/7/23.
 */
public class GuideBookVo {

    private long shopId;
    private long guideId ;
    private String bookDate;
    private long bookDateLongValue ;
    private int bookNumber;
    private int totalNumber ;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getGuideId() {
        return guideId;
    }

    public void setGuideId(long guideId) {
        this.guideId = guideId;
    }

    public String getBookDate() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }

    public long getBookDateLongValue() {
        return bookDateLongValue;
    }

    public void setBookDateLongValue(long bookDateLongValue) {
        this.bookDateLongValue = bookDateLongValue;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }
}

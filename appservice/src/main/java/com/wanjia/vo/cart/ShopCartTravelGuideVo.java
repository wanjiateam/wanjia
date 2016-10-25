package com.wanjia.vo.cart;

/**
 * Created by blake on 2016/8/5.
 */
public class ShopCartTravelGuideVo extends ShopCartBaseVo {

    private long guideId ;
    private String guideDate ;

    public long getGuideId() {
        return guideId;
    }

    public void setGuideId(long guideId) {
        this.guideId = guideId;
    }

    public String getGuideDate() {
        return guideDate;
    }

    public void setGuideDate(String guideDate) {
        this.guideDate = guideDate;
    }
}

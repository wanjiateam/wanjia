package com.wanjia.vo;

/**
 * Created by blake on 2016/8/7.
 */
public class ShopRecommendAndCommentNumberVo {

    private long shopId ;
    private int recommendNumber ;
    private int goodCommentNumber ;
    private int totalCommentNumber ;

    public ShopRecommendAndCommentNumberVo(long shopId, int recommendNumber, int goodCommentNumber, int totalCommentNumber) {
        this.shopId = shopId;
        this.recommendNumber = recommendNumber;
        this.goodCommentNumber = goodCommentNumber;
        this.totalCommentNumber = totalCommentNumber;
    }



    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public int getRecommendNumber() {
        return recommendNumber;
    }

    public void setRecommendNumber(int recommendNumber) {
        this.recommendNumber = recommendNumber;
    }

    public int getGoodCommentNumber() {
        return goodCommentNumber;
    }

    public void setGoodCommentNumber(int goodCommentNumber) {
        this.goodCommentNumber = goodCommentNumber;
    }

    public int getTotalCommentNumber() {
        return totalCommentNumber;
    }

    public void setTotalCommentNumber(int totalCommentNumber) {
        this.totalCommentNumber = totalCommentNumber;
    }
}

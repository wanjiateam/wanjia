package com.wanjia.vo.speciality;

/**
 * Created by blake on 2016/7/15.
 */
public class SpecialtyVo {

    private  long shopId;
    private  long specialtyId ;
    private String specialtyName ;
    private  double specialtyPrice ;
    private String specialtyComment ;
    private String specialtyPictureUrl;
    private int specialWeight ;
    private int weightUnit ;
    private int specialtyNumber ;
    private  String shopName ;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(long specialtyId) {
        this.specialtyId = specialtyId;
    }

    public double getSpecialtyPrice() {
        return specialtyPrice;
    }

    public void setSpecialtyPrice(double specialtyPrice) {
        this.specialtyPrice = specialtyPrice;
    }

    public String getSpecialtyComment() {
        return specialtyComment;
    }

    public void setSpecialtyComment(String specialtyComment) {
        this.specialtyComment = specialtyComment;
    }

    public String getSpecialtyPictureUrl() {
        return specialtyPictureUrl;
    }

    public void setSpecialtyPictureUrl(String specialtyPictureUrl) {
        this.specialtyPictureUrl = specialtyPictureUrl;
    }

    public int getSpecialWeight() {
        return specialWeight;
    }

    public void setSpecialWeight(int specialWeight) {
        this.specialWeight = specialWeight;
    }

    public int getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(int weightUnit) {
        this.weightUnit = weightUnit;
    }

    public int getSpecialtyNumber() {
        return specialtyNumber;
    }

    public void setSpecialtyNumber(int specialtyNumber) {
        this.specialtyNumber = specialtyNumber;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getSpecialtyName() {
        return specialtyName;
    }

    public void setSpecialtyName(String specialtyName) {
        this.specialtyName = specialtyName;
    }
}

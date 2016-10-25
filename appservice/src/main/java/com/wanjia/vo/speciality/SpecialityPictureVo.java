package com.wanjia.vo.speciality;

/**
 * Created by blake on 2016/7/20.
 */
public class SpecialityPictureVo {

    private long shopId ;
    private long specialtyId ;
    private String picUrl ;
    private String picDesc ;
    private String picName ;

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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicDesc() {
        return picDesc;
    }

    public void setPicDesc(String picDesc) {
        this.picDesc = picDesc;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }
}

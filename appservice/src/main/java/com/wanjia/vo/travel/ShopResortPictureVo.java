package com.wanjia.vo.travel;

/**店家景区图片
 * Created by blake on 2016/7/19.
 */
public class ShopResortPictureVo {

    private long resortId ;
    private String picUrl ;
    private String picDesc ;
    private String resortName ;



    public long getResortId() {
        return resortId;
    }

    public void setResortId(long resortId) {
        this.resortId = resortId;
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

    public String getResortName() {
        return resortName;
    }

    public void setResortName(String resortName) {
        this.resortName = resortName;
    }
}

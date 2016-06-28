package com.wanjia.vo;

/**
 * Created by blake on 2016/6/21.
 */
public class ShopListBaseVo {

    protected long resortId ;
    protected long shopId ;
    protected String shopName ;
    protected String shopPic ;
    protected int adShop ;
    protected String addressProvince ;
    protected String addressCity ;
    protected String addressDistinct ;
    protected String addressDetail ;
    protected Location location ;
    protected int landmarkId;
    protected long totalCommentNum ;
    protected long goodCommentNum ;
    protected long recommendNum ;
    protected double defaultSort ;

    public long getResortId() {
        return resortId;
    }

    public void setResortId(long resortId) {
        this.resortId = resortId;
    }

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

    public String getShopPic() {
        return shopPic;
    }

    public void setShopPic(String shopPic) {
        this.shopPic = shopPic;
    }

    public int getAdShop() {
        return adShop;
    }

    public void setAdShop(int adShop) {
        this.adShop = adShop;
    }

    public String getAddressProvince() {
        return addressProvince;
    }

    public void setAddressProvince(String addressProvince) {
        this.addressProvince = addressProvince;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressDistinct() {
        return addressDistinct;
    }

    public void setAddressDistinct(String addressDistinct) {
        this.addressDistinct = addressDistinct;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public int getLandmarkId() {
        return landmarkId;
    }

    public void setLandmarkId(int landmarkId) {
        this.landmarkId = landmarkId;
    }

    public long getTotalCommentNum() {
        return totalCommentNum;
    }

    public void setTotalCommentNum(long totalCommentNum) {
        this.totalCommentNum = totalCommentNum;
    }

    public long getGoodCommentNum() {
        return goodCommentNum;
    }

    public void setGoodCommentNum(long goodCommentNum) {
        this.goodCommentNum = goodCommentNum;
    }

    public long getRecommendNum() {
        return recommendNum;
    }

    public void setRecommendNum(long recommendNum) {
        this.recommendNum = recommendNum;
    }

    public double getDefaultSort() {
        return defaultSort;
    }

    public void setDefaultSort(double defaultSort) {
        this.defaultSort = defaultSort;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public class Location{

        private double lon ;
        private double lat ;
        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

    }
}

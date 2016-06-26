package com.wanjia.vo;

/**
 * 景区对应的地标vo
 * Created by blake on 2016/6/24.
 */
public class ResortLandmarkVo {

    private long landmarkId ;
    private long resortId ;
    private String  landmarkName ;
    private double lon ;
    private double lat ;
    private int isValid ;


    public long getLandmarkId() {
        return landmarkId;
    }

    public void setLandmarkId(long landmarkId) {
        this.landmarkId = landmarkId;
    }

    public long getResortId() {
        return resortId;
    }

    public void setResortId(long resortId) {
        this.resortId = resortId;
    }

    public String getLandmarkName() {
        return landmarkName;
    }

    public void setLandmarkName(String landmarkName) {
        this.landmarkName = landmarkName;
    }

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

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }
}

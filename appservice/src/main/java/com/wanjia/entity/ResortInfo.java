package com.wanjia.entity;

public class ResortInfo {
    private Integer resortid;

    private String resortname;

    private String resortpinyin;

    private Double lat;

    private Double lon;

    private Short resortaddressprovince;

    private Short resortaddresscity;

    private String resortaddressdetailinfo;

    private String resortintroduce;

    private String resortnotice;

    private Short hotseason;

    public ResortInfo(Integer resortid, String resortname, String resortpinyin, Double lat, Double lon, Short resortaddressprovince, Short resortaddresscity, String resortaddressdetailinfo, String resortintroduce, String resortnotice, Short hotseason) {
        this.resortid = resortid;
        this.resortname = resortname;
        this.resortpinyin = resortpinyin;
        this.lat = lat;
        this.lon = lon;
        this.resortaddressprovince = resortaddressprovince;
        this.resortaddresscity = resortaddresscity;
        this.resortaddressdetailinfo = resortaddressdetailinfo;
        this.resortintroduce = resortintroduce;
        this.resortnotice = resortnotice;
        this.hotseason = hotseason;
    }

    public ResortInfo() {
        super();
    }

    public Integer getResortid() {
        return resortid;
    }

    public void setResortid(Integer resortid) {
        this.resortid = resortid;
    }

    public String getResortname() {
        return resortname;
    }

    public void setResortname(String resortname) {
        this.resortname = resortname == null ? null : resortname.trim();
    }

    public String getResortpinyin() {
        return resortpinyin;
    }

    public void setResortpinyin(String resortpinyin) {
        this.resortpinyin = resortpinyin == null ? null : resortpinyin.trim();
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Short getResortaddressprovince() {
        return resortaddressprovince;
    }

    public void setResortaddressprovince(Short resortaddressprovince) {
        this.resortaddressprovince = resortaddressprovince;
    }

    public Short getResortaddresscity() {
        return resortaddresscity;
    }

    public void setResortaddresscity(Short resortaddresscity) {
        this.resortaddresscity = resortaddresscity;
    }

    public String getResortaddressdetailinfo() {
        return resortaddressdetailinfo;
    }

    public void setResortaddressdetailinfo(String resortaddressdetailinfo) {
        this.resortaddressdetailinfo = resortaddressdetailinfo == null ? null : resortaddressdetailinfo.trim();
    }

    public String getResortintroduce() {
        return resortintroduce;
    }

    public void setResortintroduce(String resortintroduce) {
        this.resortintroduce = resortintroduce == null ? null : resortintroduce.trim();
    }

    public String getResortnotice() {
        return resortnotice;
    }

    public void setResortnotice(String resortnotice) {
        this.resortnotice = resortnotice == null ? null : resortnotice.trim();
    }

    public Short getHotseason() {
        return hotseason;
    }

    public void setHotseason(Short hotseason) {
        this.hotseason = hotseason;
    }
}
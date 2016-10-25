package com.wanjia.vo.live;

/**
 * Created by blake on 2016/7/18.
 */
public class ShopRoomFacilityVo {

    private long shopId ;
    private long roomId ;
    private String facilityName ;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
}

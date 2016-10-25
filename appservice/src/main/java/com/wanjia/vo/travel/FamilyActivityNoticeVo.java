package com.wanjia.vo.travel;

import java.util.Set;

/**
 * 农家自助游的notice
 * Created by blake on 2016/7/20.
 */
public class FamilyActivityNoticeVo {
    private long shopId ;
    private long activityId ;
    private Set<String> note ;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }


    public Set<String> getNote() {
        return note;
    }

    public void setNote(Set<String> note) {
        this.note = note;
    }
}

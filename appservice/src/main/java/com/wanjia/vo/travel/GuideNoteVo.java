package com.wanjia.vo.travel;

import java.util.Set;

/**
 * Created by blake on 2016/7/20.
 */
public class GuideNoteVo {

    private long shopId;
    private Set<String> note ;
    private long guideId ;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public Set<String> getNote() {
        return note;
    }

    public void setNote(Set<String> note) {
        this.note = note;
    }

    public long getGuideId() {
        return guideId;
    }

    public void setGuideId(long guideId) {
        this.guideId = guideId;
    }
}

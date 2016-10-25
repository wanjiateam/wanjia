package com.wanjia.vo.speciality;

import java.util.List;

/**
 * Created by blake on 2016/7/20.
 */
public class SpecialtyNoteVo {

    private long shopId ;
    private long specialtyId ;
    private String note ;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

package com.wanjia.vo.travel;

/**
 * 店家给门票的添加的备注信息
 * Created by blake on 2016/7/19.
 */
public class ShopTicketNoteVo {

    private long shopId ;
    private long ticketId ;
    private String note ;


    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

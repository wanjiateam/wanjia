package com.wanjia.vo.cart;

/**
 * Created by blake on 2016/8/5.
 */
public class ShopCartTravelTicketVo extends ShopCartBaseVo{

    private long ticketId ;
    private String ticketName ;
    private String ticketType ;
    private String ticketDate ;
    private int bookNumber ;
    private int maxBookNumber ;


    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketDate() {
        return ticketDate;
    }

    public void setTicketDate(String ticketDate) {
        this.ticketDate = ticketDate;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public int getMaxBookNumber() {
        return maxBookNumber;
    }

    public void setMaxBookNumber(int maxBookNumber) {
        this.maxBookNumber = maxBookNumber;
    }
}

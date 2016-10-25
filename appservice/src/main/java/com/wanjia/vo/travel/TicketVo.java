package com.wanjia.vo.travel;

/**
 * Created by blake on 2016/7/17.
 */
public class TicketVo {

    private long ticketId ;
    private long resortId ;
    private String resortName ;
    private String ticketType;
    private String ticketName ;
    private int isCurrentDayValid ;
    private  int isTotalTicket ;
    private long shopId ;
    private  String picUrl ;
    private int maxBookNumber ;
    private double ticketPrice ;
    private String shopName ;



    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public long getResortId() {
        return resortId;
    }

    public void setResortId(long resortId) {
        this.resortId = resortId;
    }

    public String getResortName() {
        return resortName;
    }

    public void setResortName(String resortName) {
        this.resortName = resortName;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public int getIsCurrentDayValid() {
        return isCurrentDayValid;
    }

    public void setIsCurrentDayValid(int isCurrentDayValid) {
        this.isCurrentDayValid = isCurrentDayValid;
    }

    public int getIsTotalTicket() {
        return isTotalTicket;
    }

    public void setIsTotalTicket(int isTotalTicket) {
        this.isTotalTicket = isTotalTicket;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getMaxBookNumber() {
        return maxBookNumber;
    }

    public void setMaxBookNumber(int maxBookNumber) {
        this.maxBookNumber = maxBookNumber;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }


    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }
}

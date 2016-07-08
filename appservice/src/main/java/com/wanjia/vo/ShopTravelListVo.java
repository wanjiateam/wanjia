package com.wanjia.vo;

/**
 * Created by hsb11289 on 2016/6/28.
 */
public class ShopTravelListVo extends ShopListBaseVo implements  Comparable<ShopTravelListVo>{

    private int travelTicketState ;
    private int travelGuideState ;
    private int travelSpecialState ;
    private double travelGuideLowestPrice ;
    private double travelSpecialLowestPrice ;
    private double travelTicketLowestPrice ;
    private String ticketCheapestDate ;

    public int getTravelTicketState() {
        return travelTicketState;
    }

    public void setTravelTicketState(int travelTicketState) {
        this.travelTicketState = travelTicketState;
    }

    public int getTravelGuideState() {
        return travelGuideState;
    }

    public void setTravelGuideState(int travelGuideState) {
        this.travelGuideState = travelGuideState;
    }

    public int getTravelSpecialState() {
        return travelSpecialState;
    }

    public void setTravelSpecialState(int travelSpecialState) {
        this.travelSpecialState = travelSpecialState;
    }

    public double getTravelGuideLowestPrice() {
        return travelGuideLowestPrice;
    }

    public void setTravelGuideLowestPrice(double travelGuideLowestPrice) {
        this.travelGuideLowestPrice = travelGuideLowestPrice;
    }

    public double getTravelSpecialLowestPrice() {
        return travelSpecialLowestPrice;
    }

    public void setTravelSpecialLowestPrice(double travelSpecialLowestPrice) {
        this.travelSpecialLowestPrice = travelSpecialLowestPrice;
    }

    public double getTravelTicketLowestPrice() {
        return travelTicketLowestPrice;
    }

    public void setTravelTicketLowestPrice(double travelTicketLowestPrice) {
        this.travelTicketLowestPrice = travelTicketLowestPrice;
    }

    public String getTicketCheapestDate() {
        return ticketCheapestDate;
    }

    public void setTicketCheapestDate(String ticketCheapestDate) {
        this.ticketCheapestDate = ticketCheapestDate;
    }

    @Override
    public int compareTo(ShopTravelListVo o) {
        double value = o.getTravelTicketLowestPrice() - this.getTravelTicketLowestPrice() ;
        if(value > 0 ){
            return -1 ;
        }else if(value < 0){
            return 1 ;
        }else{
           return 0;
        }
    }
}

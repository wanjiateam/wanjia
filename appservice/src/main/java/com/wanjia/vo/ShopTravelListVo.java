package com.wanjia.vo;

/**
 * Created by hsb11289 on 2016/6/28.
 */
public class ShopTravelListVo extends ShopListBaseVo {

    private int travelTicketState ;
    private int travelGuideState ;
    private int travelSpecialState ;
    private double travelGuideLowesrPrice ;
    private double travelSpecialLowestPrice ;
    private double travelTicketLowestPrice ;

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

    public double getTravelGuideLowesrPrice() {
        return travelGuideLowesrPrice;
    }

    public void setTravelGuideLowesrPrice(double travelGuideLowesrPrice) {
        this.travelGuideLowesrPrice = travelGuideLowesrPrice;
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
}

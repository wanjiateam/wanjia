package com.wanjia.vo.travel;

import java.util.Set;

/**
 * 景区门票的一些提示信息（老年票优惠条件等等）
 * Created by blake on 2016/7/19.
 */
public class ShopTicketNoticeVo {

    private long resortId ;
    private String ticketIntroduce ;
    private Set<String> ticketInfo ;

    public long getResortId() {
        return resortId;
    }

    public void setResortId(long resortId) {
        this.resortId = resortId;
    }

    public String getTicketIntroduce() {
        return ticketIntroduce;
    }

    public void setTicketIntroduce(String ticketIntroduce) {
        this.ticketIntroduce = ticketIntroduce;
    }

    public Set<String> getTicketInfo() {
        return ticketInfo;
    }

    public void setTicketInfo(Set<String> ticketInfo) {
        this.ticketInfo = ticketInfo;
    }
}

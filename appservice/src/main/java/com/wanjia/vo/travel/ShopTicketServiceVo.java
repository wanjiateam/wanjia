package com.wanjia.vo.travel;

import java.util.Set;

/**
 * 门票中包含的服务项目（缆车。。。）
 * Created by blake on 2016/7/19.
 */
public class ShopTicketServiceVo {

    private long resortId ;
    private long ticketId ;
    private Set<String> services ;

    public long getResortId() {
        return resortId;
    }

    public void setResortId(long resortId) {
        this.resortId = resortId;
    }

    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public Set<String> getServices() {
        return services;
    }

    public void setServices(Set<String> services) {
        this.services = services;
    }
}

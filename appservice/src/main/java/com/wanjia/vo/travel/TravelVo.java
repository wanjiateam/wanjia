package com.wanjia.vo.travel;

import java.util.List;

/**
 * Created by blake on 2016/7/17.
 */
public class TravelVo {

    private List<TicketVo> ticketVoList  ;
    private List<FamilyActivityVo> familyActivityVoList ;
    private List<GuideVo> guideVoList  ;

    public List<TicketVo> getTicketVoList() {
        return ticketVoList;
    }

    public void setTicketVoList(List<TicketVo> ticketVoList) {
        this.ticketVoList = ticketVoList;
    }

    public List<FamilyActivityVo> getFamilyActivityVoList() {
        return familyActivityVoList;
    }

    public void setFamilyActivityVoList(List<FamilyActivityVo> familyActivityVoList) {
        this.familyActivityVoList = familyActivityVoList;
    }

    public List<GuideVo> getGuideVoList() {
        return guideVoList;
    }

    public void setGuideVoList(List<GuideVo> guideVoList) {
        this.guideVoList = guideVoList;
    }
}

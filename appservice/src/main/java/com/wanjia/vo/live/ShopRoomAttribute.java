package com.wanjia.vo.live;

import java.util.List;

/**
 * 店家住房的属性 对应店家房屋的详细信息 包括一些基础设施和住店须知
 * Created by blake on 2016/7/18.
 */

public class ShopRoomAttribute {

    private List<ShopRoomFacilityVo> shopRoomFacilityVos ;
    private List<ShopRoomNoticeVo> shopRoomNoticeVos ;

    public List<ShopRoomFacilityVo> getShopRoomFacilityVos() {
        return shopRoomFacilityVos;
    }

    public void setShopRoomFacilityVos(List<ShopRoomFacilityVo> shopRoomFacilityVos) {
        this.shopRoomFacilityVos = shopRoomFacilityVos;
    }

    public List<ShopRoomNoticeVo> getShopRoomNoticeVos() {
        return shopRoomNoticeVos;
    }

    public void setShopRoomNoticeVos(List<ShopRoomNoticeVo> shopRoomNoticeVos) {
        this.shopRoomNoticeVos = shopRoomNoticeVos;
    }
}

package com.wanjia.vo.cart;

import java.util.List;

/**
 * Created by blake on 2016/8/5.
 */
public class ShopCartSpecialtyOverViewVo extends  ShopCartBaseVo {

    private List<ShopCartSpecialtyVo> shopCartSpecialtyVoList ;

    public List<ShopCartSpecialtyVo> getShopCartSpecialtyVoList() {
        return shopCartSpecialtyVoList;
    }

    public void setShopCartSpecialtyVoList(List<ShopCartSpecialtyVo> shopCartSpecialtyVoList) {
        this.shopCartSpecialtyVoList = shopCartSpecialtyVoList;
    }
}

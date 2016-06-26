package com.wanjia.service;

import com.wanjia.vo.ShopProductLogoVo;

import java.util.List;

/**
 * Created by blake on 2016/6/25.
 */
public interface ShopInfoService {

    public List<ShopProductLogoVo> getShopProductLogoByShopId(long shopId, int productType,String indexName,String esType) ;


}

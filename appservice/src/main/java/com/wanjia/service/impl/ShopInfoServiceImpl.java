package com.wanjia.service.impl;

import com.wanjia.service.ShopInfoService;
import com.wanjia.utils.ElasticSearchClient;
import com.wanjia.vo.ShopProductLogoVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by blake on 2016/6/25.
 */
public class ShopInfoServiceImpl implements ShopInfoService{

    @Autowired
    ElasticSearchClient elasticSearchClient ;
    /**
     * 获取店家住食游产展示页头部的logo图片
     * @param shopId
     * @param productType
     * @return
     */
    @Override
    public List<ShopProductLogoVo> getShopProductLogoByShopId(long shopId, int productType,String indexName,String esType) {




        return null;
    }
}

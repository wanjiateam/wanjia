package com.wanjia.service;

import com.wanjia.utils.PageResult;
import com.wanjia.utils.SortField;

import java.util.List;

/**
 * Created by blake on 2016/6/22.
 * 用于展示用户按照地区搜索的店铺结果
 */
public interface ShopListService {

    public PageResult getShopListByResort(String indexName, String type , long resortId, List<SortField> sortFields, int pageSize, int page, int productType) ;
    public PageResult getShopHotelListByResort(String indexName, String type , long resortId, List<SortField> sortFields, int pageSize, int page) ;

    public PageResult getShopListByLandmark(String indexName, String type , long resortId, List<SortField> sortFields, int pageSize, int page,int productType, int landmarkId) ;
    public PageResult getShopListByDistance(String indexName, String type , long resortId, List<SortField> sortFields, int pageSize, int page,int productType,double lon,double lat) ;


}

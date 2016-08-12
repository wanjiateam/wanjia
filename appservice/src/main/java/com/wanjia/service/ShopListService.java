package com.wanjia.service;

import com.wanjia.utils.PageResult;
import com.wanjia.utils.SortField;

import java.util.Date;
import java.util.List;

/**
 * Created by blake on 2016/6/22.
 * 用于展示用户按照地区搜索的店铺结果
 */
public interface ShopListService {

    public PageResult getShopHotelListByResort(String indexName, String type , long resortId, long startDate, long endDate , List<SortField> sortFields, int pageSize, int page,Class clazz ,Integer landmarkId,String shopName) ;
    public PageResult getShopHotelListPriceLowFirstByResort(String indexName, String type , long resortId, long startDate, long endDate , List<SortField> sortFields, int pageSize, int page,Class clazz ,Integer landmarkId,String shopName) ;
    public PageResult getShopTravelListByResort(String indexName, String type , long resortId, long startDate, long endDate , List<SortField> sortFields, int pageSize, int page,Class clazz ,Integer landmarkId,String shopName) ;

    public PageResult getShopListByResort(String indexName, String type , long resortId, long startDate, long endDate , List<SortField> sortFields, int pageSize, int page,Class clazz,Integer landmarkId,String shopName) ;
    public PageResult getShopListByDistance(String indexName, String type , long resortId, List<SortField> sortFields,Class clazz , int pageSize, int page,int productType,double lon,double lat,long startDate,long endDate) ;


}

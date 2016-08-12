package com.wanjia.controller;

import com.wanjia.service.ShopListService;
import com.wanjia.utils.JsonReturnBody;
import com.wanjia.utils.JsonUtil;
import com.wanjia.utils.PageResult;
import com.wanjia.utils.SortField;
import com.wanjia.vo.*;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by blake on 2016/6/22.
 */
@Controller
@RequestMapping("/shopList")
public class ShopListController {


    @Autowired
    ShopListService shopListService ;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ShopListController.class) ;
    /**
     * 获取住的店家列表
     * @param resortId 景区的id
     * @param startDate 住店的开始日期
     * @param endDate 住店的结束日期
     * @param sort 排序方式 根据不同的产品，排序的编码不同，1 推荐优先 2 好评 3 价格
     * @param page 当前的页数
     * @param pageSize 每页显 示多少条数据
     * @param  sortOrder  排序方式 1 desc 0 asc
     * @return
     */
    @RequestMapping(value = "listHotel", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopHotelListPaging(long resortId, String startDate, String endDate , int sort, int sortOrder, int page, int pageSize,Integer landmarkId,String shopName){

         String indexName = "shop_hotel" ;
         String type = "hotel" ;
        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        jsonReturnBody.setType("getShopHotelListPaging");

        long startDateNumber  =  parseDateToLongValue(startDate,jsonReturnBody);
        long endDateNumber =  parseDateToLongValue(endDate,jsonReturnBody) ;

        if(jsonReturnBody.getMessage() != null){
            return JsonUtil.toJsonString(jsonReturnBody);
        }

        String sortField = null ;

        List<SortField> sortFields = new ArrayList<SortField>();
        if(sort != 3){
            switch (sort) {
                case 1 : sortField = "recommendNum"; break; //推荐优先
                case 2 : sortField = "goodCommentNum"; break; //好评优先
            }
        }else{
            sortField = "roomPrice" ;
        }

        generateSortField(sortField,sortOrder,sortFields);

        PageResult pageResult = null ;
        if(sort != 3){
            sortFields.add(new SortField("defaultSort",SortOrder.DESC)) ;
            pageResult = shopListService.getShopHotelListByResort(indexName,type,resortId,startDateNumber,endDateNumber,sortFields,pageSize,page, ShopHotelListVo.class,landmarkId,shopName) ;
        }else{
            //按照店家的房价最低价
            indexName = "shop_hotel_lowestprice";
            type = "price";
            pageResult = shopListService.getShopHotelListPriceLowFirstByResort(indexName,type,resortId,startDateNumber,endDateNumber,sortFields,pageSize,page,HotelPriceVo.class ,landmarkId,shopName) ;
        }

        parsePageResult(pageResult,jsonReturnBody);

        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     * 获取提供餐饮服务的店家
     * @param resortId
     * @param startDate
     * @param endDate
     * @param sort
     * @param sortOrder
     * @param page
     * @param pageSize
     * @param landmarkId
     * @param shopName
     * @return
     */
    @RequestMapping(value = "listRestaurant", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopRestaurantListPaging(long resortId, String startDate, String endDate , int sort, int sortOrder, int page, int pageSize,Integer landmarkId,String shopName){

        String indexName = "shop_restaurant" ;
        String type = "restaurant" ;
        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        jsonReturnBody.setType("getShopRestaurantListPaging");

        long startDateNumber =  parseDateToLongValue(startDate,jsonReturnBody);
        long endDateNumber =  parseDateToLongValue(endDate,jsonReturnBody) ;

        if(jsonReturnBody.getMessage() != null){
            return JsonUtil.toJsonString(jsonReturnBody);
        }

        String sortField = null ;

        List<SortField> sortFields = new ArrayList<SortField>();
        switch (sort) {
            case 1 : sortField = "recommendNum"; break; //推荐优先
            case 2 : sortField = "goodCommentNum"; break; //好评优先
            case 3 : sortField = "foodAveragePrice"; break;
        }

       generateSortField(sortField,sortOrder,sortFields);

        sortFields.add(new SortField("defaultSort",SortOrder.DESC)) ;
        PageResult pageResult = shopListService.getShopListByResort(indexName,type,resortId,startDateNumber,endDateNumber,sortFields,pageSize,page,ShopRestaurantListVo.class,landmarkId,shopName) ;
        parsePageResult(pageResult,jsonReturnBody);

        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     * 获取提供特产服务店家列表
     * @param resortId
     * @param startDate
     * @param endDate
     * @param sort
     * @param sortOrder
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "listSpecialty", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopSpecialityListPaging(long resortId, String startDate, String endDate , int sort, int sortOrder, int page, int pageSize,Integer landmarkId,String shopName){

        String indexName = "shop_specialty" ;
        String type = "specialty" ;
        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        jsonReturnBody.setType("getShopSpecialityListPaging");

        long startDateNumber =  parseDateToLongValue(startDate,jsonReturnBody);
        long endDateNumber =  parseDateToLongValue(endDate,jsonReturnBody) ;

        if(jsonReturnBody.getMessage() != null){
            return JsonUtil.toJsonString(jsonReturnBody);
        }

        String sortField = null ;

        List<SortField> sortFields = new ArrayList<SortField>();
        switch (sort) {
            case 1 : sortField = "recommendNum"; break; //推荐优先
            case 2 : sortField = "goodCommentNum"; break; //好评优先
            case 3 : sortField = "specialtyLowestPrice"; break;
        }

        generateSortField(sortField,sortOrder,sortFields);

        sortFields.add(new SortField("defaultSort",SortOrder.DESC)) ;
        PageResult pageResult = shopListService.getShopListByResort(indexName,type,resortId,startDateNumber,endDateNumber,sortFields,pageSize,page, ShopSpecialtyListVo.class,landmarkId,shopName) ;

        parsePageResult(pageResult,jsonReturnBody);


        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     * 获取提供游玩服务店家列表(对于安装门票低价优先处理逻辑是 对于一级界面存放的都是成人票的普通价格票)
     * @param resortId
     * @param startDate
     * @param endDate
     * @param sort
     * @param sortOrder
     * @param page
     * @param pageSize
     * @param  shopName  店家的名称
     * @return
     */
    @RequestMapping(value = "listTravel", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelListPaging(long resortId, String startDate, String endDate , int sort, int sortOrder, int page, int pageSize,Integer landmarkId,String shopName){

        String indexName = "shop_travel" ;
        String type = "travel" ;
        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        jsonReturnBody.setType("getShopTravelListPaging");

        long startDateNumber =  parseDateToLongValue(startDate,jsonReturnBody);
        long endDateNumber =  parseDateToLongValue(endDate,jsonReturnBody) ;

        if(jsonReturnBody.getMessage() != null){
            return JsonUtil.toJsonString(jsonReturnBody);
        }

        String sortField = null ;
        String travelStateField = null;
        List<SortField> sortFields = new ArrayList<SortField>();
        switch (sort) {
            case 1 : sortField = "recommendNum"; break; //推荐优先
            case 2 : sortField = "goodCommentNum"; break; //好评优先
            case 3 : sortField = "travelTicketLowestPrice"; travelStateField= "travelTicketState";  break; //门票低价优先
            case 4 : sortField = "travelGuideLowestPrice"; travelStateField= "travelGuideState";break;//导游低价优先
            case 5 : sortField = "travelSpecialLowestPrice"; travelStateField= "travelSpecialState"; break;//农家特色游低价优先

        }
        if(travelStateField != null){
            SortField sf = new SortField(travelStateField) ;
            sf.setSortOrder(SortOrder.DESC);
            sortFields.add(sf) ;
        }

        generateSortField(sortField,sortOrder,sortFields);


        PageResult pageResult = null ;
        sortFields.add(new SortField("defaultSort",SortOrder.DESC)) ;
        pageResult = shopListService.getShopTravelListByResort(indexName,type,resortId,startDateNumber,endDateNumber,sortFields,pageSize,page, ShopTravelListVo.class,landmarkId,shopName) ;


        parsePageResult(pageResult,jsonReturnBody);


        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     *根据经纬度查询用户的附近的店家
     * @param resortId
     * @param productType 1住2食3游4特产
     * @param page
     * @param pageSize
     * @param lon
     * @param lat
     * @return
     */
    @RequestMapping(value = "listByDistance", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopListPagingByDistance(long resortId,int productType,int page, int pageSize,double lon, double lat,String startDate,String endDate){

        String indexName = "" ;
        String type = "" ;
        Class clazz = null ;

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        jsonReturnBody.setType("getShopListPagingByDistance");

        long startDateNumber =  parseDateToLongValue(startDate,jsonReturnBody);
        long endDateNumber =  parseDateToLongValue(endDate,jsonReturnBody) ;

        switch(productType){
            case 1 : indexName="shop_hotel";type = "hotel" ;clazz = ShopHotelListVo.class ;break;
            case 2 : indexName="shop_restaurant";type = "restaurant" ; clazz = ShopRestaurantListVo.class; break;
            case 3 : indexName="shop_travel";type = "travel" ; clazz = ShopTravelListVo.class ; break;
            case 4 : indexName="shop_specialty";type = "specialty" ; clazz = ShopSpecialtyListVo.class ;break;
        }



        List<SortField> sortFields = new ArrayList<SortField>();
        sortFields.add(new SortField("defaultSort",SortOrder.DESC)) ;

        PageResult pageResult = shopListService.getShopListByDistance(indexName,type,resortId,sortFields,clazz ,pageSize,page,productType,lon,lat,startDateNumber,endDateNumber);
        parsePageResult(pageResult,jsonReturnBody);

        return JsonUtil.toJsonString(jsonReturnBody);
    }


    //包装排序字段类
    private void generateSortField(String sortField,int sortOrder ,List<SortField> sortFields){

        if(sortField  != null){
            SortField sf = new SortField(sortField) ;
            if(sortOrder==1){
                sf.setSortOrder(SortOrder.DESC);
            }else {
                sf.setSortOrder(SortOrder.ASC);
            }
            sortFields.add(sf) ;
        }
    }

    //解析日期字符串 为时间戳
    private long parseDateToLongValue(String date,JsonReturnBody jsonReturnBody) {

        long value = 0;
        try {
            value = sdf.parse(date).getTime();
        } catch (ParseException e) {
            logger.error("paramter error",e);
            jsonReturnBody.setCode(-1);
            jsonReturnBody.setMessage("parameter type error");
        }
        return  value;
    }

    //解析pageResult code=0 查询es异常 code=1 正常返回 code=2 结果为空
    private void parsePageResult(PageResult pageResult ,JsonReturnBody jsonReturnBody){

        if(pageResult.getE() != null){
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("query es error:"+pageResult.getE().getStackTrace());
        }else {
            List results = (List) pageResult.getResult() ;
            if(results.size() == 0){
                jsonReturnBody.setCode(2);
                jsonReturnBody.setMessage("get a empty result in es");
            }else{
                jsonReturnBody.setCode(1);
                jsonReturnBody.setMessage(pageResult);
            }
        }

    }

}

package com.wanjia.controller;

import com.wanjia.service.ShopListService;
import com.wanjia.utils.JsonReturnBody;
import com.wanjia.utils.JsonUtil;
import com.wanjia.utils.PageResult;
import com.wanjia.utils.SortField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by blake on 2016/6/22.
 */
@Controller
@RequestMapping("/shopList")
public class ShopListController {

    private String indexName = "shoplist" ;
    private String type = "shopinfo" ;
    @Autowired
    ShopListService shopListService ;


    /**
     *
     * @param resortId 景区的id
     * @param productType 产品类型 1住2食3门票4导游5农家自有项目6特产
     * @param sort 排序方式 根据不同的产品，排序的编码不同
     * @param page 当前的页数
     * @param pageSize 每页显 示多少条数据
     * @param  sortOrder  排序方式 1 desc 0 asc
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopListPaging(long resortId,int productType,int sort,int sortOrder,int page, int pageSize){

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        jsonReturnBody.setType("getShopListPaging");

        String sortField = null ;
        switch (productType){
            //live
            case 1 : switch (sort){
                case 1 : sortField = "l_recommendNum"; break; //推荐优先
                case 2 : sortField = "l_goodCommentNum"; break; //好评优先
                case 3 : sortField = "liveLowestPrice"; break; //低价优先

            } break ;
                //food
            case 2 : switch (sort){
                case 1 : sortField = "f_recommendNum"; break;//推荐优先
                case 2 : sortField = "f_goodCommentNum"; break;//好评优先
                case 3 : sortField = "foodAveragePrice"; break; //按照平均菜价从高到低
            }break;
                //travel
            case 3 :
            case 4 :
            case 5 : switch (sort){
                case 1 : sortField = "t_recommendNum"; break;//推荐优先
                case 2 : sortField = "t_goodCommentNum"; break;//好评优先
                case 3 : sortField = "travelTicketLowestPrice"; break; //门票低价优先
                case 4 : sortField = "travelGuideLowesrPrice"; break; //导游低价优先
                case 5 : sortField = "travelSpecialLowestPrice"; break;// 农家特色游低价优先
            } break ;
                //special food
            case 6 : switch (sort){
                case 1 : sortField = "s_recommendNum"; break;
                case 2 : sortField = "s_goodCommentNum"; break;
                case 3 : sortField = "specialFoodLowestPrice"; break;
            } break ;
        }

        List<SortField> sortFields = new ArrayList<SortField>();
        if(sortField  != null){
            SortField sf = new SortField(sortField) ;
            if(sortOrder==1){
                sf.setSortOrder(SortOrder.DESC);
            }else {
                sf.setSortOrder(SortOrder.ASC);
            }
            sortFields.add(sf) ;
        }
        sortFields.add(new SortField("defaultSort",SortOrder.DESC)) ;

       PageResult pageResult =  shopListService.getShopListByResort(indexName,type,resortId,sortFields,pageSize,page,productType);
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

        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     * 获取住的店家列表
     * @param resortId 景区的id
     * @param startDate 住店的开始日期
     * @param endDate 住店的结束日期
     * @param sort 排序方式 根据不同的产品，排序的编码不同
     * @param page 当前的页数
     * @param pageSize 每页显 示多少条数据
     * @param  sortOrder  排序方式 1 desc 0 asc
     * @return
     */
    @RequestMapping(value = "listHotel", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopHotelListPaging(long resortId, Date startDate, Date endDate , int sort, int sortOrder, int page, int pageSize){

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        jsonReturnBody.setType("getShopHotelListPaging");

        String sortField = null ;

            switch (sort) {
                case 1 : sortField = "recommendNum"; break; //推荐优先
                case 2 : sortField = "goodCommentNum"; break; //好评优先
                case 3 : sortField = "cheapestPrice"; break; //好评优先
            }

        List<SortField> sortFields = new ArrayList<SortField>();
        if(sortField  != null){
            SortField sf = new SortField(sortField) ;
            if(sortOrder==1){
                sf.setSortOrder(SortOrder.DESC);
            }else {
                sf.setSortOrder(SortOrder.ASC);
            }
            sortFields.add(sf) ;
        }
        sortFields.add(new SortField("defaultSort",SortOrder.DESC)) ;

        PageResult pageResult =  shopListService.getShopListByResort(indexName,type,resortId,sortFields,pageSize,page,productType);
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

        return JsonUtil.toJsonString(jsonReturnBody);
    }







    /**
     * 按照地标展示店家列表
     * @param resortId
     * @param productType
     * @param landmarkId 地标id
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "listBylandmark", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopListPagingByLandmark(long resortId,int productType,int landmarkId,int page, int pageSize){


        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        jsonReturnBody.setType("getShopListPagingByLandmark");

        List<SortField> sortFields = new ArrayList<SortField>();
        sortFields.add(new SortField("defaultSort",SortOrder.DESC)) ;
        PageResult pageResult = shopListService.getShopListByLandmark(indexName,type,resortId,sortFields,pageSize,page,productType,landmarkId);
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
        return JsonUtil.toJsonString(jsonReturnBody);
    }



    @RequestMapping(value = "listByDistance", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopListPagingByDistance(long resortId,int productType,int page, int pageSize,double lon, double lat){

        JsonReturnBody jsonReturnBody = new JsonReturnBody() ;
        jsonReturnBody.setType("getShopListPagingByDistance");

        List<SortField> sortFields = new ArrayList<SortField>();
        sortFields.add(new SortField("defaultSort",SortOrder.DESC)) ;
        PageResult pageResult = shopListService.getShopListByDistance(indexName,type,resortId,sortFields,pageSize,page,productType,lon,lat);
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
        return JsonUtil.toJsonString(jsonReturnBody);
    }



}

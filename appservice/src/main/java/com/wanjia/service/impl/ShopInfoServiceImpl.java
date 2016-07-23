package com.wanjia.service.impl;

import com.wanjia.service.ShopInfoService;
import com.wanjia.utils.ElasticSearchClient;
import com.wanjia.utils.JsonUtil;
import com.wanjia.utils.RedisClient;
import com.wanjia.utils.SortField;
import com.wanjia.vo.HotelPriceVo;
import com.wanjia.vo.ShopProductLogoVo;
import com.wanjia.vo.live.*;
import com.wanjia.vo.restaurant.CourseBookVo;
import com.wanjia.vo.restaurant.CourseVo;
import com.wanjia.vo.restaurant.ShopCourseDetailInfoVo;
import com.wanjia.vo.restaurant.ShopCoursePictureVo;
import com.wanjia.vo.speciality.SpecialityPictureVo;
import com.wanjia.vo.speciality.SpecialtyNoteVo;
import com.wanjia.vo.speciality.SpecialtyVo;
import com.wanjia.vo.travel.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by blake on 2016/6/25.
 */
@Service("shopInfoService")
public class ShopInfoServiceImpl implements ShopInfoService{

    @Autowired
    ElasticSearchClient elasticSearchClient ;

    @Autowired
    RedisClient redisClient ;
    /**
     * 获取店家住食游产展示页头部的logo图片
     * @param shopId
     * @param productType
     * @return
     */
    @Override
    public List<ShopProductLogoVo> getShopProductLogoByShopId(long shopId, int productType,String indexName,String esType) throws Exception{

        List<SortField> sortFields = new ArrayList<SortField>();
        sortFields.add(new SortField("sort", SortOrder.DESC)) ;
        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId) ;
        QueryBuilder postFilter = QueryBuilders.termQuery("picType",productType) ;
        List<ShopProductLogoVo> logoList = (List<ShopProductLogoVo>)elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,postFilter,indexName,esType,ShopProductLogoVo.class ,sortFields) ;


        return logoList;
    }

    /**
     *获得每个店家的住房信息的二级列表（在预定时间范围内的房价的平均价格，房间的最大可预订数）
     * @param shopId
     * @param startDate
     * @param endDate
     * @param indexName
     * @param esType
     * @return
     * @throws Exception
     */
    @Override
    public List<RoomVo> getShopRoomVoByShopId(long shopId, long startDate, long endDate, String indexName, String esType) throws Exception {
        long gap = (endDate -startDate)/(24 * 60 * 60 * 1000) ;
        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId) ;
        //获得店家的所有房型
        List<RoomVo> roomVos = (List<RoomVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,indexName,esType,RoomVo.class) ;

        if(roomVos != null && roomVos.size() > 0){
            //获得店家所有房间的指定预订日期的价格
            for(RoomVo roomVo : roomVos){
               long roomId =  roomVo.getRoomId();
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery() ;
                boolQueryBuilder.must(QueryBuilders.termQuery("shopId",shopId)).must(QueryBuilders.termQuery("roomId",roomId));
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("priceDateLongValue").gte(startDate).lt(endDate);
                //获得指定房间的指定日期的所有价格
                List<HotelPriceVo> hotelPriceVos = elasticSearchClient.queryDataFromEsWithoutPaging(boolQueryBuilder,rangeQueryBuilder,"shop_hotel_price","price", HotelPriceVo.class);
                if(hotelPriceVos.size() >0){
                    long totalPrice = 0 ;
                    for(HotelPriceVo hotelPriceVo : hotelPriceVos){
                        totalPrice += hotelPriceVo.getRoomPrice() ;
                    }
                    roomVo.setPrice((int)Math.ceil(totalPrice/gap));
                }

                //获得店家指定房型房间的总数
                int roomCapacity = roomVo.getRoomCapacity() ;

                //获得指定房间的预定情况
                List<RoomBookVo>  roomBookVos = elasticSearchClient.queryDataFromEsWithoutPaging(boolQueryBuilder,rangeQueryBuilder,"shop_room_book","book", RoomBookVo.class);
                //记录最大可以预定的房间数（部分房间可能已经被预定，同一房型房间数有限）
                int allowBookNumber = 0 ;
                if(roomBookVos.size() >0){
                    roomVo.setRoomBookVoList(roomBookVos);
                    for(RoomBookVo roomBookVo : roomBookVos){
                        int bookNumber = roomBookVo.getBookRoomNumber() ;
                        if(bookNumber >= roomCapacity){
                            roomVo.setAllowBookNumber(0);
                            break ;
                        }else{
                            int tmpAllowBookNumber =  roomCapacity - bookNumber ;
                            if(tmpAllowBookNumber < allowBookNumber || allowBookNumber == 0){
                                allowBookNumber = tmpAllowBookNumber ;
                            }
                        }
                    }
                }
                if(allowBookNumber == 0){
                    allowBookNumber = roomCapacity ;
                }
                roomVo.setAllowBookNumber(allowBookNumber);
            }
        }
        return roomVos;
    }

    /**
     * 获得店家的具体房型对应的属性信息和住房注意事项  三级界面
     * @param shopId
     * @param roomId
     * @return
     * @throws Exception
     */
    @Override
    public ShopRoomAttribute getShopRoomDetailInfo(long shopId, long roomId) throws Exception {

        ShopRoomAttribute shopRoomAttribute  = new ShopRoomAttribute() ;
        //获得店房间的属性信息
        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId);
        QueryBuilder postFilter = QueryBuilders.termQuery("roomId",roomId) ;
        String facilityIndexName = "shop_room_faciality" ;
        String facilityType = "facility" ;
        List<ShopRoomFacilityVo> shopRoomFacilityVos = (List<ShopRoomFacilityVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,postFilter,facilityIndexName,facilityType,ShopRoomFacilityVo.class) ;
        shopRoomAttribute.setShopRoomFacilityVos(shopRoomFacilityVos);

        //获得房间的备注信息
        String noticeIndexName = "shop_room_notice" ;
        String noticeType = "notice" ;
        List<ShopRoomNoticeVo> shopRoomNoticeVos = (List<ShopRoomNoticeVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,noticeIndexName,noticeType,ShopRoomNoticeVo.class) ;
        shopRoomAttribute.setShopRoomNoticeVos(shopRoomNoticeVos);

        return shopRoomAttribute;
    }


    /**
     * 获取店家具体房型对应的图片信息
     * @param shopId
     * @param roomId
     * @return
     * @throws Exception
     */
    @Override
    public List<RoomPictureVo> getShopRoomPictures(long shopId, long roomId) throws Exception {

        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId);
        QueryBuilder postFilter = QueryBuilders.termQuery("roomId",roomId) ;
        String indexName = "shop_room_picture" ;
        String indexType = "picture" ;
        List<RoomPictureVo> roomPictureVos = (List<RoomPictureVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,postFilter,indexName,indexType,RoomPictureVo.class) ;

        return roomPictureVos;
    }


    /**
     * 获得店家的菜品信息 二级界面
     * @param shopId
     * @param bookDate
     * @param indexName
     * @param esType
     * @return
     * @throws Exception
     */
    @Override
    public List<CourseVo> getShopCourseVoByShopId(long shopId, long bookDate,String indexName, String esType) throws Exception {

        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId) ;
        //获得店家所有的菜品信息
        List<CourseVo> courseVos = (List<CourseVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,indexName,esType,CourseVo.class) ;

        if(courseVos != null && courseVos.size() > 0){

            for(CourseVo courseVo : courseVos){
                long courseId = courseVo.getCourseId() ;
                int courseNum = courseVo.getCourseNumber() ;
                //如果店家这个菜品的每天售卖的限制数量是-1表示没有限制
                if(courseNum == -1){
                    courseVo.setAllowBookNumber(-1);
                    continue;
                }

                //获得店家某一个菜品在预定日期的售卖情况，用来查看是不是已经售罄或者最大可售卖的数量
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery() ;
                boolQueryBuilder.must(QueryBuilders.termQuery("shopId",shopId))
                        .must(QueryBuilders.termQuery("courseId",courseId)).must(QueryBuilders.termQuery("buyDateLongValue",bookDate));
                List<CourseBookVo> courseBookVos = elasticSearchClient.queryDataFromEsWithoutPaging(boolQueryBuilder,null,"shop_course_book","book", CourseBookVo.class);
                if(courseBookVos.size() > 0){
                    int num  = courseBookVos.get(0).getNumber() ;
                    //获得剩余的最大可预定的数量
                    courseVo.setAllowBookNumber(courseNum - num);
                }

            }
        }
        return courseVos;
    }

    /**
     * 获得店家一道菜的集体信息 菜的三级界面
     * @param shopId
     * @param courseId
     * @return
     * @throws Exception
     */
    @Override
    public List<ShopCourseDetailInfoVo> getShopCourseDetailInfo(long shopId, long courseId) throws Exception {

        String indexName = "shop_course_detailinfo" ;
        String indexType = "course";
        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId) ;
        QueryBuilder postFilter = QueryBuilders.termQuery("courseId",courseId) ;

        List<ShopCourseDetailInfoVo> shopCourseDetailInfoVos = (List<ShopCourseDetailInfoVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,postFilter,indexName,indexType,ShopCourseDetailInfoVo.class) ;

        return shopCourseDetailInfoVos;
    }

    /**
     * 获得店家特定菜品的图片 三级界面
     * @param shopId
     * @param courseId
     * @return
     * @throws Exception
     */
    @Override
    public List<ShopCoursePictureVo> getShopCoursePicture(long shopId, long courseId) throws Exception {

        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId);
        QueryBuilder postFilter = QueryBuilders.termQuery("courseId",courseId);
        String  indexName = "shop_course_picture" ;
        String indexType = "picture" ;
        List<ShopCoursePictureVo> shopCoursePictureVos = (List<ShopCoursePictureVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,postFilter,indexName,indexType,ShopCoursePictureVo.class) ;
        return shopCoursePictureVos;
    }


    /**
     * 获得店家的特产信息(特产有总数量的限制) 二级界面
     * @param shopId
     * @param indexName
     * @param esType
     * @return
     * @throws Exception
     */
    @Override
    public List<SpecialtyVo> getShopSpecialtyVoByShopId(long shopId, String indexName, String esType) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId) ;
        List<SpecialtyVo> specialtyVos = (List<SpecialtyVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,indexName,esType,SpecialtyVo.class) ;
        return specialtyVos ;
    }


    /**
     * 获得店家特产的图片 三级界面
     * @param shopId
     * @param specialtyId
     * @return
     */
    @Override
    public List<SpecialityPictureVo> getShopSpecialtyPicture(long shopId, long specialtyId) throws Exception{

        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId) ;
        QueryBuilder postFilter = QueryBuilders.termQuery("specialtyId",specialtyId);

        String indexName = "shop_specialty_picture" ;
        String indexType = "picture" ;
        List<SpecialityPictureVo> specialityPictureVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,postFilter,indexName,indexType,SpecialityPictureVo.class) ;

        return specialityPictureVos;
    }

    /**
     * 获得店家特产的备注 三级界面
     * @param shopId
     * @param specialtyId
     * @return
     */
    @Override
    public SpecialtyNoteVo getShopSpecialtyNote(long shopId, long specialtyId) throws Exception{

        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId);
        QueryBuilder postFilter = QueryBuilders.termQuery("specialtyId",specialtyId);
        String indexName = "shop_specialty_note";
        String indexType = "note";

        List<SpecialtyNoteVo> specialtyNoteVos = (List<SpecialtyNoteVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,postFilter,indexName,indexType,SpecialtyNoteVo.class) ;

        return specialtyNoteVos.get(0);
    }


    /**
     *获得店家的游的信息（包括 门票，导游，特色游的二级界面）
     * @param shopId
     * @return
     * @throws Exception
     */
    @Override
    public TravelVo getShopTravelVoByShopId(long shopId,long startDate,long endDate) throws Exception {
        //获得店家门票的所有列表 门票没有每天总数量的限制，但是每单最大可预的门票数有限制
        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId) ;
        TravelVo travelVo = new TravelVo() ;
        String ticketIndexName = "shop_travel_ticket" ;
        String ticketType = "ticket";
        List<TicketVo> TicketVos = (List<TicketVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,ticketIndexName,ticketType,TicketVo.class) ;
        travelVo.setTicketVoList(TicketVos);
        //获得店家导游服务的列表，每天有数量的限制（比如每天能提供五个导游的服务）（用户每单每天只能预定一个导游）
        String guideIndexName = "shop_travel_guide" ;
        String guideType = "guide";
        List<GuideVo> guideVos = (List<GuideVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,guideIndexName,guideType,GuideVo.class) ;
        if(guideVos.size() > 0 ){
            String guideBookIndexName = "shop_travel_guide_book" ;
            String guideBookIndexType = "book" ;
            GuideVo guideVo = guideVos.get(0);
            long guideId  = guideVo.getGuideId() ;

            int guideCapacity = guideVo.getGuideCapacity() ;
            if(guideCapacity == -1){
                //表示预定数量没有限制
                guideVo.setAllowBookNumber(-1);
            }else{
                //根据预定日期获得最大的可预定数（获得每天预定数与最大预定数的差，然后取其中的最小值）
                QueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("shopId",shopId)).must(QueryBuilders.termQuery("guideId",guideId))
                        .must(QueryBuilders.rangeQuery("bookDateLongValue").gte(startDate).lt(endDate)) ;
                List<GuideBookVo> guideBookVos = (List<GuideBookVo> )elasticSearchClient.queryDataFromEsWithoutPaging(boolQuery,null,guideBookIndexName,
                        guideBookIndexType,GuideBookVo.class) ;
                if(guideBookVos.size() > 0){
                    int allowBookNumber = 0 ;
                    for(GuideBookVo guideBookVo : guideBookVos){
                             int tmpAllowCapacity = guideCapacity - guideBookVo.getBookRoomNumber() ;
                             if(tmpAllowCapacity < allowBookNumber || allowBookNumber == 0){
                                 allowBookNumber = tmpAllowCapacity ;
                             }
                    }
                    guideVo.setAllowBookNumber(allowBookNumber);
                }
            }

        }
        travelVo.setGuideVoList(guideVos);

        // 获得店家特色游的列表 没有总数的限制，但是每单最大数量有限制
        String familyActivityIndexName = "shop_travel_familyactivity" ;
        String familyActivityType = "familyactivity";
        List<FamilyActivityVo> familyActivityVos = (List<FamilyActivityVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,familyActivityIndexName,familyActivityType,FamilyActivityVo.class) ;
        travelVo.setFamilyActivityVoList(familyActivityVos);

        return travelVo;
    }



    /**
     * 获得景区店家的图片列表 三级界面
     * @param resortId
     * @return
     * @throws Exception
     */
    @Override
    public List<ShopResortPictureVo> getShopResortPicture(long resortId) throws Exception {

        List<ShopResortPictureVo> shopResortPictureVos = new ArrayList<ShopResortPictureVo>();
        String key = "shop_resort_picture_"+resortId ;
        Set<String> values = redisClient.getSortedSet(key);
        if(values.size() > 0 ){
            for (String value : values){
                shopResortPictureVos.add((ShopResortPictureVo) JsonUtil.toObject(value,ShopResortPictureVo.class));
            }
        }
        return shopResortPictureVos;
    }

    /**
     * 获得景区门票的提示信息 三级节目
     * @param resortId
     * @return
     */
    @Override
    public ShopTicketNoticeVo getShopTicketNoticeByResortId(long resortId) throws Exception{

        ShopTicketNoticeVo shopTicketNoticeVo = null ;

        String key = "notice_"+resortId;
        String value = redisClient.getValueByKey(key) ;

        if(value != null ){
            shopTicketNoticeVo = (ShopTicketNoticeVo) JsonUtil.toObject(value,ShopTicketNoticeVo.class);
        }
        return shopTicketNoticeVo;
    }

    /**
     * 获得店家对具体门票的备注信息
     * @param shopId
     * @param ticketId
     * @return
     */
    @Override
    public ShopTicketNoteVo getShopTicketNote(long shopId, long ticketId) throws Exception{

        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId);
        QueryBuilder postFilter = QueryBuilders.termQuery("ticketId",ticketId);
        String  indexName = "shop_ticket_note" ;
        String indexType = "note" ;
        List<ShopTicketNoteVo> shopCoursePictureVos = (List<ShopTicketNoteVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,postFilter,indexName,indexType,ShopTicketNoteVo.class) ;

        return shopCoursePictureVos.get(0);
    }

    /**
     * 获得门票的包含的服务信息
     * @param resortId
     * @param ticketId
     * @return
     */
    @Override
    public Set<String> getShopTicketService(long resortId, long ticketId) throws Exception{

        String key = "ticketservice_"+resortId+"_"+ticketId ;
        Set<String> values = redisClient.getSortedSet(key);
        return values ;
    }

    /**
     * 获得具体农家自助游项目的图片,图片信息放在redis中  三级界面
     * @param shopId
     * @param activityId
     * @return
     * @throws Exception
     */
    @Override
    public List<FamilyActivityPictureVo> getShopFamilyActivityPicture(long shopId, long activityId) throws Exception {

        List<FamilyActivityPictureVo> familyActivityPictureVos = new ArrayList<FamilyActivityPictureVo>() ;

        String key = "shop_familityactivity_"+shopId+"_"+activityId ;
        Set<String> values = redisClient.getSortedSet(key);
        if(values.size() > 0 ){
            for (String value : values){
                familyActivityPictureVos.add((FamilyActivityPictureVo) JsonUtil.toObject(value,FamilyActivityPictureVo.class));
            }
        }
        return familyActivityPictureVos;
    }

    /**
     * 获得农家自助游项目的提示信息 三级界面
     * @param shopId
     * @param activityId
     * @return
     * @throws Exception
     */
    @Override
    public FamilyActivityNoticeVo getShopFamilyActivityNotice(long shopId, long activityId) throws Exception {

        FamilyActivityNoticeVo familyActivityNoticeVo = new FamilyActivityNoticeVo();
        String key = "familyActivity_"+shopId+"_"+activityId ;
        String value = redisClient.getValueByKey(key);
        familyActivityNoticeVo.setNote(value);
        familyActivityNoticeVo.setShopId(shopId);
        familyActivityNoticeVo.setActivityId(activityId);

        return familyActivityNoticeVo;
    }



    /**
     * 获得具体农家自助游项目的图片,图片信息放在redis中  三级界面
     * @param shopId
     * @param activityId
     * @return
     * @throws Exception
     */
    @Override
    public List<GuidePictureVo> getShopGuidePicture(long shopId, long guideId) throws Exception {

        List<GuidePictureVo> guidePictureVos = new ArrayList<GuidePictureVo>() ;

        String key = "shop_guide_"+shopId+"_"+guideId ;
        Set<String> values = redisClient.getSortedSet(key);
        if(values.size() > 0 ){
            for (String value : values){
                guidePictureVos.add((GuidePictureVo) JsonUtil.toObject(value,GuidePictureVo.class));
            }
        }
        return guidePictureVos;
    }

    /**
     * 获得农家自助游项目的提示信息 三级界面
     * @param shopId
     * @param activityId
     * @return
     * @throws Exception
     */
    @Override
    public GuideNoteVo getShopGuideNotice(long shopId, long guideId) throws Exception {

        GuideNoteVo guideNoteVo = new GuideNoteVo();
        String key = "guide_"+shopId+"_"+guideId ;
        String value = redisClient.getValueByKey(key);
        guideNoteVo.setNote(value);
        guideNoteVo.setShopId(shopId);
        guideNoteVo.setGuideId(guideId);
        return guideNoteVo;
    }


}

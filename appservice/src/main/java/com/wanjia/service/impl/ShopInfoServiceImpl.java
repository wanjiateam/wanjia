package com.wanjia.service.impl;

import com.wanjia.service.ShopInfoService;
import com.wanjia.utils.*;
import com.wanjia.vo.HotelPriceVo;
import com.wanjia.vo.ShopProductLogoVo;
import com.wanjia.vo.ShopRecommendAndCommentNumberVo;
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
import java.util.Map;
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

    private String roomPriceKeyPrefix = "shop_room_price_";
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
        long gap = (endDate -startDate) / DateUtil.dayMillis;
        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId) ;
        //获得店家的所有房型
        List<RoomVo> roomVos = (List<RoomVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,indexName,esType,RoomVo.class) ;

        if(roomVos != null && roomVos.size() > 0){
            //获得店家所有房间的指定预订日期的价格
            for(RoomVo roomVo : roomVos){

                long roomId =  roomVo.getRoomId();

                double totalPrice = getRoomTotalPriceDuringDateRange(shopId,roomId,startDate,endDate) ;

                if(totalPrice ==0){
                    continue;
                }

                roomVo.setPrice((int)Math.ceil(totalPrice/gap));

                //获得店家指定房型房间的总数
                int roomNumber = roomVo.getRoomNumber() ;
                //获得一段时间内指定店家特定房型的预订情况
                List<RoomBookVo>  roomBookVos =  getRoomBookInfoDuringDateRange(shopId,roomId,startDate,endDate);
                //记录最大可以预定的房间数（部分房间可能已经被预定，同一房型房间数有限）
                int allowBookNumber = -1 ;
                if(roomBookVos.size() >0){
                    roomVo.setRoomBookVoList(roomBookVos);
                    for(RoomBookVo roomBookVo : roomBookVos){
                        int bookNumber = roomBookVo.getBookRoomNumber() ;
                        if(bookNumber >= roomNumber){
                            allowBookNumber = 0 ;
                            break ;
                        }else{
                            int tmpAllowBookNumber =  roomNumber - bookNumber ;
                            //如何某一天的可预订数小于以前的可预订数 那么按照这一天的可预订数作为最大可预订数
                            if(tmpAllowBookNumber < allowBookNumber || allowBookNumber == -1){
                                allowBookNumber = tmpAllowBookNumber ;
                            }
                        }
                    }
                }
                if(allowBookNumber == -1){
                    allowBookNumber = roomNumber ;
                }
                roomVo.setAllowBookNumber(allowBookNumber);
            }
        }
        return roomVos;
    }



    //获得店家指定日期范围内的房间预订情况

    public List<RoomBookVo> getRoomBookInfoDuringDateRange(long shopId,long roomId,long startDate,long endDate) throws Exception {

        //形成查询预订情况的基础条件 shopId：xxx  roomId:xxx
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("shopId",shopId))
                .must(QueryBuilders.termQuery("roomId",roomId));
        QueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("bookDateLongValue").gte(startDate).lt(endDate);
        //获得指定房间的预定情况
        List<RoomBookVo>  roomBookVos = elasticSearchClient.queryDataFromEsWithoutPaging(boolQueryBuilder,rangeQueryBuilder,"shop_room_book","book", RoomBookVo.class);

        return roomBookVos ;
    }

    //获取一段时间内住的总价格
    @Override
    public double getRoomTotalPriceDuringDateRange( long shopId,long roomId,long startDate,long endDate) {

        String roomPriceKey = roomPriceKeyPrefix + shopId + "_" + roomId;
        //如果没有设置具体房间的价格 直接返回
        Map<String, String> allRoomPrices = redisClient.getAllHashValue(roomPriceKey);
        double totalPrice = 0;
        if (allRoomPrices.size() != 0) {
            List<String> dateStrList = DateUtil.getDateList(startDate, endDate);
            String price = null;
            for(int i = 0 ; i < dateStrList.size() -1 ; i++){
                String dateStr = dateStrList.get(i);
                price = allRoomPrices.get(dateStr);
                if (price == null) {
                    boolean isWeekend = DateUtil.isWeekend(dateStr);
                    if (isWeekend) {
                        String weekendPrice = allRoomPrices.get("weekend");
                        if (weekendPrice != null) {
                            price = weekendPrice;
                        } else {
                            price = allRoomPrices.get("normal");
                        }
                    } else {
                        price = allRoomPrices.get("normal");
                    }
                }
                totalPrice += Double.valueOf(price);
            }
        }
        return totalPrice ;
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
        String facilityIndexName = "shop_room_facility" ;
        String facilityType = "facility" ;

        List<ShopRoomFacilityVo> shopRoomFacilityVos = (List<ShopRoomFacilityVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,postFilter,facilityIndexName,facilityType,ShopRoomFacilityVo.class) ;
        shopRoomAttribute.setShopRoomFacilityVos(shopRoomFacilityVos);

        //获得房间的备注信息 放在redis中
        List<ShopRoomNoticeVo> shopRoomNoticeVos = new ArrayList<ShopRoomNoticeVo>();
        String key = "room_notice_"+shopId ;
        Set<String>  notices =  redisClient.getSortedSet(key) ;
        if(notices.size() >0){
            for(String value : notices){
                ShopRoomNoticeVo shopRoomNoticeVo = new ShopRoomNoticeVo();
                shopRoomNoticeVo.setShopId(shopId);
                shopRoomNoticeVo.setNotice(value);
                shopRoomNoticeVos.add(shopRoomNoticeVo) ;
            }
        }

        shopRoomAttribute.setShopRoomNoticeVos(shopRoomNoticeVos);
        return shopRoomAttribute;
    }


    /**
     * 获取店家具体房型对应的图片信息 三级界面
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
                    if(courseNum - num <=0){
                        courseVo.setAllowBookNumber(0) ;
                    }else{
                        courseVo.setAllowBookNumber(courseNum - num);
                    }
                }else{
                    //表示没有任何预订记录 可预订数为最大的数量
                    courseVo.setAllowBookNumber(courseNum) ;
                }
            }
        }
        return courseVos;
    }

    /**
     * 获得店家一道菜的具体信息 三级界面
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
    public List<SpecialtyNoteVo> getShopSpecialtyNote(long shopId, long specialtyId) throws Exception{

        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId);
        QueryBuilder postFilter = QueryBuilders.termQuery("specialtyId",specialtyId);
        String indexName = "shop_specialty_note";
        String indexType = "note";

        List<SpecialtyNoteVo> specialtyNoteVos = (List<SpecialtyNoteVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,postFilter,indexName,indexType,SpecialtyNoteVo.class) ;

        return specialtyNoteVos;
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
        List<TicketVo> ticketVos = (List<TicketVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,ticketIndexName,ticketType,TicketVo.class) ;
        travelVo.setTicketVoList(ticketVos);
        //默认二级界面展示门票 导游 农家特色游的价格为用户入店的价格
        String strDate = DateUtil.formatDate(startDate) ;

        String ticketPricePrefix = "shop_travel_ticket_price_"+shopId+"_" ;
        String guidePricePrefix = "shop_travel_guide_price_"+shopId+"_" ;
        String familyactivityPricePrefix = "shop_travel_familyactivity_price_"+shopId+"_";
        //获得门票的价格 以普通价格显示的方式显示给用户 不显示特殊日期的价格，在用户真正选择了下单日期后显示门票票选择的日期的真实价格
        for(TicketVo ticketVo : ticketVos ){
            long ticketId = ticketVo.getTicketId() ;
            String ticketKey = ticketPricePrefix+ticketId ;
            //获得门票的普通价格
            Map<String,String> priceAll = redisClient.getAllHashValue(ticketKey) ;
            String price =  priceAll.get(strDate) ;
            if(price == null){
                price = priceAll.get("normal") ;
            }
            if(price != null){
                ticketVo.setTicketPrice(Double.valueOf(price));
            }
        }


        //获得店家导游服务的列表，每天有数量的限制（比如每天能提供五个导游的服务）（用户每单每天只能预定一个导游）
        String guideIndexName = "shop_travel_guide" ;
        String guideType = "guide";
        List<GuideVo> guideVos = (List<GuideVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,guideIndexName,guideType,GuideVo.class) ;
        if(guideVos.size() > 0 ){

            //对于店家来书 提供的只是一个导游服务 ，不需要把每个导游的信息录入系统
            GuideVo guideVo = guideVos.get(0);
            long guideId  = guideVo.getGuideId() ;
            int guideNumber = guideVo.getGuideNumber() ;

            if(guideNumber == -1){
                //表示预定数量没有限制
                guideVo.setAllowBookNumber(-1);
            }else{

                String guideBookIndexName = "shop_travel_guide_book" ;
                String guideBookIndexType = "book" ;
                //根据预定日期获得最大的可预定数（获得每天预定数与最大预定数的差，然后取其中的最小值） 导游按照开始日期获得
                QueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("shopId",shopId)).must(QueryBuilders.termQuery("guideId",guideId))
                        .must(QueryBuilders.termQuery("bookDateLongValue",startDate)) ;

                GuideBookVo guideBookVo = getShopTravelGuideBookInfoShopId(shopId,guideId,startDate) ;

                if(guideBookVo  != null ){
                    int allowBookNumber = guideNumber - guideBookVo.getBookNumber() ;
                    if(allowBookNumber <= 0 ){
                                 allowBookNumber = 0  ;
                    }
                    guideVo.setAllowBookNumber(allowBookNumber);
                }else{
                    //如果没有预订记录 表示可预订数为最大可预订的数量
                    guideVo.setAllowBookNumber(guideNumber);
                }
            }
            //获得导游的价格
            String guideKey =  guidePricePrefix+guideId ;
            Map<String,String> priceAll = redisClient.getAllHashValue(guideKey) ;
            String price =  priceAll.get(strDate) ;
            if(price == null){
                price = priceAll.get("normal") ;
            }
            if(price != null){
                guideVo.setTourGuardPrice(Double.valueOf(price));
            }



        }

        travelVo.setGuideVoList(guideVos);

        // 获得店家特色游的列表 没有总数的限制，但是每单最大数量有限制
        String familyActivityIndexName = "shop_travel_familyactivity" ;
        String familyActivityType = "familyactivity";
        List<FamilyActivityVo> familyActivityVos = (List<FamilyActivityVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,familyActivityIndexName,familyActivityType,FamilyActivityVo.class) ;
        travelVo.setFamilyActivityVoList(familyActivityVos);

        for(FamilyActivityVo familyActivityVo  : familyActivityVos ){
            long familyActiveId = familyActivityVo.getFamilyActiveId() ;
            String familyKey = familyactivityPricePrefix+familyActiveId ;
            //获得农家特色游得得价格
            Map<String,String> priceAll = redisClient.getAllHashValue(familyKey) ;
            String price =  priceAll.get(strDate) ;
            if(price == null){
                price = priceAll.get("normal") ;
            }
            if(price != null){
                familyActivityVo.setFamilyActivityPrice(Double.valueOf(price));
            }
        }
        return travelVo;
    }

	/**
     * 获得指定日期店家的导游预订情况
     * @param shopId
     * @param guideId
     * @param dateTime
     * @return
     * @throws Exception
     */
    @Override
    public GuideBookVo getShopTravelGuideBookInfoShopId(long shopId, long guideId, long dateTime) throws Exception {

        String guideBookIndexName = "shop_travel_guide_book" ;
        String guideBookIndexType = "book" ;
        //根据预定日期获得最大的可预定数（获得每天预定数与最大预定数的差，然后取其中的最小值） 导游按照开始日期获得
        QueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("shopId",shopId)).must(QueryBuilders.termQuery("guideId",guideId))
                .must(QueryBuilders.termQuery("bookDateLongValue",dateTime)) ;

        List<GuideBookVo> guideBookVos = (List<GuideBookVo> )elasticSearchClient.queryDataFromEsWithoutPaging(boolQuery,null,guideBookIndexName,
                guideBookIndexType,GuideBookVo.class) ;

        return guideBookVos.isEmpty() ? null : guideBookVos.get(0);
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
        String key = "resort_picture_list_"+resortId ;
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
    public List<ShopTicketNoticeVo> getShopTicketNoticeByResortId(long resortId) throws Exception{

        List<ShopTicketNoticeVo> shopTicketNoticeVos = new ArrayList<ShopTicketNoticeVo>() ;

        String key = "resort_notice_"+resortId;
        Set<String> values = redisClient.getSortedSet(key) ;

        if(values != null && values.size() >0){
            for(String value : values){
                ShopTicketNoticeVo shopTicketNoticeVo = (ShopTicketNoticeVo) JsonUtil.toObject(value,ShopTicketNoticeVo.class);
                shopTicketNoticeVos.add(shopTicketNoticeVo) ;
            }
        }
        return shopTicketNoticeVos;
    }

    /**
     * 获得店家对具体门票的备注信息
     * @param shopId
     * @param ticketId
     * @return
     */
    @Override
    public List<ShopTicketNoteVo> getShopTicketNote(long shopId, long ticketId) throws Exception{

        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId);
        QueryBuilder postFilter = QueryBuilders.termQuery("ticketId",ticketId);
        String  indexName = "shop_ticket_note" ;
        String indexType = "note" ;
        List<ShopTicketNoteVo> shopCoursePictureVos = (List<ShopTicketNoteVo> )elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,postFilter,indexName,indexType,ShopTicketNoteVo.class) ;

        return shopCoursePictureVos;
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
        String key = "familyActivity_notice_"+shopId+"_"+activityId ;
        Set<String> value = redisClient.getSortedSet(key);
        familyActivityNoticeVo.setNote(value);
        familyActivityNoticeVo.setShopId(shopId);
        familyActivityNoticeVo.setActivityId(activityId);

        return familyActivityNoticeVo;
    }



    /**
     * 获得具体农家自助游项目的图片,图片信息放在redis中  三级界面
     * @param shopId
     * @param guideId
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
     * @param guideId
     * @return
     * @throws Exception
     */
    @Override
    public GuideNoteVo getShopGuideNotice(long shopId, long guideId) throws Exception {

        GuideNoteVo guideNoteVo = new GuideNoteVo();
        String key = "guide_"+shopId+"_"+guideId ;
        Set<String> values = redisClient.getSortedSet(key) ;
        guideNoteVo.setNote(values);
        guideNoteVo.setShopId(shopId);
        guideNoteVo.setGuideId(guideId);
        return guideNoteVo;
    }

    /**
     * 获得游得价格
     * @param key
     * @param dateTime
     * @return
     * @throws Exception
     */
    @Override
    public double getTravelPrice(String key, long dateTime) throws Exception {

        String dateStr =  DateUtil.formatDate(dateTime);
        Map<String,String> travelPrices = redisClient.getAllHashValue(key) ;
        String price =  travelPrices.get(dateStr) ;
        if(price == null){
            price = travelPrices.get("normal") ;
        }
        if(price != null ){
            return Double.parseDouble(price) ;
        }
        return 0;
    }

    /**
     * 获得店家的推荐数好评数和全部评论数（根据不同的产品 住食游产）
     * @param shopId
     * @param indexname
     * @param indextype
     * @return
     */
    @Override
    public ShopRecommendAndCommentNumberVo getShopRecommendAndCommentNumber(long shopId, String indexname, String indextype) throws  Exception{

        ShopRecommendAndCommentNumberVo  shopRecommendAndCommentNumberVo = null ;
        List fields = new ArrayList();
        fields.add("goodCommentNum");
        fields.add("recommendNum");
        fields.add("totalCommentNum") ;
        QueryBuilder queryBuilder = QueryBuilders.termQuery("shopId",shopId) ;
        Map<String,Object> mapValue = elasticSearchClient.queryUniqueColumnSpecificField(queryBuilder,null,fields,indexname,indextype) ;

        if(mapValue.size() >0){
            try{
                int recommentNumber = Integer.valueOf(mapValue.get("recommendNum").toString());
                int goodCommentNum = Integer.valueOf(mapValue.get("goodCommentNum").toString());
                int totalCommentNum = Integer.valueOf(mapValue.get("totalCommentNum").toString());
                shopRecommendAndCommentNumberVo = new ShopRecommendAndCommentNumberVo(shopId,recommentNumber,goodCommentNum,totalCommentNum);
            }catch(Exception e){
               throw e ;
            }
        }

        return shopRecommendAndCommentNumberVo;
    }


    @Override
    public boolean checkRoomVoExistById(String id) throws Exception{
        return elasticSearchClient.checkEntityExist(ESIndexAndTypeConstant.SHOP_ROOM_INDEX,ESIndexAndTypeConstant.SHOP_ROOM_TYPE,id);
    }

    @Override
    public boolean checkCourseVoExistById(String id) throws Exception{

        return elasticSearchClient.checkEntityExist(ESIndexAndTypeConstant.SHOP_COURSE_INDEX,ESIndexAndTypeConstant.SHOP_COURSE_TYPE,id);
    }

    @Override
    public boolean checkSpecialtyVoExistById(String id) throws Exception{
        return elasticSearchClient.checkEntityExist(ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_INDEX,ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_TYPE,id);
    }

    @Override
    public boolean checkTicketVoExistById(String id) throws Exception{
        return elasticSearchClient.checkEntityExist(ESIndexAndTypeConstant.SHOP_TRAVEL_TICKET_INDEX,ESIndexAndTypeConstant.SHOP_TRAVEL_TICKET_TYPE,id);
    }

    @Override
    public boolean checkGuideVoExistById(String id) throws Exception{
        return elasticSearchClient.checkEntityExist(ESIndexAndTypeConstant.SHOP_TRAVEL_GUIDE_INDEX,ESIndexAndTypeConstant.SHOP_TRAVEL_GUIDE_TYPE,id);
    }

    @Override
    public boolean checkFamilyActivityVoByExistId(String id) throws Exception{
        return elasticSearchClient.checkEntityExist(ESIndexAndTypeConstant.SHOP_TRAVEL_FAMILYACTIVITY_INDEX,ESIndexAndTypeConstant.SHOP_TRAVEL_FAMILYACTIVITY_TYPE,id);
    }


    @Override
    public List<RoomVo> getRoomVoListById(Set<String> ids) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(ids);
        return elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,ESIndexAndTypeConstant.SHOP_ROOM_INDEX,ESIndexAndTypeConstant.SHOP_ROOM_TYPE,RoomVo.class);
    }

    @Override
    public List<CourseVo> getCourseVoListById(Set<String> ids) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(ids);
        return elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,ESIndexAndTypeConstant.SHOP_COURSE_INDEX,ESIndexAndTypeConstant.SHOP_COURSE_INDEX,CourseVo.class);
    }

    @Override
    public List<SpecialtyVo> getSpecialtyVoListById(Set<String> ids) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(ids);
        return elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_INDEX,ESIndexAndTypeConstant.SHOP_SPECIALTY_ITEM_TYPE,SpecialtyVo.class);
    }

    @Override
    public List<TicketVo> getTicketVoListById(Set<String> ids) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(ids);
        return elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,ESIndexAndTypeConstant.SHOP_TRAVEL_TICKET_INDEX,ESIndexAndTypeConstant.SHOP_TRAVEL_TICKET_TYPE,TicketVo.class);
    }

    @Override
    public List<GuideVo> getGuideVoListById(Set<String> ids) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(ids);
        return elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,ESIndexAndTypeConstant.SHOP_TRAVEL_GUIDE_INDEX,ESIndexAndTypeConstant.SHOP_TRAVEL_GUIDE_TYPE,GuideVo.class);
    }

    @Override
    public List<FamilyActivityVo> getFamilyActivityVoListById(Set<String> ids) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(ids);
        return elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,null,ESIndexAndTypeConstant.SHOP_TRAVEL_FAMILYACTIVITY_INDEX,ESIndexAndTypeConstant.SHOP_TRAVEL_FAMILYACTIVITY_TYPE,FamilyActivityVo.class);
    }
}

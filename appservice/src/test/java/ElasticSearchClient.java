import com.sun.org.apache.xml.internal.dtm.ref.DTMNamedNodeMap;
import com.wanjia.utils.JsonUtil;
import com.wanjia.vo.*;
import com.wanjia.vo.live.RoomBookVo;
import com.wanjia.vo.live.RoomPictureVo;
import com.wanjia.vo.live.RoomVo;
import com.wanjia.vo.live.ShopRoomFacilityVo;
import com.wanjia.vo.restaurant.CourseBookVo;
import com.wanjia.vo.restaurant.CourseVo;
import com.wanjia.vo.restaurant.ShopCourseDetailInfoVo;
import com.wanjia.vo.restaurant.ShopCoursePictureVo;
import com.wanjia.vo.speciality.SpecialityPictureVo;
import com.wanjia.vo.speciality.SpecialtyNoteVo;
import com.wanjia.vo.speciality.SpecialtyVo;
import com.wanjia.vo.travel.*;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.RangeFilterBuilder;
import org.apache.lucene.queryparser.xml.builders.RangeQueryBuilder;
import org.apache.lucene.search.SearcherManager;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryAction;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.deletionpolicy.SnapshotIndexCommit;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.engine.EngineException;
import org.elasticsearch.index.engine.Segment;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.translog.Translog;
import org.elasticsearch.plugin.deletebyquery.DeleteByQueryPlugin;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by blake on 2016/6/21.
 */
public class ElasticSearchClient {

    Settings settings = Settings.settingsBuilder().put("cluster.name","wanjia").put("client.transport.sniff",true).build() ;

    Client client ;

    @Before
    public void initESClient() {
        try {
          /*  client = TransportClient.builder().settings(settings).addPlugin(DeleteByQueryPlugin.class).build().addTransportAddress(
                    new InetSocketTransportAddress(new InetSocketAddress("112.124.15.101",9300)))
                    .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("112.124.49.117",9300)))
                    .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("112.124.39.68",9300))) ;*/

            client = TransportClient.builder().settings(settings).build().addTransportAddress(
                    new InetSocketTransportAddress(new InetSocketAddress("112.124.15.101",9300)))
                    .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("112.124.49.117",9300)))
                    .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("112.124.39.68",9300))) ;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void addShopHotelListDataToEs(){

        Random random  = new Random() ;
        List<ShopHotelListVo> vos = new ArrayList<ShopHotelListVo>();
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        for(int i = 1 ; i <= 20 ; i ++){
            ShopHotelListVo vo = new ShopHotelListVo() ;
            vo.setResortId(1);
            vo.setShopId(i);
            vo.setShopName("店家-"+i);
           // vo.setRoomId(random.nextInt(3));
            vo.setAddressProvince("安徽");
            vo.setAddressCity("池州");
            vo.setAddressDistinct("九华山");
            vo.setAddressDetail("街道"+i);
            if(i % 5 == 0){
                vo.setAdShop(1);
            }else{
                vo.setAdShop(0);
            }

            vo.setDefaultSort(random.nextInt(20));
            int goodNum =  random.nextInt(2000) ;
            vo.setGoodCommentNum(goodNum);
            vo.setRecommendNum(random.nextInt(3000));
            vo.setTotalCommentNum(goodNum+random.nextInt(1000));
            vo.setLandmarkId(i%4);
            ShopListBaseVo.Location location = vo.new Location();

            double lon = random.nextInt(100) ;
            double lat = random.nextInt(90);
            location.setLon(lon);
            location.setLat(lat);
            vo.setLocation(location);
            vo.setShopPic("http://www.whateverblake.com/shoplist_"+i+".jpg");

            bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_hotel").setType("hotel").setId(String.valueOf(i)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;

            vos.add(vo);
        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }

    @Test
    public void addShopHotelPriceToEs(){

        Random random  = new Random() ;
        List<HotelPriceVo> vos = new ArrayList<HotelPriceVo>();
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        int id = 0  ;

        for(int i = 1 ; i <= 20 ; i ++){
            DateTime dateTime = new DateTime() ;
            for(int j =1 ; j <= 90 ; j++){
                dateTime = dateTime.plusDays(1) ;
                String dateTime1Str = dateTime.toString("yyyy-MM-dd");
                long timevalue = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dateTime1Str).getMillis() ;
                for(int k=1;k<=3 ;k++){
                    HotelPriceVo vo = new HotelPriceVo();
                    vo.setRoomId(k-1);
                    //vo.setRoomId(random.nextInt(3));
                    vo.setShopId(i);
                    vo.setLandmarkId(i%4);
                    vo.setShopName("店家-"+i);
                    vo.setPriceDate(dateTime1Str);
                    vo.setPriceDateLongValue(timevalue);
                    vo.setResortId(1);
                    vo.setRoomPrice(random.nextInt(500));
                    bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_hotel_price").setType("price").setId(String.valueOf(id++)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
                    vos.add(vo);
                }

            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }


    @Test
    public void addShopRestaurantListDataToEs(){

        Random random  = new Random() ;
        List<ShopRestaurantListVo> vos = new ArrayList<ShopRestaurantListVo>();
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        for(int i = 1 ; i <= 20 ; i ++){
            ShopRestaurantListVo vo = new ShopRestaurantListVo() ;
            vo.setResortId(1);
            vo.setShopId(i);
            vo.setShopName("店家-"+i);
            vo.setAddressProvince("安徽");
            vo.setAddressCity("池州");
            vo.setAddressDistinct("九华山");
            vo.setAddressDetail("街道"+i);
            if(i % 5 == 0){
                vo.setAdShop(1);
            }else{
                vo.setAdShop(0);
            }

            vo.setDefaultSort(random.nextInt(20));
            int goodNum =  random.nextInt(2000) ;
            vo.setGoodCommentNum(goodNum);
            vo.setRecommendNum(random.nextInt(3000));
            vo.setTotalCommentNum(goodNum+random.nextInt(1000));
            vo.setLandmarkId(i%4);
            ShopListBaseVo.Location location = vo.new Location();

            double lon = random.nextInt(100) ;
            double lat = random.nextInt(90);
            location.setLon(lon);
            location.setLat(lat);
            vo.setLocation(location);
            vo.setShopPic("http://www.whateverblake.com/shoplist_"+i+".jpg");
            vo.setFoodTotalNum(random.nextInt(50));
            vo.setFoodAveragePrice(random.nextInt(200));

            bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_restaurant").setType("restaurant").setId(String.valueOf(i)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;

            vos.add(vo);
        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }


    @Test
    public void addShopSpecialtyListDataToEs(){

        Random random  = new Random() ;
        List<ShopSpecialtyListVo> vos = new ArrayList<ShopSpecialtyListVo>();
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        for(int i = 1 ; i <= 20 ; i ++){
            ShopSpecialtyListVo vo = new ShopSpecialtyListVo() ;
            vo.setResortId(1);
            vo.setShopId(i);
            vo.setShopName("店家-"+i);
            vo.setAddressProvince("安徽");
            vo.setAddressCity("池州");
            vo.setAddressDistinct("九华山");
            vo.setAddressDetail("街道"+i);
            if(i % 5 == 0){
                vo.setAdShop(1);
            }else{
                vo.setAdShop(0);
            }

            vo.setDefaultSort(random.nextInt(20));
            int goodNum =  random.nextInt(2000) ;
            vo.setGoodCommentNum(goodNum);
            vo.setRecommendNum(random.nextInt(3000));
            vo.setTotalCommentNum(goodNum+random.nextInt(1000));
            vo.setLandmarkId(i%4);
            ShopListBaseVo.Location location = vo.new Location();

            double lon = random.nextInt(100) ;
            double lat = random.nextInt(90);
            location.setLon(lon);
            location.setLat(lat);
            vo.setLocation(location);
            vo.setShopPic("http://www.whateverblake.com/shoplist_"+i+".jpg");
            vo.setSpecialtyLowestPrice(random.nextInt(800));
            bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_specialty").setType("specialty").setId(String.valueOf(i)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;

            vos.add(vo);
        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }



    @Test
    public void addShopTravelListDataToEs(){

        Random random  = new Random() ;
        List<ShopTravelListVo> vos = new ArrayList<ShopTravelListVo>();
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        for(int i = 1 ; i <= 20 ; i ++){
            ShopTravelListVo vo = new ShopTravelListVo();

            vo.setResortId(1);
            vo.setShopId(i);
            vo.setShopName("店家-"+i);
            vo.setAddressProvince("安徽");
            vo.setAddressCity("池州");
            vo.setAddressDistinct("九华山");
            vo.setAddressDetail("街道"+i);
            if(i % 5 == 0){
                vo.setAdShop(1);
            }else{
                vo.setAdShop(0);
            }



            vo.setDefaultSort(random.nextInt(20));
            int goodNum =  random.nextInt(2000) ;
            vo.setGoodCommentNum(goodNum);
            vo.setRecommendNum(random.nextInt(3000));
            vo.setTotalCommentNum(goodNum+random.nextInt(1000));
            vo.setLandmarkId(i%4);
            ShopListBaseVo.Location location = vo.new Location();

            double lon = random.nextInt(100) ;
            double lat = random.nextInt(90);
            location.setLon(lon);
            location.setLat(lat);
            vo.setLocation(location);
            vo.setShopPic("http://www.whateverblake.com/shoplist_"+i+".jpg");
            //vo.setTravelTicketLowestPrice(random.nextInt(300));
            vo.setTravelTicketState(1);
            vo.setTravelTicketLowestPrice(random.nextInt(500));

            if(i % 3 == 0){
                vo.setTravelGuideLowestPrice(random.nextInt(200));
                vo.setTravelGuideState(1);
            }else if(i % 3 ==1){
                vo.setTravelSpecialState(1);
                vo.setTravelSpecialLowestPrice(random.nextInt(400));
            }

            bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_travel").setType("travel").setId(String.valueOf(i)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;

            vos.add(vo);
        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }

    @Test
    public void addShopTravelTicketPriceToEs(){

        Random random  = new Random() ;
        List<TicketPriceVo> vos = new ArrayList<TicketPriceVo>();
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        int id = 0  ;

        for(int i = 1 ; i <= 20 ; i ++){
            DateTime dateTime = new DateTime() ;
            for(int j =1 ; j <= 90 ; j++){
                dateTime = dateTime.plusDays(1) ;
                String dateTime1Str = dateTime.toString("yyyy-MM-dd");
                long timevalue = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dateTime1Str).getMillis() ;
                for(int k=1 ;k<=3;k++){
                    TicketPriceVo vo = new TicketPriceVo();
                    vo.setTicketId(random.nextInt(3));
                    vo.setPriceDate(dateTime1Str);
                    vo.setPriceDateLongValue(timevalue);
                    vo.setResortId(1);
                    vo.setShopId(i);
                    vo.setLandmarkId(i%4);
                    vo.setShopName("店家-"+i);
                    vo.setPrice(random.nextInt(500));
                    bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_travel_ticketprice").setType("price").setId(String.valueOf(id++)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
                    vos.add(vo);
                }

            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }



    @Test
    public void addLandmarkDataToEs(){
        Character[] name = {'A','B','C','D','E','F','G','H','I'} ;

        Random random  = new Random() ;
        List<ResortLandmarkVo> vos = new ArrayList<ResortLandmarkVo>();
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        for(int i = 1 ; i < 9 ; i ++){
            ResortLandmarkVo vo = new ResortLandmarkVo() ;
            vo.setResortId(i % 3);
            vo.setLandmarkName("地标_"+name[i]);
            vo.setLandmarkId(i);
            vo.setLat(random.nextInt(180));
            vo.setLon(random.nextInt(90));
            vo.setIsValid(1);
            vos.add(vo);
            bulkRequestBuilder.add(client.prepareIndex().setIndex("resort_landmark").setType("landmark").setId(String.valueOf(i)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;

        }


        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }


    @Test
    public void deleteByQuery(){
        String indexname = "shop_logo";
        String type = "logo" ;
        DeleteByQueryResponse rsp = new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
                .setIndices(indexname)
                .setTypes(type)
                .setQuery(QueryBuilders.matchAllQuery())
                .execute()
                .actionGet();
    }

    @Test
    public void testQuery(){

        QueryBuilder queryBuilder =  QueryBuilders.termQuery("resortId",1);
        String sortField = "f_goodCommentNum";
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("shoplist").setTypes("shopinfo").setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(queryBuilder).setFrom(1).setSize(20).addSort(sortField, SortOrder.DESC) ;
        SearchResponse searchResponse =  searchRequestBuilder.execute().actionGet() ;

        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.totalHits() ;

        if(totalHits >0){
            SearchHit[] hits = searchHits.getHits() ;
            for (SearchHit hit :hits){
               // System.out.println(hit.getScore());
               System.out.println(hit.getSourceAsString()) ;
            }
        }


    }


    @Test
    public void testAggQuery(){

        BoolQueryBuilder booleanQueryBuilder = QueryBuilders.boolQuery() ;
        QueryBuilder queryBuilder =  QueryBuilders.termQuery("resortId",1);
        booleanQueryBuilder.must(queryBuilder) ;
        org.elasticsearch.index.query.RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("priceDateLongValue").gte(1467648000000l).lt(1467907200000l);
        booleanQueryBuilder.must(rangeQueryBuilder) ;


        int page  = 2 ;
        int pageSize = 10 ;

       AggregationBuilder  aggregationBuilder =  AggregationBuilders.terms("shopGroup").field("shopId").size(pageSize*page).order(Terms.Order.aggregation("sum_price",true))
                .subAggregation(AggregationBuilders.sum("sum_price").field("roomPrice")) ;

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("shop_hotel_price").setTypes("price").setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(booleanQueryBuilder).setFrom(0).setSize(0).addAggregation(aggregationBuilder) ;

        SearchResponse searchResponse =  searchRequestBuilder.execute().actionGet() ;

        Terms agg = searchResponse.getAggregations().get("shopGroup") ;

        // For each entry
        for (Terms.Bucket entry : agg.getBuckets()) {
            String key = entry.getKey().toString();                    // bucket key
            long docCount = entry.getDocCount();            // Doc count
            System.out.println("key is =="+key+"----docCount is ---"+docCount);
            // We ask for top_hits for each bucket
            Sum sum  = entry.getAggregations().get("sum_price");
            System.out.println(sum.getValue());
        }

        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.totalHits() ;

        if(totalHits >0){
            SearchHit[] hits = searchHits.getHits() ;
            for (SearchHit hit :hits){
                // System.out.println(hit.getScore());
                System.out.println(hit.getSourceAsString()) ;
            }
        }


    }


    @Test
    public void addShopProductLogo(){

         BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

         int id = 201 ;

        for(int i = 1 ; i <=20 ; i++){
            for(int j=1; j <=5 ; j++){
                ShopProductLogoVo shopProductLogoVo = new ShopProductLogoVo() ;
                 shopProductLogoVo.setShopId(i);
                shopProductLogoVo.setPicType(3);
                shopProductLogoVo.setPicDescribe("门票服务，导游服务");
                shopProductLogoVo.setPicUrl("http://www.whateverblake.com/shop_travel_"+j+".jpg");
                shopProductLogoVo.setPicName("农家的门票服务_"+j);
                shopProductLogoVo.setSort(j);
                bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_logo").setType("logo").setId(String.valueOf(id++)).setSource(JsonUtil.toJsonString(shopProductLogoVo)).setOpType(IndexRequest.OpType.INDEX)) ;
            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }


    @Test
    public void addRoomVo(){

        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        Random random = new Random() ;
        int id = 201 ;

        for(int i = 1 ; i <=20 ; i++){
            for(int j=0; j <=2 ; j++){
                RoomVo vo = new RoomVo() ;
                vo.setShopId(i);
                vo.setIsWindow(j==0 ? 1:0);
                vo.setPicUrl("http://www.whateverblake.com/shop_room_bed_demo.jpg");
                vo.setBedDescribe("舒适,美梦");
                vo.setRoomCapacity(j+2);
                vo.setRoomNumber(random.nextInt(70));
                int start = random.nextInt(4);
                vo.setRoomFloor(start+"-"+(start+3));
                vo.setRoomId(j);
                vo.setRoomName("房间_"+j);
                vo.setRoomSquare((start+8)+"-"+(start+15));
                String roomType = "" ;
                switch(j){
                    case 0 : roomType = "双人床" ;break ;
                    case  1 : roomType = "大圆床" ; break ;
                    case 2 : roomType = "单人床" ; break ;
                }
                vo.setRoomType(roomType);
                vo.setRoomDescribe("自然，清新，梦回故里");
                vo.setShopName("店家_"+i);

                bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_room").
                        setType("room").setId(String.valueOf(i+"_"+j)).
                        setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }

    @Test
    public void addRoomVoBook(){

        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        Random random = new Random() ;

        for(int i = 1 ; i <=20 ; i++){
            for(int j=0; j <=2 ; j++){
                int bookNumber = random.nextInt(100) ;
                DateTime dateTime = new DateTime("2016-7-7") ;
                for(int d =1 ; d <= 90 ; d++) {
                    RoomBookVo vo = new RoomBookVo() ;
                    vo.setShopId(i);
                    vo.setRoomId(j);
                    dateTime = dateTime.plusDays(1);
                    String dateTime1Str = dateTime.toString("yyyy-MM-dd");
                    long timevalue = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dateTime1Str).getMillis();

                    vo.setBookDate(dateTime1Str);
                    vo.setBookDateLongValue(timevalue);
                    vo.setBookRoomNumber(bookNumber-random.nextInt(20));
                    vo.setTotalRoomNumber(bookNumber);
                    String id = i+"_"+j+"_"+dateTime1Str ;
                    bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_room_book").
                            setType("book").setId(String.valueOf(id)).
                            setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
                }


            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }


//添加房间的属性信息
    @Test
    public void addRoomAttribute(){

        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        Random random = new Random() ;
        int id = 1 ;
       String []  facilityNames = {"wifi","热水","吹风","停车位","加床：不可加","窗户：有","无早"} ;

        for(int i = 1 ; i <=20 ; i++){
            for(int j=0; j <=2 ; j++){
                for(int p = 0; p <facilityNames.length ; p++){
                    ShopRoomFacilityVo vo = new ShopRoomFacilityVo() ;
                    vo.setShopId(i);
                    vo.setRoomId(j);
                    vo.setFacilityName(facilityNames[p]);
                    bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_room_facility").
                            setType("facility").setId(String.valueOf(id++)).
                            setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
                }

            }

            }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }

    //添加房间的图片信息
    @Test
    public void addRoomPic(){

        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        Random random = new Random() ;
        int id = 1 ;
        for(int i = 1 ; i <=20 ; i++){
            for(int j=0; j <=2 ; j++){
                for(int p = 1; p <=5 ; p++){
                    RoomPictureVo vo = new RoomPictureVo();
                    vo.setShopId(i);
                    vo.setRoomId(j);
                    vo.setPicDesc("房间信息图片");
                    vo.setPicName("卧室");
                    vo.setPicUrl("http://www.whateverblake.com/shop_room_pic_"+p+".jpg");
                    bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_room_picture").
                            setType("picture").setId(String.valueOf(id++)).
                            setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
                }

            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }

    @Test
    public void addCourseVo(){

        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        Random random = new Random() ;
        int id = 1 ;

        for(int i = 1 ; i <=20 ; i++){
            for(int j=1; j <=20 ; j++){
                DateTime dateTime = new DateTime("2016-7-7") ;
                    CourseVo vo = new CourseVo() ;
                    vo.setShopId(i);
                    vo.setCourseId(j);
                    vo.setCourseName("菜品_"+j);
                    vo.setCourseNumber(random.nextInt(200));
                    vo.setCoursePrice(random.nextInt(300));
                    vo.setSpicyLevel(j % 3);
                    vo.setShopName("店家_"+i);
                    bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_course").
                            setType("course").setId(String.valueOf(i+"_"+j)).
                            setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }



    @Test
    public void addCourseVoBook(){

        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        Random random = new Random() ;

        for(int i = 1 ; i <=20 ; i++){
            for(int j=1; j <=20 ; j++){
                DateTime dateTime = new DateTime("2016-7-7") ;
                int totalNumber = random.nextInt(100) ;
                for(int d =1 ; d <= 90 ; d++) {
                    CourseBookVo vo = new CourseBookVo() ;
                    vo.setShopId(i);
                    vo.setCourseId(j);
                    dateTime = dateTime.plusDays(1);
                    String dateTime1Str = dateTime.toString("yyyy-MM-dd");
                    long timevalue = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dateTime1Str).getMillis();
                    vo.setBuyDate(dateTime1Str);
                    vo.setBuyDateLongValue(timevalue);
                    vo.setTotalNumber(totalNumber);
                    vo.setBookedNumber(totalNumber - random.nextInt(10));
                    String id = i+"_"+j+"_"+dateTime1Str;
                    bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_course_book").
                            setType("book").setId(id).
                            setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
                }


            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }

    @Test
    public void addCourseDetailInfo(){
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        Random random = new Random() ;
        int id = 1 ;

        for(int i = 1 ; i <=20 ; i++){
            for(int j=1; j <=20 ; j++){
                    ShopCourseDetailInfoVo vo = new ShopCourseDetailInfoVo() ;
                    vo.setShopId(i);
                    vo.setCourseId(j);
                    vo.setTaste(i % 3);
                    vo.setNote("原材料全部来自农家自己耕种");
                    vo.setConsist("土豆，青椒，腊肉");

                    bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_course_detailinfo").
                            setType("course").setId(String.valueOf(id++)).
                            setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }

    //添加菜品的图片信息
    @Test
    public void addCoursPic(){
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        Random random = new Random() ;
        int id = 1 ;

        for(int i = 1 ; i <=20 ; i++){
            for(int j=1; j <=20 ; j++){
                for(int p = 1 ; p <=5 ; p++ ){
                    ShopCoursePictureVo vo = new ShopCoursePictureVo() ;
                    vo.setShopId(i);
                    vo.setCourseId(j);
                    vo.setPicDesc("菜品信息图片");
                    vo.setPicName("美味佳肴");
                    vo.setPicUrl("http://www.whateverblake.com/shop_course_pic_"+p+".jpg");

                    bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_course_picture").
                            setType("picture").setId(String.valueOf(id++)).
                            setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
                }
            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }

    @Test
    public void addSpecialtyVo(){

        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        Random random = new Random() ;
        int id = 1 ;

        for(int i = 1 ; i <=20 ; i++){
            for(int j=1; j <=20 ; j++){
                SpecialtyVo vo = new SpecialtyVo() ;
                vo.setShopId(i);
                vo.setShopName("店家_"+i);
                vo.setSpecialtyId(j);
                vo.setSpecialtyComment("特产，来自大自然");
                vo.setSpecialtyPrice(random.nextInt(200));
                vo.setSpecialWeight(random.nextInt(2000));
                vo.setWeightUnit(1);
                vo.setSpecialtyPictureUrl("http://www.whateverblake.com/shop_specialty_item_"+j+".jpg");
                vo.setSpecialtyNumber(random.nextInt(100));
                vo.setSpecialtyName("特产_"+j);
                bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_specialty_item").
                        setType("specialty").setId(String.valueOf(i+"_"+j)).
                        setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }

    //添加特产的图片信息
    @Test
    public void addSpecialtyPic(){
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        Random random = new Random() ;
        int id = 1 ;

        for(int i = 1 ; i <=20 ; i++){
            for(int j=1; j <=20 ; j++){
                for(int p = 1 ; p <=5 ; p++ ){
                    SpecialityPictureVo vo = new SpecialityPictureVo() ;
                    vo.setShopId(i);
                    vo.setSpecialtyId(j);
                    vo.setPicDesc("特产信息图片");
                    vo.setPicName("特产");
                    vo.setPicUrl("http://www.whateverblake.com/shop_specialty_pic_"+p+".jpg");

                    bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_specialty_picture").
                            setType("picture").setId(String.valueOf(id++)).
                            setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
                }
            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }


    //添加特产的备注信息
    @Test
    public void addSpecialtyNote(){

        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        int id = 1 ;

        for(int i = 1 ; i <=20 ; i++){
            for(int j=1; j <=20 ; j++){
                for(int p = 1 ; p <=2 ; p++){
                    SpecialtyNoteVo vo = new SpecialtyNoteVo() ;
                    vo.setShopId(i);
                    vo.setSpecialtyId(j);
                    if(p==1){
                        vo.setNote("生长环境：深山中，生长2年才能进行挖掘；");
                    }else{
                        vo.setNote("效用：清热解毒、丰胸养颜、益脑增发。");
                    }

                    bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_specialty_note").
                            setType("note").setId(String.valueOf(id++)).
                            setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
                }

            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }


    @Test
    public void addTravelVo(){

        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        Random random = new Random() ;
        int id = 1 ;

        String [] ticketType = {"成人票","儿童片","老年票"} ;
        for(int i = 1 ; i <=20 ; i++){
            for(int j=1; j <=3 ; j++){
                TicketVo vo = new TicketVo() ;
                vo.setShopId(i);
                vo.setIsCurrentDayValid(j==3 ? 1:2);
                vo.setIsTotalTicket(j==3 ? 1:2);
                vo.setResortId(i);
                vo.setResortName("九华山");
                vo.setTicketType(ticketType[j-1]);
                vo.setTicketName(ticketType[j-1]);
                vo.setTicketId(j);
                vo.setMaxBookNumber(random.nextInt(30));
                vo.setPicUrl("http://www.whateverblake.com/shop_travel_ticket_item_"+j+".jpg");
                vo.setShopName("店家_"+i);
                bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_travel_ticket").
                        setType("ticket").setId(i+"_"+j).
                        setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
            }

            for(int j=1; j <=1 ; j++){
                GuideVo vo = new GuideVo() ;
                vo.setShopId(i);
                vo.setShopName("店家_"+i);
                vo.setResortId(i);
                vo.setPicUrl("http://www.whateverblake.com/shop_travel_guide_item_"+j+".jpg");
                vo.setGuideId(j);
                vo.setComments("好的导游");
                vo.setCarService(1);
                vo.setTourGuideService(1);
                vo.setDescribe("到本店消费，免费提供导游服务");
                vo.setTourGuardPrice(random.nextInt(300));
                vo.setGuideNumber(random.nextInt(10));
                bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_travel_guide").
                        setType("guide").setId(String.valueOf(i+"_"+j)).
                        setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
            }


            String familyActivity[] = {"游船","赏花","漂流"} ;
            for(int j=1; j <=3 ; j++){
                FamilyActivityVo vo = new FamilyActivityVo() ;
                vo.setShopId(i);
                vo.setShopName("店家_"+i);
                vo.setPicUrl("http://www.whateverblake.com/shop_travel_familyactivity_item_"+j+".jpg");
                vo.setFamilyActiveId(j);
                vo.setFamilyActivityName(familyActivity[j-1]);
                vo.setFamilyActivityComment("好玩的自家组织的游玩项目");
                vo.setFamilyActivityPrice(random.nextInt(300));
                int num = random.nextInt(5) ;
                vo.setPersonNumMax(num +2);
                vo.setPersonNumMin(num);
                vo.setTourTimeElapse(random.nextInt(3));
                vo.setMaxBookNumber(random.nextInt(20));

                bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_travel_familyactivity").
                        setType("familyactivity").setId(String.valueOf(i+"_"+j)).
                        setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
            }


        }



        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }


    //添加导游预定信息表的备注信息
    @Test
    public void addGuideVoBook(){

        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        Random random = new Random() ;

        for(int i = 1 ; i <=20 ; i++){
                DateTime dateTime = new DateTime("2016-7-7") ;
                int totalNumber = random.nextInt(10) ;
                for(int d =1 ; d <= 90 ; d++) {
                    GuideBookVo vo = new GuideBookVo() ;
                    vo.setShopId(i);
                    vo.setGuideId(1);
                    dateTime = dateTime.plusDays(1);
                    String dateTime1Str = dateTime.toString("yyyy-MM-dd");
                    long timevalue = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dateTime1Str).getMillis();
                    vo.setBookDate(dateTime1Str);
                    vo.setBookDateLongValue(timevalue);
                    vo.setTotalNumber(totalNumber);
                    vo.setBookNumber(totalNumber- random.nextInt(3));
                    String id = i+"_"+1+"_"+dateTime1Str;

                    bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_travel_guide_book").
                            setType("book").setId(id).
                            setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;


            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }



    //添加店家门票的备注信息
    @Test
    public void addTicketNote(){

        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        int id = 1 ;

        String[] values = {"购票当天有效，扫描后作废","详细信息，请参见票面介绍"} ;
        for(int i=1 ; i<=20 ; i++){
            for(int j=1; j<=2;j++){
                ShopTicketNoteVo vo = new ShopTicketNoteVo() ;
                vo.setShopId(i);
                vo.setNote(values[j-1]);
                vo.setTicketId(i);
                bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_ticket_note").
                        setType("note").setId(String.valueOf(id++)).
                        setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
            }
        }





        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }

    }




    @Test
    public void testGeo(){

        String indexname  = "shop_travel" ;
        String type = "travel" ;
        double lon = 97;
        double lat = 52 ;
        QueryBuilder geoQueryBuilder = QueryBuilders.geoDistanceQuery("location").lon(lon).lat(lat).
                                                    distance(10, DistanceUnit.KILOMETERS).geoDistance(GeoDistance.PLANE);
        GeoDistanceSortBuilder geoDistanceSortBuilder =  SortBuilders.geoDistanceSort("location").order(SortOrder.ASC).point(lat,lon)
                .unit(DistanceUnit.KILOMETERS) ;

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexname).setSearchType(SearchType.QUERY_THEN_FETCH)
                .setTypes(type).setQuery(geoQueryBuilder);
        searchRequestBuilder.addSort(geoDistanceSortBuilder) ;

        SearchResponse response = searchRequestBuilder.execute().actionGet() ;
        SearchHits searchHits = response.getHits();
        long totalHits = searchHits.totalHits();
        System.out.println("totalhits is "+totalHits);

    }


    @Test
    public void queryIds(){

        String indexname  = "shop_course" ;
        String type = "course" ;
        List<String> ids = new ArrayList<String>();
        ids.add("1_2");
        ids.add("1_10");
        QueryBuilder termsQuery = QueryBuilders.termsQuery("_id",ids);
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexname).setSearchType(SearchType.QUERY_THEN_FETCH)
                .setTypes(type).setQuery(termsQuery);
        UpdateRequest updateRequest = new UpdateRequest("","","") ;
        SearchResponse response = searchRequestBuilder.execute().actionGet() ;

        SearchHits searchHits = response.getHits();
        long totalHits = searchHits.totalHits();
        for(SearchHit hit : searchHits){
            Map map = hit.sourceAsMap();
            System.out.println(hit.getSource());
            System.out.println(hit.getId());
            System.out.println(hit.getVersion());
        }
        System.out.println("totalhits is "+totalHits);

    }

    @Test
     public void updateTest(){
        String index = "shop_specialty_item" ;
        String type = "specialty";

        String id = "1_2";
        GetRequest getRequest = new GetRequest(index,type,id) ;
        GetResponse getResponse = client.get(getRequest).actionGet() ;
        Map<String,Object> result = getResponse.getSource() ;
        System.out.println(getResponse.getVersion());
        result.put("specialtyNumber",124);

        UpdateRequest updateRequest = new UpdateRequest(index,type,id).version(getResponse.getVersion());
        updateRequest.doc(result) ;
        client.update(updateRequest).actionGet() ;






    }



}

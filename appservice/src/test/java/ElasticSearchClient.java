import com.wanjia.utils.JsonUtil;
import com.wanjia.vo.ResortLandmarkVo;
import com.wanjia.vo.ShopListVo;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexAction;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by blake on 2016/6/21.
 */
public class ElasticSearchClient {

    Settings settings = Settings.settingsBuilder().put("cluster.name","wanjia").put("client.transport.sniff",true).build() ;

    Client client ;

    @Before
    public void initESClient() {
        try {
            client = TransportClient.builder().settings(settings).build().addTransportAddress(
                    new InetSocketTransportAddress(new InetSocketAddress("112.124.15.101",9300)))
                    .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("112.124.49.117",9300)))
                    .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("112.124.39.68",9300))) ;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void addShopListDataToEs(){

        Random random  = new Random() ;
        List<ShopListVo> vos = new ArrayList<ShopListVo>();
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        for(int i = 1 ; i <= 20 ; i ++){
            ShopListVo vo = new ShopListVo() ;
            vo.setResortId(i%3);
            vo.setShopId(i);
            vo.setShopName("店家—"+i);
            vo.setAddressProvince("安徽");
            vo.setAddressCity("池州");
            vo.setAddressDistinct("九华山");
            vo.setAddressDetail("街道"+i);
            if(i % 5 == 0){
                vo.setAdShop(1);
            }else{
                vo.setAdShop(0);
            }
            vo.setLiveState(1);
            vo.setFoodState(1);

            if(i%4 == 0){
                vo.setSpecialState(1);
            }else{
                vo.setSpecialState(0);
            }

            if(i % 2 == 0){
                vo.setTravelTicketState(1);
            }else{
                vo.setTravelTicketState(0);
            }

            if(i % 4 == 0){
                vo.setTravelGuideState(1);
            }else{
                vo.setTravelGuideState(0);
            }

            if(i % 8 == 0){
                vo.setTravelSpecialState(1);
            }else{
                vo.setTravelSpecialState(0);
            }

            vo.setDefaultSort(random.nextInt(20));
            int goodNum =  random.nextInt(2000) ;
            vo.setF_goodCommentNum(goodNum);
            vo.setF_recommendNum(random.nextInt(3000));
            vo.setF_totalCommentNum(goodNum+random.nextInt(1000));
            double basePrice = 20 ;
            vo.setFoodAveragePrice(basePrice+random.nextInt(30));
            vo.setFoodTotalNum(random.nextInt(100));
            long lgood = random.nextInt(2000) ;
            vo.setL_goodCommentNum(lgood);
            vo.setL_totalCommentNum(lgood+random.nextInt(1000));
            vo.setL_recommendNum(random.nextInt(3000));
            vo.setLandmarkId(i%3);
            vo.setLiveLowestPrice(100+random.nextInt(200));
            ShopListVo.Location location = vo.new Location();

            double lon = random.nextInt(100) ;
            double lat = random.nextInt(90);
            location.setLon(lon);
            location.setLat(lat);

            vo.setLocation(location);
            long tgood = random.nextInt(2000) ;
            vo.setT_goodCommentNum(tgood);
            vo.setT_recommendNum(random.nextInt(3000));
            vo.setT_totalCommentNum(tgood+random.nextInt(800));
            vo.setTravelGuideLowesrPrice(50+random.nextInt(100));
            vo.setTravelSpecialLowestPrice(100+random.nextInt(100));
            vo.setTravelTicketLowestPrice(10+random.nextInt(200));

            long sgood = random.nextInt(1000) ;
            vo.setS_goodCommentNum(sgood);
            vo.setS_recommendNum(random.nextInt(2000));
            vo.setS_totalCommentNum(sgood+random.nextInt(1000));
            vo.setSpecialFoodLowestPrice(random.nextInt(100));
            vo.setShopPic("http://www.whateverblake.com/shoplist_"+i+".jpg");
            bulkRequestBuilder.add(client.prepareIndex().setIndex("shoplist").setType("shopinfo").setId(String.valueOf(i)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;

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









}

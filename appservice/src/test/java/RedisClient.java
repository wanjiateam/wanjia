import com.google.gson.reflect.TypeToken;
import com.wanjia.entity.PopularityRecommendEntity;
import com.wanjia.utils.JsonUtil;
import com.wanjia.vo.ResortLandmarkVo;
import com.wanjia.vo.travel.FamilyActivityPictureVo;
import com.wanjia.vo.travel.GuidePictureVo;
import com.wanjia.vo.travel.ShopResortPictureVo;
import com.wanjia.vo.travel.ShopTicketNoticeVo;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by blake on 2016/6/13.
 */
public class RedisClient {

    private String redisIp = "112.124.39.68" ;
    private int  redisPort=6660 ;
    Jedis jedis = null ;

    @Before
    public void initRedisClient(){
        jedis = new Jedis(redisIp,redisPort);
    }

    @Test
    public void addSortedSet(){

        Map<String,Double> map = new HashMap<String,Double>() ;

        Random random = new Random();

        int goodnum = 100;
        int recommendnum = 180 ;
        int productType = 4 ;
        String picPrefix = "special" ;

        for(int i =1 ; i <5 ; i++){
            goodnum+=random.nextInt(1000) ;
            recommendnum+=random.nextInt(1800) ;
            PopularityRecommendEntity entity = new PopularityRecommendEntity() ;
            entity.setGoodNum(goodnum);
            entity.setShopId(i);random.nextInt(1000);
            entity.setPruductType(productType);
            entity.setPicUrl("http://www.whateverblake.com/"+picPrefix+i+".jpg");
            entity.setRecommendNum(recommendnum);
            entity.setResortId(i);
            entity.setResortName("景区"+i);
            entity.setShopUrl("www.wanjia"+i+".com");
            entity.setShopName("店家"+i);
            goodnum =100 ;
            recommendnum = 180 ;
            map.put(JsonUtil.toJsonString(entity),Integer.valueOf(random.nextInt(1000)).doubleValue()) ;
        }



        jedis.zadd("popularity",map) ;

    }

    //添加店家住房须知
    @Test
    public void addShopNotice(){

        Map<String,Double> map = new HashMap<String,Double>() ;
        map.put("预支付：下单后，需在线预付房费，方可生效",6d);
        map.put("取消变更：订单支付前可随时变更；订单支付后需和店家沟通，并扣除一定手续费；",3d);
        map.put("实名入驻：需持身份证、护照等证件入住；",1d);

        Random random = new Random();
        String picPrefix = "room_notice_" ;

        for(int i =1 ; i <=20 ; i++){
            jedis.zadd(picPrefix+i,map) ;
        }

    }

    //添加景区图片
    @Test
    public void addResortPics(){

        Map<String,Double> map = new HashMap<String,Double>() ;
        for(int i = 1 ;  i<=20 ; i++){
            ShopResortPictureVo vo = new ShopResortPictureVo() ;
            vo.setPicUrl("http://www.whateverblake.com/shop_resort_picture_"+i+".jpg");
            vo.setPicDesc("好山，好水，好风光");
            vo.setResortName("九华山");
            vo.setResortId(1);
            map.put(JsonUtil.toJsonString(vo),Double.valueOf(i)) ;
        }
        Random random = new Random();
        String picPrefix = "resort_picture_list_" ;

        for(int i =1 ; i <=20 ; i++){
            jedis.zadd(picPrefix+i,map) ;
        }

    }
    //添加门票提供的服务
    @Test
    public void addResorService(){

        Map<String,Double> map = new HashMap<String,Double>() ;
        map.put("缆车",1d) ;
        map.put("电动车",1d) ;
        map.put("当天有效",1d) ;
        map.put("景区大巴",1d) ;
        map.put("通票",1d) ;

        Random random = new Random();
        String picPrefix = "resort_picture_list_" ;

        for(int i =1 ; i <=20 ; i++){
            for(int j=1 ; j <=3; j++){
                String key =  "ticketservice_"+i+"_"+j;
                jedis.zadd(key,map) ;
            }
        }

    }

    //获得门票的票种介绍

    @Test
    public void addResortNotice(){

            Map<String,Double> map = new HashMap<String,Double>() ;

            ShopTicketNoticeVo vo = new ShopTicketNoticeVo() ;
            vo.setResortId(1);
            vo.setTicketIntroduce("免票条件");
            Set<String> notices = new HashSet<String>();
            notices.add("16岁以下儿童");
            vo.setTicketInfo(notices);
            map.put(JsonUtil.toJsonString(vo),Double.valueOf(10));

            notices = new HashSet<String>();
            notices.add("学生、教师、军人");
            notices.add("60岁以上老人") ;
            vo.setTicketIntroduce("优惠票购买条件");
            vo.setTicketInfo(notices);

            map.put(JsonUtil.toJsonString(vo),Double.valueOf(2));

          notices = new HashSet<String>();
          vo.setTicketIntroduce("其余游客，都必须购买全票方可进入景区内");
          vo.setTicketInfo(notices);

         map.put(JsonUtil.toJsonString(vo),Double.valueOf(2));



        Random random = new Random();
        String picPrefix = "resort_notice_" ;

        for(int i =1 ; i <=20 ; i++){
            jedis.zadd(picPrefix+i,map) ;
        }

    }


    //添加导游图片
    @Test
    public void addGuidePics(){

        for(int i = 1 ;  i<=20 ; i++){

            Map<String,Double> map = new HashMap<String,Double>() ;
            for(int j=1;j<=3 ;j++){
                GuidePictureVo vo = new GuidePictureVo() ;
                vo.setShopId(i);
                vo.setGuideId(1);
                vo.setPicDesc("导游图片");
                vo.setPicName("美丽的导游");
                vo.setPicUrl("http://www.whateverblake.com/shop_guide_picture_"+j+".jpg");
                map.put(JsonUtil.toJsonString(vo),Double.valueOf(j)) ;
            }

            jedis.zadd("shop_guide_"+i+"_1",map) ;
        }


    }

    //添加自助游图片
    @Test
    public void addFamilyActivityPics(){

        for(int i = 1 ;  i<=20 ; i++){

            for(int j=1;j<=3 ;j++){
                Map<String,Double> map = new HashMap<String,Double>() ;
                for(int p = 1 ;  p <=3 ;p++){
                   FamilyActivityPictureVo vo = new FamilyActivityPictureVo() ;
                   vo.setShopId(i);
                   vo.setActivityId(1);
                   vo.setPicDesc("好玩的农家游玩项目");
                   vo.setPicName("好玩的项目");
                   vo.setPicUrl("http://www.whateverblake.com/shop_familyactivity_picture_"+p+".jpg");
                   map.put(JsonUtil.toJsonString(vo),Double.valueOf(j)) ;
               }
                jedis.zadd("shop_familityactivity_"+i+"_"+j,map) ;
            }

        }


    }

    //添加自助游备注
    @Test
    public void addFamilyActivityNote(){

        for(int i = 1 ;  i<=20 ; i++){
            for(int j=1;j<=3 ;j++){
                 Map<String,Double> map = new HashMap<String,Double>() ;
                 map.put("游船项目用时估计2小时，从地点1出发一直游玩到地点2",1d);
                 map.put("游船过程中有全程解说",2d);
                 map.put("特别是和情侣、亲子游玩",3d);

                jedis.zadd("familyActivity_notice_"+i+"_"+j,map) ;

            }
            }

        }




    //添加导游备注
    @Test
    public void addGuideNote(){

        for(int i = 1 ;  i<=20 ; i++){
            Map<String,Double> map = new HashMap<String,Double>() ;
            map.put("店家免费提供旅游路线指导",1d);
            map.put("联系店家可以车接车送",2d);
            jedis.zadd("guide_"+i+"_1",map) ;
        }


    }


    @Test
    public void setKeyValue(){
        String key1 = "resortDestination" ;
        String key2 = "hotDestination";
        String value = jedis.get(key1) ;
        jedis.set("hotDestination",value) ;
    }

    @Test
    public void addKeyListValue(){

        Random random = new Random() ;

        Character[] name = {'A','B','C','D','E','F','G','H','I'} ;
        int p = 0;
        for(int i =1 ; i <=3 ; i++){

            List<ResortLandmarkVo> vos = new ArrayList<ResortLandmarkVo>() ;
            for(int j=0;j<3;j++){
                ResortLandmarkVo vo = new ResortLandmarkVo();
                vo.setResortId(i);
                vo.setLandmarkId(j);
                vo.setLandmarkName("地标_"+name[p]);
                vo.setLon(random.nextInt(180));
                vo.setLat(random.nextInt(90));
                vo.setIsValid(1);
                p++ ;
                vos.add(vo) ;
            }
            jedis.set("resort_"+i,JsonUtil.toJsonString(vos)) ;
        }
    }

    @Test
    public void getKeyListValue(){

        long resortId  = 2 ;
        String key = "resort_"+resortId ;
        String value = jedis.get(key) ;
        List<ResortLandmarkVo> vos = (List<ResortLandmarkVo>)JsonUtil.toList(value,new TypeToken<List<ResortLandmarkVo>>(){}.getType());
        System.out.println(vos.size());
    }

    @Test
    public void addHotelPrice(){
        Random random = new Random() ;
        String prefix = "shop_room_price_";
        for(int i=1;i <=20 ; i++){
            for(int j=0 ; j<=3 ; j++){
                int base = random.nextInt(300) ;
                String key = prefix+i+"_"+j ;
                jedis.hset(key,"normal",String.valueOf(base));
                jedis.hset(key,"weekend",String.valueOf(base+30));

                int times = random.nextInt(4);
                DateTime dateTime = new DateTime("2016-7-7") ;

                for(int p= 1 ; p<= times ;p++){
                    int gap = random.nextInt(30);
                    dateTime = dateTime.plusDays(gap);
                    String dateStr =  dateTime.toString("yyyy-MM-dd") ;
                    jedis.hset(key,dateStr,String.valueOf(base+70));
                }
            }

        }
    }


    @Test
    public void getHotelPrice(){
        String prefix = "shop_room_price_";
        for(int i=1;i <=20 ; i++){
            for(int j=1 ; j<=3 ; j++){
                String key = prefix+i+"_"+j ;
                Map<String,String> map = jedis.hgetAll(key) ;
                Set<Map.Entry<String,String>> entries = map.entrySet() ;
                for(Map.Entry<String,String> entry : entries){
                    System.out.println("key is = "+entry.getKey()+"----value = "+entry.getValue());
                }
            }
        }
    }

    @Test
    public void addTicketPrice(){

        Random random = new Random() ;
        String ticketKeyPrefix = "shop_travel_ticket_price_";
        for(int i=1 ; i <=20 ; i++){
            for(int j=0 ; j<=3 ; j++){
                String key = ticketKeyPrefix+i+"_"+j ;
                int base = random.nextInt(200) ;

                jedis.hset(key,"normal",String.valueOf(base));
                int times = random.nextInt(4);
                DateTime dateTime = new DateTime("2016-7-7") ;

                for(int p= 1 ; p<= times ;p++){
                    int gap = random.nextInt(30);
                    dateTime = dateTime.plusDays(gap);
                    String dateStr =  dateTime.toString("yyyy-MM-dd") ;
                    jedis.hset(key,dateStr,String.valueOf(base+70));
                }
            }

        }

    }

    @Test
    public void addGuidePrice() {

        Random random = new Random();
        String ticketKeyPrefix = "shop_travel_guide_price_";
        for (int i = 1; i <= 20; i++) {
            for (int j = 0; j < 1; j++) {
                String key = ticketKeyPrefix + i + "_" + j;
                int base = random.nextInt(200);

                jedis.hset(key, "normal", String.valueOf(base));
                int times = random.nextInt(4);
                DateTime dateTime = new DateTime("2016-7-7");

                for (int p = 1; p <= times; p++) {
                    int gap = random.nextInt(30);
                    dateTime = dateTime.plusDays(gap);
                    String dateStr = dateTime.toString("yyyy-MM-dd");
                    jedis.hset(key, dateStr, String.valueOf(base + 70));
                }
            }

        }
    }

    @Test
    public void addFamilyActivityPrice() {

        Random random = new Random();
        String ticketKeyPrefix = "shop_travel_familyactivity_price_";
        for (int i = 1; i <= 20; i++) {
            for (int j = 0; j < 3; j++) {
                String key = ticketKeyPrefix + i + "_" + j;
                int base = random.nextInt(600);

                jedis.hset(key, "normal", String.valueOf(base));
                int times = random.nextInt(4);
                DateTime dateTime = new DateTime("2016-7-7");

                for (int p = 1; p <= times; p++) {
                    int gap = random.nextInt(30);
                    dateTime = dateTime.plusDays(gap);
                    String dateStr = dateTime.toString("yyyy-MM-dd");
                    jedis.hset(key, dateStr, String.valueOf(base + 70));
                }
            }

        }
    }

}

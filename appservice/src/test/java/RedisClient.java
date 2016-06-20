import com.wanjia.entity.PopularityRecommendEntity;
import com.wanjia.utils.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by blake on 2016/6/13.
 */
public class RedisClient {

    private String redisIp = "120.76.130.191" ;
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

    @Test
    public void setKeyValue(){
        String key1 = "resortDestination" ;
        String key2 = "hotDestination";
        String value = jedis.get(key1) ;
        jedis.set("hotDestination",value) ;


    }
}

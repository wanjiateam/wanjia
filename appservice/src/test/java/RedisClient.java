import com.google.gson.reflect.TypeToken;
import com.wanjia.entity.PopularityRecommendEntity;
import com.wanjia.utils.JsonUtil;
import com.wanjia.vo.ResortLandmarkVo;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.*;

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
}

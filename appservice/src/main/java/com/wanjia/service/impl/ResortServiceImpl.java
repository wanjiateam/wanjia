package com.wanjia.service.impl;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.gson.reflect.TypeToken;
import com.wanjia.dao.ResortInfoMapper;
import com.wanjia.entity.ResortInfo;
import com.wanjia.service.ResortService;
import com.wanjia.utils.*;
import com.wanjia.vo.ResortDestinationVo;
import com.wanjia.vo.ResortLandmarkVo;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by blake on 2016/6/12.
 */
@Service("resortService")
public class ResortServiceImpl implements ResortService {


    private static Logger logger = Logger.getLogger(ResortServiceImpl.class);

    @Autowired
    ResortInfoMapper resortInfoMapper ;

    @Autowired
    RedisClient  redisClient ;

    @Autowired
    ElasticSearchClient elasticSearchClient ;

    @Override
    public ResortDestinationVo getAllResortNameAndPinYin() throws  Exception{


        ResortDestinationVo resortDestinationVo ;

        String resortDestinationInfos = redisClient.getValueByKey("resortDestination") ;

        if(resortDestinationInfos == null || resortDestinationInfos.equals("")){
            resortDestinationVo  = new ResortDestinationVo() ;
            List<ResortInfo> names = resortInfoMapper.getAllResortNameAndPinYin();
            if(names.size() > 0){
                for (ResortInfo info : names){
                    ResortDestinationVo.Destination destination = resortDestinationVo.new Destination() ;
                    destination.setResortid(info.getResortid());
                    destination.setName(info.getResortname());
                    destination.setPinYin(PinyinHelper.convertToPinyinString(info.getResortname(),"", PinyinFormat.WITHOUT_TONE));
                    destination.setBriefPinYin(PinyinHelper.getShortPinyin(info.getResortname()));
                    resortDestinationVo.addDestination(destination);
                }
            }

            redisClient.setKeyValue("resortDestination",JsonUtil.toJsonString(resortDestinationVo));

        }else{
            resortDestinationVo =  (ResortDestinationVo)JsonUtil.toObject(resortDestinationInfos,ResortDestinationVo.class) ;
        }
        return resortDestinationVo;
    }


    @Override
    public ResortDestinationVo getHotDestination() throws Exception {

        ResortDestinationVo resortDestinationVo = null  ;

        String resortDestinationInfos = redisClient.getValueByKey("hotDestination") ;

        if(resortDestinationInfos == null  || resortDestinationInfos.equals("")){
            resortDestinationVo = new ResortDestinationVo() ;
            //todo
            //check data in dbbase
        }else{
            resortDestinationVo =  (ResortDestinationVo)JsonUtil.toObject(resortDestinationInfos,ResortDestinationVo.class) ;
        }

        return resortDestinationVo;
    }

    @Override
    public void getLandmarkByResort(long resortId,String indexName,String esType,int from,int pageSize,PageResult pageResult) {
        String key = "resort_"+resortId ;
        List<ResortLandmarkVo> vos = null ;
        try {
           String value = redisClient.getValueByKey(key) ;
           vos = (List<ResortLandmarkVo> )JsonUtil.toList(value,new TypeToken<List<ResortLandmarkVo>>(){}.getType());
        }catch (Exception e) {
            logger.error("get landmark from redis error",e);
        }
        if (vos != null && vos.size() >0){
            pageResult.setTotalNumber(vos.size());
            pageResult.setResult(vos);
        }else{
            List<SortField> sortFields = new ArrayList<SortField>() ;
            sortFields.add(new SortField("landmarkId", SortOrder.ASC)) ;
            QueryBuilder queryBuilder = QueryBuilders.termQuery("resortId",resortId) ;
            QueryBuilder queryBuilder1 = QueryBuilders.termQuery("isValid",1) ;
            try {
                elasticSearchClient.queryDataFromEsWithPostFilter(queryBuilder,queryBuilder1,sortFields,null,indexName,esType,from,pageSize,ResortLandmarkVo.class,pageResult);
            } catch (Exception e) {
                pageResult.setE(e);
                logger.error("get landmark from es error",e);
            }
        }
    }
}

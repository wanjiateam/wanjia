package com.wanjia.service.impl;

import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.wanjia.dao.ResortInfoMapper;
import com.wanjia.entity.ResortInfo;
import com.wanjia.service.ResortService;
import com.wanjia.utils.JsonUtil;
import com.wanjia.utils.RedisClient;
import com.wanjia.vo.ResortDestinationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by blake on 2016/6/12.
 */
@Service("resortService")
public class ResortServiceImpl implements ResortService {



    @Autowired
    ResortInfoMapper resortInfoMapper ;

    @Autowired
    RedisClient  redisClient ;

    @Override
    public ResortDestinationVo getAllResortNameAndPinYin() throws  Exception{


        ResortDestinationVo resortDestinationVo ;

        String resortDestinationInfos = redisClient.getValueByKey("resortDestination") ;

        if(resortDestinationInfos == null || resortDestinationInfos.equals("")){

            resortDestinationVo  = new ResortDestinationVo() ;
            Map<Character,ResortDestinationVo.DestinationGroup> destinationGroupMap = new HashMap<Character,ResortDestinationVo.DestinationGroup>() ;

            List<ResortInfo> names = resortInfoMapper.getAllResortNameAndPinYin();
            if(names.size() > 0){
                for (ResortInfo info : names){
                    ResortDestinationVo.DestinationGroup  destinationGroup =  destinationGroupMap.get(info.getResortpinyin().charAt(0)) ;
                    if(destinationGroup == null){
                        destinationGroup = resortDestinationVo.new DestinationGroup() ;
                        destinationGroup.setGroup(info.getResortpinyin().charAt(0));
                        resortDestinationVo.addDestinationGroup(destinationGroup);
                        destinationGroupMap.put(info.getResortpinyin().charAt(0),destinationGroup) ;

                    }
                    ResortDestinationVo.DestinationGroup.Destination destination = destinationGroup.new Destination() ;
                    destination.setName(info.getResortname());
                    destination.setPinYin(info.getResortpinyin());
                    destination.setBriefPinYin(PinyinHelper.getShortPinyin(info.getResortname()));
                    destinationGroup.addDestination(destination);
                }
            }

            redisClient.setKeyValue("resortDestination",JsonUtil.toJsonString(resortDestinationVo));

        }else{
            resortDestinationVo =  (ResortDestinationVo)JsonUtil.toObject(resortDestinationInfos,ResortDestinationVo.class) ;
        }
        return resortDestinationVo;
    }


    public int getResortCount(){
        return resortInfoMapper.getResortCount() ;
    }
}

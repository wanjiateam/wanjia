package com.wanjia.service;

import com.wanjia.utils.PageResult;
import com.wanjia.vo.ResortDestinationVo;
import com.wanjia.vo.ResortLandmarkVo;

import java.util.List;
import java.util.Set;

/**
 * Created by blake on 2016/6/12.
 */
public interface ResortService {
     ResortDestinationVo getAllResortNameAndPinYin() throws Exception;
     ResortDestinationVo getHotDestination() throws Exception;
     void getLandmarkByResort(long resortId,String indexName,String esType,int from,int pageSize,PageResult pageResult);
}

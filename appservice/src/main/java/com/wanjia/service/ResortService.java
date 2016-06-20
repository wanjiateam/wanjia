package com.wanjia.service;

import com.wanjia.vo.ResortDestinationVo;

import java.util.List;
import java.util.Set;

/**
 * Created by blake on 2016/6/12.
 */
public interface ResortService {
     ResortDestinationVo getAllResortNameAndPinYin() throws Exception;
     int  getResortCount() throws Exception ;
}

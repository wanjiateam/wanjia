package com.wanjia.dao;

import com.wanjia.entity.ResortInfo;

import java.util.List;

public interface ResortInfoMapper {
    int deleteByPrimaryKey(Integer resortid);

    int insert(ResortInfo record);

    int insertSelective(ResortInfo record);

    ResortInfo selectByPrimaryKey(Integer resortid);

    int updateByPrimaryKeySelective(ResortInfo record);

    int updateByPrimaryKey(ResortInfo record);

    List<ResortInfo> getAllResortNameAndPinYin();
    int  getResortCount() ;
}
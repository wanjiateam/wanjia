package com.wanjia.dao;

import com.wanjia.entity.ResortInfo;
import com.wanjia.exceptions.MySqlException;

import java.util.List;

public interface ResortInfoMapper {
    int deleteByPrimaryKey(Integer resortid) throws MySqlException;

    int insert(ResortInfo record) throws MySqlException;

    int insertSelective(ResortInfo record) throws MySqlException;

    ResortInfo selectByPrimaryKey(Integer resortid) throws MySqlException;

    int updateByPrimaryKeySelective(ResortInfo record) throws MySqlException;

    int updateByPrimaryKey(ResortInfo record) throws MySqlException;

    List<ResortInfo> getAllResortNameAndPinYin() throws MySqlException;
    int  getResortCount()  throws MySqlException;
}
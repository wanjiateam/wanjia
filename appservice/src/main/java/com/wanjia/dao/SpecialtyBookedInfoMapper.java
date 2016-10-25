package com.wanjia.dao;

import com.wanjia.entity.order.book.SpecialtyBookedInfo;
import com.wanjia.exceptions.MySqlException;

import java.util.Map;

public interface SpecialtyBookedInfoMapper {

    int deleteByPrimaryKey(Long id) throws MySqlException;

    int insert(SpecialtyBookedInfo record) throws MySqlException;

    int insertSelective(SpecialtyBookedInfo record) throws MySqlException;

    SpecialtyBookedInfo selectByPrimaryKey(Long id) throws MySqlException;

    int updateByPrimaryKeySelective(SpecialtyBookedInfo record) throws MySqlException;

    int updateByPrimaryKey(SpecialtyBookedInfo record) throws MySqlException;

    SpecialtyBookedInfo selectByShopIdAndSpecialtyId(Map map) throws MySqlException ;

    int lockSpecialtyResource(Map map) throws MySqlException ;
}
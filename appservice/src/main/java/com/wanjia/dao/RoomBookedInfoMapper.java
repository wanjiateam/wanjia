package com.wanjia.dao;

import com.wanjia.entity.order.book.RoomBookedInfo;
import com.wanjia.exceptions.MySqlException;

import java.util.Date;
import java.util.Map;

public interface RoomBookedInfoMapper {
    int deleteByPrimaryKey(Long bookId) throws MySqlException;

    int insert(RoomBookedInfo record) throws MySqlException;

    int insertSelective(RoomBookedInfo record) throws MySqlException;

    RoomBookedInfo selectByPrimaryKey(Long bookId) throws MySqlException;

    int updateByPrimaryKeySelective(RoomBookedInfo record) throws MySqlException;

    int updateByPrimaryKey(RoomBookedInfo record) throws MySqlException;

    RoomBookedInfo selectByShopIdRoomIdAndDate(Map queryBookedRecordParamaters) throws MySqlException;

    int updateRoomNumberById(Map map) throws MySqlException;
}
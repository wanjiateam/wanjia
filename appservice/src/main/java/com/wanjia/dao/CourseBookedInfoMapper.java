package com.wanjia.dao;

import com.wanjia.entity.order.book.CourseBookedInfo;
import com.wanjia.exceptions.MySqlException;

import java.util.Map;

public interface CourseBookedInfoMapper {
    int deleteByPrimaryKey(Long bookId)throws MySqlException;

    int insert(CourseBookedInfo record) throws MySqlException;

    int insertSelective(CourseBookedInfo record) throws MySqlException;

    CourseBookedInfo selectByPrimaryKey(Long bookId) throws MySqlException;

    int updateByPrimaryKeySelective(CourseBookedInfo record) throws MySqlException;

    int updateByPrimaryKey(CourseBookedInfo record) throws MySqlException;

    CourseBookedInfo selectByShopIdCourseIdAndDate(Map map) throws MySqlException;
    int updateCourseBookedNumber(Map map) throws MySqlException;
}
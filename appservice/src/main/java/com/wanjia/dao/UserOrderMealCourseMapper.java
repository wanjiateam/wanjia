package com.wanjia.dao;

import com.wanjia.entity.order.UserOrderMealCourse;
import com.wanjia.exceptions.MySqlException;

import java.util.List;

public interface UserOrderMealCourseMapper {
    int deleteByPrimaryKey(Long id) throws MySqlException;

    int insert(UserOrderMealCourse record) throws MySqlException;

    int insertSelective(UserOrderMealCourse record) throws MySqlException;

    UserOrderMealCourse selectByPrimaryKey(Long id) throws MySqlException;

    int updateByPrimaryKeySelective(UserOrderMealCourse record) throws MySqlException;

    int updateByPrimaryKey(UserOrderMealCourse record) throws MySqlException;

    int  batchInsert(List<UserOrderMealCourse> userOrderMealCourseList) throws MySqlException;
}
package com.wanjia.service;

import com.wanjia.exceptions.RedisException;
import com.wanjia.vo.cart.*;

import java.util.List;
import java.util.Map;

/**
 * Created by blake on 2016/8/3.
 */
public interface ShopCartService {

    public int addRoomToShopCart(ShopCartRoomVo shopCartRoomVo) throws Exception;
    public long changeRoomNumber(long userId,long shopId,long roomId,int changeNumber , String startDate,String endDate) throws Exception;

    public List<ShopCartCourseVo> addMealToShopCart(ShopCartMealVo shopCartMealVo) throws Exception;
    public int delMeal(long userId,long shopId,String bookDate,int mealType) throws RedisException;
    public void cleanShopCartByUserId(long userId) throws RedisException;

    public int changeCourseNumber(long userId,long shopId,String bookDate,int mealType,long courseId,int changeNumber) throws Exception;

    public int addSpecialtyToShopCart(ShopCartSpecialtyVo shopCartSpecialtyVo) throws Exception;
    public int deleteAllSpecialtyByShopId(long userId,long shopId) throws  RedisException;
    public int updateSpecialtyNumber(long shopId,long userId,long specialtyId,int changeNumber) throws RedisException;
    public int addTravelTicketToShopCart(ShopCartTravelTicketVo shopCartTravelTicketVo) throws Exception;
    public int addTravelGuideToShopCart(ShopCartTravelGuideVo shopCartTravelGuideVo) throws Exception;
    public int addTravelFamilyActivityToShopCart(ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo) throws Exception;
    public int changeTravelNumber(long shopId,long userId,long id,int type,int changeNumber,String bookDate) throws Exception;
    public Map<Long, ShopCartProductContainerVo> getShopCartInfo(long userId) throws Exception ;
    public ShopCartMealVo getUserMealInfo(long shopId,long userId,String bookDate,int mealType) throws Exception ;
    public int getShopCartProductCount(long userId) throws Exception ;
    public int getMealCourseCount(long shopId,long userId,String bookDate,int mealType) throws Exception ;

    public int getRoomCount(long shopId,long userId,String startDate,String endDate,long roomId) throws Exception;
    public int getSpecialtyCount(long shopId,long userId,long specialtyId) throws Exception;
    public int getTravelTicketCount(long shopId,long userId,long ticketId,String tourDate) throws Exception;
    public int getTravelFACount(long shopId,long userId,long fmId,String tourDate) throws Exception;




}

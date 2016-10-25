package com.wanjia.service;

import com.wanjia.exceptions.RedisException;
import com.wanjia.vo.ShopProductLogoVo;
import com.wanjia.vo.ShopRecommendAndCommentNumberVo;
import com.wanjia.vo.live.RoomBookVo;
import com.wanjia.vo.live.RoomPictureVo;
import com.wanjia.vo.live.RoomVo;
import com.wanjia.vo.live.ShopRoomAttribute;
import com.wanjia.vo.restaurant.CourseVo;
import com.wanjia.vo.restaurant.ShopCourseDetailInfoVo;
import com.wanjia.vo.restaurant.ShopCoursePictureVo;
import com.wanjia.vo.speciality.SpecialityPictureVo;
import com.wanjia.vo.speciality.SpecialtyNoteVo;
import com.wanjia.vo.speciality.SpecialtyVo;
import com.wanjia.vo.travel.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by blake on 2016/6/25.
 */
public interface ShopInfoService {

    public List<ShopProductLogoVo> getShopProductLogoByShopId(long shopId, int productType,String indexName,String esType) throws Exception ;
    public List<RoomVo> getShopRoomVoByShopId(long shopId, long startDate,long endDate, String indexName, String esType) throws Exception ;
    public List<CourseVo> getShopCourseVoByShopId(long shopId, long bookDate , String indexName, String esType) throws Exception ;
    public List<SpecialtyVo> getShopSpecialtyVoByShopId(long shopId, String indexName, String esType) throws Exception ;
    public TravelVo getShopTravelVoByShopId(long shopId,long startDate , long endDate) throws Exception ;
    public GuideBookVo getShopTravelGuideBookInfoShopId(long shopId,long guideId ,long dateTime) throws Exception ;

    public ShopRoomAttribute getShopRoomDetailInfo(long shopId,long roomId) throws Exception ;
    public List<RoomPictureVo> getShopRoomPictures(long shopId, long roomId) throws Exception ;
    public List<ShopCourseDetailInfoVo> getShopCourseDetailInfo(long shopId, long courseId) throws Exception ;
    public List<ShopCoursePictureVo> getShopCoursePicture(long shopId, long courseId) throws Exception ;
    public List<ShopResortPictureVo> getShopResortPicture(long resortId) throws Exception ;
    public List<ShopTicketNoticeVo> getShopTicketNoticeByResortId(long resortId) throws Exception;
    public List<ShopTicketNoteVo> getShopTicketNote(long shopId, long ticketId) throws Exception;
    public Set<String> getShopTicketService(long resortId, long ticketId) throws Exception;

    public List<FamilyActivityPictureVo> getShopFamilyActivityPicture(long shopId,long activityId) throws Exception ;
    public FamilyActivityNoticeVo getShopFamilyActivityNotice(long shopId, long activityId) throws Exception;

    public List<SpecialityPictureVo> getShopSpecialtyPicture(long shopId,long specialtyId)  throws Exception ;
    public List<SpecialtyNoteVo> getShopSpecialtyNote(long shopId,long specialtyId)  throws Exception ;
    public List<GuidePictureVo> getShopGuidePicture(long shopId, long guideId) throws Exception ;
    public GuideNoteVo getShopGuideNotice(long shopId, long guideId) throws Exception ;
    public double getRoomTotalPriceDuringDateRange(long shopId, long roomId,long startDate,long endDate) throws RedisException;
    public List<RoomBookVo> getRoomBookInfoDuringDateRange(long shopId, long roomId, long startDate, long endDate) throws Exception ;
    public int  getRoomAllowBookNumberDuringDateRange(long shopId, long roomId, String startDate, String endDate) throws Exception ;

    public double getTravelPrice(String key,long dateTime) throws RedisException ;

    public ShopRecommendAndCommentNumberVo getShopRecommendAndCommentNumber(long shopId, String indexname, String indextype) throws Exception ;

    public  boolean checkRoomVoExistById(String id) throws Exception;
    public  boolean checkCourseVoExistById(String id) throws Exception;
    public  boolean checkSpecialtyVoExistById(String id) throws Exception;
    public  boolean checkTicketVoExistById(String id) throws Exception;
    public  boolean checkGuideVoExistById(String id) throws Exception;
    public  boolean checkFamilyActivityVoByExistId(String id) throws Exception;

    public  List<RoomVo> getRoomVoListById(Set<String> ids) throws Exception;
    public  List<CourseVo> getCourseVoListById(Set<String> ids) throws Exception;
    public  List<SpecialtyVo> getSpecialtyVoListById(Set<String> ids) throws Exception;
    public  List<TicketVo> getTicketVoListById(Set<String> ids) throws Exception;
    public  List<GuideVo> getGuideVoListById(Set<String> ids) throws Exception;
    public  List<FamilyActivityVo> getFamilyActivityVoListById(Set<String> ids) throws Exception;



}

package com.wanjia.service;

import com.wanjia.vo.ShopProductLogoVo;
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
    public TravelVo getShopTravelVoByShopId(long shopId) throws Exception ;
    public ShopRoomAttribute getShopRoomDetailInfo(long shopId,long roomId) throws Exception ;
    public List<RoomPictureVo> getShopRoomPictures(long shopId, long roomId) throws Exception ;
    public List<ShopCourseDetailInfoVo> getShopCourseDetailInfo(long shopId, long courseId) throws Exception ;
    public List<ShopCoursePictureVo> getShopCoursePicture(long shopId, long courseId) throws Exception ;
    public List<ShopResortPictureVo> getShopResortPicture(long resortId) throws Exception ;
    public ShopTicketNoticeVo getShopTicketNoticeByResortId(long resortId) throws Exception;
    public ShopTicketNoteVo getShopTicketNote(long shopId, long ticketId) throws Exception;
    public Set<String> getShopTicketService(long resortId, long ticketId) throws Exception;

    public List<FamilyActivityPictureVo> getShopFamilyActivityPicture(long shopId,long activityId) throws Exception ;
    public FamilyActivityNoticeVo getShopFamilyActivityNotice(long shopId, long activityId) throws Exception;

    public List<SpecialityPictureVo> getShopSpecialtyPicture(long shopId,long specialtyId)  throws Exception ;
    public SpecialtyNoteVo getShopSpecialtyNote(long shopId,long specialtyId)  throws Exception ;




}

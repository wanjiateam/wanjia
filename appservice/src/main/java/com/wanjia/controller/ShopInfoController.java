package com.wanjia.controller;

import com.wanjia.service.ShopInfoService;
import com.wanjia.utils.DateUtil;
import com.wanjia.utils.JsonReturnBody;
import com.wanjia.utils.JsonUtil;
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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

/**
 * 展示店家住食游产的相关的详细信息
 * Created by blake on 2016/6/25.
 */

@Controller
@RequestMapping("/shop")
public class ShopInfoController {

    Logger logger = Logger.getLogger(ShopInfoController.class);
    @Autowired
    ShopInfoService shopInfoService;

    /**
     * 获取店家的二级界面的logo
     * @param shopId
     * @param productType 1住2食3游4产
     * @return
     */
    @RequestMapping(value = "logo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopLogoByProductType(long shopId, int productType) {

        String indexName = "shop_logo";
        String type = "logo";
        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopHotelListPaging");
        try {
            List<ShopProductLogoVo> logos = shopInfoService.getShopProductLogoByShopId(shopId, productType, indexName, type);
            generateJsonReturnResult(logos,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }


        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     *获得店家住的具体住房的信息 二级界面
     * @param shopId
     * @param dateStart
     * @param dateEnd
     * @return
     */
    @RequestMapping(value = "live", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopLiveInfo(long shopId,String dateStart,String dateEnd) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopLiveInfo");

        try {
            long startTime = DateUtil.parseDateToLongValue(dateStart) ;
            long endTime =   DateUtil.parseDateToLongValue(dateEnd) ;

            String indexName = "shop_room";
            String type = "room";
            List<RoomVo>  roomVos = shopInfoService.getShopRoomVoByShopId(shopId,startTime,endTime,indexName,type) ;
            generateJsonReturnResult(roomVos,jsonReturnBody);
        }catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }
    /**
     *获得店家住的具体房间的属性和注意是事项信息  三级列表
     * @param shopId
     * @param roomId
     * @return
     */
    @RequestMapping(value = "roomInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopRoomInfo(long shopId ,long roomId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopRoomInfo");

        try {
            //包含了房间的属性信息，包括wifi，有窗，和住房注意事项
            ShopRoomAttribute shopRoomAttribute = shopInfoService.getShopRoomDetailInfo(shopId,roomId) ;
            generateJsonReturnResult(shopRoomAttribute,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     *获得店家住的具体房间的图片信息  三级列表
     * @param shopId
     * @param roomId
     * @return
     */
    @RequestMapping(value = "roomPic", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopRoomPic(long shopId ,long roomId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopRoomPic");

        try {
            //获得房型的图片信息
            List<RoomPictureVo> roomPictureVos = shopInfoService.getShopRoomPictures(shopId,roomId) ;
            generateJsonReturnResult(roomPictureVos,jsonReturnBody) ;
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }



    /**
     *获得菜品的列表信息 二级界面
     * @param shopId
     * @param dateTime
     * @return
     */
    @RequestMapping(value = "course", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopCourseList(long shopId,String dateTime) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopCourseList");

        try {
            long bookTime = DateUtil.parseDateToLongValue(dateTime) ;

            String indexName = "shop_course";
            String type = "course";
            List<CourseVo>  courseVos = shopInfoService.getShopCourseVoByShopId(shopId,bookTime,indexName,type) ;
            generateJsonReturnResult(courseVos,jsonReturnBody);

        }catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     * 获得菜品的详细信息 三级界面
     * @param shopId
     * @param courseId
     * @return
     */
    @RequestMapping(value = "courseInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopCourseInfo(long shopId,long courseId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopCourseInfo");

        try {
            List<ShopCourseDetailInfoVo>  shopCourseDetailInfoVos = shopInfoService.getShopCourseDetailInfo(shopId,courseId) ;
            generateJsonReturnResult(shopCourseDetailInfoVos,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     * 获得菜品的图片信息 三级界面
     * @param shopId
     * @param courseId
     * @return
     */
    @RequestMapping(value = "coursePic", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopCoursePic(long shopId,long courseId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopCoursePic");

        try {

            List<ShopCoursePictureVo>  shopCoursePictureVos = shopInfoService.getShopCoursePicture(shopId, courseId) ;
            generateJsonReturnResult(shopCoursePictureVos,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }




    /**
     * 获得特产信息 二级界面
     * @param shopId
     * @return
     */
    @RequestMapping(value = "specialty", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopSpecialtyInfo(long shopId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopSpecialtyInfo");
        try {
            String indexName = "shop_specialty_item";
            String type = "specialty";
            List<SpecialtyVo>  specialtyVos = shopInfoService.getShopSpecialtyVoByShopId(shopId,indexName,type);
            generateJsonReturnResult(specialtyVos,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     * 获得特产图片信息 三级界面
     * @param shopId
     * @return
     */
    @RequestMapping(value = "specialtyPic", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopSpecialtyPic(long shopId,long specialtyId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopSpecialtyPic");

        try {
            List<SpecialityPictureVo>  specialityPictureVos = shopInfoService.getShopSpecialtyPicture(shopId, specialtyId) ;
            generateJsonReturnResult(specialityPictureVos,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     * 获得特产的备注信息 三级界面
     * @param shopId
     * @return
     */
    @RequestMapping(value = "specialtyNote", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopSpecialtyNote(long shopId,long specialtyId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopSpecialtyNote");

        try {
            List<SpecialtyNoteVo> specialtyNoteVos = shopInfoService.getShopSpecialtyNote(shopId, specialtyId) ;
            generateJsonReturnResult(specialtyNoteVos,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     * 获得游的列表信息 二级列表
     * @param shopId
     * @return
     */
    @RequestMapping(value = "travel", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelInfo(long shopId,String dateStart,String dateEnd) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelInfo");

        try {
            long startTime = DateUtil.parseDateToLongValue(dateStart) ;
            long endTime =   DateUtil.parseDateToLongValue(dateEnd) ;
            TravelVo  travelVo = shopInfoService.getShopTravelVoByShopId(shopId,startTime,endTime) ;
            generateJsonReturnResult(travelVo,jsonReturnBody);
        }
         catch (Exception e) {
             generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     * 获得游的门票详细信息 三级列表
     * @param resortId
     * @return
     */
    @RequestMapping(value = "ticketResortPic", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelTicketResortPic(long resortId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelTicketInfo");

        try {
            List<ShopResortPictureVo>  shopResortPictureVos = shopInfoService.getShopResortPicture(resortId) ;
            generateJsonReturnResult(shopResortPictureVos,jsonReturnBody);
        } catch (Exception e) {
           generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     * 获得游的门票提供的服务信息 如缆车，电动车。。。 三级列表
     * @param resortId
     * @return
     */
    @RequestMapping(value = "ticketService", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelTicketService(long resortId,long ticketId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelTicketService");

        try {
            Set<String> services  = shopInfoService.getShopTicketService(resortId, ticketId) ;
            ShopTicketServiceVo shopTicketServiceVo = null ;
            if(services !=null && services.size() > 0){
                shopTicketServiceVo = new ShopTicketServiceVo() ;
                shopTicketServiceVo.setResortId(resortId);
                shopTicketServiceVo.setTicketId(ticketId);
                shopTicketServiceVo.setServices(services);
            }
            generateJsonReturnResult(shopTicketServiceVo,jsonReturnBody);

        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     * 获得游的门票的备注 三级界面
     * @param shopId
     * @param  ticketId
     * @return
     */
    @RequestMapping(value = "ticketNote", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelTicketNote(long shopId,long ticketId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelTicketNote");

        try {
            List<ShopTicketNoteVo> shopTicketNoteVos  = shopInfoService.getShopTicketNote(shopId,ticketId) ;
            generateJsonReturnResult(shopTicketNoteVos,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     * 获得游的门票的须知 三级界面
     * @param  resortId
     * @return
     */
    @RequestMapping(value = "ticketNotice", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelTicketNotice(long resortId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelTicketNotice");

        try {
            List<ShopTicketNoticeVo> shopTicketNoticeVos  = shopInfoService.getShopTicketNoticeByResortId(resortId) ;
            generateJsonReturnResult(shopTicketNoticeVos,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     * 获得游的导游的图片信息 三级列表
     * @param shopId
     * @param  guideId
     * @return
     */
    @RequestMapping(value = "guidePic", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelGuidePic(long shopId,long guideId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelGuidePic");

        try {
            List<GuidePictureVo> guidePictureVos  = shopInfoService.getShopGuidePicture(shopId, guideId) ;
            generateJsonReturnResult(guidePictureVos,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     * 获得游的导游的介绍信息 三级列表
     * @param shopId
     * @param  guideId
     * @return
     */
    @RequestMapping(value = "guideNote", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelGuideNote(long shopId,long guideId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelGuideNote");

        try {
            GuideNoteVo guideNoteVo  = shopInfoService.getShopGuideNotice(shopId, guideId);
            generateJsonReturnResult(guideNoteVo,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     * 获得游的农家特色游的图片信息 三级列表
     * @param shopId
     * @param  familyActivityId
     * @return
     */
    @RequestMapping(value = "familyActivityPic", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelFamilyActivityPic(long shopId,long familyActivityId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelFamilyActivityPic");

        try {
            List<FamilyActivityPictureVo> familyActivityPictureVos  = shopInfoService.getShopFamilyActivityPicture(shopId, familyActivityId) ;
            generateJsonReturnResult(familyActivityPictureVos,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     * 获得游的农家特色游的备注信息 三级列表
     * @param shopId
     * @param  familyActivityId
     * @return
     */
    @RequestMapping(value = "familityActivityNote", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelFamilyActivityNote(long shopId,long familyActivityId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelFamilyActivityNote");

        try {
            FamilyActivityNoticeVo  familyActivityNoticeVo  = shopInfoService.getShopFamilyActivityNotice(shopId, familyActivityId) ;
            generateJsonReturnResult(familyActivityNoticeVo,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     * 获得点店家住房不同日期的价格
     * @param shopId
     * @param roomId
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "getRoomPriceByDateRange", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopRoomPriceByDateRange(long shopId,long roomId,String startDate,String endDate) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopRoomPriceByDateRange");

        try {
            long startDateNum = DateUtil.parseDateToLongValue(startDate);
            long endDateNum = DateUtil.parseDateToLongValue(endDate) ;

            Double  totalPrice  = shopInfoService.getRoomTotalPriceDuringDateRange(shopId,roomId,startDateNum,endDateNum);
            generateJsonReturnResult(totalPrice,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     * 获得点店家住房不同日期已经预订的房间数
     * @param shopId
     * @param roomId
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "getRoomBookInfoByDateRange", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopRoomBookInfoByDateRange(long shopId,long roomId,String startDate,String endDate) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopRoomBookInfoByDateRange");

        try {
            long startDateNum = DateUtil.parseDateToLongValue(startDate);
            long endDateNum = DateUtil.parseDateToLongValue(endDate) ;

            List<RoomBookVo> roomBookVos  = shopInfoService.getRoomBookInfoDuringDateRange(shopId,roomId,startDateNum,endDateNum);
            generateJsonReturnResult(roomBookVos,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     * 获得店家门票导游特色游的不同日期的价格
     * @param shopId
     * @param id 导游 门票 特色游的id
     * @param travelType 1门票2导游3农家特色游
     * @param dateTime
     * @return
     */
    @RequestMapping(value = "getTravelPrice", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelPrice(long shopId,long id,int travelType ,String dateTime) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelPrice");

        try {
            long dateTimeNum = DateUtil.parseDateToLongValue(dateTime);
            String suffix  = shopId+"_"+id ;
            String key = "" ;
            if(travelType == 1){
                key = "shop_travel_ticket_price_"+suffix;
            }else if(travelType == 2){
                key = "shop_travel_guide_price_"+suffix ;
            }else if(travelType == 3){
                key =  "shop_travel_familyactivity_price_"+suffix ;
            }

            Double  totalPrice  = shopInfoService.getTravelPrice(key,dateTimeNum) ;
            generateJsonReturnResult(totalPrice,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }



    @RequestMapping(value = "getTravelGuideBookInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelGuideBookInfo(long shopId,long guideId,String dateTime) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getTravelGuideBookInfo");

        try {
            long dateTimeNum = DateUtil.parseDateToLongValue(dateTime);

            GuideBookVo guideBookVo    = shopInfoService.getShopTravelGuideBookInfoShopId(shopId,guideId,dateTimeNum) ;
            generateJsonReturnResult(guideBookVo,jsonReturnBody);
        } catch (Exception e) {
            generateJsonReturnResultException(e,jsonReturnBody);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }




    private void generateJsonReturnResult(Object result , JsonReturnBody jsonReturnBody){

        if(result instanceof  List){
            List list = (List) result ;
            if(list.size() > 0){
                jsonReturnBody.setCode(1);
                jsonReturnBody.setMessage(list);
            }else{
                jsonReturnBody.setCode(2);
                jsonReturnBody.setMessage("get a empty result");
            }
        } else{
            if(result != null){
                jsonReturnBody.setCode(1);
                jsonReturnBody.setMessage(result);
            }else{
                jsonReturnBody.setCode(2);
                jsonReturnBody.setMessage("get a empty result");
            }
        }
    }




    private void generateJsonReturnResultException(Exception e, JsonReturnBody jsonReturnBody){

        if(e instanceof  ParseException){
            jsonReturnBody.setCode(3);
            jsonReturnBody.setMessage("date format  error");
            logger.error("date format error for shopTravel list",e);
        }else {
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("query data from es error");
            logger.error("query es error",e);
        }

     }


    /**
     * 获得店家住食游产的好评数和评论数
     * @param shopId
     * @param type  1住 2食 3游 4产
     * @return
     */
    @RequestMapping(value = "getRecommendAndCommentNumber", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JsonReturnBody getShopRecommendAndCommentNumber(long shopId,int type) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopRecommendAndCommentNumber");
        String indexname = "" ;
        String indextype = "" ;



        switch (type){

            case 1 : indexname = "shop_hotel" ; indextype = "hotel" ; break;
            case 2 : indexname = "shop_restaurant" ; indextype = "restaurant" ; break;
            case 3 : indexname = "shop_travel" ; indextype = "travel" ; break;
            case 4 : indexname = "shop_specialty" ; indextype = "specialty" ; break;
        }

        if(indexname.equals("") || indextype.equals("")){
            jsonReturnBody.setCode(2);
            jsonReturnBody.setMessage("input parameter error,please check it !!");
        }else{
            try {
                ShopRecommendAndCommentNumberVo shopRecommendAndCommentNumberVo =  shopInfoService.getShopRecommendAndCommentNumber(shopId,indexname,indextype) ;
                if(shopRecommendAndCommentNumberVo != null){
                    jsonReturnBody.setCode(1);
                    jsonReturnBody.setMessage(shopRecommendAndCommentNumberVo);
                }else{
                    jsonReturnBody.setCode(-2);
                    jsonReturnBody.setMessage("shop comment and recommend  number is null please check it ！！！");
                }

            } catch (Exception e) {
                generateJsonReturnResultException(e,jsonReturnBody);
            }
        }
        return jsonReturnBody;
    }



}







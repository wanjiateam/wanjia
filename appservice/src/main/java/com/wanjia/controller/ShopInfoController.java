package com.wanjia.controller;

import com.wanjia.service.ShopInfoService;
import com.wanjia.utils.DateUtil;
import com.wanjia.utils.JsonReturnBody;
import com.wanjia.utils.JsonUtil;
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
            //包含了房间的属性信息，包括wifi，有窗，和住房注意事项
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
        jsonReturnBody.setType("getShopProductByProductType");

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
            SpecialtyNoteVo shopSpecialtyNote = shopInfoService.getShopSpecialtyNote(shopId, specialtyId) ;
            if(shopSpecialtyNote == null ){
                jsonReturnBody.setCode(2);
                jsonReturnBody.setMessage("got a empty result");
            }else {
                jsonReturnBody.setCode(1);
                jsonReturnBody.setMessage(shopSpecialtyNote);
            }
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
    @RequestMapping(value = "ticketInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelTicketInfo(long resortId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelInfo");

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
     * 获得游的门票提供的服务信息 如缆车，电动车。。。 三级列表
     * @param shopId
     * @param  ticketId
     * @return
     */
    @RequestMapping(value = "ticketNote", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelTicketNote(long shopId,long ticketId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelTicketService");

        try {
            ShopTicketNoteVo shopTicketNoteVo  = shopInfoService.getShopTicketNote(shopId,ticketId) ;
            generateJsonReturnResult(shopTicketNoteVo,jsonReturnBody);
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
        jsonReturnBody.setType("getShopTravelTicketService");

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
        jsonReturnBody.setType("getShopTravelTicketService");

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
    @RequestMapping(value = "familityActivityPic", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
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
}







package com.wanjia.controller;

import com.wanjia.service.ShopInfoService;
import com.wanjia.utils.*;
import com.wanjia.vo.HotelPriceVo;
import com.wanjia.vo.ShopHotelListVo;
import com.wanjia.vo.ShopProductLogoVo;
import com.wanjia.vo.live.RoomPictureVo;
import com.wanjia.vo.live.RoomVo;
import com.wanjia.vo.live.ShopRoomAttribute;
import com.wanjia.vo.restaurant.CourseVo;
import com.wanjia.vo.restaurant.ShopCourseDetailInfoVo;
import com.wanjia.vo.restaurant.ShopCoursePictureVo;
import com.wanjia.vo.speciality.SpecialtyVo;
import com.wanjia.vo.travel.ShopResortPictureVo;
import com.wanjia.vo.travel.TravelVo;
import org.apache.log4j.Logger;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

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
            if (logos.size() > 0) {
                jsonReturnBody.setCode(1);
                jsonReturnBody.setMessage(logos);
            } else {
                jsonReturnBody.setCode(2);
                jsonReturnBody.setMessage("get empty result");
            }
        } catch (Exception e) {
            logger.error("query es error", e);
            e.printStackTrace();
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("query es error");
        }


        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     *
     * @param shopId
     * @param dateStart
     * @param dateEnd
     * @return
     */
    @RequestMapping(value = "live", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopLiveInfo(long shopId,String dateStart,String dateEnd) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopProductByProductType");

        try {
            long startTime = DateUtil.parseDateToLongValue(dateStart) ;
            long endTime =   DateUtil.parseDateToLongValue(dateEnd) ;

            String indexName = "shop_room";
            String type = "room";
            List<RoomVo>  roomVos = shopInfoService.getShopRoomVoByShopId(shopId,startTime,endTime,indexName,type) ;
            if(roomVos.size() > 0){
                jsonReturnBody.setCode(1);
                jsonReturnBody.setMessage(roomVos);
            }else{
                jsonReturnBody.setCode(2);
                jsonReturnBody.setMessage("get a empty result");
            }
        } catch (Exception e) {
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("query data from es error");
            logger.error("query es error",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     *
     * @param shopId
     * @param dateTime
     * @return
     */
    @RequestMapping(value = "course", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopCourseInfo(long shopId,String dateTime) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopProductByProductType");

        try {
            long bookTime = DateUtil.parseDateToLongValue(dateTime) ;

            String indexName = "shop_course";
            String type = "course";
            List<CourseVo>  courseVos = shopInfoService.getShopCourseVoByShopId(shopId,bookTime,indexName,type) ;
            if(courseVos.size() > 0){
                jsonReturnBody.setCode(1);
                jsonReturnBody.setMessage(courseVos);
            }else{
                jsonReturnBody.setCode(2);
                jsonReturnBody.setMessage("get a empty result");
            }
        } catch (Exception e) {
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("query data from es error");
            logger.error("query es error",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     *
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
            if(specialtyVos.size() > 0){
                jsonReturnBody.setCode(1);
                jsonReturnBody.setMessage(specialtyVos);
            }else{
                jsonReturnBody.setCode(2);
                jsonReturnBody.setMessage("get a empty result");
            }
        } catch (Exception e) {
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("query data from es error");
            logger.error("query es error",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }



    /**
     *
     * @param shopId
     * @return
     */
    @RequestMapping(value = "travel", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTravelInfo(long shopId) {

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopTravelInfo");

        try {

            TravelVo  travelVo = shopInfoService.getShopTravelVoByShopId(shopId) ;
            jsonReturnBody.setCode(1);
            jsonReturnBody.setMessage(travelVo);
        } catch (Exception e) {
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("query data from es error");
            logger.error("query es error",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     *获取店家具体房型的详细信息
     * @param shopId
     * @return
     */
    @RequestMapping(value = "roomDetailInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopRoomDetail(long shopId,long roomId){

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopRoomDetail");

        try {

            ShopRoomAttribute shopRoomAttribute = shopInfoService.getShopRoomDetailInfo(shopId,roomId);
            jsonReturnBody.setCode(1);
            jsonReturnBody.setMessage(shopRoomAttribute);

        } catch (Exception e) {
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("query data from es error");
            logger.error("query es error",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     *获得店家具体房型的图片
     * @param shopId
     * @return
     */
    @RequestMapping(value = "roomPics", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopRoomPics(long shopId,long roomId){

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopRoomPics");

        try {

            List<RoomPictureVo> roomPictureVos = shopInfoService.getShopRoomPictures(shopId,roomId);
            jsonReturnBody.setCode(1);
            jsonReturnBody.setMessage(roomPictureVos);

        } catch (Exception e) {
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("query data from es error");
            logger.error("query es error",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     *获得店家具体菜品的详细信息
     * @param shopId
     * @return
     */
    @RequestMapping(value = "courseDetailInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopCourseDetailInfo(long shopId,long courseId){

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopCourseDetailInfo");

        try {

            List<ShopCourseDetailInfoVo> shopCourseDetailInfoVos = shopInfoService.getShopCourseDetailInfo(shopId,courseId) ;
            jsonReturnBody.setCode(1);
            jsonReturnBody.setMessage(shopCourseDetailInfoVos);

        } catch (Exception e) {
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("query data from es error");
            logger.error("query es error",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }

    /**
     *获得店家具体菜品的图片信息
     * @param shopId
     * @param courseId
     * @return
     */
    @RequestMapping(value = "coursePicture", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopCoursePicture(long shopId,long courseId){

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopCoursePicture");

        try {

            List<ShopCoursePictureVo> shopCoursePictureVos = shopInfoService.getShopCoursePicture(shopId,courseId) ;
            jsonReturnBody.setCode(1);
            jsonReturnBody.setMessage(shopCoursePictureVos);

        } catch (Exception e) {
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("query data from es error");
            logger.error("query es error",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }


    /**
     *获得店家所在景区的图片
     * @param resortId
     * @return
     */
    @RequestMapping(value = "resortPicture", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopResortPicture(long resortId){

        JsonReturnBody jsonReturnBody = new JsonReturnBody();
        jsonReturnBody.setType("getShopCoursePicture");

        try {

            List<ShopResortPictureVo> shopResortPictureVos = shopInfoService.getShopResortPicture(resortId) ;
            jsonReturnBody.setCode(1);
            jsonReturnBody.setMessage(shopResortPictureVos);

        } catch (Exception e) {
            jsonReturnBody.setCode(0);
            jsonReturnBody.setMessage("query data from es error");
            logger.error("query es error",e);
        }
        return JsonUtil.toJsonString(jsonReturnBody);
    }





}





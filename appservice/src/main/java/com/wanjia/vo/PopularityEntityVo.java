package com.wanjia.vo;

import com.wanjia.entity.PopularityRecommendEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blake on 2016/6/13.
 */

public class PopularityEntityVo {

    List<PopularityShopCollection> resortShops = new ArrayList<PopularityShopCollection>();

    public void addShopCollection(PopularityShopCollection shopCollection){
        resortShops.add(shopCollection) ;
    }


    public class PopularityShopCollection{
          private String resortname ;
          private int resortId ;
          List<PopularityRecommendEntity> entitys;

        public String getResortname() {
            return resortname;
        }

        public void setResortname(String resortname) {
            this.resortname = resortname;
        }

        public int getResortId() {
            return resortId;
        }

        public void setResortId(int resortId) {
            this.resortId = resortId;
        }

        public List<PopularityRecommendEntity> getEntitys() {
            return entitys;
        }

        public void setEntitys(List<PopularityRecommendEntity> entitys) {
            this.entitys = entitys;
        }
    }

}

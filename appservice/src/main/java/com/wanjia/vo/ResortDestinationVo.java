package com.wanjia.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blake on 2016/6/20.
 */
public class ResortDestinationVo {

    private List<Destination> destinations ;

    public ResortDestinationVo(){
        destinations = new ArrayList<Destination>() ;
    }

    public void addDestination(Destination destination){
        destinations.add(destination) ;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }


    public class Destination{

        private long resortid ;
        private  String name ;
        private  String pinYin ;
        private String briefPinYin ;

        public long getResortid() {
            return resortid;
        }

        public void setResortid(long resortid) {
            this.resortid = resortid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPinYin() {
            return pinYin;
        }

        public void setPinYin(String pinYin) {
            this.pinYin = pinYin;
        }

        public String getBriefPinYin() {
            return briefPinYin;
        }

        public void setBriefPinYin(String briefPinYin) {
            this.briefPinYin = briefPinYin;
        }
    }


}

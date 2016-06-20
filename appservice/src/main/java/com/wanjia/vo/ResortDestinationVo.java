package com.wanjia.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blake on 2016/6/20.
 */
public class ResortDestinationVo {

    private List<DestinationGroup> destinationGroups ;

    public ResortDestinationVo(){
        destinationGroups = new ArrayList<DestinationGroup>() ;
    }

    public void addDestinationGroup(DestinationGroup dg){
        destinationGroups.add(dg) ;
    }

    public List<DestinationGroup> getDestinationGroups() {
        return destinationGroups;
    }

    public void setDestinationGroups(List<DestinationGroup> destinationGroups) {
        this.destinationGroups = destinationGroups;
    }


    public class DestinationGroup{
        private char group ;
        private  List<Destination> destinations ;
        public DestinationGroup(){
            destinations = new ArrayList<Destination>() ;
        }

        public char getGroup() {
            return group;
        }

        public void setGroup(char group) {
            this.group = group;
        }

        public List<Destination> getDestinations() {
            return destinations;
        }

        public void setDestinations(List<Destination> destinations) {
            this.destinations = destinations;
        }

        public void addDestination(Destination destination){
            this.destinations.add(destination) ;
        }

        public class Destination{

           private  String name ;
           private  String pinYin ;
           private String briefPinYin ;

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


}

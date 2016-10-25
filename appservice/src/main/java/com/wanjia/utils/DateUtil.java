package com.wanjia.utils;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by blake on 2016/7/14.
 */
public class DateUtil {
    public final static String  format  = "yyyy-MM-dd" ;
    private  final static  Logger logger = Logger.getLogger(DateUtil.class) ;
    private  final  static  SimpleDateFormat sdf = new SimpleDateFormat(format) ;
    public static long dayMillis = 24 * 60 * 60 * 1000 ;

    //解析日期字符串 为时间戳
    public static long   parseDateToLongValue(String date)  throws ParseException {
        return   sdf.parse(date).getTime();
    }

    //format一个日期
    public static String formatDate(long dateTime,String format){
        String strDate = null ;
        Date date = new Date(dateTime) ;
        if(format == null){
            strDate =  sdf.format(date) ;
         }else{
            SimpleDateFormat innerSDF = new SimpleDateFormat(format) ;
            strDate = innerSDF.format(date) ;
        }
        return strDate ;
    }

    //format一个日期
    public static String formatDate(long dateTime){
        return formatDate(dateTime,null) ;
    }

    //获取一段时间中的每天的日期
    public static List<String> getDateList(long startTime, long endTime){

        long gap = (endTime - startTime) / dayMillis ;

        List<String> dateStrList = new ArrayList<String>((int)gap+1) ;
        Date date = new Date(startTime);
        DateTime dateTime = new DateTime(sdf.format(date)) ;
        dateStrList.add(dateTime.toString(format)) ;
        for(int i=1 ; i<= gap ;  i++){
            dateTime = dateTime.plusDays(i);
            dateStrList.add(dateTime.toString(format)) ;
        }
        return dateStrList ;
    }

    //获取一段时间中的每天的日期
    public static List<String> getDateList(String startTimeStr, String endTimeStr){

        List<String> dateList = null;
        try {
            long startTime = parseDateToLongValue(startTimeStr);
            long endTime = parseDateToLongValue(endTimeStr);
            dateList = getDateList(startTime,endTime);
        } catch (ParseException e) {
           logger.error("parse date error ",e);
        }

        return dateList;

    }


    //获取一段时间中的每天的日期
    public static List<Date> dateList(String startTimeStr, String endTimeStr){

        List<Date> dateList = null ;
        List<String> dateStrList = getDateList(startTimeStr,endTimeStr);
        if(dateStrList != null && !dateStrList.isEmpty()){
            dateList = new ArrayList<Date>();
            for(String dateStr : dateStrList){
                try {
                    dateList.add(new Date(parseDateToLongValue(dateStr)));
                } catch (ParseException e) {
                    logger.error("parse date error ",e);
                }
            }
        }
        return dateList ;
    }



    public static boolean isWeekend(String dateStr){

        DateTime dateTime = new DateTime(dateStr);
        int day = dateTime.getDayOfWeek() ;
        if(day==5 || day == 6){
            return true;
        }
        return false ;
    }


    public static  Date parseDateStrToDate(String dateStr){
        Date date = null ;
        try {
            long dataLong = parseDateToLongValue(dateStr) ;
            date =  new Date(dataLong) ;
        } catch (ParseException e) {
            logger.error("parse date error ",e);
        }
        return  date ;
    }


}

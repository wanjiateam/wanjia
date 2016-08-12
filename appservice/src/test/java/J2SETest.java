import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hsb11289 on 2016/5/25.
 */
public class J2SETest {


    @Test
    public void testRandom(){
        Random random = new Random() ;
        StringBuilder code = new StringBuilder() ;
        for (int i=0 ; i<4 ;i ++){
            code.append(random.nextInt(10));
        }
        System.out.println(code.toString());
        ;
    }


    @Test
    public void testBase64(){


        BASE64Encoder encoder = new BASE64Encoder() ;
        String encodeString =  encoder.encode("123".getBytes()) ;
        System.out.println(encodeString);
        BASE64Decoder  decoder = new BASE64Decoder();
        try {
            String originString =   new String(decoder.decodeBuffer(encodeString)) ;
            System.out.println(originString);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testDate(){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)) ;
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        long a = cal.getTimeInMillis() ;
        Date date  = new Date(a);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss") ;
        String value = simpleDateFormat.format(date) ;
        System.out.println("a="+a+"----"+value);
    }

    @Test
    public void testJodaDate(){
        DateTime dateTime = new DateTime() ;
       // DateTime dateTime1 = new DateTime(11111111);
        dateTime = dateTime.plusDays(3);
        long value = dateTime.getMillis() ;
        System.out.println(value+"---"+dateTime.toString("yyyy-MM-dd"));
        DateTime value2 = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dateTime.toString("yyyy-MM-dd"));
        System.out.println(value2.getMillis());

       //Days  days = Days.daysBetween(dateTime1,dateTime) ;
        //System.out.println(days.getDays());

    }

    @Test
    public  void testMap(){

        Map<Long,Double> map = new HashMap<Long,Double>();
        Double value =  map.get(11) ;

        //System.out.println(value);

    }
    @Test
    public  void testMath(){


        double a =   50 ;
        System.out.println(a/3);
        System.out.println(Math.ceil(a/3)) ;

        //System.out.println(value);

    }

    @Test
    public void testDayOfWeek(){
        DateTime dateTime = new DateTime("2016-7-29");
        System.out.println(dateTime.getDayOfWeek());
    }

}

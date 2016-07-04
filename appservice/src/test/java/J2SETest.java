import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

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

        long value = dateTime.getMillis() ;
        System.out.println(value+"---"+dateTime.toString("yyyy-MM-dd"));
        DateTime value2 = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dateTime.toString("yyyy-MM-dd"));
        System.out.println(value2.getMillis());

    }


}

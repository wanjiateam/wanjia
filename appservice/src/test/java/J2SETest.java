import org.junit.Test;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
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


}

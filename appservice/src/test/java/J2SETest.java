import org.junit.Test;

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
}

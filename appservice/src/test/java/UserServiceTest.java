import com.wanjia.entity.UserInfo;
import com.wanjia.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ClassName: UserServiceTest
 * Description:
 * Date: 2016/5/20
 * Time: 15:27
 *
 * @author ly13872
 * @version V1.0.6
 */
public class UserServiceTest extends SpringTestCase {

    @Autowired
    UserService userService ;

    @Test
    public void testUser(){
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("blake");
        userInfo.setEmail("1223@613.com");
       // userService.addUser(userInfo,"123");
    }
}

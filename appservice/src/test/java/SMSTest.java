import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

/**
 * Created by hsb11289 on 2016/5/25.
 */
public class SMSTest {

    private static String url = "http://gw.api.taobao.com/router/rest";
    private static String appkey = "23369491";
    private static String secret = "da516e8a1ab1490f1e56767af11f2e22" ;

    public static void main(String[] args) {

        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setSmsType( "normal" );
        req.setSmsFreeSignName("短信测试");
        req.setSmsParamString( "{\"code\":\"1234\",\"product\":\"玩家\"}" );
        req.setRecNum( "18860906646" );
        req.setSmsTemplateCode( "SMS_9665796" );
        AlibabaAliqinFcSmsNumSendResponse rsp = null;
        try {
            rsp = client.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
    }
}

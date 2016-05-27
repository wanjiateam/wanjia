package com.wanjia.utils;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.apache.log4j.Logger;

/**
 * Created by hsb11289 on 2016/5/25.
 */
public class MessageClient {

    private Logger logger = Logger.getLogger(MessageClient.class);

    TaobaoClient client;
    private String dayu_url;
    private String dayu_appkey;
    private String dayu_secret;
    private String smsSignName;
    private String product;
    private String smsTemplate;

    public void setDayu_url(String dayu_url) {
        this.dayu_url = dayu_url;
    }

    public void setDayu_appkey(String dayu_appkey) {
        this.dayu_appkey = dayu_appkey;
    }

    public void setDayu_secret(String dayu_secret) {
        this.dayu_secret = dayu_secret;
    }

    /**
     * @param dayu_url    大鱼发送短信url接口
     * @param dayu_appkey 应用的appkey
     * @param dayu_secret 应用的密码
     * @param smsSignName 大鱼短信签名
     * @param smsTemplate 大鱼短信发送模板
     * @param product     公司简称（玩家）
     */
    public MessageClient(String dayu_url, String dayu_appkey, String dayu_secret, String smsSignName, String smsTemplate, String product) {
        this.dayu_url = dayu_url;
        this.dayu_appkey = dayu_appkey;
        this.dayu_secret = dayu_secret;
        this.smsSignName = smsSignName;
        this.smsTemplate = smsTemplate;
        this.product = product;
        client = new DefaultTaobaoClient(dayu_url, dayu_appkey, dayu_secret);
    }

    /**
     * @param code        验证码
     * @param phoneNumber 电话号码
     * @return 0 表示发送失败 1 表示发送成功
     */
    public int sendCode(String code, String phoneNumber) {

        int sendSuccess = 0;
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setSmsType("normal");
        req.setSmsFreeSignName("短信测试");
        req.setSmsParamString("{\"code\":\"" + code + "\",\"product\":\"" + product + "\"}");
        req.setRecNum(phoneNumber);
        req.setSmsTemplateCode(smsTemplate);
        AlibabaAliqinFcSmsNumSendResponse rsp = null;
        try {
            rsp = client.execute(req);
        } catch (ApiException e) {
            logger.error("send sms encouter an error", e);
        }
        String smsReturnMsg = rsp.getBody();
        if (smsReturnMsg.contains("\"success\":true")) {
            sendSuccess = 1;
        } else {
            logger.error("send sms error", new Exception(smsReturnMsg));
        }
        return sendSuccess;
    }


}

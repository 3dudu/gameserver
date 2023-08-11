package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

public class AiWanLoginService extends AbsLoginService{

    private static final String  AUTH_URL = " http://apicn-sdk.jxywl.cn/User/auth";
    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        Constants.PlatformOption.AiWanSDK config = getConfig();
        if (parameters.length == 5) {
            String channel = parameters[0];
            String pf = parameters[1];//AiWanSDK
            String account	=  parameters[2];//用戶Id
            String app_id = parameters[3];//应用id
            String token = parameters[4]; //用户token
            long time = System.currentTimeMillis()/1000;

            if (config.apps.get(app_id) == null) {
                logger.info("AiWanSDK 后台参数app_id未配置："+app_id);
                return null;
            }
            //account=2020092315165768&app_id=ovdqy3we3nekjrz8&time=1600845363&token=NwVe8n4zMizM1Mzg3MzZfMTYwMDg0NTM2M184MDkz
            String signStr = "account="+account + "&app_id="+app_id +"&time="+time+"&token="+token;
            String sign = Misc.md5(signStr + config.apps.get(app_id).app_key);
            logger.info("拼接签名的字符串为：" + signStr +"生成的签名为："+sign);
            String request = RequestUtil.request(AUTH_URL+"?"+ signStr +"&sign="+ sign);
            logger.info("AiWanSDK get:" + AUTH_URL + ",param=" + (signStr +"&sign="+ sign) + ",result=" + request);
            if (request != null) {
                JSONObject obj = JSON.parseObject(request);
                //{"code":200,"msg":"success","data":{"account":"2020092315165768","nick_name":"天天",
                // "mobile":"0****","union_id":"oZZQH5xHvXFHlYudnyKLitPfpRy0","open_id":"ovUuq6trjfrk_62F1fFHlO54HSyM",
                // "is_cert":"0","is_bind_mobile":0}}
                if (obj.getIntValue("code") == 200) {
                    obj = JSONObject.parseObject(obj.getString("data"));
                    return openUserBase(obj.getString("account"), getPlatformName(), channel);
                }
            }
        }
        return null;
    }
}

package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

public class SimoSDKLoginService  extends AbsLoginService{

        /** queryLoginUrl */
        public static String queryLoginUrl = "https://sdkapi.51simo.com/user/v1/token/verify/";

        @Override
        public UserPo loginUser(String[] parameters, JSONObject rs) {

            Constants.PlatformOption.SimoSDK config = getConfig();
            if (parameters.length == 5) {
                String channel = parameters[0];
                String pf = parameters[1];//SimoSDK
                String cchid =  parameters[2];//用戶Id
                String appid = parameters[3];
                String access_token = parameters[4];
                long tm = System.currentTimeMillis()/100;

                if (config.apps.get(appid) == null) {
                    logger.info("SimoSDK 后台参数app_id未配置："+appid);
                    return null;
                }
                String signStr = "access_token="+access_token + "&appid="+appid +"&cchid="+cchid+"&tm="+tm;
                String sign = Misc.md5(signStr + "&"+ config.apps.get(appid).appkey);
                logger.info("拼接签名的字符串为：" + signStr +"生成的签名为："+sign);
                String request = RequestUtil.requestWithPost(queryLoginUrl, (signStr +"&sign="+ sign).getBytes(CharsetUtil.UTF_8));
                logger.info("SimoSDK post:" + queryLoginUrl + ",param=" + (signStr +"&sign="+ sign) + ",result=" + request);
                if (request != null) {
                    JSONObject obj = JSON.parseObject(request);
                   // {"code": 200,"msg": "success","data": {"openid": "300878" //用户唯一标识}}//
                    if (obj.getIntValue("code") == 200) {
                        obj = JSONObject.parseObject(obj.getString("data"));
                        return openUserBase(obj.getString("openid"), getPlatformName(), channel);
                    }
                }
            }
            return null;
        }
}

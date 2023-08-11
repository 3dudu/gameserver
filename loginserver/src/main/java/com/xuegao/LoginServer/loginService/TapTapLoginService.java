package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.util.TapTapUtils;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

public class TapTapLoginService  extends  AbsLoginService{
    /** queryLoginUrl */
    public static String queryLoginUrl = "https://openapi.taptap.com/account/profile/v1?client_id=";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        if (parameters.length == 5) {
            String channel = parameters[0];
            String pf = parameters[1];  //TapTapSDK
            String client_id = parameters[2];
            String kid = parameters[3];
            String mac_key = parameters[4];

            String  requestUrl  = queryLoginUrl + client_id;
            String authorization = TapTapUtils.getAuthorization(requestUrl, "GET", kid, mac_key);
            logger.info("获取请求头内容为：" + authorization );
            Map<String ,String> headlMap = new HashMap<>();
            headlMap.put("Authorization", authorization);
            String result = RequestUtil.requestSetRp(requestUrl, headlMap, "GET",null);
            logger.info("TapTapSDK post:" + requestUrl + ",headlMap=" + headlMap + ",返回=" + result);
            if (result != null) {
                JSONObject json = JSONObject.parseObject(result);
                if ("true".equals(json.getString("success"))) {
                    json =JSONObject.parseObject(json.getString("data"));
                    return openUserBase(json.getString("openid"), getPlatformName(), channel);
                }
            }
        }
        return null;
    }

}

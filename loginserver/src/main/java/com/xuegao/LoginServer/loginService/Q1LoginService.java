package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;
import java.util.HashMap;
import java.util.Map;

public class Q1LoginService extends AbsLoginService{

    private static final String requestUri = "https://sdkapi.q1.com/api/sdk/v2/profile/detail";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
       try {
            if (parameters.length == 3) {
                String channel = parameters[0];
                String session = parameters[2];
                Constants.PlatformOption.Q1SDK config = getConfig();
                logger.info("config:" + config.toString());
                if (config.apps.get(channel) == null) {
                    logger.info("Q1平台channel参数未配置,channel=" + channel);
                    return null;
                }
                Map<String, String> requestParams = new HashMap<>();
                requestParams.put("session", session);
                requestParams.put("rtype", "json");

                String request = RequestUtil.requestWithGet(requestUri,new HashMap<String,String>(),requestParams);
                //logger.info("Q1 get: requestUri=" + request + ";session = " + session + ";rtype= 'json'" );
                if (request != null) {
                    //logger.info("json:" + request);
                    JSONObject json = JSONObject.parseObject(request);
                    if ("1".equals(json.getString("code"))) {
                        return openUserBase(json.getString("user"), getPlatformName(), channel);
                    }
                }
            }
        } catch (Exception e) {
           e.printStackTrace();
       }
        return null;
    }
}

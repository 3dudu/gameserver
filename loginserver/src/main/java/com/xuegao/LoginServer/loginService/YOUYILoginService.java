package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;

import java.util.HashMap;
import java.util.Map;

public class YOUYILoginService extends AbsLoginService {
    private static final String requestUrl = "https://plat.gameyisi.com/api/mobile/v1/cp/user";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        try {
            if (parameters.length == 3) {
                String channel = parameters[0];
                String oauth_token = parameters[2];
                Constants.PlatformOption.YOUYISDK config = getConfig();
                logger.info("config:" + config.toString());
                if (config.apps.get(channel) == null) {
                    logger.info("YOUYI平台channel参数未配置,channel=" + channel);
                    return null;
                }
                Map<String, String> requestParams = new HashMap<>();
                requestParams.put("oauth_token", oauth_token);
                requestParams.put("game_id", config.apps.get(channel).game_id);

                String request = RequestUtil.requestWithGet(requestUrl, new HashMap<String, String>(), requestParams);
                if (request != null) {
                    JSONObject json = JSONObject.parseObject(request);
                    if ("0".equals(json.getString("code"))) {
                        json = JSONObject.parseObject(json.getString("data"));
                        return openUserBase(json.getString("cp_uid"), getPlatformName(), channel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
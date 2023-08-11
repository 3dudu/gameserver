package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;

import java.util.HashMap;
import java.util.Map;

public class LeWanLoginService extends  AbsLoginService{
    private static final String  LOGIN_VERIFY = "https://exc.llewan.com/v1/gameUser/checkToken";
    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        Constants.PlatformOption.LeWanSDK config = getConfig();
        if (parameters.length == 3) {
            String channel_code = parameters[0]; //渠道号
            String pf = parameters[1];     //LeWanSDK
            String access_token  = parameters[2];

            if (config.apps.get(channel_code) == null) {
                logger.info("LeWanSDK 后台参数channel_code未配置："+channel_code);
                return null;
            }
            String gameId = config.apps.get(channel_code).game;
            Map<String,String> requestJson = new HashMap();
            requestJson.put("game",gameId);
            requestJson.put("token",access_token);

            String request = null;
            try {
                request = BingChengLoginService.post(LOGIN_VERIFY,JSON.toJSONString(requestJson));
                logger.info("LeWanSDK post请求url：" + LOGIN_VERIFY+"请求参数为="+requestJson +"，请求返回内容为：" +request );
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (request != null) {
                JSONObject json = JSON.parseObject(request);
                if (json.getIntValue("code") == 1) {
                json = json.getJSONObject("d");
                    return openUserBase(json.getString("uid"), getPlatformName(), channel_code);
                }
            }
        }
        return null;
    }
}

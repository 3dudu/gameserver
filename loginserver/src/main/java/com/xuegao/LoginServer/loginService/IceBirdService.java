package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

public class IceBirdService extends AbsLoginService {


    /**queryLoginUrl*/
    private static final String requestUri = "http://token.aiyinghun.com/user/token";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        //取配置参数
        Constants.PlatformOption.IceBirdSDK config = getConfig();
        if (parameters.length == 7) {
            String channel = parameters[0];
            String gameId = parameters[2];
            String channelId = parameters[3];
            String appId = parameters[4];
            String sid = parameters[5];
            String extra = parameters[6];
            if (!config.game_id.equals(gameId)) {
                logger.info("冰鸟平台 参数未配置,gameId=" + gameId);
                return null;
            }
            if (config.app_secret==null) {
                logger.info("app_secret:未配置" + config.app_secret);
                return null;
            }
            String str = "appId=" + appId + "channelId=" + channelId + "extra=" + extra + "gameId=" + gameId+ "sid=" + sid + config.app_secret;
            String sign = Misc.md5(str).toLowerCase();
            String postData = "gameId=" + gameId + "&channelId=" + channelId + "&appId=" + appId + "&extra=" + extra + "&sid=" + sid + "&sign=" + sign;
            String request = RequestUtil.requestWithPost(requestUri, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("冰鸟 post:" + requestUri + ",param=" + postData + ",result=" + request);
            if (request != null) {
                JSONObject json = JSONObject.parseObject(request);
                logger.info("json:" + json.toString());
                if (json.getIntValue("ret") == 0) {
                    JSONObject data = json.getJSONObject("content").getJSONObject("data");
                    JSONObject cData = json.getJSONObject("content").getJSONObject("cData");
                    rs.put("data", data.toString());
                    rs.put("cData", cData.toString());
                    return openUserBase(data.getString("userId"), getPlatformName(), channel);
                }
            }
        }
        return null;
    }
}

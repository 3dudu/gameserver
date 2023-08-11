package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.RequestUtil;
import com.xuegao.core.util.StringUtil;

public class SqXcxLoginService extends AbsLoginService {

    private static final String getAccessToken = "https://api.q.qq.com/api/getToken";
    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        Constants.PlatformOption.SqXcxSDK config = getConfig();
        if (parameters.length == 3) {
            String channel = parameters[0];
            String pf = parameters[1];
            String openid = parameters[2];
            if (openid.equals("")) {
                logger.info("获取的openid为null");
                return  null;
            }
            JedisUtil redis = JedisUtil.getInstance("LoginServer");
            String accessToken = redis.STRINGS.get(config.appId + "_SQ_accessToken");
            logger.info("accessToken:" + accessToken);
            //如果accessToken已过期则重新获取
            if (StringUtil.empty(accessToken)) {
                String requestData = getAccessToken + "?grant_type=client_credential" + "&appid=" + config.appId + "&secret=" + config.appSecret;
                String result = RequestUtil.request(requestData);
                if (result != null) {
                    JSONObject obj = JSON.parseObject(result);
                    logger.info("请求get_token_url返回:" + obj.toString());
                    accessToken = obj.getString("access_token");
                    //存入redis
                    redis.STRINGS.set(config.appId + "_SQ_accessToken", accessToken);
                    //设置过期时间为7000S
                    redis.expire( config.appId+ "_SQ_accessToken", 7000);
                }
            }
            return openUserBase(openid, getPlatformName(), channel);

        }
        return null;
    }

}


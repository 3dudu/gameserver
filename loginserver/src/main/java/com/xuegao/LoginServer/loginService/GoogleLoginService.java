package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Base64;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.RequestUtil;
import org.apache.poi.hssf.util.HSSFColor;

public class GoogleLoginService extends AbsLoginService{
    private static final String PUBLIC_KEY_URL = "https://oauth2.googleapis.com/tokeninfo";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        if (parameters.length == 4) {
            String channel = parameters[0];
            String pf = parameters[1];//GoogleLogin
            String userId = parameters[2];
            String id_token = parameters[3];//授权用户的JWT凭证
            try {

                JedisUtil redis = JedisUtil.getInstance("LoginServer");
                String accessToken = redis.STRINGS.get("GOOLELOGIN_"+ userId);
                logger.info("accessToken:" + accessToken);
                if ( accessToken ==null || !accessToken.equals(id_token) ) {
                    String request = PUBLIC_KEY_URL + "?id_token=" + id_token ;
                    String result = RequestUtil.request(request);
                    logger.info("google login get url :" + request + ",res :" + result);
                    JSONObject resFields = JSONObject.parseObject(result);
                    String  sub=  resFields.getString("sub");
                    if (sub.equals(userId)) {
                        return openUserBase(userId, getPlatformName(), channel);
                    }
                }else {
                    //存入redis
                    redis.STRINGS.set("GOOLELOGIN_"+ userId, id_token);
                    //设置过期时间为7000S
                    redis.expire("GOOLELOGIN_"+ userId, 70000);
                    logger.info("google login token 未失效或已存在，直接登录game");
                    return openUserBase(userId, getPlatformName(), channel);
                }

            } catch (Exception ex) {
                logger.info("google login err :"+ex.getMessage());
            }
        }
        return null;
    }
}

package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

public class YDSqSDKService extends AbsLoginService {

    private static final String verify_url = "https://user.ofgame.net/usr/verifyUserExit.do";//用于验证某个用户是否存在

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        Constants.PlatformOption.YDSqSDK config = getConfig();
        if (parameters.length == 4) {
            String channel = parameters[0];
            String uid = parameters[2];
            String appId = parameters[3];
            if (config.apps.get(appId) == null) {
                logger.info("优点-手Q平台 参数未配置,appId=" + appId);
                return null;
            }
            String openkey = config.apps.get(appId).openkey;
            if (openkey == null) {
                logger.info("openkey:未配置" + openkey);
                return null;
            }
            String postData = "appId=" + appId + "&uid=" + uid;
            String result = RequestUtil.requestWithPost(verify_url, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("优点(手Q)SDK post:" + verify_url + ",param=" + postData + ",返回=" + result);
            if (result != null) {
                JSONObject json = JSONObject.parseObject(result);
                json = JSONObject.parseObject(json.getString("content"));
                if ("true".equals(json.getString("isUserExit"))) {
                    return openUserBase(uid, getPlatformName(), channel);
                }
            }
        }
        return null;
    }
}


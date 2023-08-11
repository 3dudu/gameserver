package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

public class YDSDKKuaiWanService extends AbsLoginService{

    private static final String verify_url = "https://user.ofgame.net/api/v3/user/verify/token";//用于验证某个用户是否存在

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        if (parameters.length == 5) {
            String channel = parameters[0];
            String uid = parameters[2];
            String appId = parameters[3];
            String token = parameters[4];

            String postData = "appID=" + appId + "&uid=" + uid +"&token=" + token;
            String result = RequestUtil.requestWithPost(verify_url, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("优点 快应用SDK post:" + verify_url + ",param=" + postData + ",返回=" + result);
            if (result != null) {
                JSONObject json = JSONObject.parseObject(result);
                int content = json.getInteger("content");
                int statusCode = json.getInteger("statusCode");
                if (content == 1 && statusCode ==200) {
                    return openUserBase(uid, getPlatformName(), channel);
                }
            }
        }
        return null;
    }
}

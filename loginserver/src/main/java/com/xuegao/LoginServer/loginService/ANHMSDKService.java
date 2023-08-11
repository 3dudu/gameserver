package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

public class ANHMSDKService extends AbsLoginService {

    /**queryLoginUrl*/
    private static final String requestUri = "http://api.icegames.cn/auth/checktoken";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        if (parameters.length == 4) {
            String channel = parameters[0];
            String uid = parameters[2];
            String access_token = parameters[3];
            String postData = "uid="+uid+"&access_token="+access_token;
            String request = RequestUtil.requestWithPost(requestUri, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("安纳海姆 post:" + requestUri + ",param=" + postData + ",result=" + request);
            if (request!=null) {
                JSONObject json = JSONObject.parseObject(request);
                JSONObject data = json.getJSONObject("data");
                logger.info("json:" + json.toString());
                if (json.getIntValue("statusCode") == 200) {
                    return openUserBase(data.getString("user_id"), getPlatformName(), channel);
                }
            }
        }
        return null;
    }
}

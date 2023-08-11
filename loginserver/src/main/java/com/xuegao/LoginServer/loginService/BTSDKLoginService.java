package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;


public class BTSDKLoginService extends AbsLoginService{
    private static final String  DOMESTIC_URL = "https://supersdk.7pa.com/login/checkuserinfo";

    private static final String  KOREA_URL = "https://supersdk.wan73.com/login/checkuserinfo";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        try {

            if (parameters.length == 6) {
                String channel = parameters[0];
                String game_id = parameters[2];
                String super_user_id = parameters[3];
                String token = parameters[4];
                String is_china = parameters[5]; //0代表国内 1代表韩国
                String requestParams = "token="+token +"&super_user_id="+super_user_id;
                String requestUri = is_china.equals("0") ?  DOMESTIC_URL : KOREA_URL;
                String request = RequestUtil.requestWithPost(requestUri, requestParams.getBytes(CharsetUtil.UTF_8));
                logger.info("BTSDK post:" + requestUri + ",param=" + requestParams + ",result=" + request);
                if (request != null) {
                    JSONObject json = JSONObject.parseObject(request);
                    logger.info("json:" + json.toString());
                    if (json.getIntValue("code") == 200) {
                        return openUserBase(super_user_id, getPlatformName(), channel);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

public class XMWSDKService extends AbsLoginService {

    private static final String requestUri = "http://open.xmwan.com/v2/oauth2/access_token";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        //取配置参数
        Constants.PlatformOption.XMWSDK config = getConfig();
        if (parameters.length == 5) {
            String channel = parameters[0];
            String client_id = parameters[2];//授权应用游戏的ClientID
            String grant_type = parameters[3];//默认值authorization_code
            String code = parameters[4];//传入授权验证码，SDK客户端返回的authorization_code
            if (config.apps.get(client_id) == null) {
                logger.info("熊貓玩平台 参数未配置,client_id=" + config.apps.get(client_id));
                return null;
            }
            String client_secret = config.apps.get(client_id).client_secret;
            if (client_secret == null) {
                logger.info("client_secret:未配置" + config.apps.get(client_id).client_secret);
                return null;
            }
            String postData = "client_id=" + client_id + "&client_secret=" + client_secret + "&grant_type=" + grant_type + "&code=" + code;
            String result = RequestUtil.requestWithPost(requestUri, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("熊猫玩 post:" + requestUri + ",param=" + postData + ",result=" + result);
            if (result != null) {
                JSONObject json = JSONObject.parseObject(result);
                rs.put("access_token", json.getString("access_token"));
                logger.info("json:" + json.toString());
                return openUserBase(json.getString("xmw_open_id"), getPlatformName(), channel);
            }
        }
        return null;
    }
}


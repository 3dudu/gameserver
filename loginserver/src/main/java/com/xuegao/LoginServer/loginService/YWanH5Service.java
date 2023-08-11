
package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

public class YWanH5Service extends AbsLoginService {

    /**queryLoginUrl*/
    private static final String requestUri = "https://japi.game-props.com/oauth/token";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        //取配置参数
        Constants.PlatformOption.YWanH5 config = getConfig();
        if (parameters.length == 4) {
            String channel = parameters[0];
            String j_game_id = parameters[2];
            String authorize_code = parameters[3];
            if (config.apps.get(j_game_id) == null) {
                logger.info("悦玩平台H5支付 参数未配置,j_game_id=" + j_game_id);
                return null;
            }
            String j_game_secret = config.apps.get(j_game_id).j_game_secret;
            if (j_game_secret == null) {
                logger.info("j_game_secret:未配置" + j_game_secret);
                return null;
            }
            String j_request_uri = config.apps.get(j_game_id).j_request_uri;
            if (j_request_uri == null || "".equals(j_request_uri)) {
                logger.info("j_request_uri:未配置" + j_request_uri);
                return null;
            }
            String time = "" + System.currentTimeMillis() / 1000;
            String signStr = "authorize_code=" + authorize_code + "&j_game_id=" + j_game_id + "&j_game_secret=" + j_game_secret + "&time=" + time;
            String sign = Misc.md5(signStr);
            String postData = "j_game_id=" + j_game_id + "&authorize_code=" + authorize_code + "&sign=" + sign + "&time=" + time;
            String request = RequestUtil.requestWithPost(j_request_uri, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("悦玩H5 post:" + request + ",param=" + postData + ",result=" + request);
            if (request != null) {
                JSONObject obj = JSON.parseObject(request);
                //{ "content": { "access_token": "296ebf9ca90afcde08aa8d743ec5d01e", "user_id": 88027, "user_name": "Lizhiwei5"},"msg": "", "ret": 1}
                if (obj.getIntValue("ret") == 1) {
                    JSONObject user = obj.getJSONObject("content");
                    String user_id = user.getString("user_id");
                    if (user_id != null) {
                        rs.put("ext", user);
                        return openUserBase(user_id, getPlatformName(), channel);
                    }
                }
            }
        }
        return null;
    }
}


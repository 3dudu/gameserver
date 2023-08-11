package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;

public class FTNNSDKLoginService extends AbsLoginService {

    private static final String CHECK_LOGIN = "https://m.4399api.com/openapi/oauth-check.html";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        if (parameters.length == 4) {
            String channel_code = parameters[0];
            String uid = parameters[2];
            String state = parameters[3];

            String request = CHECK_LOGIN + "?uid=" + uid + "&state=" + state;
            String result = RequestUtil.request(request);
            if (result != null) {
                JSONObject obj = JSON.parseObject(result);
                int code = obj.getIntValue("code");
                if (code == StatusCode.VALID_SUCCESS.code) {
                    String guid = obj.getJSONObject("result").getString("uid");
                    return openUserBase(guid, getPlatformName(), channel_code);
                } else {
                    logger.info("请求失败！状态码: " + code + " 描述：" + obj.getString("message"));
                    return null;
                }
            }
        }
        return null;
    }

    private enum StatusCode {
        VALID_SUCCESS(100, "验证成功"),
        PARAMETER_MISSING(87, "参数不全"),
        VALID_FAILED(85, "验证失败"),
        VALID_PARTITION(82, "验证成功、但state有效期超时，重置state");

        private final int code;
        private final String message;

        StatusCode(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}

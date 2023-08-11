package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.util.SignHelper;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class FFSDKService extends AbsLoginService {
    private static final String requestUri = "https://www.kokorogo.com/sdk/sdk_oauth.php";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        try {
            //取配置参数
            Constants.PlatformOption.FFSDK config = getConfig();
            logger.info(config.toString());
            if (parameters.length == 5) {
                String channel = parameters[0];
                String appid = parameters[2];
                String uid = parameters[3];
                String sessionid = parameters[4];
                logger.info(channel+appid+uid+sessionid);
                if (config.apps.get(channel) == null) {
                    logger.info("菲凡平台channel参数未配置,channel=" + channel);
                    return null;
                }
                String LoginKey = config.apps.get(channel).loginKey;
                Map<String, String> requestParams = new HashMap<>();
                requestParams.put("appid", appid);
                requestParams.put("uid", uid);
                requestParams.put("sessionid", sessionid);
                requestParams.put("token", Misc.md5(appid+uid+sessionid+LoginKey));
                logger.info("requestParams:" + requestParams.toString());
                //cpSign,playerSSign进行编码后传递
                String request = RequestUtil.request(requestUri, requestParams);
                logger.info("FFSDK post:" + requestUri + ",param=" + requestParams + ",result=" + request);
                if (request != null) {
                    JSONObject json = JSONObject.parseObject(request);
                    logger.info("json:" + json.toString());
                    if (json.getIntValue("code") == 1) {
                        return openUserBase(uid, getPlatformName(), channel);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

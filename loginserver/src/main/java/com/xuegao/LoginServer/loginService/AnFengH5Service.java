package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

public class AnFengH5Service extends AbsLoginService {

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        //取配置参数
        Constants.PlatformOption.AFengH5 config = getConfig();
        if (parameters.length == 5) {
            String channel = parameters[0];
            String app_id = parameters[2];//应用ID，即APPID
            String open_id = parameters[3];//用户在SDK的唯一标识
            String token = parameters[4];//登录成功后返回的token
            if (config.apps.get(app_id) == null) {
                logger.info("安锋H5 参数未配置,app_id=" + app_id);
                return null;
            }
            String sign_key = config.apps.get(app_id).sign_key;
            if (sign_key == null) {
                logger.info("sign_key_login:未配置" + sign_key);
                return null;
            }
            String login_url = config.apps.get(app_id).login_url;
            if (login_url == null) {
                logger.info("login_url:未配置" + login_url);
                return null;
            }
            String str = "app_id=" + app_id + "&open_id=" + open_id + "&token=" + token + "&sign_key=" + sign_key;
            String sign = Misc.md5(str).toLowerCase();
            String postData = "app_id=" + app_id + "&sign=" + sign + "&open_id=" + open_id + "&token=" + token;
            String request = RequestUtil.requestWithPost(login_url, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("安锋H5 post:" + login_url + ",param=" + postData + ",result=" + request);
            if (request != null) {
                JSONObject json = JSONObject.parseObject(request);
                logger.info("json:" + json.toString());
                if (json.getIntValue("code") == 0) {
                    return openUserBase(open_id, getPlatformName(), channel);
                }
            }
        }
        return null;
    }
}

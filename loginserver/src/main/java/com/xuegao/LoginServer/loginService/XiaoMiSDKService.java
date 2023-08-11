package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.util.HmacSHA1Encryption;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;


public class XiaoMiSDKService extends AbsLoginService {

    private static final String requestUri ="http://mis.migc.xiaomi.com/api/biz/service/loginvalidate";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs){

        //配置参数
        Constants.PlatformOption.XiaoMiSDK config = getConfig();
        if (parameters.length == 5) {
            String channel = parameters[0];
            String appId = parameters[2];//游戏ID
            String session = parameters[3];//用户sessionID
            String uid = parameters[4];//小米用户ID

            if (config.apps.get(appId) == null) {
                logger.info("小米平台 参数未配置,appId=" + config.apps.get(appId));
                return null;
            }
            String app_key=config.apps.get(appId).appKey;
            if (app_key == null) {
                logger.info("app_key:未配置" + config.apps.get(appId).appKey);
                return null;
            }
            String app_secret = config.apps.get(appId).appSecret;
            if (app_secret == null) {
                logger.info("app_secret:未配置" + config.apps.get(appId).appSecret);
                return null;
            }
            //签名
            String str ="appId="+appId+"&session="+session+"&uid="+uid;
            String signature = HmacSHA1Encryption.HmacSHA1Encrypt(str,app_secret);
            String postData = "appId=" + appId + "&session=" + session + "&uid=" + uid + "&signature=" + signature;
            String result = RequestUtil.requestWithPost(requestUri, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("小米 post:" + requestUri + ",param=" + postData + ",result=" + result);
            if (result != null) {
                JSONObject json = JSONObject.parseObject(result);
                logger.info("json:" + json.toString());
                if (json.getIntValue("errcode")==200) {
                    return openUserBase(uid, getPlatformName(), channel);
                }
            }
        }
        return null;
    }
}

package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

public class ZTSDKLoginService extends AbsLoginService{

    private static final String VERIFY   =  "http://www.1377.com/user/verifyAccount/";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        Constants.PlatformOption.ZTSDK config = getConfig();
        if (parameters.length == 5) {
            String channel_code = parameters[0];
            String pf = parameters[1];  //ZTSDK
            String userID = parameters[2];
            String appid = parameters[3];
            String token = parameters[4];

            long time  =  System.currentTimeMillis();

            if (config.apps.get(channel_code) == null) {
                logger.info("赞钛SDK参数 channel_code:"+ channel_code +"未配置");
                return null;
            }
            if (config.apps.get(channel_code).login_key == null) {
                logger.info("赞钛SDK参数 login_key 未配置");
                return null;
            }
            /* md5(userID+appid+token+time+loginkey); login_key由平台提供*/
            String sign= Misc.md5(userID+config.apps.get(channel_code).appid+token+time+ config.apps.get(channel_code).login_key);

            String postData = "userID=" + userID + "&appid=" + config.apps.get(channel_code).appid +"&token="+token +"&time=" + time +"&sign="+sign;
            String result = RequestUtil.requestWithPost(VERIFY, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("ZTSDK post:" + VERIFY + ",param=" + postData + ",返回=" + result);

            if (result != null) {
                JSONObject json = JSONObject.parseObject(result);
                if ("1".equals(json.getString("state"))) {
                    return openUserBase(userID, getPlatformName(), channel_code);
                }
            }
        }
        return null;
    }
}

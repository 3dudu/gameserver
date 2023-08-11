package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;



public class BilibiliSDKLoginService extends  AbsLoginService{

    private static final String  CHECK_LOGIN = "http://pnew.biligame.net/api/server/session.verify";
    //private static final String login_check_standby = "http://pserver.bilibiligame.net/api/server/session.verify";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        //获取配置参数
        Constants.PlatformOption.BilibiliSDK config = getConfig();
        if (parameters.length == 7) {
            String channel_code = parameters[0]; //渠道号
            String pf = parameters[1];     //BilibiliSDK
            String app_id = parameters[2];
            String merchant_id = parameters[3]; //分配给研发的商户id
            String uid = parameters[4]; //B站的用户id
            String version = parameters[5]; //默认 1
            String access_key = parameters[6]; //客户端登录接口返回的access_token

            long timestamp  =  System.currentTimeMillis();
            if (config.apps.get(app_id) == null) {
                logger.info("BilibiliSDK 后台参数app_id未配置："+app_id);
                return null;
            }
            String secret_key = config.apps.get(app_id).secretKey;
            if (secret_key == null){
                logger.info("BilibiliSDK 后台参数secret_key未配置:" + secret_key);
                return null;
            }
            //生成签名
            String signStr = access_key + app_id + merchant_id + timestamp + uid + version + secret_key;
            String sign = Misc.md5(signStr);
            logger.info("拼接签名的字符串为：" + signStr +"生成的签名为："+sign);
            String postData = "uid="+uid+"&access_key="+access_key+"&game_id="+app_id+"&merchant_id="+merchant_id+
                    "&version="+version+"&timestamp="+timestamp+"&sign="+sign;
            String request = RequestUtil.requestWithPost(CHECK_LOGIN, postData.getBytes(CharsetUtil.UTF_8));
            logger.info("请求B站服务器的参数为："+postData + "返回的结果为：" + request);
            if (request != null) {
                 JSONObject json = JSON.parseObject(request);
                 if (0 == json.getIntValue("code")) {
                     String guid = json.getString("open_id");
                     return openUserBase(guid, getPlatformName(), channel_code);
                 }
            }
        }
        return null;
    }
}

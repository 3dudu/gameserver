package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.util.HmacSHA1And256EncryptionWx;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.RequestUtil;
import com.xuegao.core.util.StringUtil;


public class WxXcxService extends AbsLoginService {
    private static final String get_AccessToken_Url = "https://api.weixin.qq.com/cgi-bin/token";
    private static final String check_SessionKey_Url = "https://api.weixin.qq.com/wxa/checksession";

   private static String accessToken = null;

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        Constants.PlatformOption.WxXcxSDK config = getConfig();
        if (parameters.length == 5) {
            String channel = parameters[0];
            String pf = parameters[1];
            String appId  = parameters[2];//appId
            String openid = parameters[3];
            String session_key = parameters[4];
            if (null == config.apps.get(appId)) {
                logger.info("米大师平台 参数未配置,appid=" + appId);
                return null;
            }
            String secret = config.apps.get(appId).appSecret;
            if (null == secret) {
                logger.info("米大师 参数未配置，secret is null ,appid =" + appId );
                return null;
            }
            getAccessToken(appId,secret);
            //生成签名
            String sign = "";
            try {
                sign = HmacSHA1And256EncryptionWx.GENHMACSHA256("", session_key);
                //通过校验登录态签名
                String request = RequestUtil.request(check_SessionKey_Url + "?" + "access_token=" + accessToken + "&signature=" + sign + "&openid=" + openid + "&sig_method=hmac_sha256");
                logger.info("check_SessionKey_Url返回的参数为：" + request);
                if (request != null) {
                    JSONObject j = JSON.parseObject(request);
                    if(j.getIntValue("errcode")==42001  || j.getIntValue("errcode")==40001){
                        getAccessToken(appId,secret);
                        request = RequestUtil.request(check_SessionKey_Url + "?" + "access_token=" + accessToken + "&signature=" + sign + "&openid=" + openid + "&sig_method=hmac_sha256");
                        logger.info("重新获取token, check_SessionKey_Url返回的参数为：" + request);
                        }
                    if (request != null) {
                        j = JSON.parseObject(request);
                        if (j.getIntValue("errcode") == 0) {
                            return openUserBase(openid, getPlatformName(), channel);
                        }
                    }
                }

            } catch (Exception e) {
                logger.info("验签失败");
                return null;
            }

        }
        return null;
    }

    private void  getAccessToken(String appId ,String secret){
        JedisUtil redis = JedisUtil.getInstance("LoginServer");
        String requestData = get_AccessToken_Url + "?grant_type=client_credential" + "&appid=" + appId + "&secret=" + secret;
        String result = RequestUtil.request(requestData);
        if (result != null) {
            JSONObject obj = JSON.parseObject(result);
            logger.info("请求get_token_url返回:" + obj.toString());
            accessToken = obj.getString("access_token");
            //存入redis
            redis.STRINGS.set(appId + "_accessToken", accessToken);
            //设置过期时间为7000S
            redis.expire(appId + "_accessToken", 7000);
        }
    }


}
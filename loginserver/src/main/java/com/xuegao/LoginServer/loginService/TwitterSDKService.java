package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.data.MsgFactory;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;
import com.xuegao.core.util.twitter.HmacSHA1Encryption;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class TwitterSDKService extends AbsLoginService {
    private static org.apache.log4j.Logger logger = Logger.getLogger(TwitterSDKService.class);

    private static final String verify_url = "https://api.twitter.com/1.1/account/verify_credentials.json";
    private static final String ENCODING = "UTF-8";


    /**
     * 登录请求
     *
     * @param
     * @param
     * @throws Exception APP端上传用户的Token,Token Secret,id.
     */
    public UserPo loginUser(String[] parameters, JSONObject rs){
        Constants.PlatformOption.TwitterSDK config = getConfig();
        logger.info("TwitterSDK配置参数为:" + config.toString());
        if (parameters.length == 6) {
            String oauth_token = parameters[2];
            String oauth_token_secret = parameters[3];
            String name = parameters[4];
            String uid = parameters[5];
            Map<String, String> map = new HashMap<>();
            map.put("oauth_consumer_key", config.consumerKey);
            map.put("oauth_token", oauth_token);
            map.put("oauth_signature_method", "HMAC-SHA1");
            map.put("oauth_timestamp", String.valueOf(System.currentTimeMillis()/1000L));
            map.put("oauth_nonce", UUID.randomUUID().toString());
            map.put("oauth_version", "1.0");
            String sign = null;
            try {
                sign = getSign(map, config.consumerSecret+"&"+oauth_token_secret,"GET");
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e.getMessage());
            }
            String authorization = "OAuth " +
                    "oauth_consumer_key=\""+config.consumerKey+"\"," +
                    "oauth_token=\""+oauth_token+"\"," +
                    "oauth_signature_method=\"HMAC-SHA1\"," +
                    "oauth_timestamp=\""+map.get("oauth_timestamp")+"\"," +
                    "oauth_nonce=\""+map.get("oauth_nonce")+"\"," +
                    "oauth_version=\"1.0\"," +
                    "oauth_signature=\""+sign+"\"";
            map.clear();
            map.put("Authorization", authorization);
            logger.info("请求头信息:"+map.toString());
            String request = RequestUtil.requestSetRp(verify_url, map, "GET", null);
            logger.info("返回的request为：" + request);
            if (request != null) {
                JSONObject json = JSONObject.parseObject(request);
                String id_str = json.getString("id_str");
                logger.info("查询到的用户id：" + id_str);
                    if (id_str.equals(uid)) {
                    return openUserBase(id_str, getPlatformName(), "");
                }
            }
        }

        return null;
    }

    public static String getSign(Map<String, String> params, String secretKey,String methodName) throws Exception {
        String signString = getSortQueryString(params,methodName);
        return URLEncoder.encode(HmacSHA1Encryption.hmacSHA1Encrypt(signString, secretKey),ENCODING);
    }

    /**
     * 获取得到排序好的查询字符串
     *
     * @param params
     * @return
     */
    public static String getSortQueryString(Map<String, String> params,String methodName) throws UnsupportedEncodingException {
        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);
        StringBuffer sb = new StringBuffer();
        for (Object key : keys) {
            sb.append(key).append("=").append(params.get(String.valueOf(key))).append("&");
        }
        String text = sb.toString();
        if (text.endsWith("&")) {
            text = text.substring(0, text.length() - 1);
        }
        return methodName+"&"+URLEncoder.encode(verify_url, ENCODING)+"&"+URLEncoder.encode(text, ENCODING);
    }
}

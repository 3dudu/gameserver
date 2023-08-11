
package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class OPPOSDKService extends AbsLoginService {

    /**queryLoginUrl*/
    private static final String requestUri = "https://iopen.game.oppomobile.com/sdkopen/user/fileIdInfo";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        if (parameters.length == 8) {
            String channel = parameters[0];
            //String pkgName = parameters[2];
            String token = parameters[3];
            //String timeStamp = parameters[4];
            //String version = parameters[5];
            String appId = parameters[6];
            String ssoid = parameters[7];
            Constants.PlatformOption.OPPOSDK config = getConfig();
            logger.info("config:" + config.toString());
            if (config.apps.get(appId) == null) {
                logger.info("OPPO平台appId参数未配置,appId=" + appId);
                return null;
            }
            if (config.apps.get(appId).appkey == null || config.apps.get(appId).appsecret == null) {
                logger.info("appkey未配置或appsecret未配置");
                return null;
            }
            String appKey=config.apps.get(appId).appkey;
            String appSecret=config.apps.get(appId).appsecret;
            //生成基串baseStr
            String baseStr="oauthConsumerKey="+appKey+"&oauthToken="+token+"&oauthSignatureMethod=HMAC-SHA1"
                    +"&oauthTimestamp="+""+System.currentTimeMillis()+"&oauthNonce="+""+ Math.random()+"&oauthVersion=1.0&";
            logger.info("baseStr:" + baseStr);
            String oauthSignature = "";
            try {
                oauthSignature = generateSign(baseStr, appSecret);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = requestUri+"?fileId="+ssoid+"&token="+token;
            //基串和签名放在请求头中
            String result = httpURLConectionGET(baseStr, oauthSignature,url);
            logger.info("请求Url:"+url+",[param="+baseStr+",oauthSignature="+oauthSignature+"],result:"+result);
            JSONObject json = JSONObject.parseObject(result);
            if (json!=null) {
                logger.info("json:"+json.toString());
                if ("200".equals(json.getString("resultCode"))) {
                    return openUserBase(json.getString("ssoid"), getPlatformName(), channel);
                }
            }
        }
        return null;
    }


    /**
     * GET设置请求头
     * @param param
     * @param oauthSignature
     * @param getUrl
     * @return
     */
    public static String httpURLConectionGET(String param, String oauthSignature,String getUrl) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(getUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
            //addRequestProperty添加相同的key不会覆盖，如果相同，内容会以{name1,name2}
            //connection.setRequestProperty("设置请求头key", "请求头value");
            connection.setRequestProperty("param", param);
            connection.setRequestProperty("oauthSignature", oauthSignature);
            connection.connect();// 连接会话
            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {// 循环读取流
                sb.append(line);
            }
            br.close();// 关闭流
            connection.disconnect();// 断开连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 签名
     * @param baseStr
     * @param AppSecret
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String generateSign(String baseStr, String AppSecret) throws UnsupportedEncodingException {
        byte[] byteHMAC = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = null;
            String oauthSignatureKey = AppSecret + "&";
            spec = new SecretKeySpec(oauthSignatureKey.getBytes(), "HmacSHA1");
            mac.init(spec);
            byteHMAC = mac.doFinal(baseStr.getBytes());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return URLEncoder.encode(String.valueOf(base64Encode(byteHMAC)), "UTF-8");
    }

    /**
     * Base64
     * @param data
     * @return
     */
    public static char[] base64Encode(byte[] data) {
        final char[] alphabet =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
                        .toCharArray();
        char[] out = new char[((data.length + 2) / 3) * 4];
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = (0xFF & (int) data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & (int) data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & (int) data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            out[index + 0] = alphabet[val & 0x3F];
        }
        return out;
    }
}


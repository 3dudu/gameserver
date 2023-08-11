package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.util.SignHelper;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BingChengLoginService extends  AbsLoginService{
    private static final String  LOGIN_VERIFY = " https://oursdk.bingchenghuyu.com/cp/loginVerify";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs)  {
        //获取配置参数
        Constants.PlatformOption.BingChengSDK config = getConfig();
        if (parameters.length == 6) {
            String channel_code = parameters[0]; //渠道号
            String pf = parameters[1];     //BingChengSDK
            String game_id  = parameters[2]; //游戏id
            String uid = parameters[3]; //用户id
            String token = parameters[4]; //登录token
            String login_time = parameters[5]; //登录时间

            long check_time   =  System.currentTimeMillis()/1000;

            if (config.apps.get(game_id) == null) {
                logger.info("BingChengSDK 后台参数game_id未配置："+game_id);
                return null;
            }
            String product_key = config.apps.get(game_id).product_key;
            if (product_key == null){
                logger.info("BingChengSDK 后台参数product_key未配置:" + product_key);
                return null;
            }
            //生成签名
            Map<String,String> signMap = new HashMap<>();
            signMap.put("check_time",String.valueOf(check_time));
            signMap.put("login_time",login_time);
            signMap.put("token",token);
            signMap.put("uid",uid);
            signMap.put("game_id",game_id);

            String signStr = format(signMap) + "&" + product_key;
            String  sign = Misc.md5(signStr);
            logger.info("冰橙SDK 签名参数为："+ signStr +"签名为：" +sign);

            signMap.put("sign",sign);
            String request = post(LOGIN_VERIFY, JSONObject.toJSONString(signMap));
            logger.info("请求冰橙 服务器的参数为："+signMap + "返回的结果为：" + request);
            if (request != null) {
                JSONObject json = JSON.parseObject(request);
                if (20000 == json.getIntValue("code")) {
                    return openUserBase(uid, getPlatformName(), channel_code);
                }
            }
        }
        return null;
    }
    /**
     *排序 map 根据a-z
     * @param params
     * @return
     */
    private static String format(Map<String, String> params) {
        StringBuffer base = new StringBuffer();
        Map<String, String> tempMap = new TreeMap<String, String>(params);
        // Obtain the basic string to calculate nsp_key.
        try {
            for (Map.Entry<String, String> entry : tempMap.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                base.append(k).append("=").append(URLEncoder.encode(v, "UTF-8")).append("&");
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encode parameters failed.");
            e.printStackTrace();
        }
        String body = base.toString().substring(0, base.toString().length() - 1);
        // Space and asterisks are escape characters.
        body = body.replaceAll("\\+", "%20").replaceAll("\\*", "%2A");
        return body;
    }
    /**
     * post请求 请求参数为json格式
     * @param strURL
     * @param params json
     * @return
     */
    public static String post(String strURL, String params ) {
        BufferedReader reader = null;
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            // connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            //一定要用BufferedReader 来接收响应， 使用字节来接收响应的方法是接收不到内容的
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
            out.append(params);
            out.flush();
            out.close();
            // 读取响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            String res = "";
            while ((line = reader.readLine()) != null) {
                res += line;
            }
            reader.close();


            return res;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "error"; // 自定义错误信息
    }
}

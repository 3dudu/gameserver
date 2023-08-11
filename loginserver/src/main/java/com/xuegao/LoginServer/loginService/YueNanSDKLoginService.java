package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class YueNanSDKLoginService  extends  AbsLoginService{

    private static final String requestUrl = "https://api.gamate.vn/verifyaccesstoken";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        try {
            if (parameters.length == 3) {
                String channel = parameters[0];
                String authorization = parameters[2];
                authorization = "Bearer "+authorization;
                String request = post(requestUrl, null,authorization);
                if (request != null) {
                    JSONObject json = JSONObject.parseObject(request);
                    logger.info("越南sdk登录返回参数为 ：" + json);
                    if ("Success".equals(json.getString("m"))) {
                        json = json.getJSONObject("r");
                        return openUserBase(json.getString("UserID"), getPlatformName(), channel);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * post请求 请求参数为json格式
     * @param strURL
     * @param params json
     * @return
     */
    public static String post(String strURL, String params ,String auth) {
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
            //connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.setRequestProperty("Authorization",auth);
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

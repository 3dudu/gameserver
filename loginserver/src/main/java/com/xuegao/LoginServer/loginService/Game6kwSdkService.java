package com.xuegao.LoginServer.loginService;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.Misc;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Game6kwSdkService extends AbsLoginService{

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {
        //取配置参数
        Constants.PlatformOption.Game6kwSDK config = getConfig();
        if (parameters.length == 4) {
            String channel = parameters[0];
            String pf = parameters[1];
            String token  = parameters[2];//应用ID，即APPID
            String app_id = parameters[3];

            if (config.apps.get(app_id)==null) {
                logger.info("6kw 参数未配置,app_id="+app_id);
                return null;
            }
            String sign_key=config.apps.get(app_id).sign_key_login;
            if (sign_key==null) {
                logger.info("6kw sign_key_login:未配置"+sign_key);
                return null;
            }
            String login_url=config.apps.get(app_id).login_url + app_id;
            if (login_url==null) {
                logger.info("6kw login_url:未配置"+login_url);
                return null;
            }
            JSONObject postJson = new JSONObject();
            postJson.put("token",token);
            String postData = postJson.toJSONString();
            String str="uscpcheck/" + app_id  + postData + sign_key;
            String sign = Misc.md5(str).toLowerCase();

            String request = post(login_url, postData,sign);

            logger.info("6kw post:"+login_url+",param="+postData+ ",auth6kw="+sign +",result="+request);
            if (request!=null) {
                JSONObject json = JSONObject.parseObject(request);
                logger.info("json:"+json.toString());
                if (json.getIntValue("code")==1) {
                    JSONObject data = JSONObject.parseObject(json.getString("data"));
                    return openUserBase(data.getString("uid"), getPlatformName(),channel);
                }
            }
        }
        return null;
    }

    /**
     * post请求 请求参数为json格式
     * @param strURL
     * @param params json
     * @return
     */
    public static String post(String strURL, String params ,String auth6kw) {
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
            connection.setRequestProperty("AUTH6KW",auth6kw);
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

package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.LoginServer.data.Constants.PlatformOption.SMengSDK;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 手盟
 *
 */
public class SMengSDKService extends AbsLoginService {

    /** queryLoginUrl */
    public static String queryLoginUrl = "http://www.19meng.com/api/v1/verify_session_id";

    @Override
    public UserPo loginUser(String[] parameters, JSONObject rs) {

        SMengSDK config = getConfig();
        if (parameters.length == 4) {
            String channel = parameters[0];
            String login_account  = parameters[2];//用戶Id
            String session_id = parameters[3];

            //拼接json
            StringBuffer buff = new StringBuffer();
            buff.append("{\"login_account\":").append("\"").append(login_account).append("\"").append(",")
                    .append("\"session_id\":").append("\"").append(session_id).append("\"}");

            String request = post(queryLoginUrl, buff.toString());
            logger.info("手盟平台 post:"+queryLoginUrl+",param="+buff.toString()+",result="+request);
            if(request!=null){
                JSONObject obj=JSON.parseObject(request);
                //{"result": 1,"message":"SESSION_ID有效"}
                //{"result": 0,"message":"SESSION_ID无效"}
                if(obj.getIntValue("result")==1){
                    return openUserBase(login_account, getPlatformName(),channel);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String login_account = "smtest_1_1904121532249618";
        String session_id = "19meng_z5veqpwekencd5q4";

        StringBuffer buff = new StringBuffer();
        buff.append("{\"login_account\":").append("\"").append(login_account).append("\"").append(",")
                .append("\"session_id\":").append("\"").append(session_id).append("\"}");
        String request = post(queryLoginUrl, buff.toString());
        System.out.println(buff.toString());
        System.out.println(JSON.parseObject(request).toJSONString());
    }


    /**
     * post请求 请求参数为json格式
     * @param strURL
     * @param params json
     * @return
     */
    public static String post(String strURL, String params) {
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

            //如果一定要使用如下方式接收响应数据， 则响应必须为: response.getWriter().print(StringUtils.join("{\"errCode\":\"1\",\"errMsg\":\"", message, "\"}")); 来返回
//            int length = (int) connection.getContentLength();// 获取长度
//            if (length != -1) {
//                byte[] data = new byte[length];
//                byte[] temp = new byte[512];
//                int readLen = 0;
//                int destPos = 0;
//                while ((readLen = is.read(temp)) > 0) {
//                    System.arraycopy(temp, 0, data, destPos, readLen);
//                    destPos += readLen;
//                }
//                String result = new String(data, "UTF-8"); // utf-8编码
//                System.out.println(result);
//                return result;
//            }
            return res;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "error"; // 自定义错误信息
    }
}

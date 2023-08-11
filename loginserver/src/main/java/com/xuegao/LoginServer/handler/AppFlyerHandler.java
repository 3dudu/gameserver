package com.xuegao.LoginServer.handler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.alibaba.fastjson.JSONObject;
import io.netty.util.CharsetUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.xuegao.LoginServer.data.MsgFactory;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.util.StringUtil;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 ICE, Inc. All rights reserved.
 * <p>
 * Company: 雪糕网络科技有限公司
 * <p>
 * " "
 *
 * @author july
 * @since 2020/6/25 15:22
 */

public class AppFlyerHandler {
    static final String AF_URL = "https://s2s.appsflyer.com/v2.0/";
    static final String AFAppid = "id1335479309";
    static final String AFDevKey = "YUwdo3EG5hfdrAnRyw8KRm";
    static final String channelCode = "26200";
    static Logger logger = Logger.getLogger(AppFlyerHandler.class);
    static DateFormat ISO8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000)// 设置连接主机服务超时时间
        .setConnectionRequestTimeout(2000)// 设置连接请求超时时间
        .setSocketTimeout(3000)// 设置读取数据连接超时时间
        .build();

    public static void main(String[] args) {
        String str = "2015-01-22T08:45:33.412127.0.0.1en-US";
        byte[] data = HmacUtils.getHmacSha256("w8sZzy8EaPaxFKfaoTqUi6".getBytes()).doFinal(str.getBytes());
        System.out.println(Hex.encodeHexString(data));
        System.out.println(getISO8601Timestamp());
    }

    public static String getISO8601Timestamp() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        ISO8601Format.setTimeZone(tz);
        String nowAsISO = ISO8601Format.format(new Date());
        return nowAsISO;
    }

    @Cmd("/api/af/report")
    public void AFReprot(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        logger.info("AFReport Receive="+jsonObject);
        String chcode = jsonObject.getString("ChannelCode");
        if (chcode == null || !chcode.equals(channelCode)) {
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("only support channelCode" + channelCode));
            return;
        }

        jsonObject.remove("ChannelCode");
        String ip = sender.getRemoteIp();
        String timestamp = getISO8601Timestamp();
        String sign = getAFSign(timestamp, ip, "zh_TW");
        if (StringUtil.empty(sign)) {
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("gen Sign Error"));
            return;
        }
        jsonObject.put("timestamp", timestamp);
        jsonObject.put("ip", ip);
        jsonObject.put("lang", "zh_TW");
        HttpPost httpPost = new HttpPost(AF_URL + AFAppid + "?af_sig=" + sign);
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("content-type", "application/json;charset=utf-8");
        httpPost.setEntity(new StringEntity(jsonObject.toJSONString(), Charset.forName("UTF-8")));
        CloseableHttpClient httpClient = HttpClients.createMinimal();
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                logger.info("AFReportResponse="+EntityUtils.toString(httpResponse.getEntity()));
                sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
                return;
            } else {
                String result = EntityUtils.toString(httpResponse.getEntity());
                logger.error("send af message error:" + result);
                sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(result));
                return;
            }
        } catch (IOException e) {
            logger.error(e);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("post message error"));
            return;
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String getAFSign(String timestamp, String ip, String lang) {
        try {
            String str = timestamp + ip + lang;
            byte[] data = HmacUtils.getHmacSha256(AFDevKey.getBytes()).doFinal(str.getBytes());
            return Hex.encodeHexString(data);
        } catch (Exception e) {
            logger.error(e);
            return "";
        }
    }
}

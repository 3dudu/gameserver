package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HmacSHA1Encryption;
import com.xuegao.PayServer.vo.DMMSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;

import io.netty.util.CharsetUtil;
import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class DMMSDKHandle {

    static Logger logger = Logger.getLogger(DMMSDKHandle.class);

    @Cmd("/p/DmmNotify/cb")
    public void DmmNotify(User sender, HttpRequestJSONObject params) {

        String json = params.toJSONString();
        String body = null;
        Map<String, String> head = params.getHttpHeaders();
        logger.info("--- DMMHeaders ---:" + head);
        JSONObject object = JSON.parseObject(json);
        logger.info("jsonobj:"+object);
        DMMSDKNotifyData.DMMDMMSDKNotifyDataParam notifyDataParam = null;
        if(object != null){
            notifyDataParam = JSONObject.toJavaObject(object, DMMSDKNotifyData.DMMDMMSDKNotifyDataParam.class);
        }
        logger.info("notifyDataParam:" + notifyDataParam);
        //param 里没有第三方订单说明是第一次调起  有就是回调
        if (notifyDataParam == null || notifyDataParam.payment_id == null) {
            if (params.getHttpBodyBuf() != null) {
                body = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
            }
            DMMSDKNotifyData.DMMSDKNotifyDataBody notifyData = JSONObject.toJavaObject(JSON.parseObject(body), DMMSDKNotifyData.DMMSDKNotifyDataBody.class);
            logger.info("DMMSDKNotifyDataBody:" + notifyData.toString());
            //校验下参数是不是一样
            OrderPo payTrade = OrderPo.findByOrdId(notifyData.ITEMS[0].SKU_ID);
            if (payTrade == null) {
                logger.info("订单号:" + notifyData.ITEMS[0].SKU_ID + ",不存在");
                sender.sendAndDisconnect(getErrorMsg("ERROR"));
                return;
            }
            if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
                logger.info("订单已经处理完毕,无须重复处理");
                sender.sendAndDisconnect(getErrorMsg("ERROR"));
                return;
            }
            //校验金额
            if (!payTrade.getPay_price().equals(Float.valueOf(notifyData.AMOUNT))) {
                logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "amount:" + Float.valueOf(notifyData.AMOUNT));
                sender.sendAndDisconnect("ERROR");
                return;
            }
            sender.sendAndDisconnect(getErrorMsg("OK"));
            payTrade.setThird_trade_no(notifyData.PAYMENT_ID);
            payTrade.updateToDB();
            logger.info("DMM已经将三方订单保存入库");
        } else {
            OrderPo payTrade = OrderPo.findByThirdId(notifyDataParam.payment_id);
            if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
                logger.info("订单已经处理完毕,无须重复处理");
                sender.sendAndDisconnect(getErrorMsg("ERROR"));
                return;
            }

            if ("3".equals(notifyDataParam.status)) {
                logger.info("订单未支付，请支付完成进行回调发货");
                sender.sendAndDisconnect(getErrorMsg("ERROR"));
                return;
            }

            //成功
            sender.sendAndDisconnect(getErrorMsg("Ok"));
            payTrade.setPlatform("DMMSDK");
            payTrade.setStatus(2);
            payTrade.updateToDB();
            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "DMMSDK");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        }

    }


    @Cmd("/p/DmmSp/buy")
    public void DmmSp(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }

        JSONObject pParam = JSON.parseObject(json);


        String appid = pParam.getString("appid");
        String token = pParam.getString("oauth_token");
        String tokenSecret = pParam.getString("oauth_token_secret");
        String xoauth_requestor_id = pParam.getString("viewerid");
        Constants.PlatformOption.DMMSDK config = GlobalCache.getPfConfig("DMMSDK");

        if (config.apps.get(appid) == null) {
            logger.info("DMM 参数未配置,appid=" + appid);
            sender.sendAndDisconnect(getErrorMsg("ERROR"));
            return;
        }
        String consumer_key = config.apps.get(appid).consumer_key;
        String consumer_secret = config.apps.get(appid).consumer_secret;
        String sp_url = config.apps.get(appid).sp_url;
        sp_url += "/payment/@me/@self/@app";

        String base_string = null;
        String hmacsha1key = null;
        String oauth_signature = null;
        long oauth_timestamp = System.currentTimeMillis() / 1000;
        long oauth_nonce = new Random().nextInt(101) + oauth_timestamp;
        try {
            /*POST&https%3A%2F%2Fsbx-osapi.dmm.com%2Fsocial_sp%2Frest%2Fpayment%2F%40me%2F%40self%
            2F%40app&oauth_consumer_key%3D0VhisrMCGswQ6OU4%26oauth_nonce%3D2855288%26oauth_signature_method
            %3DHMAC-SHA1%26oauth_timestamp%3D1685245629%26oauth_token%3D053f3d0f91554a30be4900ca4bc5e0ab%26oauth_version%3D1.0
            %26xoauth_requestor_id%3D3067411*/
            base_string = "POST&" + URLEncoder.encode(sp_url, "utf-8") + "&" + URLEncoder.encode(("oauth_consumer_key=" + consumer_key + "&oauth_nonce="
                    + oauth_nonce + "&oauth_signature_method=HMAC-SHA1" + "&oauth_timestamp=" + oauth_timestamp + "&oauth_token=" + token + "&oauth_version=1.0"
                    + "&xoauth_requestor_id=" + xoauth_requestor_id), "utf-8");
            hmacsha1key = URLEncoder.encode(consumer_secret, "utf-8") + "&" + URLEncoder.encode(tokenSecret, "utf-8");
            //生成oauth_signature 签名
            oauth_signature = URLEncoder.encode(HmacSHA1Encryption.hmacSHA1Encrypt(base_string, hmacsha1key), "utf-8");
            logger.info("base_string:" + base_string + "  ,hmacsha1key:" + hmacsha1key + ",生成oauth_signature 签名：" + oauth_signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //生成请求头 Authorization
        //Authorization：OAuth oauth_version="1.0",oauth_nonce="2855288",oauth_timestamp="1685245629",
        // oauth_consumer_key="0VhisrMCGswQ6OU4",oauth_token="053f3d0f91554a30be4900ca4bc5e0ab",
        // oauth_signature_method="HMAC-SHA1",oauth_signature="cHCLMZzXxwuyWNHQuS%2F%2BvQsosfI%3D",xoauth_requestor_id="3067411"
        String authorization = "OAuth " +
                "oauth_version=\"1.0\"," +
                "oauth_nonce=\"" + oauth_nonce + "\"," +
                "oauth_timestamp=\"" + oauth_timestamp + "\"," +
                "oauth_consumer_key=\"" + config.apps.get(appid).consumer_key + "\"," +
                "oauth_token=\"" + token + "\"," +
                "oauth_signature_method=\"HMAC-SHA1\"," +
                "oauth_signature=\"" + oauth_signature + "\"," +
                "xoauth_requestor_id=\"" + xoauth_requestor_id + "\"";
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", authorization);
        logger.info("--authorization--: " + authorization);
        //拼接请求参数
        pParam.remove("appid");
        pParam.remove("oauth_token");
        pParam.remove("oauth_token_secret");
        pParam.remove("viewerid");

        String request = null;
        try {
          /*  HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(sp_url);
            StringEntity postingString = new StringEntity(pParam.toJSONString(), ContentType.APPLICATION_JSON);// json传递
            post.setEntity(postingString);
            post.setHeader("Content-type", "application/json");
            post.setHeader("Authorization", authorization);
            HttpResponse response = httpClient.execute(post);
            request = EntityUtils.toString(response.getEntity());*/
            request =  getHttpPost(sp_url,map,pParam.toJSONString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("DMM 返回的request为：" + request);
        sender.sendAndDisconnect(request);
    }


    public static JSONObject getErrorMsg(String ret) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response_code", ret);
        return jsonObject;
    }

    private static String getHttpPost(String uri, Map<String, String> headerMap, String paramMap) {
        logger.info("url的请求为：" + uri + ",header :" +headerMap +",param :" + paramMap);
        HttpClient httpClient = wrapClient();//创建HttpClient,关键点一
        //uri,这里决定用 post,还是用其他方式
        HttpPost httpPost = new HttpPost(uri);
        //头
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpPost.addHeader(entry.getKey(), entry.getValue());
        }
        //体
        httpPost.setEntity(new StringEntity(paramMap, ContentType.create("application/json", "utf-8")));
        System.setProperty("jsse.enableSNIExtension", "false");//ssl免检  关键点二
        //访问
        HttpResponse httpResponse;
        String entity = null;
        try {
            httpResponse = httpClient.execute(httpPost);
            entity = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return entity;
    }

    public static HttpClient wrapClient() {//关键点三
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        X509Certificate[] x509Certificates, String s) {
                }

                @Override
                public void checkServerTrusted(
                        X509Certificate[] x509Certificates, String s) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sc.init(null, new TrustManager[] {tm}, null);
            SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(sc, NoopHostnameVerifier.INSTANCE);
            return HttpClients.custom().setSSLSocketFactory(ssf).build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            return HttpClients.createDefault();
        }
    }

}

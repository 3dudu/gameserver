package com.xuegao.PayServer.handler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.vo.Game6kwSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;


import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;



public class Game6kwSDKServiceHandler {
    static Logger logger = Logger.getLogger(Game6kwSDKServiceHandler.class);

    /**
     * 生成6kw的订单号传给前端
     * @param sender
     * @param params
     */
    @Cmd("/p/game6kwPlaceOrder/cb")
    public void game6kwPlaceOrder(User sender, JSONObject params) throws Exception {

        String gameOrder = params.getString("gameOrder");// 我们的订单
        String extension = params.getString("extension");// 传入游戏的appid 回调时需要

        OrderPo payTrade = OrderPo.findByOrdId(gameOrder);
        if (payTrade == null) {
            logger.info("订单号:" + gameOrder + ",不存在");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单号:" + gameOrder + ",不存在"));
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单已经处理完毕,无须重复处理"));
            return;
        }
        Constants.PlatformOption.Game6kwSDK game6kwSDK = GlobalCache.getPfConfig("Game6kwSDK");
        if (game6kwSDK == null) {
            logger.info("game6kwSDK 参数未配置 appid:" + extension);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("game6kwSDK 参数未配置 appid:" + extension));
            return;
        }

        String pay_url = game6kwSDK.apps.get(extension).pay_url + extension;
        if (pay_url == null) {
            logger.info("game6kwSDK 支付请求地址未配置 pay_url:" + pay_url);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("game6kwSDK 支付请求地址未配置 pay_url:" + pay_url));
            return;
        }
        String sign_key = game6kwSDK.apps.get(extension).sign_key_login;
        if (sign_key == null) {
            logger.info("game6kwSDK 未配置 sign_key:" + sign_key);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("game6kwSDK 未配置 sign_key:" + sign_key));
            return;
        }

        String notifyUrl = game6kwSDK.apps.get(extension).notify_url;//游戏支付的回调地址
        params.put("notifyUrl",notifyUrl);

        String str="uscppay/" + extension  + params.toJSONString() + sign_key;
        logger.info("请求md5签名字符："+ str);
        String sign = Misc.md5(str).toLowerCase();
        String result = post(pay_url,params.toJSONString(),sign);
        logger.info("请求完整的url:" + pay_url + "  ,请求的参数为："+params.toJSONString()+ " ,请求的auth6kw ；" + sign +"  ,请求6kw返回的信息为："+result);
        if (result != null) {
            JSONObject obj = JSON.parseObject(result);
            if (obj.getIntValue("code") == 1) {
                sender.sendAndDisconnect(result);
                logger.info("正常返回给客户端的参数："+result);
            }
            else {
                logger.info("请求6kw返回错误" + result);
                sender.sendAndDisconnect( result);
            }
        }
        else {
            logger.info("请求6kw返回错误,内容为空");
            sender.sendAndDisconnect("请求6kw返回错误,内容为空");
        }
    }

    /**
     * 支付回调
     * @param sender
     * @param params
     */
    @Cmd("/p/game6kwNotify/cb")
    public void game6kwNotify(User sender, HttpRequestJSONObject params)  {

        String json = null;
        Map<String ,String > head = new HashMap<>();
        if (!params.isEmpty()) {
            json = params.toJSONString();
            head = params.getHttpHeaders();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
            head = params.getHttpHeaders();
        }
        //第三方传入的签名 需要进行验证
        String auth6kw = head.get("AUTH6KW");
        Game6kwSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), Game6kwSDKNotifyData.class);
        logger.info("game6kwNotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.Game6kwSDK game6kwSDK = GlobalCache.getPfConfig("Game6kwSDK");
        String cause = "";
        if (null == game6kwSDK) {
            cause = "Game6kwSDK:参数未配置";
            logger.info(cause);
            sender.sendAndDisconnect(cause);
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.gameOrder, notifyData.gameOrder);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:gameOrder:" + notifyData.gameOrder);
            sender.sendAndDisconnect("SUCCESS");
            return;
        } else {
            redis.expire(notifyData.gameOrder, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.gameOrder);

        if (payTrade == null) {
            cause = "game_orderid error";
            sender.sendAndDisconnect(cause);
            logger.error("订单号:" + notifyData.gameOrder + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            cause = "SUCCESS";
            sender.sendAndDisconnect(cause);
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals (Float.valueOf(notifyData.total)/100)){
            cause = "订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + Float.valueOf(notifyData.total)/100;
            logger.info(cause);
            sender.sendAndDisconnect(" total_do_not_match");
            return;
        }

        String paykey  = game6kwSDK.apps.get(notifyData.extension).sign_key_pay;

        String signStr = json +  paykey;
        logger.info("生成签名前的字符串："+signStr);
        String sign = MD5Util.md5(signStr).toLowerCase();

        logger.info("服务器签名："+sign+",传入签名："+auth6kw);
        //校验签名
        if (sign.equals(auth6kw)) {
            //验签通过
            sender.sendAndDisconnect("SUCCESS");
            payTrade.setThird_trade_no(notifyData.orderID);
            payTrade.setPlatform("Game6kwSDK");
            payTrade.setStatus(2);

            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "Game6kwSDK");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {
            cause = "验签失败";
            logger.info(cause);
            sender.sendAndDisconnect("auth6kw_verify_failed");
        }

    }
    /**
     * post请求 请求参数为json格式
     * @param strURL
     * @param params json
     * @return
     */
    public static String post(String strURL, String params ,String auth6kw) {

        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 创建Post请求
        HttpPost httpPost = new HttpPost(strURL);
        // 我这里利用阿里的fastjson，将Object转换为json字符串;
        StringEntity entity = new StringEntity(params, "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);

        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        httpPost.setHeader("AUTH6KW",auth6kw);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                String  res=EntityUtils.toString(responseEntity);
                return res;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

      return "{\"code\":0,\"msg\":\"请求报错\",\"data\":{}}";
    }
}

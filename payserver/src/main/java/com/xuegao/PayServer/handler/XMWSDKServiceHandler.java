package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;

import com.xuegao.PayServer.vo.XMWSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

public class XMWSDKServiceHandler {
    static Logger logger = Logger.getLogger(XMWSDKServiceHandler.class);

    private static final String create_order_url = "http://open.xmwan.com/v2/purchases";

    /**
     * 创建熊猫玩订单接口
     * @param sender
     * @param params
     */
    @Cmd("/p/CreateOrderXMWSDK/cb")
    public void CreateOrderXMWSDK(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        Constants.PlatformOption.XMWSDK xmwsdk = GlobalCache.getPfConfig("XMWSDK");
        JSONObject data = JSONObject.parseObject(json);
        String access_token = data.getString("access_token");
        String client_id = data.getString("client_id");
        String client_secret = data.getString("client_secret");
        //应用游戏的用户标识
        String app_order_id = data.getString("app_order_id");
        String app_user_id = data.getString("app_user_id");
        String amount = data.getString("amount");//消费订单的金额,单位为元
        String app_subject  =data.getString("app_subject");
        String role_name = data.getString("role_name");
        String sid = data.getString("sid");
        String pid = data.getString("pid");
        String server_name = data.getString("server_name");
        String notify_url = "";
        if (xmwsdk.apps.get(client_id)!=null) {
            notify_url=xmwsdk.apps.get(client_id).callback_url;
        }
        String timestamp = ""+System.currentTimeMillis()/1000L;
        String sign= Misc.md5("amount="+amount+"&app_ext1="+client_id+"&app_order_id="+app_order_id+"&app_subject="+app_subject+"&app_user_id="
                +app_user_id+"&notify_url="+notify_url+"&timestamp="+timestamp+"&client_secret="+client_secret);
        String game_detail="{\"serverid\":\""+sid+"\",\"servername\":\""+server_name+"\",\"accountid\":\""+pid+"\",\"rolename\":\""+role_name+"\"}";
        String postData = "access_token="+access_token+"&client_id="+client_id+"&client_secret="+client_secret+"&app_order_id="+app_order_id
                +"&app_user_id="+app_user_id+"&notify_url="+notify_url+"&amount="+amount+"&timestamp="+timestamp+"&sign="+sign
                +"&app_subject="+app_subject +"&game_detail="+game_detail+"&app_ext1="+client_id;
        String result = RequestUtil.request3TimesWithPost(create_order_url, postData.getBytes(CharsetUtil.UTF_8));
        logger.info("熊猫玩平台 post:" + create_order_url + ",postData=" + postData + ",result=" + result);
        if (result!=null) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            data.clear();
            data.put("ret", 0);
            data.put("msg", "success");
            data.put("serial",jsonObject.getString("serial"));
            sender.sendAndDisconnect(data);
        }
    }

    /**
     * 支付回调接口
     * @param sender
     * @param params
     */
    @Cmd("/p/XMWSDKPayNotify/cb")
    public void XMWSDKPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        XMWSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), XMWSDKNotifyData.class);
        logger.info("XMWSDKNotifyData:" + notifyData.toString());
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.app_order_id, notifyData.app_order_id);
        if (isSaveSuc == 0L) {
            logger.info("同一订单多次请求,无须重复处理:order_id:" + notifyData.app_order_id);
            sender.sendAndDisconnect("fail");
            return;
        } else {
            redis.expire(notifyData.app_order_id, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.app_order_id);
        if (payTrade == null) {
            logger.info("订单号:" + notifyData.app_order_id + ",不存在");
            sender.sendAndDisconnect("fail");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("fail");
            return;
        }
        //检验client_id参数是否匹配
        Constants.PlatformOption.XMWSDK xmwsdk = GlobalCache.getPfConfig("XMWSDK");
        String cause = "";
        if (null == xmwsdk.apps.get(notifyData.app_ext1)) {
            cause = "client_id未配置:"+notifyData.app_ext1;
            logger.info(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        //检验client_secret是否配置
        String client_secret=xmwsdk.apps.get(notifyData.app_ext1).client_secret;
        if (null == client_secret) {
            cause = "xmwsdk:client_secret未配置";
            logger.info(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        //判断是否支付成功
        if (notifyData.status.equals("unpaid")) {
            cause = "该订单尚未支付";
            logger.info(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        //验签
        String baseStr = "amount="+notifyData.amount+"&app_ext1="+notifyData.app_ext1+"&app_order_id="+notifyData.app_order_id+
                "&app_subject=" +notifyData.app_subject+"&app_user_id=" +notifyData.app_user_id+ "&serial=" +notifyData.serial+
                "&status=" +notifyData.status+"&client_secret="+client_secret;
        logger.info("baseStr:"+baseStr);
        baseStr = HelperEncrypt.encryptionMD5(baseStr).toLowerCase();
        if (!notifyData.sign.equals(baseStr)) {
            logger.info("验签失败!熊猫玩SDK sign="+notifyData.sign+"==============="+"服务端生成签名:"+baseStr);
            sender.sendAndDisconnect("fail");
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals((float)notifyData.amount)) {
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + (float)notifyData.amount);
            sender.sendAndDisconnect("fail");
            return;
        }
        //成功
        sender.sendAndDisconnect("success");
        payTrade.setThird_trade_no(notifyData.serial);
        payTrade.setPlatform("XMWSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "XMWSDK");
        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
    }
}


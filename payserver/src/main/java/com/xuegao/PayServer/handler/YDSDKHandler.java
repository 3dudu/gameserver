package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.util.HmacSHA1Encryption;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.vo.IceBirdSDKNotifyData;
import com.xuegao.PayServer.vo.YDSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @Author: LiuBin
 * @Date: 2020/2/3 16:12
 */
public class YDSDKHandler {
    static Logger logger = Logger.getLogger(YDSDKHandler.class);


    @Cmd("/p/YDSDKPayNotify/cb")
    public void YDSDKPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        YDSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), YDSDKNotifyData.class);
        logger.info("YDSDKNotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.YDSDK ydsdk = GlobalCache.getPfConfig("YDSDK");
        //检验appId是否配置
        if(null==ydsdk.apps.get(notifyData.appId)){
            logger.info("优点平台 参数未配置,appId="+ notifyData.appId);
            sender.sendAndDisconnect("fail");
            return;
        }
        //检验openkey是否配置
        String openkey = ydsdk.apps.get(notifyData.appId).openkey;
        if (null==openkey) {
            logger.info("openkey未配置,openkey="+openkey);
            sender.sendAndDisconnect("fail");
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        String cause = "";
        long isSaveSuc = redis.STRINGS.setnx(notifyData.orderNo, notifyData.orderNo);
        if (isSaveSuc == 0L) {
            cause = "同一订单多次请求,无须重复处理:partnerOrder:" + notifyData.orderNo;
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("同一订单多次请求,无须重复处理"));
            return;
        } else {
            redis.expire(notifyData.orderNo, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.orderNo);
        if (payTrade == null) {
            cause = "订单号:"+notifyData.orderNo+",不存在";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单号不存在:"+notifyData.orderNo));
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单已经处理完毕,无须重复处理"));
            return;
        }
        //订单状态 0--支付失败 1—支付成功
        if (notifyData.result!=1) {
            cause = "交易结果:失败";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("交易结果:失败"));
            return;
        }
        //验签
        StringBuffer sb = new StringBuffer();
//        交易最终结果以 MD5 方式签名，签名算法如下（只需参数值参与签名）： sign= MD5(orderNo + userName + result + amount + pay_time + pay_channel + openKey)
        sb.append(notifyData.orderNo).append(notifyData.userName)
          .append(notifyData.result).append(notifyData.amount)
          .append(notifyData.pay_time).append(notifyData.pay_channel)
          .append(openkey);
        String signStr = HelperEncrypt.encryptionMD5(sb.toString()).toLowerCase();
        if (!notifyData.sign.equals(signStr)) {
            logger.info("验签失败!优点SDK签名="+notifyData.sign+"==============="+"服务器签名:"+signStr);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("验签失败"));
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(notifyData.amount))) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配支付金额:"+Float.valueOf(notifyData.amount)+"(元)");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单金额与支付金额不一致"));
            return;
        }
        //成功
        sender.sendAndDisconnect("ok");
        payTrade.setThird_trade_no(notifyData.payNo);
        payTrade.setPlatform("YDSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "YDSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }

    @Cmd("/p/YDX7SDKPayNotify/cb")
    public void YDX7SDKPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        YDSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), YDSDKNotifyData.class);
        logger.info("YDSDKNotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.YDSDK ydsdk = GlobalCache.getPfConfig("YDSDK");
        //检验appId是否配置
        if(null==ydsdk.apps.get(notifyData.appId)){
            logger.info("优点平台 参数未配置,appId="+ notifyData.appId);
            sender.sendAndDisconnect("fail");
            return;
        }
        //检验openkey是否配置
        String appSecret = ydsdk.apps.get(notifyData.appId).appSecret;
        if (null==appSecret) {
            logger.info("appSecret未配置,appSecret="+appSecret);
            sender.sendAndDisconnect("fail");
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        String cause = "";
        long isSaveSuc = redis.STRINGS.setnx(notifyData.orderNo, notifyData.orderNo);
        if (isSaveSuc == 0L) {
            cause = "同一订单多次请求,无须重复处理:partnerOrder:" + notifyData.orderNo;
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("同一订单多次请求,无须重复处理"));
            return;
        } else {
            redis.expire(notifyData.orderNo, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.orderNo);
        if (payTrade == null) {
            cause = "订单号:"+notifyData.orderNo+",不存在";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单号不存在:"+notifyData.orderNo));
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单已经处理完毕,无须重复处理"));
            return;
        }
        //订单状态 0--支付失败 1—支付成功
        if (notifyData.result!=1) {
            cause = "交易结果:失败";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("交易结果:失败"));
            return;
        }
        //验签
        Map<String ,String > sigMap = new HashMap<>();
        sigMap.put("appId", notifyData.appId);
        sigMap.put("payNo",notifyData.payNo);
        sigMap.put("orderNo",notifyData.orderNo);
        sigMap.put("userName",notifyData.userName);
        sigMap.put("result",String.valueOf( notifyData.result));
        sigMap.put("pay_channel",notifyData.pay_channel);
        sigMap.put("amount",notifyData.amount);
        sigMap.put("pay_time",notifyData.pay_time);
        sigMap.put("userpara",notifyData.userpara);
        sigMap.put("orderType",String.valueOf( notifyData.orderType ));
        sigMap.put("callback",String.valueOf( notifyData.callback ));
        String signStr =  createSign(sigMap,appSecret).toLowerCase(Locale.ROOT);
        if (!notifyData.sign.equals(signStr)) {
            logger.info("验签失败!优点SDK签名="+notifyData.sign+"==============="+"服务器签名:"+signStr);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("验签失败"));
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(notifyData.amount))) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配支付金额:"+Float.valueOf(notifyData.amount)+"(元)");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单金额与支付金额不一致"));
            return;
        }
        //成功
        sender.sendAndDisconnect("ok");
        payTrade.setThird_trade_no(notifyData.payNo);
        payTrade.setPlatform("YDSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "YDSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }

    // 生成签名
    public static String createSign( Map<String, String> params, String appSecretKey) {
        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);
        StringBuffer sb = new StringBuffer();
        for (Object key : keys) {
            if ( !"".equals (params.get(String.valueOf(key))) && !params.get(String.valueOf(key)).isEmpty() && !key.equals("sign") && !key.equals("signType") ) {
                sb.append(key).append("=").append(params.get(String.valueOf(key))).append("&");
            }
        }
        String text = sb.toString();
        if (text.endsWith("&")) {
            text = text.substring(0, text.length() - 1);
        }
        logger.info("优点-小七 签名原串为：" + text +"签名secretKey："+ appSecretKey);
        String  sign = HmacSHA1Encryption.HMACSHA256Encrypt(text,appSecretKey);
        return sign;
    }
}

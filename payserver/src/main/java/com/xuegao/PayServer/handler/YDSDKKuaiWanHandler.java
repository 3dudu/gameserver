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
import com.xuegao.PayServer.util.VivoSignUtils;
import com.xuegao.PayServer.vo.YDSDKKuaiWanNotifyData;
import com.xuegao.PayServer.vo.YDSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.Locale;
import java.util.Map;

public class YDSDKKuaiWanHandler {
    static Logger logger = Logger.getLogger(YDSDKKuaiWanHandler.class);


    @Cmd("/p/YDSDKKuaiWanPayNotify/cb")
    public void YDSDKKuaiWanPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        YDSDKKuaiWanNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), YDSDKKuaiWanNotifyData.class);
        logger.info("YDSDKKuaiWanNotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.YDSDKKuaiWan ydsdk = GlobalCache.getPfConfig("YDSDKKuaiWan");
        //检验appId是否配置
        if(null==ydsdk.apps.get(notifyData.appId)){
            logger.info("优点平台快游戏 参数未配置,appId="+ notifyData.appId);
            sender.sendAndDisconnect("fail");
            return;
        }
        //检验openkey是否配置
        String appSecret = ydsdk.apps.get(notifyData.appId).appSecret;
        if (null==appSecret) {
            logger.info("appSecret未配置");
            sender.sendAndDisconnect("fail");
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        String cause = "";
        long isSaveSuc = redis.STRINGS.setnx(notifyData.orderNo, notifyData.orderNo);
        if (isSaveSuc == 0L) {
            cause = "同一订单多次请求,无须重复处理:orderNo:" + notifyData.orderNo;
            logger.info(cause);
            sender.sendAndDisconnect("ok");
            return;
        } else {
            redis.expire(notifyData.orderNo, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.orderNo);
        if (payTrade == null) {
            cause = "订单号:"+notifyData.orderNo+",不存在";
            logger.info(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            sender.sendAndDisconnect("ok");
            return;
        }
        //订单状态 2--支付失败 1—支付成功
        if (!"1".equals( notifyData.result)) {
            cause = "交易结果:失败";
            logger.info(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        //校验签名
        Map map = JSON.parseObject(JSON.toJSONString(notifyData));
        map.remove("sign");
        String strA = VivoSignUtils.createLinkString(map,true,false);
        String signStr =  HmacSHA1Encryption.HMACSHA256Encrypt(strA,appSecret).toLowerCase(Locale.ROOT);
        logger.info("签名字符串为：" + strA + "   ,+签名结果为：" + signStr);
        if (!notifyData.sign.equals(signStr)) {
            logger.info("验签失败!优点快游戏SDK签名="+notifyData.sign+"==============="+"服务器签名:"+signStr);
            sender.sendAndDisconnect("fail");
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(notifyData.amount))) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配支付金额:"+Float.valueOf(notifyData.amount)+"(元)");
            sender.sendAndDisconnect("fail");
            return;
        }
        //成功
        sender.sendAndDisconnect("ok");
        payTrade.setThird_trade_no(notifyData.payNo);
        payTrade.setPlatform("YDSDKKuaiWan");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "YDSDKKuaiWan");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }
}

package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.util.VivoSignUtils;
import com.xuegao.PayServer.util.YueWanUtils;
import com.xuegao.PayServer.vo.BingChengSDKNotifyData;
import com.xuegao.PayServer.vo.WanXinH5SDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class BingChengSDKHandler {
    static Logger logger = Logger.getLogger(BingChengSDKHandler.class);

    @Cmd("/p/BingChengSDKPayNotify/cb")
    public void BingChengSDKPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        BingChengSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), BingChengSDKNotifyData.class);
        logger.info("BingChengSDKNotifyData:" + notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.BingChengSDK config = GlobalCache.getPfConfig("BingChengSDK");
        //检验gameId是否配置
        if (null == config.apps.get(notifyData.game_id)) {
            logger.info("冰橙SDK 参数未配置,channel_code=" + notifyData.game_id);
            sender.sendAndDisconnect( "FAIL");
            return;
        }
        //检验app_secret是否配置
        String charge_key = config.apps.get(notifyData.game_id).charge_key;
        if (null == charge_key) {
            logger.info("charge_key未配置,charge_key=" + charge_key);
            sender.sendAndDisconnect("FAIL");
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");

        long isSaveSuc = redis.STRINGS.setnx(notifyData.cp_order_id, notifyData.cp_order_id);
        if (isSaveSuc == 0L) {
            logger.info("同一订单多次请求,无须重复处理:cp_order_id:" + notifyData.cp_order_id);
            sender.sendAndDisconnect("SUCCESS");
            return;
        } else {
            redis.expire(notifyData.cp_order_id, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.cp_order_id);
        if (payTrade == null) {
            logger.info("订单号:" + notifyData.cp_order_id + ",不存在");
            sender.sendAndDisconnect("FAIL");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info( "订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("SUCCESS");
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.parseFloat( notifyData.amount ))) {
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "money:" +  notifyData.amount);
            sender.sendAndDisconnect("FAIL");
            return;
        }
        //验签
        Map<String ,String > data = new HashMap<>();
        data.put("game_id",notifyData.game_id);
        data.put("uid",notifyData.uid);
        data.put("amount",notifyData.amount);
        data.put("order_id",notifyData.order_id);
        data.put("cp_order_id",notifyData.cp_order_id);
        data.put("role_id",notifyData.role_id);
        data.put("server_id",notifyData.server_id);
        data.put("ext",String.valueOf(notifyData.ext));
        data.put("time",notifyData.time);
        logger.info("冰橙 sdk签名字符集为："+ data);
        String signStr = HelperEncrypt.encryptionMD5(YueWanUtils.getSortQueryString (data)+"&"+charge_key).toLowerCase();
        if (!notifyData.sign.equals(signStr)) {
            logger.info("验签失败!冰橙 sign=" + notifyData.sign + "============" + "服务器计算签名 signStr:" + signStr);
            sender.sendAndDisconnect("FAIL");
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //成功
        sender.sendAndDisconnect("SUCCESS");
        payTrade.setThird_trade_no(notifyData.order_id);
        payTrade.setPlatform("BingChengSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "BingChengSDK");
        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
    }

}

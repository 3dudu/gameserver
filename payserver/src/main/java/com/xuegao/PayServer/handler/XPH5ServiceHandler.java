package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.vo.XPSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

public class XPH5ServiceHandler {
    static Logger logger = Logger.getLogger(XPH5ServiceHandler.class);

    @Cmd("/p/XPH5/cb")
    public void XPH5(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        XPSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), XPSDKNotifyData.class);
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.callbackInfo, notifyData.callbackInfo);
        if (isSaveSuc == 0L) {
            logger.info("同一订单多次请求,无须重复处理:orderId:" + notifyData.callbackInfo);
            sender.sendAndDisconnect("fail");
            return;
        } else {
            redis.expire(notifyData.callbackInfo, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.callbackInfo);
        if (payTrade == null) {
            logger.info("订单号:" + notifyData.callbackInfo + ",不存在");
            sender.sendAndDisconnect("fail");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("success");
            return;
        }
        Constants.PlatformOption.XPH5 xph5 = GlobalCache.getPfConfig("XPH5");
        StringBuffer buff = new StringBuffer();
        buff.append(notifyData.appId).append(notifyData.orderId).append(notifyData.openId).
                append(notifyData.serverId).append(notifyData.roleId).
                append(notifyData.money).append(notifyData.amount).
                append(notifyData.callbackInfo).append(xph5.appServerKey);
        String sign = MD5Util.md5(buff.toString()).toLowerCase();
        //判断订单状态是否成功
        if (notifyData.orderStatus != 1) {
            logger.info("订单状态不成功");
            sender.sendAndDisconnect("fail");
            return;
        }
        //校验签名
        if (!sign.equals(notifyData.newsign)) {
            logger.info("验签失败!喜扑H5 回调newsign="+notifyData.newsign+"==============="+"服务端生成sign:"+sign);
            sender.sendAndDisconnect("fail");
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //成功
        sender.sendAndDisconnect("success");
        payTrade.setPay_price(Float.valueOf(notifyData.money)/100F);
        payTrade.setPlatform("XPH5");
        payTrade.setThird_trade_no(notifyData.orderId);
        payTrade.setStatus(2);//2.收到回调验证成功
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "XPH5");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }
}

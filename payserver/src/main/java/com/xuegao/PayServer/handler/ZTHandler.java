package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.vo.ZTSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;


public class ZTHandler {
    static Logger logger = Logger.getLogger(ZTHandler.class);

    @Cmd("/p/zantaiNotify/cb")
    public void zantaiNotify(User sender, HttpRequestJSONObject params) throws Exception {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        ZTSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), ZTSDKNotifyData.class);
        logger.info("ZTNotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.ZTSDK ztsdk = GlobalCache.getPfConfig("ZTSDK");
        if (null == ztsdk) {
            logger.info( "赞钛:参数未配置");
            sender.sendAndDisconnect(-1);
            return;
        }
        if (null == notifyData) {
            logger.info("赞钛:参数格式错误");
            sender.sendAndDisconnect(-1);
            return;
        }
        String ext = notifyData.ext;
        String[] split = ext.split(",");
        if (split.length != 2) {
            logger.error("赞钛订单生成额外参数错误");
            sender.sendAndDisconnect(-1);
            return;
        }
        String order = split[0];
        String channel_code = split[1];

        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(order, order);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:game_orderid:" + order);
            sender.sendAndDisconnect(4);
            return;
        } else {
            redis.expire(order, 30);
        }

        OrderPo payTrade = OrderPo.findByOrdId(order);

        if (payTrade == null) {
            logger.error("订单号:" + order+ ",不存在");
            sender.sendAndDisconnect(-1);
            return;
        }

        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect(4);
            return;
        }
        if (!payTrade.getPay_price().equals (Float.parseFloat(notifyData.money))){
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + notifyData.money);
            sender.sendAndDisconnect(5);
            return;
        }
        String sign = MD5Util.md5(notifyData.uid + notifyData.money + notifyData.time + notifyData.sid +
                  notifyData.orderid + notifyData.ext + ztsdk.apps.get(channel_code).pay_key);
        if (sign.equals(notifyData.flag)) {
            sender.sendAndDisconnect(1);
            payTrade.setThird_trade_no(notifyData.orderid);
            payTrade.setPlatform("ZTSDK");
            payTrade.setStatus(2);

            //发钻石
            payTrade.setPay_price(Float.valueOf(notifyData.money));
            payTrade.updateToDB();
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "ZTSDK");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {
            logger.error("签名验证失败");
            sender.sendAndDisconnect(3);
            return;
        }
    }


    /*加密签名，注：.是连接符，不参与加密
    md5("$uid.$money.$time.$sid.$orderid.$ext.$pay_key")*/
}

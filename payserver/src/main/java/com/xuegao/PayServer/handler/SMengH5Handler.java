package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.vo.SMSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

public class SMengH5Handler {
    static Logger logger = Logger.getLogger(SMengH5Handler.class);

    @Cmd("/p/SMengH5/cb")
    public void SMengH5(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        SMSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), SMSDKNotifyData.class);
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        logger.info("SMH5NotifyData:" + notifyData.toString());
        long isSaveSuc = redis.STRINGS.setnx(notifyData.coOrderId, notifyData.coOrderId);
        if (isSaveSuc == 0L) {
            logger.info("同一订单多次请求,无须重复处理:orderId:" + notifyData.coOrderId);
            sender.sendAndDisconnect("fail");
            return;
        } else {
            redis.expire(notifyData.coOrderId, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.coOrderId);
        if (payTrade == null) {
            logger.info("订单号:" + notifyData.coOrderId + ",不存在");
            sender.sendAndDisconnect("fail");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("success");
            return;
        }
        int success = notifyData.success;
        if (success == 1) {
            logger.info("订单状态=" + success + ",失败");
            return;
        }
        Constants.PlatformOption.SMengH5 pfconfig = GlobalCache.getPfConfig("SMengH5");
        //校验金额
        Float pay_price = payTrade.getPay_price();
        if (!pay_price.equals((float) notifyData.amount)) {
            logger.info("充值金额=" + notifyData.amount + ",订单金额=" + pay_price + ",充值金额与订单金额不一致");
            sender.sendAndDisconnect("fail");
            return;
        }
        //验签
        StringBuffer buff = new StringBuffer();
        String secret = pfconfig.Secret;
        buff.append("orderId=").append(notifyData.orderId).
                append("&uid=").append(notifyData.uid).
                append("&amount=").append(notifyData.amount).
                append("&coOrderId=").append(notifyData.coOrderId).
                append("&success=").append(notifyData.success).
                append("&secret=").append(secret);
        String sign = HelperEncrypt.encryptionMD5(buff.toString()).toLowerCase();
        if (!sign.equals(notifyData.sign)) {
            logger.info("验签失败");
            sender.sendAndDisconnect("fail");
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //成功
        sender.sendAndDisconnect("SUCCESS");
        payTrade.setThird_trade_no(notifyData.orderId);
        payTrade.setPlatform("SMengH5");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "SMengH5");
        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
    }
}


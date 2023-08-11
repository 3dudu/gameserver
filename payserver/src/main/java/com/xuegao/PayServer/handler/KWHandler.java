package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.vo.FFSDKGift;
import com.xuegao.PayServer.vo.FFSDKNotifyData;
import com.xuegao.PayServer.vo.KWSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.StringUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.*;

public class KWHandler {
    static Logger logger = Logger.getLogger(KWHandler.class);

    /**
     * 支付回调
     * @param sender
     * @param params
     */
    @Cmd("/p/kwsdknotify/cb")
    public void kwsdknotify(User sender, HttpRequestJSONObject params) throws Exception {

        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
       KWSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), KWSDKNotifyData.class);
        logger.info("kwsdknotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.KWSDK kwsdk = GlobalCache.getPfConfig("KWSDK");
        String cause = "";
        if (null == kwsdk) {
            cause = "kwsdk:参数未配置";
            logger.info(cause);
            sender.sendAndDisconnect(cause);
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(notifyData.ordersn).append(notifyData.fee).append(kwsdk.gameId).append(kwsdk.paykey);
        String sign = MD5Util.md5(sb.toString());

        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.attach, notifyData.attach);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:game_orderid:" + notifyData.attach);
            sender.sendAndDisconnect("success");
            return;
        } else {
            redis.expire(notifyData.attach, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.attach);

        if (payTrade == null) {
            cause = "game_orderid error";
            sender.sendAndDisconnect(cause);
            logger.error("订单号:" + notifyData.attach + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            cause = "SUCCESS";
            sender.sendAndDisconnect(cause);
            return;
        }


        logger.info(sign+","+notifyData.sign);
        //校验签名
        if (sign.equals(notifyData.sign)) {
            //验签通过
            sender.sendAndDisconnect("SUCCESS");
            payTrade.setThird_trade_no(notifyData.attach);
            payTrade.setPlatform("KWSDK");
            payTrade.setStatus(2);
            if (!payTrade.getPay_price().equals(Float.valueOf(notifyData.fee))) {
                logger.info("订单金额:"+payTrade.getPay_price()+"不匹配"+"amount:"+Float.valueOf(notifyData.fee));
                sender.sendAndDisconnect("FAILURE");
                return;
            }

            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "KWSDK");
            payTrade.setPay_price(Float.valueOf(notifyData.fee));
            payTrade.updateToDB();
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {
            cause = "验签失败";
            logger.info(cause);
            sender.sendAndDisconnect("sign_data_verify_failed");
        }
    }
}

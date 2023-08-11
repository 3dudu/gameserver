package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.vo.IceBirdSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

public class IceBirdHandler {
    static Logger logger = Logger.getLogger(IceBirdHandler.class);

    @Cmd("/p/IceBirdPayNotify/cb")
    public void IceBirdPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        IceBirdSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), IceBirdSDKNotifyData.class);
        logger.info("IceBirdSDKNotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.IceBirdSDK iceBirdSDK = GlobalCache.getPfConfig("IceBirdSDK");
        //检验gameId是否配置
        if(!iceBirdSDK.game_id.equals(notifyData.gameId)){
            String cause ="冰鸟平台 参数未配置,gameId="+ notifyData.gameId;
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("gameId未配置"));
            return;
        }
        //检验app_secret是否配置
        String app_secret = iceBirdSDK.app_secret;
        if (null==app_secret) {
            logger.info("app_secret未配置,app_secret="+app_secret);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("app_secret未配置"));
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        String cause = "";
        long isSaveSuc = redis.STRINGS.setnx(notifyData.cpOrderId, notifyData.cpOrderId);
        if (isSaveSuc == 0L) {
            cause = "同一订单多次请求,无须重复处理:partnerOrder:" + notifyData.cpOrderId;
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("同一订单多次请求,无须重复处理"));
            return;
        } else {
            redis.expire(notifyData.cpOrderId, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.cpOrderId);
        if (payTrade == null) {
            cause = "订单号:"+notifyData.cpOrderId+",不存在";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单号不存在:"+notifyData.cpOrderId));
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单已经处理完毕,无须重复处理"));
            return;
        }
        //订单状态 0--支付失败 1—支付成功
        if (notifyData.orderStatus!=1) {
            cause = "订单状态:支付失败";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单状态:支付失败"));
            return;
        }
        //验签
        StringBuffer sb = new StringBuffer();
//        appId=test_00001bfOrderId=2018062610320886199115callbackInfo=chann
//        elId=2channelInfo=nullchannelOrderId=cpOrderId=2018062624487379gameId=8668money=2000orderStatus=1time=1529980377userId=1234567890
//        202b962234w4ers2aa
        sb.append("appId=").append(notifyData.appId).append("bfOrderId=").append(notifyData.bfOrderId).append("callbackInfo=")
                .append(notifyData.callbackInfo).append("channelId=").append(notifyData.channelId).append("channelInfo=")
                .append(notifyData.channelInfo).append("channelOrderId=").append(notifyData.channelOrderId).append("cpOrderId=")
                .append(notifyData.cpOrderId).append("gameId=").append(notifyData.gameId).append("money=")
                .append(notifyData.money).append("orderStatus=").append(notifyData.orderStatus).append("time=").append(notifyData.time)
                .append("userId=").append(notifyData.userId).append(app_secret);
        String signStr = HelperEncrypt.encryptionMD5(sb.toString()).toLowerCase();
        if (!notifyData.sign.equals(signStr)) {
            logger.info("验签失败!冰鸟 sign="+notifyData.sign+"==============="+"32位小写后signStr:"+signStr);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("验签失败"));
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals((float)notifyData.money/100F)) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配"+"money:"+(float)notifyData.money/100F);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单金额与支付金额不一致"));
            return;
        }
        //成功
        sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
        payTrade.setThird_trade_no(notifyData.bfOrderId);
        payTrade.setPlatform("IceBirdSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "IceBirdSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }
}

package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.FTNNResultVo;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.po.UserPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.vo.FTNNSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

public class FTNNSDKHandler {
    static Logger logger = Logger.getLogger(FTNNSDKHandler.class);

    @Cmd("/p/FTNNSDKPayNotify/cb")
    public void FTNNSDKPayNotify(User sender, HttpRequestJSONObject params) {
        String json;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        FTNNSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), FTNNSDKNotifyData.class);
        logger.info("FTNNNotifyData: " + notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.FTNNSDK ftnnsdk = GlobalCache.getPfConfig("FTNNSDK");
        //检验secret_key是否配置
        if (null == ftnnsdk.secretKey) {
            logger.info("secretKey未配置");
            sender.sendAndDisconnect("fail");
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        String cause;
        long isSaveSuc = redis.STRINGS.setnx(notifyData.mark, notifyData.mark);
        if (isSaveSuc == 0L) {
            cause = "订单已提交（提交订单号必须唯一）" + notifyData.mark;
            logger.info(cause);
            FTNNResultVo resultVo = new FTNNResultVo.Builder().status(FTNNResultVo.Status.EXCEPTION).code("orderid_exist")
                    .money(notifyData.money).gamemoney(notifyData.gamemoney)
                    .msg(cause).build();
            sender.sendAndDisconnect(resultVo);
            return;
        } else {
            redis.expire(notifyData.mark, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.mark);
        if (payTrade == null) {
            cause = "订单号:" + notifyData.mark + ",不存在";
            logger.info(cause);
            FTNNResultVo resultVo = new FTNNResultVo.Builder().status(FTNNResultVo.Status.EXCEPTION).code("other_error")
                    .msg(cause).build();
            sender.sendAndDisconnect(resultVo);
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            FTNNResultVo resultVo = new FTNNResultVo.Builder().status(FTNNResultVo.Status.SUCCESS)
                    .money(notifyData.money).gamemoney(notifyData.gamemoney)
                    .msg(cause).build();
            sender.sendAndDisconnect(resultVo);
            return;
        }
        UserPo userPo = UserPo.findByPfUser(notifyData.uid,"FTNNSDK");
        if (null == userPo) {
            cause = "用户账号不存在";
            logger.info(cause);
            FTNNResultVo resultVo = new FTNNResultVo.Builder().status(FTNNResultVo.Status.EXCEPTION).code("user_not_sxist")
                    .money(notifyData.money).gamemoney(notifyData.gamemoney)
                    .msg(cause).build();
            sender.sendAndDisconnect(resultVo);
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals((float) notifyData.money)) {
            cause = "订单金额与支付金额不一致";
            logger.info(cause);
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配支付金额:" + (float) notifyData.money + "(元)");
            FTNNResultVo resultVo = new FTNNResultVo.Builder().status(FTNNResultVo.Status.EXCEPTION).code("money_error")
                    .money(notifyData.money).gamemoney(notifyData.gamemoney)
                    .msg(cause).build();
            sender.sendAndDisconnect(resultVo);
            return;
        }
        //验签
        StringBuilder sb = new StringBuilder();
        sb.append(notifyData.orderid)
                .append(notifyData.uid)
                .append(notifyData.money)
                .append(notifyData.gamemoney);
        if (notifyData.serverid != null) {
            sb.append(notifyData.serverid);
        }
        sb.append(ftnnsdk.secretKey)
                .append(notifyData.mark)
                .append(notifyData.time);
        if (notifyData.coupon_mark != null) {
            sb.append(notifyData.coupon_mark);
        }
        if (notifyData.coupon_money != null) {
            sb.append(notifyData.money);
        }
        String signStr = HelperEncrypt.encryptionMD5(sb.toString()).toLowerCase();
        if (!notifyData.sign.equals(signStr)) {
            cause = "验签失败!4399SDK签名=" + notifyData.sign + "===============" + "服务器签名:" + signStr;
            logger.info(cause);
            FTNNResultVo resultVo = new FTNNResultVo.Builder().status(FTNNResultVo.Status.EXCEPTION).code("sign_error")
                    .money(notifyData.money).gamemoney(notifyData.gamemoney)
                    .msg(cause).build();
            sender.sendAndDisconnect(resultVo);
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //成功
        FTNNResultVo resultVo = new FTNNResultVo.Builder().status(FTNNResultVo.Status.SUCCESS)
                .money(notifyData.money).gamemoney(notifyData.gamemoney)
                .msg("充值成功").build();
        sender.sendAndDisconnect(resultVo);
        payTrade.setThird_trade_no(notifyData.mark);
        payTrade.setPlatform("FTNNSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "FTNNSDK");
        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
    }
}

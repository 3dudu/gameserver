package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.po.UserPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.vo.MRSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.StringUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

public class MRSDKServiceHandler {
    static Logger logger = Logger.getLogger(MRSDKServiceHandler.class);


    @Cmd("/p/MRSDKPayNotify/cb")
    public void MRSDKPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        MRSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), MRSDKNotifyData.class);
        logger.info("MRSDKNotifyData:" + notifyData.toString());
        String  ownOrderId = notifyData.callback_info;

        String cause="";
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(ownOrderId, ownOrderId);
        if (isSaveSuc == 0L) {
            cause = "同一订单多次请求,无须重复处理:order_id:"+ownOrderId;
            logger.info(cause);
            sender.sendAndDisconnect("ErrorRepeat");
            return;
        } else {
            redis.expire(ownOrderId, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(ownOrderId);
        if (payTrade == null) {
            cause = "订单号:"+ownOrderId+",不存在";
            logger.info(cause);
            sender.sendAndDisconnect("FAILURE");
            return;
        }
        if (payTrade.getStatus() >= 2) {
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            sender.sendAndDisconnect("ErrorRepeat");
            return;
        }
        Constants.PlatformOption.MRSDK mrsdk = GlobalCache.getPfConfig("MRSDK");
        String mr_game_secret=mrsdk.apps.get(payTrade.getChannelCode()).mr_game_secret;
        //检验mr_game_secret是否配置
        if (mr_game_secret==null) {
            cause="mr_game_secret未配置";
            logger.info(cause);
            sender.sendAndDisconnect("FAILURE");
            return;
        }
        //订单状态 1—支付成功
        if (notifyData.status!=1) {
            cause = "订单状态:支付失败";
            logger.info(cause);
            sender.sendAndDisconnect("FAILURE");
            return;
        }
        //验签
        String baseStr = notifyData.amount+"#"+notifyData.callback_info+"#"+notifyData.order_id+"#"+notifyData.role_id+"#"
                +notifyData.server_id+"#"+notifyData.status+"#"+notifyData.timestamp+"#"+notifyData.type
                +"#"+notifyData.user_id+"#"+mr_game_secret;
        logger.info("baseStr:"+baseStr);
        baseStr = HelperEncrypt.encryptionMD5(baseStr).toLowerCase();
        if (!notifyData.sign.equals(baseStr)) {
            logger.info("验签失败!猫耳SDK sign="+notifyData.sign+"==============="+"服务端生成签名:"+baseStr);
            sender.sendAndDisconnect("ErrorSign");
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(notifyData.amount))) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配"+"amount:"+Float.valueOf(notifyData.amount));
            sender.sendAndDisconnect("FAILURE");
            return;
        }
        //根据订单信息的用户id、角色id、服id、金额分别跟通知的用户id、角色id、服id、金额要一致
        UserPo userPo = UserPo.findByUserId(payTrade.getUser_id());
        if (null == userPo) {
            logger.info("该用户不存在:" + payTrade.getUser_id());
            sender.sendAndDisconnect("ErrorUser");
            return;
        }
        if (!(userPo.getPf_user().equals(notifyData.user_id)&&payTrade.getSid().toString().equals(notifyData.server_id)&&payTrade.getPid().toString().equals(notifyData.role_id))) {
            cause="订单号+"+payTrade.getOrder_id()+",third_user_id="+userPo.getPf_user()+",pid="+payTrade.getPid()+",sid="+payTrade.getSid()+"========"+"回调用户id="+notifyData.user_id+",角色id="+notifyData.role_id+",服id="+notifyData.server_id;
            logger.info(cause);
            sender.sendAndDisconnect("ErrorUserVerify");
            return;
        }
        //成功
        sender.sendAndDisconnect("SUCCESS");
        payTrade.setThird_trade_no(notifyData.order_id);
        payTrade.setPlatform("MRSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "MRSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }
}

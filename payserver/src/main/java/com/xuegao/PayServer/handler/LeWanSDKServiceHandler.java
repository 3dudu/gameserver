package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.vo.Game6kwSDKNotifyData;
import com.xuegao.PayServer.vo.LeWanSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class LeWanSDKServiceHandler {
    static Logger logger = Logger.getLogger(LeWanSDKServiceHandler.class);
    /**
     * 支付回调
     * @param sender
     * @param params
     */
    @Cmd("/p/leWanNotify/cb")
    public void leWanNotify(User sender, HttpRequestJSONObject params)  {

        String json = null;
        Map<String ,String > head = new HashMap<>();
        if (!params.isEmpty()) {
            json = params.toJSONString();
            head = params.getHttpHeaders();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
            head = params.getHttpHeaders();
        }
        //第三方传入的签名 需要进行验证
        String leWanSign = head.get("Lewan-Authorization");
        String nonce = head.get("Lewan-Nonce");
        String notifyId = head.get("Lewan-Id");

        logger.info("乐玩请求头内容为：head = " + head  +" leWanSign = " + leWanSign + ",nonce = " + nonce + ",notifyId=" +notifyId );
        LeWanSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), LeWanSDKNotifyData.class);
        logger.info("LeWanSDKNotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.LeWanSDK leWanSDK = GlobalCache.getPfConfig("LeWanSDK");
        String cause = "";
        if (null == leWanSDK) {
            cause = "leWanSDK:参数未配置";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getMsg(0,"FAIL"));
            return;
        }
        //检查订单状态
        if (!"SUCCESS".equals(notifyData.trade_state)) {
            cause = "乐玩订单未完成支付，请进行支付!";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getMsg(0,"FAIL"));
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.cpOrderSn, notifyData.cpOrderSn);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:cpOrderSn:" + notifyData.cpOrderSn);
            sender.sendAndDisconnect(MsgFactory.getMsg(1,"SUCCESS"));
            return;
        } else {
            redis.expire(notifyData.cpOrderSn, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.cpOrderSn);

        if (payTrade == null) {
            cause = "订单号：" + notifyData.cpOrderSn + ",不存在";
            sender.sendAndDisconnect(MsgFactory.getMsg(0,"FAIL"));
            logger.error(cause);
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect(MsgFactory.getMsg(1,"SUCCESS"));
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals (Float.parseFloat(notifyData.amount)/100)){
            cause = "订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + Float.parseFloat(notifyData.amount)/100;
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getMsg(0,"FAIL"));
            return;
        }

        String paySecret  = leWanSDK.apps.get(notifyData.attach).PaySecret;

        String signStr = json  + nonce  +  paySecret;
        logger.info("生成签名前的字符串："+signStr);
        String sign = MD5Util.md5(signStr).toLowerCase();
        logger.info("服务器签名："+sign+",传入签名："+leWanSign);
        //校验签名
        if (sign.equals(leWanSign)) {
            //验签通过
            sender.sendAndDisconnect( MsgFactory.getMsg(1,"SUCCESS"));
            payTrade.setThird_trade_no(notifyId);
            payTrade.setPlatform("LeWanSDK");
            payTrade.setStatus(2);
            payTrade.updateToDB();

            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "LeWanSDK");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {
            cause = "验签失败";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getMsg(0,"FAIL"));
        }

    }
}

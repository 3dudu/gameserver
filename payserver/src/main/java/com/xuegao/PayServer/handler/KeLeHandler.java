package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.util.YueWanUtils;
import com.xuegao.PayServer.vo.KeLeSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class KeLeHandler {
    static Logger logger = Logger.getLogger(KeLeHandler.class);

    @Cmd("/p/KeLeSDKPayNotify/cb")
    public void KeLeSDKPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        KeLeSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), KeLeSDKNotifyData.class);
        logger.info("KeLeSDKNotifyData:" + notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.KeLeSDK config = GlobalCache.getPfConfig("KeLeSDK");
        //检验gameId是否配置
        if (null == config.apps.get(notifyData.gid)) {
            logger.info("可乐sdk 参数未配置,gameId=" + notifyData.gid);
            sender.sendAndDisconnect(MsgFactory.getMsg(0,"gameId未配置"));
            return;
        }
        //检验app_key是否配置
        String app_key = config.apps.get(notifyData.gid).appkey;
        if (null == app_key) {
            logger.info("app_key未配置,app_key=" + app_key);
            sender.sendAndDisconnect(MsgFactory.getMsg(2,"app_key未配置"));
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");

        long isSaveSuc = redis.STRINGS.setnx(notifyData.cpNo, notifyData.cpNo);
        if (isSaveSuc == 0L) {
            logger.info("同一订单多次请求,无须重复处理:cp_order_id:" + notifyData.cpNo);
            sender.sendAndDisconnect(MsgFactory.getMsg(1,"同一订单无需重复处理"));
            return;
        } else {
            redis.expire(notifyData.cpNo, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.cpNo);
        if (payTrade == null) {
            logger.info("订单号:" + notifyData.cpNo + ",不存在");
            sender.sendAndDisconnect(MsgFactory.getMsg(3,"订单不存在，订单号：" + notifyData.cpNo));
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info( "订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect(MsgFactory.getMsg(1,"同一订单无需重复处理"));
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.parseFloat( notifyData.money ))) {
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "money:" +  notifyData.money);
            sender.sendAndDisconnect(MsgFactory.getMsg(4,"订单金额不匹配"));
            return;
        }
        //验签
        Map<String ,String > data = new HashMap<>();
        data.put("gid",notifyData.gid);
        data.put("uid",notifyData.uid);
        data.put("amount",notifyData.amount);
        data.put("cid",notifyData.cid);
        data.put("platform",notifyData.platform);
        data.put("time",notifyData.time);
        data.put("pay_time", notifyData.pay_time);
        data.put("cpNo", notifyData.cpNo);
        data.put("order_no",notifyData.order_no);
        data.put("money",notifyData.money);
        data.put("serverid",notifyData.serverid);
        data.put("roleid",notifyData.roleid);
        data.put("goodsId",notifyData.goodsId);
        data.put("ext",notifyData.ext);
        data.put("is_test", notifyData.is_test);

        logger.info("可乐 sdk签名字符集为："+ data);
        String signStr = HelperEncrypt.encryptionMD5(YueWanUtils.getSortQueryString (data)+app_key).toLowerCase();
        if (!notifyData.sign.equals(signStr)) {
            logger.info("验签失败!可乐 sign=" + notifyData.sign + "============" + "服务器计算签名 signStr:" + signStr);
            sender.sendAndDisconnect(MsgFactory.getMsg(5,"验签失败"));
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //成功
        sender.sendAndDisconnect(MsgFactory.getMsg(1,"成功"));
        payTrade.setThird_trade_no(notifyData.order_no);
        payTrade.setPlatform("KeLeSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "KeLeSDK");
        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
    }
}

package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.VivoSignUtils;
import com.xuegao.PayServer.util.YueWanUtils;
import com.xuegao.PayServer.vo.AiWanSDKNotifyData;
import com.xuegao.PayServer.vo.SimoSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.xuegao.core.util.Misc.md5;

public class AiWanPayHandler {
    static Logger logger = Logger.getLogger(AiWanPayHandler.class);

    /**
     * 支付回调
     *
     * @param sender
     * @param params
     */
    @Cmd("/p/AiWanPayNotify/cb")
    public void AiWanPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        AiWanSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), AiWanSDKNotifyData.class);
        logger.info("AiWanSDKNotifyData:"+notifyData.toString());

        //检验参数是否匹配
        Constants.PlatformOption.AiWanSDK config = GlobalCache.getPfConfig("AiWanSDK");
        if(null==config){
            logger.info("AiWan 参数未配置");
            sender.sendAndDisconnect(getErrorMsg(-1));
            return;
        }
        //获取gameId的参数SIGN_KEY
        Constants.PlatformOption.AiWanSDK.Param app = config.apps.get(notifyData.app_id);
        if (app==null) {
            logger.info("AiWan appid 未配置 ,appid="+notifyData.app_id);
            sender.sendAndDisconnect(getErrorMsg(0));
            return;
        }
        //检验支付参数paykey是否配置
        String paykey  = config.apps.get(notifyData.app_id).pay_key;
        if(null==paykey){
            logger.info("AiWan 参数未配置,paykey="+ paykey);
            sender.sendAndDisconnect(getErrorMsg(2));
            return;
        }

        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.out_trade_no, notifyData.out_trade_no);
        if (isSaveSuc == 0L) {
            logger.info( "同一订单多次请求,无须重复处理:cpOrderId:" + notifyData.out_trade_no);
            sender.sendAndDisconnect(getErrorMsg(1));
            return;
        } else {
            redis.expire(notifyData.out_trade_no, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.out_trade_no);
        if (payTrade == null) {
            logger.info("订单号:" + notifyData.out_trade_no + ",不存在");
            sender.sendAndDisconnect(getErrorMsg(-2));
            return;
        }
        if (payTrade.getStatus() >= 2 ) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect(getErrorMsg(1));
            return;
        }
        //生成签名并校验
        Map<String ,String > signMap = new HashMap<>();
        signMap.put("site_uid",notifyData.site_uid);
        signMap.put("order_no",notifyData.order_no);
        signMap.put("app_id",notifyData.app_id);
        signMap.put("channel",notifyData.channel);
        signMap.put("amount",notifyData.amount);
        signMap.put("amount_unit",notifyData.amount_unit);
        signMap.put("amount_rate",notifyData.amount_rate);
        signMap.put("amount_usd",notifyData.amount_usd);
        signMap.put("amount_change",notifyData.amount_change);
        signMap.put("ext",notifyData.ext);
        signMap.put("out_trade_no",notifyData.out_trade_no);
        signMap.put("create_time",notifyData.create_time);
        signMap.put("item_id",notifyData.item_id);
        signMap.put("pay_time",notifyData.pay_time);
        signMap.put("time",notifyData.time);
        signMap.put("do","suc");

        String generateSign = md5(  VivoSignUtils.createLinkString( signMap , true,false) + paykey);
        if (!notifyData.sign.equals(generateSign)) {
            logger.info("验签失败，aiWan sign="+notifyData.sign+"========="+"generateSign:"+generateSign);
            sender.sendAndDisconnect(3);
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(notifyData.amount))) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配"+"amount:"+Float.valueOf(notifyData.amount));
            sender.sendAndDisconnect(4);
            return;
        }
        //成功
        sender.sendAndDisconnect(1);
        payTrade.setThird_trade_no(notifyData.order_no);
        payTrade.setPlatform("AiWanSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "AiWanSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }

    public static JSONObject getErrorMsg(int ret) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("RET", ret);
        return jsonObject;
    }
}

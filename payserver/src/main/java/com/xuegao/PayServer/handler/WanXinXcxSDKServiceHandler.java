package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.util.YueWanUtils;
import com.xuegao.PayServer.vo.WanXinH5SDKNotifyData;
import com.xuegao.PayServer.vo.WanXinXcxSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WanXinXcxSDKServiceHandler {
    static Logger logger = Logger.getLogger(WanXinRongYaoSDKServiceHandler.class);

    @Cmd("/p/WanXinXcxPayNotify/cb")
    public void WanXinXcxPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        WanXinXcxSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), WanXinXcxSDKNotifyData.class);
        logger.info("WanXinXcxSDKNotifyData:" + notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.WanXinXcxSDK config = GlobalCache.getPfConfig("WanXinXcxSDK");
        String cause = "";
        //检验gameId是否配置
        if (null == config.apps.get(notifyData.tp)) {
            logger.info("玩心-荣耀 参数未配置,channel_code=" + notifyData.tp);
            sender.sendAndDisconnect(YueWanUtils.getErrorMsg("玩心-小程序 参数未配置,channel_code=" + notifyData.tp, 0, ""));
            return;
        }
        //检验j_pay_sign是否配置
        String j_pay_sign = config.apps.get(notifyData.tp).xx_pay_sign;
        if (null == j_pay_sign) {
            logger.info("j_pay_sign未配置,j_pay_sign=" + j_pay_sign);
            sender.sendAndDisconnect(YueWanUtils.getErrorMsg("j_pay_sign未配置", 0, ""));
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");

        long isSaveSuc = redis.STRINGS.setnx(notifyData.app_order_id, notifyData.app_order_id);
        if (isSaveSuc == 0L) {
            cause = "同一订单多次请求,无须重复处理:app_order_id:" + notifyData.app_order_id;
            logger.info(cause);
            sender.sendAndDisconnect(YueWanUtils.getErrorMsg(cause, 1, ""));
            return;
        } else {
            redis.expire(notifyData.app_order_id, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.app_order_id);
        if (payTrade == null) {
            cause = "订单号:" + notifyData.app_role_id + ",不存在";
            logger.info(cause);
            sender.sendAndDisconnect(YueWanUtils.getErrorMsg(cause, 0, ""));
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            sender.sendAndDisconnect(YueWanUtils.getErrorMsg(cause, 1, ""));
            return;
        }

        //验签
        Map<String ,String > wanXinData = new HashMap<>();
        wanXinData.put("app_order_id",notifyData.app_order_id);
        wanXinData.put("app_role_id",notifyData.app_role_id);
        wanXinData.put("product_id",notifyData.product_id);
        wanXinData.put("server_id",notifyData.server_id);
        wanXinData.put("time",notifyData.time);
        wanXinData.put("total_fee",String.valueOf(notifyData.total_fee));
        wanXinData.put("user_id",notifyData.user_id);
        wanXinData.put("j_game_id",notifyData.j_game_id);
        wanXinData.put("j_pay_sign",j_pay_sign);
        wanXinData.put("tp",notifyData.tp);
        wanXinData.put("is_sandbox",notifyData.is_sandbox);
        logger.info("玩心-小程序 sdk签名字符集为："+ wanXinData);
        String signStr = HelperEncrypt.encryptionMD5(getSortQueryString(wanXinData)).toLowerCase();
        if (!notifyData.sign.equals(signStr)) {
            logger.info("验签失败!玩心-小程序 sign=" + notifyData.sign + "============" + "服务器计算签名 signStr:" + signStr);
            sender.sendAndDisconnect(YueWanUtils.getErrorMsg("验签失败", 0, ""));
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals((float) notifyData.total_fee / 100F)) {
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "money:" + (float) notifyData.total_fee / 100F);
            sender.sendAndDisconnect(YueWanUtils.getErrorMsg("订单金额与支付金额不一致", 0, ""));
            return;
        }
        //成功
        sender.sendAndDisconnect(YueWanUtils.getErrorMsg("success", 1, ""));
        payTrade.setThird_trade_no(notifyData.order_id);
        payTrade.setPlatform("WanXinXcxSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "WanXinXcxSDK");
        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
    }

    public static String getSortQueryString(Map<String, String> params){
        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);
        StringBuffer sb = new StringBuffer();
        for (Object key : keys) {
            if (isEmpty(params.get(String.valueOf(key)))) {
                sb.append(key).append("=").append(params.get(String.valueOf(key))).append("&");
            }
        }
        String text = sb.toString();
        if (text.endsWith("&")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    public static boolean  isEmpty(Object object) {
        if (object == null) {
            return false;
        }
        String str =  object.toString();
        if ("".equals(str)) {
            return false;
        }
        return true;
    }
}

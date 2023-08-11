package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.util.VivoSignUtils;
import com.xuegao.PayServer.vo.Q1SDKNotifyData;
import com.xuegao.PayServer.vo.YOUYISDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.Map;

import static com.xuegao.PayServer.util.VivoSignUtils.LinkString;

public class YOUYISDKHandler {
    static Logger logger = Logger.getLogger(YOUYISDKHandler.class);
    /**
     * YOUYISDK支付回调
     * @param sender
     * @param params
     */
    @Cmd("/p/YOUYISDKPayNotify/cb")
    public void YOUYISDKPayNotify(User sender, HttpRequestJSONObject params){
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject  youYiData = JSONObject.parseObject(json);
        YOUYISDKNotifyData notifyData = JSONObject.toJavaObject(youYiData, YOUYISDKNotifyData.class);
        logger.info("YOUYISDK支付回调返回的参数为："+notifyData.toString());
        //检查配置参数
        Constants.PlatformOption.YOUYISDK youyisdk = GlobalCache.getPfConfig("YOUYISDK");
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.cp_order_id, notifyData.cp_order_id);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:cp_order_id:" + notifyData.cp_order_id);
            sender.sendAndDisconnect(getMsg(4,"发货失败"));
            return;
        } else {
            redis.expire(notifyData.cp_order_id, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.cp_order_id);
        if (payTrade == null) {
            sender.sendAndDisconnect(getMsg(4,"发货失败"));
            logger.error("订单号:" + notifyData.cp_order_id + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect(getMsg(2,"订单已发货"));
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals (Float.parseFloat(notifyData.amount))){
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + notifyData.amount);
            sender.sendAndDisconnect(getMsg(3,"金额不匹配"));
            return;
        }
        if (youyisdk.apps.get(notifyData.extend_params) == null) {
            logger.info("参数未配置 channel:"+ youyisdk.apps.get(notifyData.extend_params));
            sender.sendAndDisconnect(getMsg(4,"发货失败"));
            return;
        }
        String payKey =youyisdk.apps.get(notifyData.extend_params).pay_key;
        //校验签名
        Map map = JSON.parseObject(JSON.toJSONString(notifyData));
        map.remove("sign");
        String strA = VivoSignUtils.createLinkString(map,true,true);
        String sign_data = strA +"&"+payKey;
        String sign = MD5Util.md5(sign_data).toLowerCase();
        logger.info("客户端传的sign:"+notifyData.sign+"服务端生成的sign:"+sign);
        if (sign.equals(notifyData.sign)) {
            //验签通过
            sender.sendAndDisconnect(getMsg(0,"成功"));
            payTrade.setThird_trade_no(notifyData.order_id);
            payTrade.setPlatform("YOUYISDK");
            payTrade.setStatus(2);
            payTrade.updateToDB();
            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "YOUYISDK");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {
            logger.info("验签失败");
            sender.sendAndDisconnect(getMsg(2,"验签错误"));
        }

    }

    public static JSONObject getMsg(int code,String msg){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", msg);
        return jsonObject;
    }
}

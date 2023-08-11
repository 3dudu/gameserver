package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.vo.BTSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BTPayHandler {
    static Logger logger = Logger.getLogger(BTPayHandler.class);

    /**
     * 支付回调
     * @param sender
     * @param params
     */
    @Cmd("/p/btNotify/cb")
    public void btNotify(User sender, HttpRequestJSONObject params) throws UnsupportedEncodingException {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        BTSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), BTSDKNotifyData.class);
        logger.info("BTSDKNotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.BTSDK btsdk = GlobalCache.getPfConfig("BTSDK");
        if (null == btsdk) {
            logger.info("btsdk:参数未配置");
            sender.sendAndDisconnect(getErrorMsg("btsdk:参数未配置",201,""));
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.game_order_sn, notifyData.game_order_sn);
        if (isSaveSuc == 0L) {
            logger.info("同一订单多次请求,无须重复处理:gameOrder:" + notifyData.game_order_sn);
            sender.sendAndDisconnect(getErrorMsg("同一订单多次请求,无须重复处理:gameOrder:" + notifyData.game_order_sn,200,"gameOrder:"+notifyData.game_order_sn));
            return;
        } else {
            redis.expire(notifyData.game_order_sn, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.game_order_sn);

        if (payTrade == null) {
            logger.info("订单号:" + notifyData.game_order_sn + ",不存在");
            sender.sendAndDisconnect(getErrorMsg("订单号:" + notifyData.game_order_sn + ",不存在",202,"gameOrder:"+notifyData.game_order_sn));
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect(getErrorMsg("订单已经处理完毕，无需重复处理",200,"gameOrder:"+notifyData.game_order_sn));
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals (Float.valueOf(notifyData.pay_money))){

            logger.info( "订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + Float.valueOf(notifyData.pay_money));
            sender.sendAndDisconnect(getErrorMsg( "订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + Float.valueOf(notifyData.pay_money),203,"gameOrder:"+notifyData.game_order_sn));
            return;
        }

        String paykey  = btsdk.apps.get(notifyData.game_id).bt_pay_key;
        //sign =md5(good_name=%E5%BE%AE%E4%BF%A1%E6%94%AF%E4%BB%98%E6%B5%8B%E8%AF%95
        // &uuid=2&order_sn=80000242&game_order_sn=15548&game_id=1017&service_id=1313&service_name
        // =%E6%9C%8D%E5%8A%A1%E5%90%8D&role_id=213&role_name=%E8%A7%92%E8%89%B2%E5%90%8D%E7%A7%B0
        // &pay_money=1&pay_status=1&remark=%E8%A7%92%E8%89%B2%E5%90%8D%E7%A7%B0&time
        // =123213&key=123213)
        String signStr ="good_name="+ URLEncoder.encode(notifyData.good_name,"utf-8")+
         "&uuid="+ notifyData.uuid + "&order_sn=" + notifyData.order_sn + "&game_order_sn="+notifyData.game_order_sn +"&game_id="+notifyData.game_id+ "&service_id="
         + notifyData.service_id+ "&service_name="+ URLEncoder.encode(notifyData.service_name,"utf-8")+ "&role_id="+ notifyData.role_id +"&role_name="
         +URLEncoder.encode(notifyData.role_name,"utf-8")+ "&pay_money=" + notifyData.pay_money +"&pay_status="+notifyData.pay_status +"&remark="
         +URLEncoder.encode(notifyData.remark,"utf-8") + "&time="+notifyData.time + "&key="+paykey;

        logger.info("生成签名前的字符串："+signStr);
        String sign = MD5Util.md5(signStr).toLowerCase();

        logger.info("服务器签名："+sign+",传入签名："+notifyData.sign);
        //校验签名
        if (sign.equals(notifyData.sign)) {
            //验签通过
            sender.sendAndDisconnect(getErrorMsg("支付成功",200,""));
            payTrade.setThird_trade_no(notifyData.order_sn);
            payTrade.setPlatform("BTSDK");
            payTrade.setStatus(2);

            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "BTSDK");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {

            logger.info("验签失败");
            sender.sendAndDisconnect(getErrorMsg("验签失败",204,""));
        }

    }
    @Cmd("/p/btRebate/cb")
    public void btRebate(User sender, HttpRequestJSONObject params) throws UnsupportedEncodingException {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject rebateData = JSON.parseObject(json);
        Integer rebate_type = rebateData.getInteger("rebate_type");
        Integer rebate_mode = rebateData.containsKey("rebate_mode") ?  rebateData.getInteger("rebate_mode") : 0;
        String rebate_no = rebateData.getString("rebate_no");
        String game_order_no = rebateData.getString("game_order_no");
        String user_id = rebateData.getString("user_id");
        String pay_money = rebateData.getString("pay_money");
        Integer gold = rebateData.getInteger("gold");
        Integer game_id = rebateData.getInteger("game_id");
        String service_id = rebateData.getString("service_id");
        String role_id = rebateData.getString("role_id");
        String role_name = rebateData.getString("role_name");
        String time = rebateData.getString("time");
        String sign = rebateData.getString("sign");

        //检验参数是否匹配
        Constants.PlatformOption.BTSDK btsdk = GlobalCache.getPfConfig("BTSDK");
        if (null == btsdk) {
            logger.info("btsdk:参数未配置");
            sender.sendAndDisconnect(getErrorMsg("btsdk:参数未配置",201,""));
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(rebate_no, rebate_no);
        if (isSaveSuc == 0L) {
            logger.info("同一返利订单多次请求,无须重复处理:rebate_no:" + rebate_no);
            sender.sendAndDisconnect(getErrorMsg("同一订单返利多次请求,无须重复处理:rebate_no:" + rebate_no,200,"rebate_no:"+rebate_no));
            return;
        } else {
            redis.expire(rebate_no, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(game_order_no);

        if (payTrade == null) {
            logger.info("游戏订单号:" + game_order_no + ",不存在");
            sender.sendAndDisconnect(getErrorMsg("游戏订单号:" + game_order_no+ ",不存在",202,"rebate_no:"+rebate_no));
            return;
        }
        if (payTrade.getBonus() != 0 ) {
            logger.info("订单号:" + rebate_no + "已经返利！");
            sender.sendAndDisconnect(getErrorMsg("订单号:" + rebate_no + "已经返利！",205,"rebate_no"+rebate_no));
            return;
        }
        //生成签名
        /*sign=md5(rebate_type+rebate_no+game_order_no（URL编码）+user_id（URL编码）
        +pay_money+gold+game_id+service_id（URL编码）+role_id（URL编码）+time+key)*/
        String key = btsdk.apps.get(String.valueOf(game_id)).bt_rebate_key;
        String signStr = rebate_type+Integer.getInteger(rebate_no) +URLEncoder.encode(game_order_no , "utf-8")
                +URLEncoder.encode(user_id,"utf-8") +pay_money+gold+game_id+ URLEncoder.encode(service_id,"utf-8")
                + URLEncoder.encode(role_id,"utf-8") +time;
        String signRebate  = MD5Util.md5(signStr+key).toLowerCase();
        logger.info("返利签名前字符为："+signStr + ",签名key为："+key +"md5签名结果为："+ signRebate+ ",传入签名为："+ sign);

        //校验签名
        if (signRebate.equals(sign)) {
            //验签通过
            sender.sendAndDisconnect(getErrorMsg("返利成功",200,""));
            payTrade.setThird_trade_no(rebate_no);
            payTrade.setBonus(gold);
            payTrade.setPlatform("BTSDK");
            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.rebate(payTrade, "BTSDK");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {
            logger.info("验签失败");
            sender.sendAndDisconnect(getErrorMsg("验签失败",204,""));
        }

    }
    private static JSONObject getErrorMsg(String msg, int code, String data) {
        JSONObject Obj = new JSONObject();
        Obj.put("code", code);
        Obj.put("msg", msg);
        Obj.put("data", data);
        return Obj;
    }
}

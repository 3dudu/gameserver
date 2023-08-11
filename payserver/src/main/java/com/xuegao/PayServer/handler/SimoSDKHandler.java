package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.vo.BTSDKNotifyData;
import com.xuegao.PayServer.vo.SamsungNotifyData;
import com.xuegao.PayServer.vo.SimoSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.RequestUtil;
import com.xuegao.core.util.StringUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.xuegao.core.util.Misc.md5;

public class SimoSDKHandler {
    static Logger logger = Logger.getLogger(SimoSDKHandler.class);

    /**
     * 支付回调
     *
     * @param sender
     * @param params
     */
    @Cmd("/p/SimoPayNotify/cb")
    public void SimoPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        SimoSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), SimoSDKNotifyData.class);
        logger.info("SimoSDKNotifyData:"+notifyData.toString());
        //1-成功支付 其他 -支付失败，游戏需判断1的订单再下发道具
        if (!notifyData.paid_state.equals("1")) {
            logger.info("支付失败");
            sender.sendAndDisconnect("FAIL");
            return;
        }
        //检验参数是否匹配
        Constants.PlatformOption.SimoSDK simoSDK= GlobalCache.getPfConfig("SimoSDK");
        if(null==simoSDK){
            String cause ="simoSDK:未配置" ;
            logger.info(cause);
            sender.sendAndDisconnect("FAIL");
            return;
        }
        //获取gameId的参数SIGN_KEY
        Constants.PlatformOption.SimoSDK.Param app = simoSDK.apps.get(notifyData.appid);
        if (app==null) {
            logger.info("simo  appid 未配置 ,appid="+notifyData.appid);
            sender.sendAndDisconnect("FAIL");
            return;
        }
        //检验支付参数paykey是否配置
        String paykey  = simoSDK.apps.get(notifyData.appid).paykey;
        if(null==paykey){
            logger.info("simo 参数未配置,paykey="+ paykey);
            sender.sendAndDisconnect("FAIL");
            return;
        }

        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.cp_order_no, notifyData.cp_order_no);
        if (isSaveSuc == 0L) {
            logger.info( "同一订单多次请求,无须重复处理:cpOrderId:" + notifyData.cp_order_no);
            sender.sendAndDisconnect("SUCCESS");
            return;
        } else {
            redis.expire(notifyData.cp_order_no, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.cp_order_no);
        if (payTrade == null) {
            logger.info("订单号:" + notifyData.cp_order_no + ",不存在");
            sender.sendAndDisconnect("FAIL");
            return;
        }
        if (payTrade.getStatus() >= 2 ) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("SUCCESS");
            return;
        }
        //生成签名并校验
        Map<String ,String > signMap = new HashMap<>();
        signMap.put("tm",notifyData.tm);
        signMap.put("appid",notifyData.appid);
        signMap.put("cchid",notifyData.cchid);
        signMap.put("m_order_no",notifyData.m_order_no);
        signMap.put("cp_order_no",notifyData.cp_order_no);
        signMap.put("paid_amt",notifyData.paid_amt);
        signMap.put("is_test",notifyData.is_test);
        signMap.put("cp_ext",notifyData.cp_ext);
        signMap.put("product_name",notifyData.product_name);
        signMap.put("product_id",notifyData.product_id);
        signMap.put("role_name",notifyData.role_name);
        signMap.put("role_id",notifyData.role_id);
        signMap.put("server_name",notifyData.server_name);
        signMap.put("server_id",notifyData.server_id);
        signMap.put("openid",notifyData.openid);
        signMap.put("paid_time",notifyData.paid_time);
        signMap.put("paid_state",notifyData.paid_state);
        String generateSign = createSign(signMap, paykey);
        if (!notifyData.sign.equals(generateSign)) {
            logger.info("验签失败，simo sign="+notifyData.sign+"========="+"generateSign:"+generateSign);
            sender.sendAndDisconnect("FAIL");
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(notifyData.paid_amt))) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配"+"amount:"+Float.valueOf(notifyData.paid_amt));
            sender.sendAndDisconnect("FAIL");
            return;
        }
        //成功
        sender.sendAndDisconnect("SUCCESS");
        payTrade.setThird_trade_no(notifyData.m_order_no);
        payTrade.setPlatform("SimoSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "SimoSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }

    /**
     * 签名工具方法
     * @param reqMap
     * @param signKey
     * @return
     */
    public static String createSign(Map<String, String> reqMap, String signKey) {
        //将所有key按照字典顺序排序
        TreeMap<String, String> signMap = new TreeMap<>(reqMap);
        StringBuilder stringBuilder = new StringBuilder(1024);
        for (Map.Entry<String, String> entry : signMap.entrySet()) {
            // sign和signType和ver不参与签名
            if ("sign".equals(entry.getKey()) || "sign_type".equals(entry.getKey())) {
                continue;
            }
            //值为null的参数不参与签名
            if (entry.getValue() != null && !entry.getValue().equals("")) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        //拼接签名秘钥
        stringBuilder.append(signKey);
        logger.info("Simo 签名原串为 ："+ stringBuilder);
        String signSrc = stringBuilder.toString();
        return md5(signSrc).toLowerCase();
    }
}

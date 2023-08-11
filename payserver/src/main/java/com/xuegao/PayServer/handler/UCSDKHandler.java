package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
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

public class UCSDKHandler {

    static Logger logger = Logger.getLogger(UCSDKHandler.class);

    /**
     * 充值加签处理
     * @param sender
     * @param params
     */
    @Cmd("/p/UCSDKAddSign/cb")
    public void UCSDKAddSign(User sender, JSONObject params) {
        logger.info("充值加签params:"+params.toJSONString());
        String callbackInfo = params.getString("callbackInfo");//和客户端定义 传gameId
        String amount = params.getString("amount");
        String cpOrderId = params.getString("cpOrderId");
        String accountId = params.getString("accountId");
        //signType和sign不参与签名
        String signType = params.getString("signType");
        String sign = params.getString("sign");

        Constants.PlatformOption.UCSDK ucsdk= GlobalCache.getPfConfig("UCSDK");
        Constants.PlatformOption.UCSDK.Param param = ucsdk.apps.get(callbackInfo);
        String apikey="";
        String notifyUrl="";
        if (param!=null) {
            apikey=param.apikey;
            notifyUrl=param.notifyUrl;
        }
        logger.info("ucsdk:"+ucsdk.toString());
        Map<String, String> map = new HashMap<>();
        map.put("callbackInfo",callbackInfo);
        map.put("amount",amount);
        map.put("cpOrderId",cpOrderId);
        map.put("accountId",accountId);
        map.put("signType",signType);
        map.put("sign",sign);
        map.put("notifyUrl",notifyUrl);
        String createSign = createSign(map, apikey);
        logger.info("createSign:"+createSign);
        //返回签名给客户端
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("ret", 0);
        jsonObject.put("sign",createSign);
        jsonObject.put("notifyUrl",notifyUrl);
        jsonObject.put("msg","success");
        sender.sendAndDisconnect(jsonObject);
    }

    /**
     * 支付回调接口
     * @param sender
     * @param param
     */
    @Cmd("/p/UCSDKPayNotify/cb")
    public void UCSDKPayNotify(User sender, HttpRequestJSONObject param) {
        String json = null;
        if (!param.isEmpty()) {
            json = param.toJSONString();
        } else {
            json = param.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        logger.info("json:"+json);
        JSONObject params = JSON.parseObject(json);
        JSONObject data = params.getJSONObject("data");
        String orderId = data.getString("orderId");
        String gameId = data.getString("gameId");
        String accountId = data.getString("accountId");
        String amount = data.getString("amount");
        String orderStatus = data.getString("orderStatus");
        String cpOrderId = data.getString("cpOrderId");
        String sign = params.getString("sign");

        Map<String, String> map = new HashMap<>();
        map.put("gameId",gameId);
        map.put("accountId",accountId);
        map.put("creator",data.getString("creator"));
        map.put("amount",amount);
        map.put("orderId",orderId);
        map.put("failedDesc",data.getString("failedDesc"));
        map.put("callbackInfo",data.getString("callbackInfo"));
        map.put("payWay",data.getString("payWay"));
        map.put("orderStatus",orderStatus);
        map.put("cpOrderId",cpOrderId);

        //检验参数是否匹配
        Constants.PlatformOption.UCSDK ucsdk= GlobalCache.getPfConfig("UCSDK");
        if(null==ucsdk){
            String cause ="ucsdk:未配置" ;
            logger.info(cause);
            sender.sendAndDisconnect("FAILURE");
            return;
        }
        //检验支付参数gameId是否配置
        Constants.PlatformOption.UCSDK.Param app = ucsdk.apps.get(gameId);
        if(null==app){
            String cause ="九游平台 参数未配置,gameId="+ app;
            logger.info(cause);
            sender.sendAndDisconnect("FAILURE");
            return;
        }
        //获取gameId的参数SIGN_KEY
        String apikey = app.apikey;
        if (apikey==null) {
            logger.info("apikey未配置,apikey="+apikey);
            sender.sendAndDisconnect("FAILURE");
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(cpOrderId, cpOrderId);
        if (isSaveSuc == 0L) {
            String cause ="同一订单多次请求,无须重复处理:cpOrderId:" + cpOrderId;
            logger.info(cause);
            sender.sendAndDisconnect("FAILURE");
            return;
        } else {
            redis.expire(cpOrderId, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(cpOrderId);
        String cause = "";
        if (payTrade == null) {
            cause="订单号:" + cpOrderId + ",不存在";
            logger.info(cause);
            sender.sendAndDisconnect("FAILURE");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            cause="订单已经处理完毕,无须重复处理";
            logger.info(cause);
            sender.sendAndDisconnect("FAILURE");
            return;
        }
        //S-成功支付 F-支付失败，游戏需判断S的订单再下发道具
        if (!orderStatus.equals("S")) {
            cause="支付失败";
            logger.info(cause);
            sender.sendAndDisconnect("FAILURE");
            return;
        }
        //cpOrderId仅当回调时具备该参数时，才需加入签名，如无，则不需要加入签名
        if (cpOrderId==null||cpOrderId.equals("")) {
            logger.info("cpOrderId不具备参数,不需要加入签名");
            map.remove("cpOrderId");
        }
        //游戏服务端必须根据九游回调的账号标识进行校验用户登录账号与充值账号一致后再下发相应虚拟货币
        logger.info("user_id:"+payTrade.getUser_id()+"====account:"+accountId);
        /*if (!payTrade.getUser_id().equals(accountId)) {
            cause="用户登录账号与充值账号不一致";
            logger.info("用户登录账号与充值账号不一致");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            return;
        }*/
        //生成签名并校验
        String generateSign = createSign(map, apikey);
        if (!sign.equals(generateSign)) {
            cause="验签失败:";
            logger.info(cause+"九游sign="+sign+"==============="+"generateSign:"+generateSign);
            sender.sendAndDisconnect("FAILURE");
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(amount))) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配"+"amount:"+Float.valueOf(amount));
            sender.sendAndDisconnect("FAILURE");
            return;
        }
        //成功
        sender.sendAndDisconnect("SUCCESS");
        payTrade.setThird_trade_no(orderId);
        payTrade.setPlatform("UCSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "UCSDK");
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
            if ("sign".equals(entry.getKey()) || "signType".equals(entry.getKey())|| "ver".equals(entry.getKey())) {
                continue;
            }
            //值为null的参数不参与签名
            if (entry.getValue() != null) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        //拼接签名秘钥
        stringBuilder.append(signKey);
        //剔除参数中含有的'&'符号
        String signSrc = stringBuilder.toString().replaceAll("&", "");
        return md5(signSrc).toLowerCase();
    }
}


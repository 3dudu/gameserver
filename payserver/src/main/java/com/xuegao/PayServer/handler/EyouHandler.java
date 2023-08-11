package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.po.UserPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperRandom;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.util.MagicCrypt;
import com.xuegao.PayServer.vo.EyouGift;
import com.xuegao.PayServer.vo.EyouNotifyData;
import com.xuegao.PayServer.vo.FFSDKGift;
import com.xuegao.PayServer.vo.FFSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.PropertiesUtil;
import com.xuegao.core.util.StringUtil;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class EyouHandler {
    public static final String EYOUPAY = PropertiesUtil.get("eyoupay");
    static Logger logger = Logger.getLogger(EyouHandler.class);

    /**
     * 内购充值（谷歌充值、苹果充值）
     * @param sender
     * @param params
     */
    @Cmd("/p/eyounotify/cb1")
    public void eyounotify(User sender, HttpRequestJSONObject params) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Success", 0);
        jsonObject.put("Reason","");
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        EyouNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), EyouNotifyData.class);
        logger.info("EyouNotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.EyouSDK eyouSDK = GlobalCache.getPfConfig("EyouSDK");
        String errorStr = "";
        if (null == eyouSDK) {
            logger.info( "EyouSDK:参数未配置");
            sender.sendAndDisconnect(jsonObject);
            return;
        }
        if (null == notifyData) {
            logger.info("EyouSDK:参数格式错误");
            sender.sendAndDisconnect(jsonObject);
            return;
        }
        String orderInfoStr ="";
        try {
            MagicCrypt mc = new MagicCrypt(eyouSDK.secretKey);
            orderInfoStr = mc.decrypt(notifyData.Orderinfo);
        }catch (Exception e){
            logger.error("解密失败");
            jsonObject.put("Reason","解密失败");
            sender.sendAndDisconnect(jsonObject);
        }
        Map <String,String> orderInfo = new HashMap<>();
        String[] orderinfos = orderInfoStr.split("&");
        for (String str:orderinfos) {
            String[] strs = str.split("=");
            if (strs.length>1){
                orderInfo.put(strs[0],strs[1]);
            }
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(orderInfo.get("coOrderId"), orderInfo.get("coOrderId"));
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:game_orderid:" + orderInfo.get("coOrderId"));
            jsonObject.put("Success", 1);
            jsonObject.put("Reason","");
            sender.sendAndDisconnect(jsonObject);
            return;
        } else {
            redis.expire(orderInfo.get("coOrderId"), 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(orderInfo.get("coOrderId"));

        if (payTrade == null) {
            logger.error("订单号:" + orderInfo.get("coOrderId") + ",不存在");
            jsonObject.put("Reason","订单号不存在");
            sender.sendAndDisconnect(jsonObject);
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            jsonObject.put("Reason","订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect(jsonObject);
            return;
        }
        if (!payTrade.getPay_price().equals (Float.parseFloat(orderInfo.get("amount")))){
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + orderInfo.get("amount"));
            jsonObject.put("Reason","订单金额不匹配");
            sender.sendAndDisconnect(jsonObject);
            return;
        }
        jsonObject.put("Success", 1);
        jsonObject.put("Reason","");
        sender.sendAndDisconnect(jsonObject);
        payTrade.setThird_trade_no(orderInfo.get("orderId"));
        payTrade.setPlatform("EyouSDK");
        payTrade.setStatus(2);

        //发钻石
        payTrade.setPay_price(Float.valueOf(orderInfo.get("amount")));
        payTrade.updateToDB();
        if (!StringUtil.isEmpty(orderInfo.get("tax"))){
            payTrade.setExt(orderInfo.get("tax"));
        }

        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "EyouSDK");
        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
    }


    /**
     * EYOU的第三方充值、线下充值、人工补单
     * @param sender
     * @param params
     */
    @Cmd("/p/eyounotify/cb2")
    public void eyounotify2(User sender, HttpRequestJSONObject params) throws Exception {
        SortedMap<String, Object> orderMap = new TreeMap<String, Object>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            orderMap.put(entry.getKey(), entry.getValue());
        }
        //检验参数是否匹配
        Constants.PlatformOption.EyouSDK eyouSDK = GlobalCache.getPfConfig("EyouSDK");
        JSONObject rs = new JSONObject();
        rs.put("Success","0");
        rs.put("Reason","");
        String errorStr= "";
        if (null == eyouSDK) {
            errorStr = "EyouSDK:参数未配置" ;
            logger.info(errorStr);
            rs.put("Reason",errorStr);
            sender.sendAndDisconnect(rs);
            return;
        }
        //验签
        if (!orderMap.get("sign").equals(createSign("UTF-8", orderMap,eyouSDK.signKey))){
            errorStr = "EyouSDK:验签失败sign:"+orderMap.get("sign")+ ",createSign:"+ createSign("UTF-8", orderMap,eyouSDK.signKey)  ;
            logger.info(errorStr);
            rs.put("Reason",errorStr);
            sender.sendAndDisconnect(rs);
            return;
        }
        String serverId = params.getString("game_server_id");
        String playerId = params.getString("game_role_id");
        String proIdx = params.getString("order_type");
        float money = params.getFloat("total_fee");
        String third_userID = params.getString("user_id");

        String platform = "57";
        String order_type = "0";
        String channel = params.getString("chl");// 客户端在中心配置获取 然后传入到这边
        String third_orderId = params.getString("order_id");
        Integer bonus = params.getInteger("bonus_game_coin");
        if (bonus == null){
            bonus = 0;
        }
        Integer ext = params.getInteger("game_coin");
        if (StringUtil.isEmpty(proIdx)){
            proIdx = "0";
            order_type = "4";
            if (money * Float.valueOf(EYOUPAY) < ext + bonus){
                errorStr = "元宝金额校验失败，元宝："+ext +",金额："+ money ;
                logger.info(errorStr);
                rs.put("Reason",errorStr);
                sender.sendAndDisconnect(rs);
                return;
            }
        }
        if (!StringUtils.isNumeric(serverId) || !StringUtils.isNumeric(playerId) ) {
            errorStr = "参数为必须为数字serverId:" + serverId + ",playerId:" + playerId  ;
            logger.info(errorStr);
            rs.put("Reason",errorStr);
            sender.sendAndDisconnect(rs);
            return;
        }
        UserPo userPo = UserPo.findByPfUser(third_userID,"EyouSDK");

        if (null == userPo) {
            errorStr = "该用户不存在:" + third_userID;
            logger.info(errorStr);
            rs.put("Reason",errorStr);
            sender.sendAndDisconnect(rs);
            return;
        }
        long userId = userPo.getId();
        logger.info("UserPo:" + userPo.toString());
        String channelCode = userPo.getChannelCode();

        String ordId = HelperRandom.randSecendId(System.currentTimeMillis(), 8, orderMap.get("game_role_id").hashCode());
        // 入库
        OrderPo ordPO = OrderPo.findByOrdId(ordId);
        while (null != ordPO) {// 说明存在重复订单号
            // 重新生成订单号
            ordId = HelperRandom.randSecendId(System.currentTimeMillis(), 8, orderMap.get("game_role_id").hashCode());
            ordPO = OrderPo.findByOrdId(ordId);
        }
        //  直接入库
        ordPO = new OrderPo();
        ordPO.setOrder_id(ordId);
        ordPO.setOrder_type(order_type);
        ordPO.setChannelCode(channelCode);
        ordPO.setPid(Integer.parseInt(playerId));
        ordPO.setSid(Integer.parseInt(serverId));
        ordPO.setPro_idx(Integer.parseInt(proIdx)-1);
        ordPO.setUser_id(userId);
        ordPO.setChannel(channel);// 客户端传入
        ordPO.setBonus(bonus);
        ordPO.setSource(ext+","+bonus);
        ordPO.setPay_price(money);
        ordPO.setExt(ext+"");
        ordPO.setStatus(0);
        ordPO.setCreate_time(System.currentTimeMillis());
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(ordPO.getOrder_id(), ordPO.getOrder_id());
        if (isSaveSuc == 0L) {
            errorStr = "同一订单多次请求,无须重复处理:game_orderid:" + ordPO.getOrder_id();
            logger.info(errorStr);
            rs.put("Reason",errorStr);
            sender.sendAndDisconnect(rs);
            return;
        } else {
            redis.expire(ordPO.getOrder_id(), 30);
        }
        ordPO.setThird_trade_no(third_orderId);
        ordPO.setPlatform("EyouSDK");
        ordPO.setStatus(2);

        //发钻石
        ordPO.insertToDB();
        rs.put("Success","1");
        sender.sendAndDisconnect(rs);
        boolean isSychnSuccess = PaymentToDeliveryUnified.eyoudelivery(ordPO, "EyouSDK");
        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
    }

   /**
     * 礼包
     * @param sender
     * @param params
     */
    @Cmd("/p/eyou/gift")
    public void gift(User sender, HttpRequestJSONObject params) throws Exception {
        SortedMap<String, Object> giftMap = new TreeMap<String, Object>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            giftMap.put(entry.getKey(), entry.getValue());
        }
        JSONObject rs = new JSONObject();
        rs.put("code","0");
        rs.put("reason","");
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        EyouGift eyouGift = JSONObject.toJavaObject(JSON.parseObject(json), EyouGift.class);
        logger.info("EyouGift:"+eyouGift.toString());
        //检验参数是否匹配
        Constants.PlatformOption.EyouSDK eyouSDK = GlobalCache.getPfConfig("EyouSDK");
        String errorStr = "";
        if (null == eyouSDK) {
            errorStr = "eyouSDK:参数未配置";
            logger.info(errorStr);
            sender.sendAndDisconnect(errorStr);
            return;
        }
        if (!giftMap.get("sign").equals(createSign("UTF-8", giftMap,eyouSDK.signKey))){
            errorStr = "EyouSDK:验签失败sign:"+giftMap.get("sign")+ ",createSign:"+ createSign("UTF-8", giftMap,eyouSDK.signKey);
            logger.info(errorStr);
            sender.sendAndDisconnect(rs);
            return;
        }
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(eyouGift);
        rs.put("code","1");
        sender.sendAndDisconnect(rs);


    }



    public static String createSign(String characterEncoding, SortedMap<String, Object> parameters, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v)&!"sign".equals(k)) {
                sb.append(v);
            }
        }
        sb.append(API_KEY);
        logger.info("createSign:"+sb.toString());
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding);
        return sign;
    }

    public static void main(String[] args) {
        String order = "";
        MagicCrypt mc = new MagicCrypt("HD6!Ypc#lDigq");
        order = mc.decrypt(order);
        Map <String,String> orderInfo = new HashMap<>();
        String[] orderinfos = order.split("&");
        for (String str:orderinfos) {
            String[] strs = str.split("=");
            if (strs.length>1){
                orderInfo.put(strs[0],strs[1]);
            }
        }
        System.out.println(orderInfo);
    }

}

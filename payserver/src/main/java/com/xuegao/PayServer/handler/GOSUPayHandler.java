package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.po.UserPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.util.HelperRandom;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.vo.GOSUSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


public class GOSUPayHandler {
    static Logger logger = Logger.getLogger(GOSUPayHandler.class);

    @Cmd("/p/GOSUSDKPayNotify/cb")
    public void GOSUSDKPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        GOSUSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), GOSUSDKNotifyData.class);
        logger.info("GOSUSDKNotifyData:" + notifyData.toString());
        String orderId = null;
        String cause="";
        String[] split = notifyData.extrainfo.split("\\|");
        if (split.length == 3) {
            orderId = AddOrder(notifyData , split[1]);
            if (orderId == null) {
                cause = "网页充值添加订单失败";
                logger.info(cause);
                sender.sendAndDisconnect(MsgFactory.getMsg(0,"fail"));
                return;
            }
        }else if(split.length == 2){
            orderId =split[1];
        }

        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.orderid, notifyData.orderid);
        if (isSaveSuc == 0L) {
            cause = "同一订单多次请求,无须重复处理:order_id:"+notifyData.orderid;
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getMsg(-2,"Order repeat"));
            return;
        } else {
            redis.expire(notifyData.orderid, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(orderId);
        if (payTrade == null) {
            cause = "订单号:"+orderId+",不存在";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getMsg(0,"fail"));
            return;
        }
        if (payTrade.getStatus() >= 2) {
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getMsg(-2,"Order repeat"));
            return;
        }
        Constants.PlatformOption.GOSUSDK gosusdk = GlobalCache.getPfConfig("GOSUSDK");
        String private_key = gosusdk.private_key;
        //检验private_key是否配置
        if (private_key==null) {
            cause="private_key未配置";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getMsg(-1,"System error"));
            return;
        }

        //验签
        //sign=md5(uid+sid+ amount + charid + orderid +private_key+time+ productid)
        String md5Str = notifyData.uid + notifyData.sid + notifyData.amount +notifyData.charid + notifyData.orderid +private_key
                         + notifyData.time + notifyData.productid;
        logger.info("md5Str:"+md5Str);
        md5Str = MD5Util.MD5Encode(md5Str,"UTF-8").toLowerCase();
        if (!notifyData.sign.equals(md5Str)) {
            logger.info("验签失败!GOSUSDK sign="+notifyData.sign+"==============="+"服务端生成签名:"+md5Str);
            sender.sendAndDisconnect(MsgFactory.getMsg(0,"fail"));
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals((float)notifyData.amount)) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配"+"amount:"+ (float)notifyData.amount);
            sender.sendAndDisconnect(MsgFactory.getMsg(0,"fail"));
            return;
        }
        //成功
        sender.sendAndDisconnect(MsgFactory.getMsg(1,"success"));
        payTrade.setThird_trade_no(notifyData.orderid);
        payTrade.setPlatform("GOSUSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "GOSUSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }

    private String AddOrder(GOSUSDKNotifyData orderMap ,String proIdx){
        String order_type = "0";
        if (!StringUtils.isNumeric(orderMap.charid) || !StringUtils.isNumeric(orderMap.sid) ) {
            logger.info("参数为必须为数字serverId:" + orderMap.sid + ",playerId:" + orderMap.charid );
            return null;
        }
        UserPo userPo = UserPo.findByPfUser(orderMap.uid,"GOSUSDK");

        if (null == userPo) {
            logger.info("该用户不存在:" + orderMap.uid);
            return null;
        }
        long userId = userPo.getId();
        logger.info("UserPo:" + userPo.toString());
        String channelCode = userPo.getChannelCode();
        String ordId = HelperRandom.randSecendId(System.currentTimeMillis(), 8, orderMap.charid.hashCode());
        // 入库
        OrderPo ordPO = OrderPo.findByOrdId(ordId);
        while (null != ordPO) {// 说明存在重复订单号
            // 重新生成订单号
            ordId = HelperRandom.randSecendId(System.currentTimeMillis(), 8, orderMap.charid.hashCode());
            ordPO = OrderPo.findByOrdId(ordId);
        }
        //  直接入库
        ordPO = new OrderPo();
        ordPO.setOrder_id(ordId);
        ordPO.setOrder_type(order_type);
        ordPO.setChannelCode(channelCode);
        ordPO.setPid(Integer.parseInt(orderMap.charid));
        ordPO.setSid(Integer.parseInt(orderMap.sid));
        ordPO.setPro_idx(Integer.parseInt(proIdx));
        ordPO.setUser_id(userId);
        ordPO.setPay_price((float) orderMap.amount);
        ordPO.setStatus(0);
        ordPO.setCreate_time(System.currentTimeMillis());
        ordPO.insertToDB();
        return ordId;
    }
}

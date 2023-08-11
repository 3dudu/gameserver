package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.redis.JedisUtil;
import org.apache.log4j.Logger;
import com.xuegao.PayServer.data.Constants.PlatformOption.AFengSDK;
/**
 * 安锋
 * 为了方便游戏厂商接入，所有的接口沿用同一套签名生成（验证）规则，为了以后的开发或升级方便，建议每个接口的签名字串的拼接不要写死在代码里
 *
 * 1.排序：将【所有请求数据（不包括 sign 本身）】按key升序排列
 * 2.拼接：将第1步骤的结果组合成key1=value&key2=value格式
 * 3.追加：将第2步骤的结果末尾追加&sign_key=SIGNKEY（SIGNKEY由平台分配）
 * 4.加密：将第3步骤的结果进行md5加密(32位16进制)，并将结果转换为小写字母，便得到sign参数
 */
public class AnFengHandler {

    static Logger logger = Logger.getLogger(AnFengHandler.class);

    @Cmd("/p/AnFeng/cb")
    public void AnFeng(User sender, JSONObject params) {

        String open_id = params.getString("open_id");//用户在SDK的唯一标识
        String body = params.getString("body");//商品描述
        String subject = params.getString("subject");//商品名称
        String fee = params.getString("fee");//商品价格，单位：元，2位小数
        int vid= params.getIntValue("vid");//应用ID，即APPID
        String sn = params.getString("sn");//SDK订单号，交易查询凭证
        String vorder_id = params.getString("vorder_id");//游戏厂商订单号，SDK不作强制要求，可用作透传字段。最长100个字符
        String create_time = params.getString("create_time");//订单创建时间，格式：YYYY-MM-dd HH:mm:ss
        String version = params.getString("version");//预留字段，目前值为固定的“4.0”
        String sign = params.getString("sign");//请求数据签名，见接口签名规则

        //检验参数是否匹配
        AFengSDK aFengSDK=GlobalCache.getPfConfig("AFengSDK");
        if(null==aFengSDK){
            String cause ="AnFeng:未配置" ;
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            return;
        }
        //检验支付参数app_id是否配置
        AFengSDK.Param app = aFengSDK.apps.get(String.valueOf(vid));
        if(null==app){
            String cause ="安锋平台 参数未配置,app_id="+ app;
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            return;
        }
        //获取app_id的参数SIGN_KEY
        String sign_key = app.sign_key_pay;
        if (sign_key==null) {
            logger.info("app_id与SIGN_KEY不匹配,SIGN_KEY="+sign_key);
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(vorder_id, vorder_id);
        if (isSaveSuc == 0L) {
            String cause ="同一订单多次请求,无须重复处理:vorder_id:" + vorder_id;
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            return;
        } else {
            redis.expire(vorder_id, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(vorder_id);
        String cause = "";
        if (payTrade == null) {
            cause="订单号:" + vorder_id + ",不存在";
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            logger.info(cause);
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            cause="订单已经处理完毕,无须重复处理";
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            return;
        }
        //验签
        StringBuffer sb = new StringBuffer();
        sb.append("body=").append(body).append("&create_time=").append(create_time).append("&fee=")
                .append(fee).append("&open_id=").append(open_id).append("&sn=")
                .append(sn).append("&subject=").append(subject).append("&version=")
                .append(version).append("&vid=").append(vid).append("&vorder_id=")
                .append(vorder_id).append("&sign_key=").append(sign_key);
        String signStr = HelperEncrypt.encryptionMD5(sb.toString()).toLowerCase();
        if (!sign.equals(signStr)) {
            logger.info("安锋sign="+sign+"==============="+"32位小写后signStr:"+signStr);
            cause="验签失败";
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(fee))) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配"+"fee:"+Float.valueOf(fee));
            cause="订单金额和回调金额不一致";
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            return;
        }
        //成功
        sender.sendAndDisconnect("SUCCESS");
        payTrade.setThird_trade_no(sn);
        payTrade.setPlatform("AFengSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "AFengSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }
}

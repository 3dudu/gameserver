package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.vo.YDSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

public class YDSqSDKHandler {

    static Logger logger = Logger.getLogger(YDSDKHandler.class);


    @Cmd("/p/YDSqSDKPayNotify/cb")
    public void YDSqSDKPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        YDSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), YDSDKNotifyData.class);
        logger.info("YDSqSDKNotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.YDSqSDK ydSqsdk = GlobalCache.getPfConfig("YDSqSDK");
        //检验appId是否配置
        if(null==ydSqsdk.apps.get(notifyData.appId)){
            logger.info("优点手Q平台 参数未配置,appId="+ notifyData.appId);
            sender.sendAndDisconnect("fail");
            return;
        }
        //检验openkey是否配置
        String openkey = ydSqsdk.apps.get(notifyData.appId).openkey;
        if (null==openkey) {
            logger.info("openkey未配置,openkey="+openkey);
            sender.sendAndDisconnect("fail");
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        String cause = "";
        long isSaveSuc = redis.STRINGS.setnx(notifyData.orderNo, notifyData.orderNo);
        if (isSaveSuc == 0L) {
            cause = "同一订单多次请求,无须重复处理:partnerOrder:" + notifyData.orderNo;
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("同一订单多次请求,无须重复处理"));
            return;
        } else {
            redis.expire(notifyData.orderNo, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.orderNo);
        if (payTrade == null) {
            cause = "订单号:"+notifyData.orderNo+",不存在";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单号不存在:"+notifyData.orderNo));
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单已经处理完毕,无须重复处理"));
            return;
        }
        //订单状态 0--支付失败 1—支付成功
        if (notifyData.result!=1) {
            cause = "交易结果:失败";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("交易结果:失败"));
            return;
        }
        //验签
        StringBuffer sb = new StringBuffer();
//        交易最终结果以 MD5 方式签名，签名算法如下（只需参数值参与签名）： sign= MD5(orderNo + userName + result + amount + pay_time + pay_channel + openKey)
        sb.append(notifyData.orderNo).append(notifyData.userName)
                .append(notifyData.result).append(notifyData.amount)
                .append(notifyData.pay_time).append(notifyData.pay_channel)
                .append(openkey);
        String signStr = HelperEncrypt.encryptionMD5(sb.toString()).toLowerCase();
        if (!notifyData.sign.equals(signStr)) {
            logger.info("验签失败!优点手Q SDK签名="+notifyData.sign+"==============="+"服务器签名:"+signStr);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("验签失败"));
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(notifyData.amount))) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配支付金额:"+Float.valueOf(notifyData.amount)+"(元)");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单金额与支付金额不一致"));
            return;
        }
        //成功
        sender.sendAndDisconnect("ok");
        payTrade.setThird_trade_no(notifyData.payNo);
        payTrade.setPlatform("YDSqSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "YDSqSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }

    @Cmd("/p/getItems/cb")
    public void getItems(User sender, HttpRequestJSONObject params){
        Constants.PlatformOption.YDSqSDK config = GlobalCache.getPfConfig("YDSqSDK");
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject jsonObject = JSON.parseObject(json);
        String appId  =jsonObject.getString("appId");
        String price = jsonObject.getString("price");
        JSONObject obj = new JSONObject();
        String itemId = config.apps.get(appId).Items.get(price);
        obj.put("itemId",itemId);
        sender.sendAndDisconnect(obj);

    }
}

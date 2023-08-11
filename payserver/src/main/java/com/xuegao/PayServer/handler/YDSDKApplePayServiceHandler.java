package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.vo.YDSDKApplePayNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.StringUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

/**
 * @Author: LiuBin
 * @Date: 2020/2/18 14:53
 */
public class YDSDKApplePayServiceHandler {
    static Logger logger = Logger.getLogger(YDSDKApplePayServiceHandler.class);


    @Cmd("/p/YDSDKApplePayNotify/cb")
    public void YDSDKApplePayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        YDSDKApplePayNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), YDSDKApplePayNotifyData.class);
        logger.info("YDSDKApplePayNotifyData:"+notifyData.toString());
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
        //检验参数是否匹配
        Constants.PlatformOption.YDSDKApple ydsdkApple = GlobalCache.getPfConfig("YDSDKApple");
        //检验appId是否配置
        if(null== ydsdkApple.apps.get(payTrade.getChannelCode()).appId){
            logger.info("优点平台(ios) 参数未配置,appId="+ notifyData.appId);
            sender.sendAndDisconnect("fail");
            return;
        }
        //检验openkey是否配置
        String openkey = ydsdkApple.apps.get(payTrade.getChannelCode()).openkey;
        if (null==openkey) {
            logger.info("openkey未配置,openkey="+openkey);
            sender.sendAndDisconnect("fail");
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
        //校验productId
        String productValue = ydsdkApple.apps.get(payTrade.getChannelCode()).products.get(String.valueOf(payTrade.getPro_idx()));
        if (StringUtil.isEmpty(productValue)||!notifyData.productNo.equals(productValue)) {
            cause ="充值档位未配置或充值档位不匹配";
            logger.info("充值档位未配置或充值档位不匹配"+"product_id:"+notifyData.productNo+"========"+"productValue:"+productValue);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            return;
        }
        //验签
        StringBuffer sb = new StringBuffer();
//        交易最终结果以 MD5 方式签名，签名算法如下（只需参数值参与签名）:
//        sign= MD5(appId+ appVersion+ orderNo+ appleTransactionId+payTime+result+ amount+ productNo+ quantity+userName+ userPara+openKey)
        sb.append(notifyData.appId).append(notifyData.appVersion)
                .append(notifyData.orderNo).append(notifyData.appleTransactionId)
                .append(notifyData.payTime).append(notifyData.result).append(notifyData.amount)
                .append(notifyData.productNo).append(notifyData.quantity).append(notifyData.userName).append(openkey);
        String signStr = HelperEncrypt.encryptionMD5(sb.toString()).toLowerCase();
        if (!notifyData.sign.equals(signStr)) {
            logger.info("验签失败!优点SDK(ios)签名="+notifyData.sign+"==============="+"服务器签名:"+signStr);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("验签失败"));
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(notifyData.amount.floatValue())) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配支付金额:"+notifyData.amount.floatValue()+"(元)");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单金额与支付金额不一致"));
            return;
        }
        //成功
        sender.sendAndDisconnect("ok");
        payTrade.setThird_trade_no(notifyData.appleTransactionId);
        payTrade.setPlatform("YDSDKApple");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "YDSDKApple");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }

}

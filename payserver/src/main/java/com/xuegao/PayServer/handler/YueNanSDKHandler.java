package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HmacSHA1Encryption;
import com.xuegao.PayServer.vo.YueNanSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;


public class YueNanSDKHandler {
    static Logger logger = Logger.getLogger(YueNanSDKHandler.class);
    /**
     * YOUYISDK支付回调
     * @param sender
     * @param params
     */
    @Cmd("/p/ate_payment/cb")
    public void YueNanSDKPayNotify(User sender, HttpRequestJSONObject params){
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject yueNanData = JSONObject.parseObject(json);
        YueNanSDKNotifyData notifyData = JSONObject.toJavaObject(yueNanData, YueNanSDKNotifyData.class);
        logger.info("YueNanSDK支付回调返回的参数为："+notifyData.toString());
        //检查配置参数
        Constants.PlatformOption.YueNanSDK yueNanSDK = GlobalCache.getPfConfig("YueNanSDK");
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.GameOrderID, notifyData.GameOrderID);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:GameOrderID:" + notifyData.GameOrderID);
            sender.sendAndDisconnect(getMsg(1,"发货失败," + "同一订单多次请求,无须重复处理:GameOrderID:" + notifyData.GameOrderID));
            return;
        } else {
            redis.expire(notifyData.GameOrderID, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.GameOrderID);
        if (payTrade == null) {
            sender.sendAndDisconnect(getMsg(1,"发货失败," + "订单号:" + notifyData.GameOrderID + ",不存在"));
            logger.error("订单号:" + notifyData.GameOrderID + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect(getMsg(1,"订单已发货"));
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals (Float.parseFloat(notifyData.Amount))){
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + notifyData.Amount);
            sender.sendAndDisconnect(getMsg(1,"金额不匹配,"+"订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + notifyData.Amount));
            return;
        }

        String appSecretKey = yueNanSDK.appSecretKey;
        //校验签名
        String sign_data = notifyData.GameOrderID + notifyData.AteOrderID +notifyData.ServerID +notifyData.PackageID + notifyData.Time;
        String sign = HmacSHA1Encryption.HMACSHA256Encrypt(sign_data,appSecretKey);
        logger.info("客户端传的sign:"+notifyData.Sign+"服务端生成的sign:"+sign);
        if (sign.equals(notifyData.Sign)) {
            //验签通过
            sender.sendAndDisconnect(getMsg(0,"{'GameOrderID': "+notifyData.GameOrderID+"}"));
            payTrade.setThird_trade_no(notifyData.AteOrderID);
            payTrade.setPlatform("YueNanSDK");
            payTrade.setStatus(2);
            payTrade.updateToDB();
            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "YueNanSDK");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {
            logger.info("验签失败");
            sender.sendAndDisconnect(getMsg(1,"Sign not valid"));
        }

    }

    public static JSONObject getMsg(int code,Object msg){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", msg);
        return jsonObject;
    }
}

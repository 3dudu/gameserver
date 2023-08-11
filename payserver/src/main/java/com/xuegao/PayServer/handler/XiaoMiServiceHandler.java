package com.xuegao.PayServer.handler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HmacSHA1Encryption;
import com.xuegao.PayServer.util.wxpay.DoubleCompare;
import com.xuegao.PayServer.util.wxpay.NumUtil;
import com.xuegao.PayServer.vo.XiaoMiSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.xuegao.PayServer.util.VivoSignUtils.createLinkString;
import static com.xuegao.PayServer.util.VivoSignUtils.paraFilter;

public class XiaoMiServiceHandler {
    static Logger logger = Logger.getLogger(XiaoMiServiceHandler.class);

    @Cmd("/p/XiaoMiSDKPayNotify/cb")
    public void XiaoMiSDKPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject returnMsg = new JSONObject();
        XiaoMiSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), XiaoMiSDKNotifyData.class);
        logger.info("XiaoMiSDKNotifyData:" + notifyData.toString());
        String appid = notifyData.appId;
        String cpOrderId = notifyData.cpOrderId;
        String cause = "";
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(cpOrderId, cpOrderId);
        if (isSaveSuc == 0L) {
            cause = "同一订单多次请求,无须重复处理:cpOrderId:" + cpOrderId;
            logger.info(cause);
            returnMsg.put("errcode", 200);
            sender.sendAndDisconnect(returnMsg);
            return;
        } else {
            redis.expire(cpOrderId, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(cpOrderId);
        if (payTrade == null) {
            cause = "订单号:" + cpOrderId + ",不存在";
            logger.info(cause);
            returnMsg.put("errcode", 1506);
            sender.sendAndDisconnect(returnMsg);
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            returnMsg.put("errcode", 200);
            sender.sendAndDisconnect(returnMsg);
            return;
        }

        Constants.PlatformOption.XiaoMiSDK xiaoMiSDK = GlobalCache.getPfConfig("XiaoMiSDK");
        String app_secret = xiaoMiSDK.apps.get(appid).appSecret;
        //检验app_secret是否配置
        if (app_secret == null) {
            cause = "app_secret未配置";
            logger.info(cause);
            returnMsg.put("errcode", 3515);
            sender.sendAndDisconnect(returnMsg);
            return;
        }
        //订单状态 TRADE_SUCCESS—支付成功
        if (!notifyData.orderStatus.equals("TRADE_SUCCESS")) {
            cause = "订单状态:支付失败";
            logger.info(cause);
            returnMsg.put("errcode", 3515);
            sender.sendAndDisconnect(returnMsg);
            return;
        }
        //校验签名
        Map<String, String> map = new HashMap<>();
        map.put("uid", notifyData.uid);
        map.put("productCount", notifyData.productCount);
        map.put("cpOrderId", notifyData.cpOrderId);
        map.put("appId", appid);
        map.put("payFee", notifyData.payFee);
        map.put("cpUserInfo", notifyData.cpUserInfo);
        map.put("productCode", notifyData.productCode);
        map.put("productName", notifyData.productName);
        map.put("payTime", notifyData.payTime);
        map.put("orderStatus", notifyData.orderStatus);
        map.put("orderId", notifyData.orderId);
        Map<String, String> sortMap = paraFilter(map);
        String prestr = createLinkString(sortMap, true, false);
        String sign = HmacSHA1Encryption.HmacSHA1Encrypt(prestr, app_secret);
        if (!notifyData.signature.equals(sign)) {
            logger.info("验签失败!小米SDK sign=" + notifyData.signature + "===============" + "服务端生成签名:" + sign);
            returnMsg.put("errcode", 1525);
            sender.sendAndDisconnect(returnMsg);
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        String fen = NumUtil.yuanToFen(payTrade.getPay_price());// 实际游戏订单金额
        int rs = DoubleCompare.compareNum(notifyData.payFee, fen);// total_fee 实际支付的金额
        if (rs > 1) {// rs =0 相等 1 第一个数字大 2 第二个数字大
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "实际支付金额(元):" + Float.valueOf(notifyData.payFee)/100F);
            returnMsg.put("errcode", 3515);
            sender.sendAndDisconnect(returnMsg);
            return;
        }
        //成功
        returnMsg.put("errcode", 200);
        sender.sendAndDisconnect(returnMsg);
        //订单金额以实际接受到返回金额为准 【小米返回分单位】
        payTrade.setPay_price(Float.valueOf(notifyData.payFee)/100F);
        payTrade.setThird_trade_no(notifyData.orderId);
        payTrade.setPlatform("XiaoMiSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "XiaoMiSDK");
        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
    }
}


package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.VivoSignUtils;
import com.xuegao.PayServer.vo.VIVOSDKNotifyData;
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

public class VIVOSDKHandler {

    static Logger logger = Logger.getLogger(VIVOSDKHandler.class);

    /**获取vivo订单号orderNumber以及vivo支付SDK需要的accessKey*/
    private static final String requestUri = "https://pay.vivo.com.cn/vcoin/trade";

    @Cmd("/p/vivosign")
    public void sign(User paramUser, JSONObject paramJSONObject)
    {
        logger.info("参数为" + paramJSONObject);
        try
        {
            String appId = paramJSONObject.getString("appId");
            String cpOrderNumber  = paramJSONObject.getString("cpOrderNumber");
            String orderAmount = paramJSONObject.getString("orderAmount");
            String productName = paramJSONObject.getString("productName");
            String productDesc = paramJSONObject.getString("productDesc");
            Constants.PlatformOption.VIVOSDK localVIVOSDK = (Constants.PlatformOption.VIVOSDK)GlobalCache.getPfConfig("VIVOSDK");
            if (localVIVOSDK.apps.get(appId) == null)
            {
                logger.error(MsgFactory.getDefaultErrorMsg("app_id未配置:" + appId));
                paramUser.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("app_id未配置:" + appId));
                return;
            }
            if (((Constants.PlatformOption.VIVOSDK.Param)localVIVOSDK.apps.get(appId)).appKey == null)
            {
                logger.error(MsgFactory.getDefaultErrorMsg("PayKey未配置"));
                paramUser.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("PayKey未配置"));
                return;
            }
            if (((Constants.PlatformOption.VIVOSDK.Param)localVIVOSDK.apps.get(appId)).notifyUrl == null)
            {
                logger.error(MsgFactory.getDefaultErrorMsg("notifyUrl未配置"));
                paramUser.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("notifyUrl未配置"));
                return;
            }
            String notifyUrl = ((Constants.PlatformOption.VIVOSDK.Param)localVIVOSDK.apps.get(appId)).notifyUrl;
            HashMap hashMap = new HashMap();
            hashMap.put("appId", appId);
            hashMap.put("cpOrderNumber", cpOrderNumber);
            hashMap.put("notifyUrl", notifyUrl);
            hashMap.put("orderAmount", orderAmount);
            hashMap.put("productName", productName);
            hashMap.put("productDesc", productDesc);
            String str6 = ((Constants.PlatformOption.VIVOSDK.Param)localVIVOSDK.apps.get(appId)).appKey;
            String vivoSign = VivoSignUtils.getVivoSign(hashMap, str6);
            logger.info("vivosign:" + vivoSign);
            if (!StringUtil.isEmpty(vivoSign))
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ret", Integer.valueOf(0));
                jsonObject.put("notifyUrl", notifyUrl);
                jsonObject.put("sign", vivoSign);
                paramUser.sendAndDisconnect(jsonObject);
            }
        } catch (Exception localException)
        {
            Object localObject = new JSONObject();
            ((JSONObject)localObject).put("ret", Integer.valueOf(-1));
            paramUser.sendAndDisconnect(localObject);
        }
    }

    /**
     * VIVO订单推送接口
     *
     * @param sender
     * @param params
     */
    @Cmd("/p/VIVOSDKPushOrder/cb")
    public void VIVOSDKPushOrder(User sender, JSONObject params) {
        String version = params.getString("version");
        String cpId = params.getString("cpId");
        String appId = params.getString("appId");
        String cpOrderNumber = params.getString("cpOrderNumber");
        String orderTime = params.getString("orderTime");
        String orderAmount = params.getString("orderAmount");
        String orderTitle = params.getString("orderTitle");
        String orderDesc = params.getString("orderDesc");
        String extInfo = params.getString("extInfo");
        Constants.PlatformOption.VIVOSDK vivosdk = GlobalCache.getPfConfig("VIVOSDK");
        if (vivosdk.apps.get(appId) == null) {
            logger.error(MsgFactory.getDefaultErrorMsg("app_id未配置:" + appId));
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("app_id未配置:" + appId));
            return;
        }
        if (vivosdk.apps.get(appId).appKey == null) {
            logger.error(MsgFactory.getDefaultErrorMsg("PayKey未配置"));
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("PayKey未配置"));
            return;
        }
        if (vivosdk.apps.get(appId).notifyUrl == null) {
            logger.error(MsgFactory.getDefaultErrorMsg("notifyUrl未配置"));
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("notifyUrl未配置"));
            return;
        }
        String notifyUrl = vivosdk.apps.get(appId).notifyUrl;
        Map<String, String> para = new HashMap<>();
        para.put("version", version);
        para.put("cpId", cpId);
        para.put("appId", appId);
        para.put("cpOrderNumber", cpOrderNumber);
        para.put("notifyUrl", notifyUrl);
        para.put("orderTime", orderTime);
        para.put("orderAmount", orderAmount);
        para.put("orderTitle", orderTitle);
        para.put("orderDesc", orderDesc);
        para.put("extInfo", extInfo);
        String key = vivosdk.apps.get(appId).appKey;//密钥
        String signature = VivoSignUtils.getVivoSign(para, key);//生成签名
        String postData = "version=" + version + "&signMethod=MD5" + "&signature=" + signature + "&cpId=" + cpId + "&appId=" + appId + "&cpOrderNumber=" + cpOrderNumber
                + "&notifyUrl=" + notifyUrl + "&orderTime=" + orderTime + "&orderAmount=" + orderAmount + "&orderTitle=" + orderTitle
                + "&orderDesc=" + orderDesc + "&extInfo=" + extInfo;
        String result = RequestUtil.request3TimesWithPost(requestUri, postData.getBytes(CharsetUtil.UTF_8));
        logger.info("VIVO平台 post:" + requestUri + ",postData=" + postData + ",result=" + result);
        if (result!=null) {
            JSONObject json = JSONObject.parseObject(result);
            logger.info("VIVO平台返回:" + json.toString());
            if (json.getString("respCode").equals("200")) {
                //返回orderNumber和accessKey给客户端
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ret", 0);
                jsonObject.put("msg", json.getString("respMsg"));
                jsonObject.put("orderNumber", json.getString("orderNumber"));
                jsonObject.put("accessKey", json.getString("accessKey"));
                sender.sendAndDisconnect(jsonObject);
            }
        }
    }

    /**
     * VIVO支付回调接口
     *
     * @param sender
     * @param params
     */
    @Cmd("/p/VIVOSDKPayNotify/cb")
    public void VIVOSDKPayNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        VIVOSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), VIVOSDKNotifyData.class);
        logger.info("VIVOSDKNotifyData:" + notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.VIVOSDK vivosdk = GlobalCache.getPfConfig("VIVOSDK");
        String cause = "";
        if (null == vivosdk) {
            cause = "vivosdk:参数未配置";
            logger.error(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        if (null == vivosdk.apps.get(notifyData.appId)) {
            cause = "vivosdk:appId未配置";
            logger.error(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        if (null == vivosdk.apps.get(notifyData.appId).appKey) {
            cause = "vivosdk:PayKey未配置";
            logger.error(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.cpOrderNumber, notifyData.cpOrderNumber);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:cpOrderNumber:" + notifyData.cpOrderNumber);
            sender.sendAndDisconnect("fail");
            return;
        } else {
            redis.expire(notifyData.cpOrderNumber, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.cpOrderNumber);
        if (payTrade == null) {
            logger.error("订单号:" + notifyData.cpOrderNumber + ",不存在");
            sender.sendAndDisconnect("fail");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("fail");
            return;
        }
        //判断是否支付成功
        if (!(notifyData.respCode.equals("200") && notifyData.tradeStatus.equals("0000"))) {
            cause = "支付失败";
            logger.error(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals((float) (notifyData.orderAmount/100L))) {
            logger.error("订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + (float)(notifyData.orderAmount/100L));
            sender.sendAndDisconnect("fail");
            return;
        }
        Map<String, String> para = new HashMap<>();
        para.put("appId", notifyData.appId);
        para.put("cpId", notifyData.cpId);
        para.put("cpOrderNumber", notifyData.cpOrderNumber);
        para.put("extInfo", notifyData.extInfo);
        para.put("orderAmount", String.valueOf(notifyData.orderAmount));
        para.put("orderNumber", notifyData.orderNumber);
        para.put("payTime", notifyData.payTime);
        para.put("respCode", notifyData.respCode);
        para.put("respMsg", notifyData.respMsg);
        para.put("tradeStatus", notifyData.tradeStatus);
        para.put("tradeType", notifyData.tradeType);
        para.put("uid", notifyData.uid);
        para.put("signMethod", notifyData.signMethod);
        String key = vivosdk.apps.get(notifyData.appId).appKey;//密钥
        String sign = VivoSignUtils.getVivoSign(para, key);//生成签名
        //校验签名
        if (!notifyData.signature.equals(sign)) {
            cause = "验签失败:VIVO签名=" + notifyData.signature + ",生成签名sign=" + sign;
            logger.error(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        //成功
        sender.sendAndDisconnect("success");
        payTrade.setThird_trade_no(notifyData.orderNumber);
        payTrade.setPay_price((float) (notifyData.orderAmount/100L));
        payTrade.setPlatform("VIVOSDK");
        payTrade.setStatus(2);//2.收到回调验证成功
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "VIVOSDK");
        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
    }
}


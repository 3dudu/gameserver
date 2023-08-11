package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.HuaWeiResultVo;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.huaweipay.ATUtil;
import com.xuegao.PayServer.util.huaweipay.RSA;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.vo.HuaWeiSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class HuaWeiSDKHandler {

    static Logger logger = Logger.getLogger(HuaWeiSDKHandler.class);

    // token url to get the authorization
    private static final String TOKEN_URL = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";

    public static final String TOBTOC_SITE_URL = "https://orders-at-dre.iap.dbankcloud.com";

    public static final String TOC_SITE_URL = "https://orders-drcn.iap.hicloud.com";



    /**
     * 充值加签处理
     *
     * @param sender
     * @param params
     */
    @Cmd("/p/HuaWeiSDKAddSign/cb")
    public void HuaWeiSDKAddSign(User sender, JSONObject params) {
        try {
            if (params.isEmpty()) {
                sender.sendAndDisconnect(MsgFactory.getErrorMsg(1, "传递参数为空"));
                return;
            }
            Constants.PlatformOption.HuaWeiSDK huaWeiSDK = GlobalCache.getPfConfig("HuaWeiSDK");
            //待签名字符串
            String content = params.getString("content");
            if (null == content || "".equals(content)) {
                sender.sendAndDisconnect(MsgFactory.getErrorMsg(1, "content值为空"));
                return;
            }
            //替换为'&'
            content = content.replaceAll("@20190525145400@", "&");
            logger.info("content:" + content);
            //amount=10.00&applicationID=100818155&merchantId=900086000030901792&productDesc=100元宝＝¥10&productName=100元宝＝¥10&requestId=0000016c3ce82402979d2b62031f0c5c&3&url=http://159.138.33.74:9030/p/HuaWeiSDKPayNotify/cb&urlver=2
            String orderId=content.split("&")[5].split("=")[1];
            OrderPo payTrade = OrderPo.findByOrdId(orderId);
            if (payTrade==null) {
                logger.error("订单号:" + orderId + ",不存在");
                sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单号:" + orderId + ",不存在"));
                return;
            }
            //生成签名
            String sign = RSA.sign(content, huaWeiSDK.apps.get(payTrade.getChannelCode()).payPrivateKey, "RSA256");
            logger.info("sign:" + sign);
            //返回签名给客户端
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ret", 0);
            jsonObject.put("sign", sign);
            jsonObject.put("msg", "success");
            sender.sendAndDisconnect(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 华为支付回调接口
     *
     * @param sender
     * @param params
     */
    @Cmd("/p/HuaWeiSDKPayNotify/cb")
    public void HuaWeiSDKPayNotify(User sender, HttpRequestJSONObject params) {
        boolean isSychnSuccess = true;
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        HuaWeiSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), HuaWeiSDKNotifyData.class);
        logger.info("HuaWeiSDKPayNotify:" + notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.HuaWeiSDK huaWeiSDK = GlobalCache.getPfConfig("HuaWeiSDK");
        HuaWeiResultVo failVo;
        String cause = "";
        if (null == huaWeiSDK) {
            cause = "huaWeiSDK:参数未配置";
            logger.info(cause);
            failVo = new HuaWeiResultVo(1);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.requestId, notifyData.requestId);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:out_trade_no:" + notifyData.requestId);
            sender.sendAndDisconnect("fail");
            return;
        } else {
            redis.expire(notifyData.requestId, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.requestId);
        if (payTrade == null) {
            failVo = new HuaWeiResultVo(1);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            logger.error("订单号:" + notifyData.requestId + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            failVo = new HuaWeiResultVo(1);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        //判断是否支付成功
        if (!notifyData.result.equals("0")) {
            cause = "支付失败";
            logger.info(cause);
            failVo = new HuaWeiResultVo(1);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(notifyData.amount))) {
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + Float.valueOf(notifyData.amount));
            failVo = new HuaWeiResultVo(1);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        //验签
        Map<String, Object> map = JSON.parseObject(JSON.toJSONString(notifyData));
        if (RSA.rsaDoCheck(map, notifyData.sign,huaWeiSDK.apps.get(payTrade.getChannelCode()).payPublicKey, notifyData.signType)) {
            //验签通过
            failVo = new HuaWeiResultVo(0);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            payTrade.setThird_trade_no(notifyData.orderId);
            payTrade.setPlatform("HuaWeiSDK");
            payTrade.setStatus(2);
            payTrade.updateToDB();
            //发钻石
            isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "HuaWeiSDK");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {
            cause = "验签失败";
            logger.info(cause);
            failVo = new HuaWeiResultVo(1);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
    }

   @Cmd("/p/verifyToken/cb")
   public static void verifyToken(User sender, HttpRequestJSONObject params) throws  Exception{
       String json = null;
       if (!params.isEmpty()) {
           json = params.toJSONString();
       } else {
           json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
       }
       String purchaseToken = JSON.parseObject(json).getString("purchaseToken");
       String productId = JSON.parseObject(json).getString("productId");
       Integer accountFlag =JSON.parseObject(json).getInteger("accountFlag");
       String orderId = JSON.parseObject(json).getString("orderId");
       String channelCode = JSON.parseObject(json).getString("channelCode");
       
       Constants.PlatformOption.HuaWeiSDK huaWeiSDK = GlobalCache.getPfConfig("HuaWeiSDK");
       
       String cause;
       HuaWeiResultVo failVo;
       
       if (null == huaWeiSDK.apps.get(channelCode)) {
           cause = "huaWeiSDK:参数未配置";
           logger.info(cause);
           failVo = new HuaWeiResultVo(1);
           String jsonStr = JSON.toJSONString(failVo);
           sender.sendAndDisconnect(jsonStr);
           return;
       }

       JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
       long isSaveSuc = redis.STRINGS.setnx(orderId, orderId);
       if (isSaveSuc == 0L) {
           logger.error("同一订单多次请求,无须重复处理:out_trade_no:" + orderId);
           sender.sendAndDisconnect("fail");
           return;
       } else {
           redis.expire(orderId, 30);
       }
       OrderPo payTrade = OrderPo.findByOrdId(orderId);
       if (payTrade == null) {
           failVo = new HuaWeiResultVo(1);
           String jsonStr = JSON.toJSONString(failVo);
           sender.sendAndDisconnect(jsonStr);
           logger.error("订单号:" + orderId + ",不存在");
           return;
       }
       if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
           logger.error("订单已经处理完毕,无须重复处理");
           failVo = new HuaWeiResultVo(1);
           String jsonStr = JSON.toJSONString(failVo);
           sender.sendAndDisconnect(jsonStr);
           return;
       }
       
        // fetch the App Level AccessToken
        String grantType = "client_credentials";
        String msgBody = MessageFormat.format("grant_type={0}&client_secret={1}&client_id={2}", grantType,
                URLEncoder.encode(huaWeiSDK.apps.get(channelCode).SecretKey, "UTF-8"), huaWeiSDK.apps.get(channelCode).appId);
        String response = ATUtil.httpPost(TOKEN_URL, "application/x-www-form-urlencoded; charset=UTF-8",
                msgBody, 5000, 5000, null);
        JSONObject obj = JSONObject.parseObject(response);
        String  appAt = obj.getString("access_token");
        logger.info("请求华为生成的access_token为："+ appAt);

        // construct the Authorization in Header
        String oriString = MessageFormat.format("APPAT:{0}", appAt);
        String authorization =
                MessageFormat.format("Basic {0}", Base64.encodeBase64String(oriString.getBytes(StandardCharsets.UTF_8)));
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authorization);
        headers.put("Content-Type", "application/json; charset=UTF-8");


        // pack the request body
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("purchaseToken", purchaseToken);
        bodyMap.put("productId", productId);

        msgBody = JSONObject.toJSONString(bodyMap);

        response = ATUtil.httpPost(getRootUrl(accountFlag) + "/applications/purchases/tokens/verify",
                "application/json; charset=UTF-8", msgBody, 5000, 5000, headers);
        logger.info("请求的msgBody为："+ msgBody +",headers为："+ headers + ",返回的response:"+ response);

       JSONObject jsonObject = JSON.parseObject(response);

       if (!"0".equals(jsonObject.get("responseCode"))) {
           failVo = new HuaWeiResultVo(1);
           String jsonStr = JSON.toJSONString(failVo);
           sender.sendAndDisconnect(jsonStr);
           logger.error("支付token已经过期或不可用");
           return;
       }

       String purchaseTokenData = jsonObject.getString("purchaseTokenData");
       String dataSignature = jsonObject.getString("dataSignature");
       //将这原生字符串与返回的签名进行比对
       if (doCheck(purchaseTokenData,dataSignature,huaWeiSDK.apps.get(channelCode).payPublicKey)) {
           JSONObject purchaseTokenDataJson = JSON.parseObject(purchaseTokenData);

           failVo = new HuaWeiResultVo(0);
           String jsonStr = JSON.toJSONString(failVo);
           sender.sendAndDisconnect(jsonStr);
           //保存华为订单号及支付token
           payTrade.setThird_trade_no(purchaseTokenDataJson.getString("orderId"));
           payTrade.setExt(purchaseTokenDataJson.getString("purchaseToken"));
           payTrade.setPlatform("HuaWeiSDK");
           payTrade.setStatus(2);
           payTrade.updateToDB();
           //发钻石
           Boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "HuaWeiSDK");
           logger.info("isSychnSuccess[" + isSychnSuccess + "]");
       }else {
           cause = "验签失败";
           logger.info(cause);
           failVo = new HuaWeiResultVo(1);
           String jsonStr = JSON.toJSONString(failVo);
           sender.sendAndDisconnect(jsonStr);
           return;
       }
   }

    public static String getRootUrl(Integer accountFlag) {
        if (accountFlag != null && accountFlag == 1) {
            return TOBTOC_SITE_URL;
        }
        return TOC_SITE_URL;
    }

    /**
     * check RSA signature
     *
     * @param content content
     * @param sign sign
     * @param publicKey publicKey
     * @return boolean
     */
    public static boolean doCheck(String content, String sign, String publicKey) {
        if (StringUtils.isEmpty(sign)) {
            return false;
        }
        if (StringUtils.isEmpty(publicKey)) {
            return false;
        }
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature;
            signature = java.security.Signature.getInstance("SHA256WithRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            byte[] bsign = Base64.decodeBase64(sign);
            return signature.verify(bsign);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

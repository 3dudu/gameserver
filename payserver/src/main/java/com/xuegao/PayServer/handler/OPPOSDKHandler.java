package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.*;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.RSAUtils;
import com.xuegao.PayServer.util.huaweipay.Base64;
import com.xuegao.PayServer.util.wxpay.DoubleCompare;
import com.xuegao.PayServer.util.wxpay.NumUtil;
import com.xuegao.PayServer.vo.OPPOSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;


public class OPPOSDKHandler {

    static Logger logger = Logger.getLogger(OPPOSDKHandler.class);
    private static final String RESULT_STR = "result=%s&resultMsg=%s";
    private static final String CALLBACK_OK = "OK";
    private static final String CALLBACK_FAIL = "F/p/OPPOSDKPayNotify/cbAIL";
    private static final String payPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmreYIkPwVovKR8rLHWlFVw7YDfm9uQOJKL89Smt6ypXGVdrAKKl0wNYc3/jecAoPi2ylChfa2iRu5gunJyNmpWZzlCNRIau55fxGW0XEu553IiprOZcaw5OuYGlf60ga8QT6qToP0/dpiL/ZbmNUO9kUhosIjEu22uFgR+5cYyQIDAQAB";

    @Cmd("/p/OPPOSDKPayNotify/cb")
    public void OPPOSDKPayNotify(User sender, HttpRequestJSONObject params){
        try {
            String json = null;
            if (!params.isEmpty()) {
                json = params.toJSONString();
            } else {
                json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
            }
            OPPOSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), OPPOSDKNotifyData.class);
            logger.info("OPPOSDKNotifyData:"+notifyData.toString());
            String result = CALLBACK_OK;
            String resultMsg = "回调成功";
            //检验参数是否匹配
            Constants.PlatformOption.OPPOSDK opposdk = GlobalCache.getPfConfig("OPPOSDK");
            if (null == opposdk) {
                result = CALLBACK_FAIL;
                resultMsg = "OPPOSDK:参数未配置";
                logger.info(resultMsg);
                sender.sendAndDisconnect(String.format(RESULT_STR, result, URLEncoder.encode(resultMsg, "UTF-8")));
                return;
            }
            JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
            long isSaveSuc = redis.STRINGS.setnx(notifyData.partnerOrder, notifyData.partnerOrder);
            if (isSaveSuc == 0L) {
                result = CALLBACK_FAIL;
                resultMsg = "同一订单多次请求,无须重复处理:partnerOrder:" + notifyData.partnerOrder;
                logger.info(resultMsg);
                sender.sendAndDisconnect(String.format(RESULT_STR, result, URLEncoder.encode(resultMsg, "UTF-8")));
                return;
            } else {
                redis.expire(notifyData.partnerOrder, 30);
            }
            OrderPo payTrade = OrderPo.findByOrdId(notifyData.partnerOrder);
            if (payTrade == null) {
                result = CALLBACK_FAIL;
                resultMsg = "订单号:"+notifyData.partnerOrder+",不存在";
                logger.info(resultMsg);
                sender.sendAndDisconnect(String.format(RESULT_STR, result, URLEncoder.encode(resultMsg, "UTF-8")));
                return;
            }
            if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
                result = CALLBACK_FAIL;
                resultMsg = "订单已经处理完毕,无须重复处理";
                logger.info(resultMsg);
                sender.sendAndDisconnect(String.format(RESULT_STR, result, URLEncoder.encode(resultMsg, "UTF-8")));
                return;
            }
            //校验金额
            String  amount =payTrade.getPay_price().toString();
            String  fen = NumUtil.yuanToFen(amount);//实际游戏订单金额
            int rs = DoubleCompare.compareNum(String.valueOf(notifyData.price),fen);//notifyData.price实际支付的金额
            if (rs > 1) {// rs =0 相等 1 第一个数字大 2 第二个数字大
                result = CALLBACK_FAIL;
                resultMsg = "订单金额与实际支付金额不符";
                logger.info(resultMsg);
                sender.sendAndDisconnect(String.format(RESULT_STR, result, URLEncoder.encode(resultMsg, "UTF-8")));
                return;
            }
            //验签
            String baseStr = getBaseString(notifyData);
            if (!doCheck(baseStr,notifyData.sign,payPublicKey)){
                result = CALLBACK_FAIL;
                resultMsg = "验签失败";
                logger.info(resultMsg);
                sender.sendAndDisconnect(String.format(RESULT_STR, result, URLEncoder.encode(resultMsg, "UTF-8")));
                return;
            }
            //成功
            sender.sendAndDisconnect(String.format(RESULT_STR, result, URLEncoder.encode(resultMsg, "UTF-8")));
            payTrade.setThird_trade_no(notifyData.notifyId);
            payTrade.setPlatform("OPPOSDK");
            payTrade.setStatus(2);
            payTrade.updateToDB();
            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "OPPOSDK");
            logger.info("isSychnSuccess["+isSychnSuccess+"]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Cmd("/p/getPublicKey/cb")
    public void  getPublicKey (User sender, HttpRequestJSONObject params) throws Exception {
        Map<String, Object> pairs = RSAUtils.genKeyPair();
        logger.info ("公钥:" + RSAUtils.getPublicKey(pairs));
        sender.sendAndDisconnect(RSAUtils.getPublicKey(pairs));
    }

    /**
     * 拼接后生成基串
     * @param ne
     * @return
     */
    private String getBaseString(OPPOSDKNotifyData ne) {
        StringBuilder sb = new StringBuilder();
        sb.append("notifyId=").append(ne.notifyId);
        sb.append("&partnerOrder=").append(ne.partnerOrder);
        sb.append("&productName=").append(ne.productName);
        sb.append("&productDesc=").append(ne.productDesc);
        sb.append("&price=").append(ne.price);
        sb.append("&count=").append(ne.count);
        sb.append("&attach=").append(ne.attach);
        return sb.toString();
    }

    /**
     * 检验签名
     * @param content
     * @param sign
     * @param publicKey
     * @return
     */
    public static boolean doCheck(String content, String sign, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = com.xuegao.PayServer.util.huaweipay.Base64.decode(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature =
                    java.security.Signature.getInstance("SHA1WithRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes("utf-8"));
            boolean bverify = signature.verify(Base64.decode(sign));
            return bverify;
        } catch (Exception e) {
            logger.error("验证签名出错.",e);
        }
        return false;
    }


}

package com.xuegao.PayServer.handler;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.gm.RSAUtils;
import com.xuegao.PayServer.vo.GMNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.RequestUtil;
import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;


public class GmHandler {

    static Logger logger = Logger.getLogger(GmHandler.class);
    //Rsa公钥
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApSUc4ibbTN4JPgkp/WEIIUGT5alHaqVH9FSeerjdBkGmimJSjyVVNI4kDma97PVT++i11I7F9DUP+7pRuMRmoel4OOL6x3j3nYe0K6lcGNh0g48NDFyrUUWfQxfyMJgV7bLdIat5AXL21HHRFIZgoZTJLGfhIdaDqTLTue2SqeGXmqXYNoq/0cgdrvvTJWiy3afIDbXlfjC537rRNeTFVNaviI/ItYcy1eWBQs7XZgQmPk2B67q3pf/p7ykqCBw77FjtNcX8Sb3TkyfnLPpAMyUelFbKGW09G6cT750UZvOlMUaw1Saabr3AtFAqvecg/JwjpCxi7/OpLbc2VYT1RQIDAQAB";
    //Rsa私钥
    private static final String PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQClJRziJttM3gk+CSn9YQghQZPlqUdqpUf0VJ56uN0GQaaKYlKPJVU0jiQOZr3s9VP76LXUjsX0NQ/7ulG4xGah6Xg44vrHePedh7QrqVwY2HSDjw0MXKtRRZ9DF/IwmBXtst0hq3kBcvbUcdEUhmChlMksZ+Eh1oOpMtO57ZKp4Zeapdg2ir/RyB2u+9MlaLLdp8gNteV+MLnfutE15MVU1q+Ij8i1hzLV5YFCztdmBCY+TYHrurel/+nvKSoIHDvsWO01xfxJvdOTJ+cs+kAzJR6UVsoZbT0bpxPvnRRm86UxRrDVJppuvcC0UCq95yD8nCOkLGLv86kttzZVhPVFAgMBAAECggEAbhVNKZtJN/YSJx4otVQG/VQfaEns5zQBwObfNWMhQlhk0X41FmKGZ6AQfOET3W6zawp2mpgJcH4mh2BttUKGP4vHrfPvwyPpu0KIYUplr9Ip6MBkrEbhlC1aunit4qKei3JdYWJSKRsfWgH8ozfoFg1+BHHCarH51cGhzSCGUWKPn6Y2eDnJwdWxwG3VNZQEGi3pRkTx9kj/V47X8NH/jeBfRxXm9vHaQbSY2QmKSh7aCqKtF502KHlSPA/u4Mih5Vh1IhORkZ0KHVL9dD1E3xJzcnNk7BiyHwDRuoTLup+RMbYqNTRKrPiMbDPgm9BoMgav3QenGo9Z6IJhhho6QQKBgQDy9IG1NHDMLoVSimV5xT/ivds427fMGMB8/2ZhmnphfJ+SIr3Q+Ut5RMfyyIvFeNboDO25yVSigvhAxtozESWG1CYCH5uD8Nes2lSKAL/5V31eu8RzojrSgVlyo5212R5T3XSxSH+s05Dbj5BtyoztIgQOq0q4SgQd38qgGeUyaQKBgQCuAxTsafNnm/9q68VrNIfIM0oSck+HRpj0jITdru4D4cd8nTy2lC5nZogcj7IS+LbYtl5XgOI132WXqcFAredxqGNUsUCofHE17rIuGRirReOrtKsYFUFPrwTJNRT0xc/ndAHW+n0upQ9UPqj/HJ/Ovi37PJo+fqjJqtaxlaWYfQKBgQDcSiTdx5nLGRdb2w7dlaMylVETwe1qSrsl23HaZ/Y1NIl/OK8Brzjm0R23Hm3VdJbvuuFGRq3N2JD+Mw+fpBlxoiSAYmZhANyd5y0mID3w+Io9fmVHL77EJfKTxpT2UNJ12mO3Z3QUoZRD8G1Vj4WucdxZ7KiIZtxKtiMEfdZamQKBgHE84CjY6eTKx3Q06cvR62qEtfc7HDXT40WBDBWW+JzeGIsnZ5MI6wmu13R+rktaPuLYCpy26n5UWjBP78q/YJW+FqXOk10RXjrSknEdM8iBOp9Keuy5KD9KjbrCKFkBQUJFY80aRMxN7aPNAvzBC93mNNYBof55Pi2+VuhJkfGhAoGBANuHAgY6sWzhD/g93bSQZFtpfGtBpWzsmGOFFmN95v4I+LioPmfaV/3eKbmMekp9P0IWB9gT1bMeXJX7CkFB5Oa+W4hiS3pk0WgDxxroDNM9TQOFH78tnYEeWkUG7/XdRQQA0u+Qdtq1jOjMVrC0wALE6lFoRt2iJVpPjhBlePqk";

    public static void main(String[] args) throws Exception {


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("order_id", "00000172a19ca731bdb6ee6e00170064");
        jsonObject.put("amount", "6");
        jsonObject.put("role_id", "10085");
        jsonObject.put("server_id", "10086");
        String jsonStr = jsonObject.toString();
        System.out.println("jsonStr = " + jsonStr);

        byte[] requestData = RSAUtils.clientEncrypt(jsonStr.getBytes(StandardCharsets.UTF_8), PUBLIC_KEY);
        String encode = "data=" + cn.hutool.core.codec.Base64.encode(requestData).replaceAll("[+]", "@");
        String result = RequestUtil.request3TimesWithPost("http://159.138.33.74:10000/p/GMPayNotify/cb", encode.getBytes());
//        String result = RequestUtil.request3TimesWithPost("http://192.168.1.188:9031/p/GMPayNotify/cb", encode.getBytes());
//        String result = RequestUtil.requestTimesWithPost("http://192.168.1.188:9031/p/GMPayNotify/cb", requestData);
        System.out.println("result = " + result);

        byte[] bytes1 = RSAUtils.serverDecrypt(requestData, PRIVATE_KEY);
        String str = new String(bytes1);
        System.out.println("解密后 = " + str);

    }


    /**
     * GM代充值回调接口
     */
    @Cmd("/p/GMPayNotify/cb")
    public void GMPayNotify(User sender, JSONObject params) throws Exception {
        String data = (String) params.get("data");
        logger.info("data=" + data);
        byte[] req = Base64.decode(data.replaceAll("@", "+"));
        String json = "";
        try {
            //使用私钥解密参数
            byte[] plainData = RSAUtils.serverDecrypt(req, PRIVATE_KEY);
            json = new String(plainData);
            logger.info("json=" + json);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("私钥解密参数异常"));
            return;
        }
        GMNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), GMNotifyData.class);
        logger.info("GMNotifyData:" + notifyData.toString());
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.order_id, notifyData.order_id);
        String cause = "";
        if (isSaveSuc == 0L) {
            cause = "同一订单多次请求,无须重复处理:order_id:" + notifyData.order_id;
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            return;
        } else {
            redis.expire(notifyData.order_id, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.order_id);
        if (payTrade == null) {
            cause = "订单号:" + notifyData.order_id + ",不存在";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            return;
        }
        if (payTrade.getStatus() >= 2) {
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(notifyData.amount))) {
            cause = "订单金额:" + payTrade.getPay_price() + "不匹配" + "amount:" + Float.valueOf(notifyData.amount);
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
            return;
        }
        //ret=1 成功 ret=-1 失败
        sender.sendAndDisconnect(MsgFactory.getErrorMsg(1, "支付成功"));
        payTrade.setOrder_type("3");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "");
        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
    }
}

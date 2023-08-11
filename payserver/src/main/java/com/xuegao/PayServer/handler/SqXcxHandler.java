package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.wxpay.HmacSHA1And256EncryptionWx;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.RequestUtil;
import com.xuegao.core.util.StringUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.util.HashMap;

import static com.xuegao.PayServer.util.VivoSignUtils.createLinkString;

public class SqXcxHandler {
    public static Logger logger = Logger.getLogger(AdHandler.class);
    private static final String code2Session = "https://api.q.qq.com/sns/jscode2session";
    private static final String GamePrePay = "https://api.q.qq.com/api/json/openApiPay/GamePrePay";
    private static final String getAccessToken = "https://api.q.qq.com/api/getToken";

    /**
     * 获取登录及支付时需要的参数openid 、session_key
     * @param sender
     * @param params
     */
    @Cmd("/p/code2Session/cb")
    public void code2Session(User sender, HttpRequestJSONObject params){
        Constants.PlatformOption.SqXcxSDK config = GlobalCache.getPfConfig("SqXcxSDK");
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.getString("code");
        String url = code2Session + "?" + "appid=" + config.appId + "&secret=" + config.appSecret + "&js_code=" + code + "&grant_type=authorization_code";
        logger.info("请求时拼接的url为："+ url);
        String result = RequestUtil.request3Times(url);
        logger.info("请求获取到的result为："+result);
        if (result != null) {
            JSONObject obj = JSON.parseObject(result);
            logger.info("获取到的数据为:" + obj.toString());
            sender.sendAndDisconnect(obj);
        }

    }

    /**
     * 生成手Q的订单号传给前端 prepayId
     * @param sender
     * @param params
     */
    @Cmd("/p/sqPlaceOrder/cb")
    public void sqPlaceOrder(User sender, JSONObject params) throws Exception {
        long ts = System.currentTimeMillis() / 1000L;//UNIX时间戳
        String openid = params.getString("openid");
        String zone_id = params.getString("zone_id");
        String pf = params.getString("pf");
        String amt = params.getString("amt");
        String billno = params.getString("billno");
        String goodid = params.getString("goodid");
        String good_num = params.getString("good_num");
        String session_key = params.getString("session_key");
        logger.info("客户端传给服务器的session_key:"+session_key);
        OrderPo payTrade = OrderPo.findByOrdId(billno);
        if (payTrade == null) {
            logger.info("订单号:" + billno + ",不存在");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单号:" + billno + ",不存在"));
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单已经处理完毕,无须重复处理"));
            return;
        }
        Constants.PlatformOption.SqXcxSDK sqXcxSDK= GlobalCache.getPfConfig("SqXcxSDK");

        //签名需要的参数及拼接
        String method = "POST&";
        String scriptName = "/api/json/openApiPay/GamePrePay";
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", sqXcxSDK.appId);
        map.put("openid", openid);
        map.put("pf", pf);
        map.put("ts", String.valueOf(ts));
        map.put("zone_id", zone_id);
        map.put("goodid",goodid);
        map.put("bill_no",billno);
        map.put("good_num",good_num);
        map.put("amt",amt);
        String strA = createLinkString(map,true,false);
        String sigStr = method + URLEncoder.encode(scriptName,"utf-8") + "&" + strA +"&session_key="+session_key;
        logger.info("拼接的签名字符串为："+sigStr);

        //生成签名
        String sig = HmacSHA1And256EncryptionWx.GENHMACSHA256(sigStr,session_key);
        logger.info("生成的签名串sig："+sig);

        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        String accessToken = redis.STRINGS.get(sqXcxSDK.appId + "_SQ_accessToken");
        logger.info("accessToken:" + accessToken);
        //如果accessToken已过期则重新获取
        if (StringUtil.isEmpty(accessToken)) {
            String requestData = getAccessToken + "?grant_type=client_credential" + "&appid=" + sqXcxSDK.appId + "&secret=" + sqXcxSDK.appSecret;
            String result = RequestUtil.request3Times(requestData);
            if (result != null) {
                JSONObject obj = JSON.parseObject(result);
                logger.info("请求get_token_url返回:" + obj.toString());
                accessToken = obj.getString("access_token");
                //存入redis
                redis.STRINGS.set(sqXcxSDK.appId + "_SQ_accessToken", accessToken);
                //设置过期时间为7000S
                redis.expire(sqXcxSDK.appId + "_SQ_accessToken", 7000);
            }
        }
        logger.info("获取到的accessToken："+accessToken);
        //请求手Q时的json
        String postData = "{\"openid\":\""+openid+"\",\"appid\":\""+sqXcxSDK.appId+"\",\"ts\":"+ts+",\"zone_id\":\""+zone_id+"\",\"pf\":\""+pf+"\",\"amt\":"+amt+",\"goodid\":\""+goodid+"\",\"good_num\":"+good_num+",\"bill_no\":\""+billno+"\",\"sig\":\""+sig+"\"}";
        String result = RequestUtil.post(GamePrePay+"?access_token="+accessToken,postData);
        logger.info("请求完整的url:"+GamePrePay+"?access_token="+accessToken+"  ,请求的参数为："+postData+"  ,请求手Q返回的信息为："+result);
        if (result != null) {
            JSONObject obj = JSON.parseObject(result);
            if (obj.getIntValue("errcode") == 0) {
                sender.sendAndDisconnect(obj);
                logger.info("返回给客户端的参数："+obj);
            }
        }
    }
    @Cmd("/p/sqCallbackNotify/cb")
    public void sqCallbackNotify (User sender, JSONObject params) throws Exception {
        String openid = params.getString("openid");
        String sig = params.getString("sig");
        String ts = params.getString("ts");
        String amt = params.getString("amt");
        String billno = params.getString("bill_no");

        OrderPo paytradeByDb = OrderPo.findByOrdId(billno);
        if (null == paytradeByDb) {// 订单号不存在
            logger.info("订单:[bill_no:"+billno+"] 不存在");
            sender.sendAndDisconnect(MsgFactory.getMsg(-1,"订单bill_no:"+billno+" 不存在"));
            return;
        }
        if(paytradeByDb.getStatus()>=2){
            logger.info("订单bill_no:"+billno+" 已经处理");
            sender.sendAndDisconnect(MsgFactory.getMsg(0,"订单bill_no:"+billno+" 已经处理"));
            return;
        }
        //校验金额
        if (!paytradeByDb.getPay_price().equals(Float.valueOf(amt)/10)){
            logger.info("订单金额:" + paytradeByDb.getPay_price() + "不匹配," + "客户端请求扣款金额:" + Float.valueOf(amt)/10F);
            sender.sendAndDisconnect(MsgFactory.getMsg(-1,"订单金额:" + paytradeByDb.getPay_price() + "不匹配" + "客户端请求的扣款金额:" + Float.valueOf(amt)/10F));
            return;
        }
        Constants.PlatformOption.SqXcxSDK sqXcxSDK= GlobalCache.getPfConfig("SqXcxSDK");
        //校验签名
        String method = "POST&";
        String AppSecret = sqXcxSDK.appSecret;
        HashMap<String, String> map = new HashMap<>();
        map.put("openid", openid);
        map.put("ts", ts);
        map.put("bill_no",billno);
        map.put("amt",amt);

        String stringA = createLinkString(map,true,false);
        String scriptName ="/p/sqCallbackNotify/cb";
        String sigStr = method + URLEncoder.encode(scriptName,"utf-8") + "&" + stringA +"&AppSecret="+AppSecret;
        logger.info("sigStr:"+sigStr+"签名时用到的key："+AppSecret);
        //生成签名
        String stringSign = HmacSHA1And256EncryptionWx.GENHMACSHA256(sigStr,AppSecret);
        logger.info("生成的签名串stringSign："+stringSign);

        if (!sig.equals(stringSign)) {
            logger.info("验签失败!手Q_SDK签名="+sig+"==============="+"服务器签名:"+stringSign);
            sender.sendAndDisconnect(MsgFactory.getMsg(-1,"验签失败"));
            paytradeByDb.setStatus(1);//1.收到回调验证失败
            paytradeByDb.updateToDB_WithSync();
            return;
        }
        //成功
        sender.sendAndDisconnect(MsgFactory.getMsg(0,"成功"));
        paytradeByDb.setPlatform("SqXcxSDK");
        paytradeByDb.setPay_price(Float.valueOf(amt));
        paytradeByDb.setStatus(2);
        paytradeByDb.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(paytradeByDb, "SqXcxSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }

}

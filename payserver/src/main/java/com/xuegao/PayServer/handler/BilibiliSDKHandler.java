package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;

import com.xuegao.PayServer.util.MD5Util;

import com.xuegao.PayServer.vo.BilibiliSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


import static com.xuegao.PayServer.util.VivoSignUtils.LinkString;
import static com.xuegao.PayServer.util.VivoSignUtils.createLinkString;



public class BilibiliSDKHandler {
    static Logger logger = Logger.getLogger(BilibiliSDKHandler.class);

    /**
     * bili充值加签
     * @param sender
     * @param params
     */
    @Cmd("/p/BilibiliSDKAddSign/cb")
    public void BilibiliSDKAddSign (User sender , JSONObject params){
        try{
            if (params.isEmpty()) {
                sender.sendAndDisconnect(MsgFactory.getErrorMsg(1, "传递参数为空"));
                return;
            }
            //按顺序拼接游戏内货币、本次交易金额、支付回调地址、商户订单号、开放平台所提供的服务端密钥后，进行md5加签的值。
            String app_id = params.getString("app_id");
            String game_money = params.getString("game_money"); //游戏内货币
            String money = params.getString("money");//本次交易金额 此参数即为客户端add.pay.order方法中的total_fee参数
            String out_trade_no = params.getString("out_trade_no"); //商户订单号
            String notifyUrl = params.getString("notifyUrl"); //游戏回调地址
            //获取配置参数
            Constants.PlatformOption.BilibiliSDK bilibiliSDK = GlobalCache.getPfConfig("BilibiliSDK");
            String secretKey = bilibiliSDK.apps.get(app_id).secretKey;
            if (secretKey == null) {
                logger.info("BilibiliSDK 平台参数未配置 appId:"+ app_id + "secretKey:" + secretKey );
                return;
            }
            String sign = game_money + money + notifyUrl + out_trade_no + secretKey; //参数拼接排序请按服务端key名称序列化

            String order_sign = MD5Util.md5(sign);
            logger.info("签名时的字符串StrA："+sign + "生成的签名为："+order_sign);
            //返回签名给客户端
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ret", 0);
            jsonObject.put("order_sign",order_sign);
            jsonObject.put("msg", "success");
            sender.sendAndDisconnect(jsonObject);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * B站支付回调
     * @param sender
     * @param params
     */
    @Cmd("/p/BilibiliSDKPayNotify/cb")
    public void BilibiliSDKPayNotify(User sender, JSONObject params){
        String json = null;
        if (params.isEmpty()) {
            json = params.toJSONString();
        }else {
            json = params.getString("data");
        }
        JSONObject  biliData = JSONObject.parseObject(json);
        BilibiliSDKNotifyData notifyData = JSONObject.toJavaObject(biliData,BilibiliSDKNotifyData.class);
        logger.info("bilibiliSDK支付回调返回的参数为："+notifyData.toString());
        //检查配置参数
        Constants.PlatformOption.BilibiliSDK bilibiliSDK = GlobalCache.getPfConfig("BilibiliSDK");
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.out_trade_no, notifyData.out_trade_no);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:game_orderid:" + notifyData.out_trade_no);
            sender.sendAndDisconnect("success");
            return;
        } else {
            redis.expire(notifyData.out_trade_no, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.out_trade_no);
        if (payTrade == null) {
            sender.sendAndDisconnect("failure");
            logger.error("订单号:" + notifyData.out_trade_no + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("success");
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals (Float.parseFloat(notifyData.pay_money)/ 100)){
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + notifyData.pay_money);
            sender.sendAndDisconnect("failure");
            return;
        }
        if (bilibiliSDK.apps.get(notifyData.extension_info) == null) {
            logger.info("参数未配置 appid:"+ bilibiliSDK.apps.get(notifyData.extension_info));
            sender.sendAndDisconnect("failure");
            return;
        }
        String secret_key =bilibiliSDK.apps.get(notifyData.extension_info).secretKey;
        //校验签名
        Map map = JSON.parseObject(JSON.toJSONString(notifyData));
        map.remove("sign");
        String strA = LinkString(map,true);
        String sign_data = strA +secret_key;
        String sign = MD5Util.md5(sign_data).toLowerCase();
        logger.info("客户端传的sign:"+notifyData.sign+"服务端生成的sign:"+sign);
        if (sign.equals(notifyData.sign)) {
            //验签通过
            sender.sendAndDisconnect("success");
            payTrade.setThird_trade_no(notifyData.order_no);
            payTrade.setPlatform("BilibiliSDK");
            payTrade.setStatus(2);
            payTrade.updateToDB();
            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "BilibiliSDK");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {
            logger.info("验签失败");
            sender.sendAndDisconnect("failure");
        }

    }
}




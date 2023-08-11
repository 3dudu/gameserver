package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.vo.Q1SDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Q1PayHandler {

    static Logger logger = Logger.getLogger(Q1PayHandler.class);

    /**
     * Q1充值加签
     * @param sender
     * @param params
     */
    @Cmd("/p/Q1SDKAddSign/cb")
    public void Q1SDKAddSign (User sender , JSONObject params){
        try{
            if (params.isEmpty()) {
                sender.sendAndDisconnect(MsgFactory.getErrorMsg(1, "传递参数为空"));
                return;
            }
            String orderItem = params.getString("orderItem");//商品ID * 单价（分）* 数量*商品名称 示例 8*600*1*6元充值
            orderItem = URLDecoder.decode(orderItem,"utf-8");
            String orderNo = params.getString("orderNo"); //游戏订单号
            String payNum = params.getString("payNum");//充值金额  /元
            String userId = params.getString("userId"); //冰川用户id
            String channel = params.getString("channel");//渠道号
            String currencyType = params.getString("currencyType");
            //获取配置参数
            Constants.PlatformOption.Q1SDK q1SDK = GlobalCache.getPfConfig("Q1SDK");
            String buyKey = q1SDK.apps.get(channel).buyKey;
            if (buyKey == null) {
                logger.info("Q1SDK 平台参数未配置 channel:"+ channel + "buyKey:" + buyKey );
                return;
            }
            //MD5(orderItem * orderNo * payNum * userID * currencyType * buyKey)
            String sign = orderItem + orderNo + payNum + userId +buyKey; //参数拼接排序请按服务端key名称序列化

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
     * Q1支付回调
     * @param sender
     * @param params
     */
    @Cmd("/p/Q1SDKPayNotify/cb")
    public void Q1SDKPayNotify(User sender, HttpRequestJSONObject params) throws UnsupportedEncodingException {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject  q1Data = JSONObject.parseObject(json);
        Q1SDKNotifyData notifyData = JSONObject.toJavaObject(q1Data, Q1SDKNotifyData.class);
        logger.info("Q1SDK支付回调返回的参数为："+notifyData.toString());
        //检查配置参数
        Constants.PlatformOption.Q1SDK q1SDK = GlobalCache.getPfConfig("Q1SDK");
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.order, notifyData.order);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:game_orderid:" + notifyData.order);
            sender.sendAndDisconnect("5");
            return;
        } else {
            redis.expire(notifyData.order, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.order);
        if (payTrade == null) {
            sender.sendAndDisconnect("3");
            logger.error("订单号:" + notifyData.order + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("5");
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals (Float.parseFloat(notifyData.amount)/ 100)){
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + notifyData.amount);
            sender.sendAndDisconnect("6");
            return;
        }
        String developerPayLoad = URLDecoder.decode(notifyData.developerPayload,"utf-8");
        JSONObject developerPayLoadObj = JSONObject.parseObject(developerPayLoad);
        developerPayLoad = developerPayLoadObj.getString("gameExtInfo");
        if (q1SDK.apps.get(developerPayLoad) == null) {
            logger.info("参数未配置 channel:"+ developerPayLoad);
            sender.sendAndDisconnect("其他");
            return;
        }
        String payKey =q1SDK.apps.get(developerPayLoad).payKey;
        //校验签名
        String strA = notifyData.pid+"_"+ notifyData.user+"_"+notifyData.order+"_"+notifyData.sdkorder+"_"+notifyData.amount+"_"
               +notifyData.sid+"_"+notifyData.productid+"_"+notifyData.checkproduct+"_"+notifyData.actorid;
        String sign_data = strA +"_"+payKey;
        String sign = MD5Util.md5(sign_data);
        logger.info("客户端传的sign:"+notifyData.sign+"服务端生成的sign:"+sign);
        if (sign.equals(notifyData.sign)) {
            //验签通过
            sender.sendAndDisconnect("1");
            payTrade.setThird_trade_no(notifyData.sdkorder);
            payTrade.setPlatform("Q1SDK");
            payTrade.setStatus(2);
            payTrade.updateToDB();
            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "Q1SDK");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {
            logger.info("验签失败");
            sender.sendAndDisconnect("-3");
        }

    }
}

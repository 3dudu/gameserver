package com.xuegao.PayServer.handler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.vo.FTNNSDKNotifyData;
import com.xuegao.PayServer.vo.Game6kwSDKNotifyData;
import com.xuegao.PayServer.vo.SamsungNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.RequestUtil;
import com.xuegao.core.util.StringUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;


public class SamsungHandler {

    static Logger logger = Logger.getLogger(SamsungHandler.class);
    static String request_url = "https://iap.samsungapps.com/iap/v6/receipt";

    /**
     * 支付回调
     *
     * @param sender
     * @param params
     */
    @Cmd("/p/SamsungNotify/cb")
    public void SamsungNotify(User sender, HttpRequestJSONObject params) {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        String purchaseID = JSON.parseObject(json).getString("purchaseId");
        String result = RequestUtil.request3Times(request_url + "?purchaseID=" + purchaseID);
        logger.info("samsung 请求url :"+ request_url + "请求参数 purchaseId: " +purchaseID +" 返回示例 result:"+ result);
        if (JSON.parseObject(result).getString("status").equals("success")) {
            SamsungNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(result), SamsungNotifyData.class);
            logger.info("SamsungNotifyData : " + notifyData.toString());
            //检验参数是否匹配
            Constants.PlatformOption.Samsung samsung = GlobalCache.getPfConfig("Samsung");

            if (null == samsung) {
                logger.info("samsung:参数未配置");
                sender.sendAndDisconnect("samsung:参数未配置");
                return;
            }
            JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
            long isSaveSuc = redis.STRINGS.setnx(notifyData.passThroughParam, notifyData.passThroughParam);
            if (isSaveSuc == 0L) {
                logger.error("同一订单多次请求,无须重复处理:gameOrder:" + notifyData.passThroughParam);
                sender.sendAndDisconnect("SUCCESS");
                return;
            } else {
                redis.expire(notifyData.passThroughParam, 30);
            }
            OrderPo payTrade = OrderPo.findByOrdId(notifyData.passThroughParam);

            if (payTrade == null) {
                sender.sendAndDisconnect("订单号:" + notifyData.passThroughParam + ",不存在");
                logger.error("订单号:" + notifyData.passThroughParam + ",不存在");
                return;
            }
            if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
                logger.error("订单已经处理完毕,无须重复处理");
                sender.sendAndDisconnect("订单已经处理完毕,无须重复处理");
                return;
            }
            String productValue=samsung.products.get(String.valueOf(payTrade.getPro_idx()));
            logger.info("三星返回的product_id:"+notifyData.itemId+"========"+"后台配置的productId:"+productValue);
            if (StringUtil.isEmpty(productValue)||!notifyData.itemId.equals(productValue)) {
                logger.info("充值档位未配置或充值档位不匹配");
                sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("充值档位未配置或充值档位不匹配"));
                return;
            }
            sender.sendAndDisconnect("SUCCESS");
            payTrade.setThird_trade_no(notifyData.orderId);
            payTrade.setPlatform("Samsung");
            payTrade.setStatus(2);
            payTrade.updateToDB();
            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "Samsung");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        }else {
            logger.info("请求错误 ");
            sender.sendAndDisconnect("请求错误");
        }

    }
}
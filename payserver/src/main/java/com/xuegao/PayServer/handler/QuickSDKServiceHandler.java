package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.QuickSDKUtil;
import com.xuegao.PayServer.util.wxpay.XMLUtil;
import com.xuegao.PayServer.vo.QuickSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.Misc;
import org.apache.log4j.Logger;

import java.util.Map;


public class QuickSDKServiceHandler {

    public static Logger logger = Logger.getLogger(QuickSDKServiceHandler.class);


    @Cmd("/p/quickCallbackNotify/cb")
    public void quickCallbackNotify (User sender, JSONObject params) throws Exception {
        String nt_data = params.getString("nt_data");
        String sign = params.getString("sign");
        String md5Sign = params.getString("md5Sign");

        // 校验签名
        // 判断同和参数配置情况
        Constants.PlatformOption.QuickSDK quickSDK= GlobalCache.getPfConfig("QuickSDK");

        String md5key = quickSDK.md5Key;
        if (null == md5key) {
            logger.info("QuickSDK参数md5key未配置");
            sender.sendAndDisconnect("FAIL");
            return;
        }

        String md5SignLocal = Misc.md5(nt_data+ sign +md5key);

        if (!md5SignLocal.equals(md5Sign)) {
            logger.info("QuickSDK验签失败,生成的签名md5SignLocal:"+md5SignLocal +"，传入的签名md5Sign:"+md5Sign );
            sender.sendAndDisconnect("FAIL");
            return;
        }
        // 解析nt_data中的数据
        String callbackKey = quickSDK.callbackKey;
        if (null == callbackKey) {
            logger.info("QuickSDK参数callbackKey未配置");
            sender.sendAndDisconnect("FAIL");
            return;
        }
        String nt_data_xml = QuickSDKUtil.decode(nt_data, callbackKey);
        //将xml的数据转化为map
        nt_data_xml = nt_data_xml.replace("<quicksdk_message>","").replace("</quicksdk_message>","");
        Map nt_data_map = XMLUtil.doXMLParse(nt_data_xml);
        logger.info("回调参数nt_data解析的xml为："+ nt_data_xml+ "，转为map："+nt_data_map);

        if (null == nt_data_map) {
            logger.info("参数 nt_data 解析失败!");
            sender.sendAndDisconnect("FAIL");
            return;
        }
        QuickSDKNotifyData notifyData = JSON.parseObject(JSON.toJSONString(nt_data_map), QuickSDKNotifyData.class );

        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.game_order, notifyData.game_order);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:game_orderid:" + notifyData.game_order);
            sender.sendAndDisconnect("FAIL");
            return;
        } else {
            redis.expire(notifyData.game_order, 30);
        }

        // QuickSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(nt_data_map.toString()), QuickSDKNotifyData.class);
        OrderPo paytradeByDb = OrderPo.findByOrdId(notifyData.game_order);
        if (null == paytradeByDb) {// 订单号不存在
            logger.info("game_order:"+notifyData.game_order+" 不存在");
            sender.sendAndDisconnect("FAIL");
            return;
        }
        if(paytradeByDb.getStatus()>=2){
            logger.info("订单game_order:"+notifyData.game_order+" 已经处理");
            sender.sendAndDisconnect("SUCCESS");
            return;
        }
        //校验金额
        if (!paytradeByDb.getPay_price().equals(Float.valueOf(notifyData.amount))){
            logger.info("订单金额:" + paytradeByDb.getPay_price() + "不匹配," + "客户端请求扣款金额:" + Float.valueOf(notifyData.amount));
            sender.sendAndDisconnect("FAIL");
            return;
        }


        //成功
        sender.sendAndDisconnect("SUCCESS");
        paytradeByDb.setPlatform("QuickSDK");
        paytradeByDb.setPay_price(Float.valueOf(notifyData.amount));
        paytradeByDb.setStatus(2);
        paytradeByDb.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(paytradeByDb, "QuickSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }
}

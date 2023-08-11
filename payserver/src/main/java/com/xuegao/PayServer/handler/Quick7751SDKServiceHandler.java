package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.QuickSDKUtil;
import com.xuegao.PayServer.util.wxpay.XMLUtil;
import com.xuegao.PayServer.vo.Quick7751SDKNotifyData;
import com.xuegao.PayServer.vo.QuickSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.Misc;
import org.apache.log4j.Logger;

import java.util.Map;

public class Quick7751SDKServiceHandler {

    public static Logger logger = Logger.getLogger(Quick7751SDKServiceHandler.class);


    @Cmd("/p/quick7751CallbackNotify/cb")
    public void quick7751CallbackNotify (User sender, JSONObject params) throws Exception {
        String nt_data = params.getString("nt_data");
        String sign = params.getString("sign");
        String md5Sign = params.getString("md5Sign");

        // 校验签名
        // 判断同和参数配置情况
        Constants.PlatformOption.Quick7751SDK quick7751SDK= GlobalCache.getPfConfig("Quick7751SDK");

        String md5key = quick7751SDK.md5Key;
        if (null == md5key) {
            logger.info("Quick7751SDK参数md5key未配置");
            sender.sendAndDisconnect("FAIL");
            return;
        }

        String md5SignLocal = Misc.md5(nt_data+ sign +md5key);

        if (!md5SignLocal.equals(md5Sign)) {
            logger.info("Quick7751SDK验签失败,生成的签名md5SignLocal:"+md5SignLocal +"，传入的签名md5Sign:"+md5Sign );
            sender.sendAndDisconnect("FAIL");
            return;
        }
        // 解析nt_data中的数据
        String callbackKey = quick7751SDK.callbackKey;
        if (null == callbackKey) {
            logger.info("Quick7751SDK参数callbackKey未配置");
            sender.sendAndDisconnect("FAIL");
            return;
        }
        String nt_data_xml = QuickSDKUtil.decode(nt_data, callbackKey);
        //将xml的数据转化为map
        nt_data_xml = nt_data_xml.replace("<quick_message>","").replace("</quick_message>","");
        Map nt_data_map = XMLUtil.doXMLParse(nt_data_xml);
        logger.info("回调参数nt_data解析的xml为："+ nt_data_xml+ "，转为map："+nt_data_map);

        if (null == nt_data_map) {
            logger.info("参数 nt_data 解析失败!");
            sender.sendAndDisconnect("FAIL");
            return;
        }
        Quick7751SDKNotifyData notifyData = JSON.parseObject(JSON.toJSONString(nt_data_map), Quick7751SDKNotifyData.class );

        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.out_order_no, notifyData.out_order_no);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:game_orderid:" + notifyData.out_order_no);
            sender.sendAndDisconnect("FAIL");
            return;
        } else {
            redis.expire(notifyData.out_order_no, 30);
        }

        // QuickSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(nt_data_map.toString()), QuickSDKNotifyData.class);
        OrderPo paytradeByDb = OrderPo.findByOrdId(notifyData.out_order_no);
        if (null == paytradeByDb) {// 订单号不存在
            logger.info("game_order:"+notifyData.out_order_no+" 不存在");
            sender.sendAndDisconnect("FAIL");
            return;
        }
        if(paytradeByDb.getStatus()>=2){
            logger.info("订单game_order:"+notifyData.out_order_no+" 已经处理");
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
        paytradeByDb.setPlatform("Quick7751SDK");
        paytradeByDb.setStatus(2);
        paytradeByDb.setThird_trade_no(notifyData.order_no);
        paytradeByDb.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(paytradeByDb, "Quick7751SDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }
}

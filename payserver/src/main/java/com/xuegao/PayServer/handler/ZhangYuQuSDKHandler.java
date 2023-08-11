package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.Base64Utils;
import com.xuegao.PayServer.vo.ZhangYuQuSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.Base64;

public class ZhangYuQuSDKHandler {
    static Logger logger = Logger.getLogger(ZhangYuQuSDKHandler.class);


    @Cmd("/p/ZhangYuQuPayNotify/cb")
    public void ZhangYuQuPayNotify(User sender, HttpRequestJSONObject params) throws IOException {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        ZhangYuQuSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), ZhangYuQuSDKNotifyData.class);
        logger.info("ZhangYuQuSDKNotifyData:" + notifyData.toString());

        String ext = new String(Base64.getDecoder().decode(notifyData.ext));
        String[] split = ext.split("-");
        String channel_code = split[0];
        String orderId = split[1];
        logger.info("ext解析内容： channel_code =" + channel_code + "orderId ="+ orderId);

        String cause="";
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(orderId, orderId);
        if (isSaveSuc == 0L) {
            cause = "同一订单多次请求,无须重复处理:order_id:"+orderId;
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getStatus(-1,cause));
            return;
        } else {
            redis.expire(orderId, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(orderId);
        if (payTrade == null) {
            cause = "订单号:"+orderId+",不存在";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getStatus(-2,cause));
            return;
        }
        if (payTrade.getStatus() >= 2) {
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getStatus(1,cause));
            return;
        }
        Constants.PlatformOption.ZhangYuQuSDK pfConfig = GlobalCache.getPfConfig("ZhangYuQuSDK");
        String recharge_token = pfConfig.apps.get(channel_code).recharge_token;
        //检验recharge_token是否配置
        if (recharge_token==null) {
            cause="recharge_token未配置";
            logger.info(cause);
            sender.sendAndDisconnect(MsgFactory.getStatus(-3,cause));
            return;
        }
        //验签
        String baseStr = "uid="+ notifyData.uid +"&role_id=" + notifyData.role_id +"&gkey="+ notifyData.gkey +
                         "&skey=" + notifyData.skey + "&order_id=" + notifyData.order_id + "&coins="+ notifyData.coins +
                         "&moneys=" + notifyData.moneys + "&ext="+ notifyData.ext +"&time=" + notifyData.time + recharge_token;
        logger.info("baseStr:"+baseStr);
        baseStr =  AdHandler.getSHA256StrJava(baseStr);
        if (!notifyData.sign.equals(baseStr)) {
            logger.info("验签失败!掌娱趣sign="+notifyData.sign+"==============="+"服务端生成签名:"+baseStr);
            sender.sendAndDisconnect(MsgFactory.getStatus(-4,cause));
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.parseFloat(notifyData.moneys)/100)) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配"+"amount:"+Float.parseFloat(notifyData.moneys)/100);
            sender.sendAndDisconnect(MsgFactory.getStatus(-5,cause));
            return;
        }
        //成功
        sender.sendAndDisconnect(MsgFactory.getStatus(1,"交易成功"));
        payTrade.setThird_trade_no(notifyData.order_id);
        payTrade.setPlatform("ZhangYuQuSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "ZhangYuQuSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }
}

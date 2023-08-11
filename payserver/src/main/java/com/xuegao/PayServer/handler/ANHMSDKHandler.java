package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.ANHMResultVo;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.vo.ANHMSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

public class ANHMSDKHandler {

    static Logger logger = Logger.getLogger(ANHMSDKHandler.class);


    @Cmd("/p/ANHMSDKPayNotify/cb")
    public void ANHMSDKPayNotify(User sender, HttpRequestJSONObject params){
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        ANHMSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), ANHMSDKNotifyData.class);
        logger.info("ANHMSDKPayNotify:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.ANHMSDK anhmsdk = GlobalCache.getPfConfig("ANHMSDK");
        //检验支付参数gid是否配置
        if(null==anhmsdk.apps.get(notifyData.gid)){
            String cause ="安纳海姆平台 参数未配置,gid="+ notifyData.gid;
            logger.info(cause);
            ANHMResultVo successVo= new ANHMResultVo("发货失败",300);
            String jsonStr = JSON.toJSONString(successVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        //获取gid的参数payKey
        String payKey = anhmsdk.apps.get(notifyData.gid).PayKey;
        if (null==payKey) {
            logger.info("payKey不匹配,payKey="+payKey);
            ANHMResultVo successVo= new ANHMResultVo("发货失败",300);
            String jsonStr = JSON.toJSONString(successVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        String cause = "";
        long isSaveSuc = redis.STRINGS.setnx(notifyData.game_other, notifyData.game_other);
        if (isSaveSuc == 0L) {
            cause = "同一订单多次请求,无须重复处理:partnerOrder:" + notifyData.game_other;
            logger.info(cause);
            ANHMResultVo successVo= new ANHMResultVo("发货失败",300);
            String jsonStr = JSON.toJSONString(successVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        } else {
            redis.expire(notifyData.game_other, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.game_other);
        if (payTrade == null) {
            cause = "订单号:"+notifyData.game_other+",不存在";
            logger.info(cause);
            ANHMResultVo successVo= new ANHMResultVo("发货失败",300);
            String jsonStr = JSON.toJSONString(successVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            cause = "订单已经处理完毕,无须重复处理";
            logger.info(cause);
            ANHMResultVo successVo= new ANHMResultVo("发货失败",300);
            String jsonStr = JSON.toJSONString(successVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        //验签
        StringBuffer sb = new StringBuffer();
        sb.append("order_num=").append(notifyData.order_num).append("&partnerid=").append(notifyData.partnerid).append("&user_id=")
                .append(notifyData.user_id).append("&gid=").append(notifyData.gid).append("&sid=")
                .append(notifyData.sid).append("&money=").append(notifyData.money).append("&order_ip=")
                .append(notifyData.order_ip).append("&sandbox=").append(notifyData.sandbox).append("&game_other=")
                .append(notifyData.game_other).append("&payts=").append(notifyData.payts).append("&pay_key=").append(payKey);
        String signStr = HelperEncrypt.encryptionMD5(sb.toString()).toLowerCase();
        if (!notifyData.sign.equals(signStr)) {
            logger.info("验签失败!安纳海姆 sign="+notifyData.sign+"==============="+"32位小写后signStr:"+signStr);
            ANHMResultVo successVo= new ANHMResultVo("验签失败,发货失败",300);
            String jsonStr = JSON.toJSONString(successVo);
            sender.sendAndDisconnect(jsonStr);
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(notifyData.money.floatValue())) {
            logger.info("订单金额:"+payTrade.getPay_price()+"不匹配"+"money:"+notifyData.money.floatValue());
            ANHMResultVo successVo= new ANHMResultVo("发货失败",300);
            String jsonStr = JSON.toJSONString(successVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        //成功
        ANHMResultVo successVo= new ANHMResultVo("发货成功",200);
        String jsonStr = JSON.toJSONString(successVo);
        sender.sendAndDisconnect(jsonStr);
        payTrade.setThird_trade_no(notifyData.order_num);
        payTrade.setPlatform("ANHMSDK");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "ANHMSDK");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }
}

package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.YwResultVo;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.util.wxpay.DoubleCompare;
import com.xuegao.PayServer.util.wxpay.NumUtil;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.redis.JedisUtil;
import org.apache.log4j.Logger;

public class YWanH5Handler {

    static Logger logger = Logger.getLogger(YWanH5Handler.class);

    @Cmd("/p/YWanH5PayNotify/cb")
    public void YWanH5PayNotify(User sender, JSONObject params) {
        String app_order_id = params.getString("app_order_id");
        String app_role_id = params.getString("app_role_id");
        String is_sandbox = params.getString("is_sandbox");//1是测试订单，0是正常订单
        String j_game_id = params.getString("j_game_id");
        String product_id= params.getString("product_id");
        String order_id = params.getString("order_id");//悦玩的订单ID 注意：此参数不参与签名
        String server_id = params.getString("server_id");
        String total_fee = params.getString("total_fee");// 单位为分
        String sign = params.getString("sign");
        String user_id = params.getString("user_id");
        String time = params.getString("time");//单位：秒

        Constants.PlatformOption.YWanH5 yWanConfig = GlobalCache.getPfConfig("YWanH5");
        if(null==yWanConfig){
            String cause ="YWanH5:参数未配置";
            logger.info(cause);
            YwResultVo failVo= new YwResultVo("",cause,0);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        if(null==yWanConfig.apps.get(j_game_id)){
            String cause ="j_game_id未配置:"+j_game_id;
            logger.info(cause);
            YwResultVo failVo= new YwResultVo("",cause,0);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        if(null==yWanConfig.apps.get(j_game_id).j_pay_sign){
            String cause ="pay_sign未配置:"+yWanConfig.apps.get(j_game_id).j_pay_sign;
            logger.info(cause);
            YwResultVo failVo= new YwResultVo("",cause,0);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }

        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(app_order_id, app_order_id);
        if (isSaveSuc == 0L) {
            String cause ="同一订单多次请求,无须重复处理:app_order_id:" + app_order_id;
            logger.info(cause);
            YwResultVo failVo= new YwResultVo("",cause,1);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        } else {
            redis.expire(app_order_id, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(app_order_id);
        if (payTrade == null) {
            String cause="订单号:" + app_order_id + ",不存在";
            logger.info(cause);
            YwResultVo failVo= new YwResultVo("",cause,0);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            String cause="订单已经处理完毕,无须重复处理";
            logger.info(cause);
            YwResultVo successVo= new YwResultVo("",cause,1);
            String jsonStr = JSON.toJSONString(successVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        payTrade.setExt("1".equals(is_sandbox)?"沙盒支付":"正式支付");
        StringBuffer  preSignStr =new StringBuffer();
        preSignStr.append("app_order_id=").append(app_order_id).append("&app_role_id=").append(app_role_id)
                .append("&is_sandbox=").append(is_sandbox).append("&j_game_id=").append(j_game_id)
                .append("&j_pay_sign=").append(yWanConfig.apps.get(j_game_id).j_pay_sign)
                .append("&product_id=").append(product_id).append("&server_id=").append(server_id)
                .append("&time=").append(time).append("&total_fee=").append(total_fee).append("&user_id=").append(user_id);
        logger.info("悦玩H5待签名字符串="+preSignStr.toString());
        String signStr = HelperEncrypt.encryptionMD5(preSignStr.toString()).toLowerCase();
        //验签
        if(!sign.equalsIgnoreCase(signStr)){
            logger.info("悦玩H5 sign="+sign+"==============="+"32位小写后signStr:"+signStr);
            String cause="验签失败";
            YwResultVo failVo= new YwResultVo("",cause,0);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        //校验金额
        String  amount =payTrade.getPay_price().toString();
        String  fen = NumUtil.yuanToFen(amount);// 实际游戏订单金额
        int rs = DoubleCompare.compareNum(total_fee,fen);// total_fee 实际支付的金额
        if (rs > 1) {// rs =0 相等 1 第一个数字大 2 第二个数字大
            logger.info("订单金额:"+amount+"不匹配"+"实际支付金额:"+Float.valueOf(total_fee));
            String cause="订单金额和回调金额不一致";
            YwResultVo failVo= new YwResultVo("",cause,0);
            String jsonStr = JSON.toJSONString(failVo);
            sender.sendAndDisconnect(jsonStr);
            return;
        }
        //成功
        YwResultVo successVo= new YwResultVo("","success",1);
        String jsonStr = JSON.toJSONString(successVo);
        sender.sendAndDisconnect(jsonStr);
        payTrade.setThird_trade_no(order_id);
        //订单金额以实际接受到返回金额为准 【悦玩返回分单位】
        payTrade.setPay_price(Float.valueOf(total_fee)/100F);
        payTrade.setUnits("CNY");
        payTrade.setPlatform("YWanH5");
        payTrade.setStatus(2);//2.收到回调验证成功
        payTrade.updateToDB();
        //发钻石
        boolean  isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "YWanH5");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
    }
}

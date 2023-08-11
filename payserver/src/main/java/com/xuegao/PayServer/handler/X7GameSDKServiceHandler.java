package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.HuaWeiResultVo;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperJson;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.util.RSAUtils;
import com.xuegao.PayServer.vo.X7GameSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import io.netty.util.CharsetUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import static com.xuegao.PayServer.util.VivoSignUtils.createLinkString;

public class X7GameSDKServiceHandler {
    static Logger logger = Logger.getLogger(X7GameSDKServiceHandler.class);

    /**
     * 充值加签
     * @param sender
     * @param params
     */
    @Cmd("/p/X7GameSDKAddSign/cb")
    public void X7GameSDKAddSign(User sender, JSONObject params) {

        try {
            if (params.isEmpty()) {
                sender.sendAndDisconnect(MsgFactory.getErrorMsg(1, "传递参数为空"));
                return;
            }
            Constants.PlatformOption.X7GameSDK x7GameSDK = GlobalCache.getPfConfig("X7GameSDK");
            //待签名字符串
            String channel_code = params.getString("channelCode");
            String game_area = params.getString("game_area"); //角色所在的游戏区服
            String game_orderid = params.getString("game_orderid"); //游戏订单号
            String game_price = params.getString("game_price"); //商品的单位
            String game_uid = params.getString("game_guid"); //用户的唯一标识
            String subject = params.getString("subject"); //道具简介

            OrderPo payTrade = OrderPo.findByOrdId(game_orderid);
            if (payTrade==null) {
                logger.error("订单号:" + game_orderid + ",不存在");
                sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单号:" + game_orderid + ",不存在"));
                return;
            }
            //生成签名
            Map<String,String> map = new HashMap<>();
            map.put("game_area",game_area);
            map.put("game_orderid",game_orderid);
            map.put("game_price",game_price);
            map.put("game_guid",game_uid);
            map.put("subject",subject);
            //game_area=区服信息& &game_guid = 44&game_orderid=abcd123456789&game_price=60.00&subject=60.00_60 元宝+【游戏公钥】
            String StrA = createLinkString(map,true,false);
            String publicKey = x7GameSDK.apps.get(channel_code).publicKey;
            if ( publicKey == null) {
               logger.info("小七公钥 publicKey 未配置" +  publicKey);
            }
            String sign = StrA + publicKey;
            String game_sign = MD5Util.md5(sign);
            logger.info("签名时的字符串StrA："+StrA + "生成的签名为："+game_sign);
            //返回签名给客户端
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ret", 0);
            jsonObject.put("game_sign",game_sign);
            jsonObject.put("msg", "success");
            sender.sendAndDisconnect(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * 支付回调
     * @param sender
     * @param params
     */
    @Cmd("/p/X7GameSDKPayNotify/cb")
    public void X7GameSDKPayNotify(User sender, HttpRequestJSONObject params) throws Exception {

        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        X7GameSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), X7GameSDKNotifyData.class);
        logger.info("X7GameSDKNotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.X7GameSDK x7GameSDK = GlobalCache.getPfConfig("X7GameSDK");
        String cause = "";
        if (null == x7GameSDK) {
            cause = "X7GameSDK:参数未配置";
            logger.info(cause);
            sender.sendAndDisconnect(cause);
            return;
        }


        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.game_orderid, notifyData.game_orderid);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:game_orderid:" + notifyData.game_orderid);
            sender.sendAndDisconnect("success");
            return;
        } else {
            redis.expire(notifyData.game_orderid, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.game_orderid);
        if (payTrade == null) {
            cause = "game_orderid error";
            sender.sendAndDisconnect(cause);
            logger.error("订单号:" + notifyData.game_orderid + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            cause = "success";
            sender.sendAndDisconnect(cause);
            return;
        }

        //解析encryp_data
        String publicKey  = x7GameSDK.apps.get(payTrade.getChannelCode()).publicKey;
        byte[] encryp_data_byte = RSAUtils.decryptByPublicKey(Base64.decodeBase64(notifyData.encryp_data.getBytes()),publicKey);
        String encryp_data_str = new String(encryp_data_byte);
        //game_orderid=2018182571972272&guid =1 219663&pay_price=1.00
        Map<String ,String> encryp_data = HelperJson.URLRequest(encryp_data_str);
        if (encryp_data.get("game_orderid") == null || encryp_data.get("guid") == null || encryp_data.get("pay_price") == null) {
            cause = "encryp_data解码/解析失败";
            logger.info(cause);
            sender.sendAndDisconnect("encryp_data_decrypt_failed");
            return;
        }
        if (!encryp_data.get("game_orderid").equals(notifyData.game_orderid)) {
            cause = "返回订单与encryp中订单不匹配";
            logger.info(cause);
            sender.sendAndDisconnect("game_orderid error");
            return;
        }

        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(encryp_data.get("pay_price")))) {
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "回调金额:" + encryp_data.get("pay_price"));
            cause = "订单金额与回调金额不匹配";
            sender.sendAndDisconnect(cause);
            return;
        }
        //校验签名
        Map map = JSON.parseObject(JSON.toJSONString(notifyData));
        map.remove("sign_data");
        String strA = createLinkString(map,true,false);
        PublicKey pk = RSAUtils.loadPublicKeyByStr(publicKey);
        if (RSAUtils.verifySign(strA,notifyData.sign_data,pk)) {
            //验签通过
            sender.sendAndDisconnect("success");
            payTrade.setThird_trade_no(notifyData.xiao7_goid);
            payTrade.setPlatform("X7GameSDK");
            payTrade.setStatus(2);
            payTrade.updateToDB();
            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "X7GameSDK");
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {
            cause = "验签失败";
            logger.info(cause);
            sender.sendAndDisconnect("sign_data_verify_failed");
        }



    }


}

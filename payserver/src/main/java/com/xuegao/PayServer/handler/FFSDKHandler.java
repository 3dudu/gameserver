package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.po.UserPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperJson;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.util.RSAUtils;
import com.xuegao.PayServer.util.wxpay.WxPayCommonUtil;
import com.xuegao.PayServer.vo.FFSDKGift;
import com.xuegao.PayServer.vo.FFSDKNotifyData;
import com.xuegao.PayServer.vo.X7GameSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.StringUtil;
import io.netty.util.CharsetUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.security.PublicKey;
import java.util.*;

import static com.xuegao.PayServer.util.VivoSignUtils.createLinkString;

public class FFSDKHandler {
    static Logger logger = Logger.getLogger(FFSDKHandler.class);

    /**
     * 支付回调
     * @param sender
     * @param params
     */
    @Cmd("/p/ffsdknotify/cb")
    public void ffsdkNotify(User sender, HttpRequestJSONObject params) throws Exception {

        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        FFSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), FFSDKNotifyData.class);
        logger.info("ffsdknotifyData:"+notifyData.toString());
        //检验参数是否匹配
        Constants.PlatformOption.FFSDK ffsdk = GlobalCache.getPfConfig("FFSDK");
        String cause = "";
        if (null == ffsdk) {
            cause = "fsdk:参数未配置";
            logger.info(cause);
            sender.sendAndDisconnect(cause);
            return;
        }
        SortedMap<String, Object> parameterMap = new TreeMap<String, Object>();
        parameterMap.put("orderid",notifyData.orderid);
        parameterMap.put("cp_orderid",notifyData.cp_orderid);
        parameterMap.put("product_id",notifyData.product_id);
        parameterMap.put("product_type",notifyData.product_type);
        parameterMap.put("money",notifyData.money);
        parameterMap.put("currency",notifyData.currency);
        parameterMap.put("pay_usd",notifyData.pay_usd);
        parameterMap.put("pay_type",notifyData.pay_type);
        parameterMap.put("all_game_gold",notifyData.all_game_gold);
        parameterMap.put("game_gold",notifyData.game_gold);
        parameterMap.put("extra_game_gold",notifyData.extra_game_gold);
        parameterMap.put("uid",notifyData.uid);
        parameterMap.put("role_id",notifyData.role_id);
        parameterMap.put("role_name",notifyData.role_name);
        parameterMap.put("server_id",notifyData.server_id);
        parameterMap.put("pay_date",notifyData.pay_date);
        parameterMap.put("ext1",notifyData.ext1);
        parameterMap.put("ext2",notifyData.ext2);
        parameterMap.put("ext3",notifyData.ext3);
        parameterMap.put("os",notifyData.os);

        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.cp_orderid, notifyData.cp_orderid);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:game_orderid:" + notifyData.cp_orderid);
            sender.sendAndDisconnect("success");
            return;
        } else {
            redis.expire(notifyData.cp_orderid, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.cp_orderid);

        if (payTrade == null) {
            cause = "game_orderid error";
            sender.sendAndDisconnect(cause);
            logger.error("订单号:" + notifyData.cp_orderid + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            cause = "success";
            sender.sendAndDisconnect(cause);
            return;
        }

        String paykey  = ffsdk.apps.get(payTrade.getChannelCode()).paykey;
        String sign = createSign("UTF-8", parameterMap,paykey);

        //game_orderid=2018182571972272&guid =1 219663&pay_price=1.00


        logger.info(sign+","+notifyData.sign);
        //校验签名
        if (sign.equals(notifyData.sign)) {
            //验签通过
            sender.sendAndDisconnect("SUCCESS");
            payTrade.setThird_trade_no(notifyData.orderid);
            payTrade.setPlatform("FFSDK");
            payTrade.setStatus(2);

            //发钻石
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "FFSDK");
            payTrade.setPay_price(Float.valueOf(notifyData.money));
            payTrade.updateToDB();
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        } else {
            cause = "验签失败";
            logger.info(cause);
            sender.sendAndDisconnect("sign_data_verify_failed");
        }


    }
    /**
     * 礼包
     * @param sender
     * @param params
     */
    @Cmd("/p/ffsdk/gift")
    public void gift(User sender, HttpRequestJSONObject params) throws Exception {

        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        FFSDKGift ffgift = JSONObject.toJavaObject(JSON.parseObject(json), FFSDKGift.class);
        logger.info("ffsdknotifyData:"+ffgift.toString());
        //检验参数是否匹配
        Constants.PlatformOption.FFSDK ffsdk = GlobalCache.getPfConfig("FFSDK");
        String cause = "";
        if (null == ffsdk) {
            cause = "fsdk:参数未配置";
            logger.info(cause);
            sender.sendAndDisconnect(cause);
            return;
        }

        String sign = Misc.md5(ffgift.cp_gift+ (StringUtil.isEmpty(ffgift.ext)?"":ffgift.ext) +ffgift.roleid+ ffgift.serverid+ ffgift.time+ffgift.uid+ffsdk.giftkey);

        if(!sign.equals(ffgift.sign)){
            logger.info("sign不一致");
            sender.sendAndDisconnect("SUCCESS");
            return;
        }
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(ffgift);
        sender.sendAndDisconnect("SUCCESS");


    }
    // 生成签名
    public static String createSign(String characterEncoding, SortedMap<String, Object> parameters, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !v.equals(0) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(API_KEY);
        logger.info("createSign:"+sb.toString());
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding);
        return sign;
    }

}

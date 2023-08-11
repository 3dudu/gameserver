package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.AdPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.util.wxpay.WxOrderQueryUtil;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.util.StringUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;

public class AdHandler {
    public static Logger logger = Logger.getLogger(AdHandler.class);

    @Cmd("/ad/CSJ/cb")
    public void CSJ(User sender, JSONObject params) {
        String sign = params.getString("sign");
        String user_id = params.getString("user_id");
        String trans_id = params.getString("trans_id");
        String extra = params.getString("extra");
        JSONObject oriJson = JSONObject.parseObject(extra);
        Integer sid = oriJson.getInteger("sid");
        Integer pid = oriJson.getInteger("pid");
        String reward_name = oriJson.getString("reward_name");
        String advertisement_id = oriJson.getString("advertisement_id");
        logger.info("param:" + params.toJSONString());
        Constants.PlatformOption.CSJSDK CSJsdk = GlobalCache.getPfConfig("CSJSDK");
        String appSecurityKey = CSJsdk.apps.get(advertisement_id);
        logger.info("signkey:" + appSecurityKey);
        String vSign = "";
        JSONObject result = new JSONObject();
        result.put("isValid", false);
        String str = appSecurityKey + ":" + trans_id;
        vSign = getSHA256StrJava(str);
        logger.info("vSignkey:" + vSign);
        if (!StringUtil.isEmpty(vSign) & vSign.equals(sign)) {
            AdPo adPo = new AdPo();
            adPo.setCreate_time(System.currentTimeMillis());
            adPo.setPid(pid);
            adPo.setPlatform("CSJ");
            adPo.setReward_name(reward_name);
            adPo.setUid(user_id);
            adPo.setSid(sid);
            adPo.setTrans_id(trans_id);
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(adPo);
            result.put("isValid", true);
        }
        sender.sendAndDisconnect(result.toJSONString());
    }

    @Cmd("/ad/YWANCSJ/cb")
    public void YWANCSJ(User sender, JSONObject params) {
        String sign = params.getString("sign");
        String user_id = params.getString("user_id");
        Integer app_role_id = params.getInteger("app_role_id");
        String app_role_name = params.getString("app_role_name");
        Integer server_id = params.getInteger("server_id");
        String server_name = params.getString("server_name");
        String channel_order_id = params.getString("channel_order_id");
        String code_id =params.getString("code_id");
        String j_game_id = params.getString("j_game_id");

        logger.info("param:" + params.toJSONString());

        String vSign = "app_role_id="+ URLEncoder.encode(app_role_id.toString()) + "&app_role_name="+URLEncoder.encode(app_role_name.trim()) +
                "&channel_order_id="+URLEncoder.encode(channel_order_id.trim())+"&code_id=" + URLEncoder.encode(code_id.trim()) + "&j_game_id="+
                URLEncoder.encode(j_game_id.trim())+"&server_id="+URLEncoder.encode(server_id.toString()) + "&server_name="
                +URLEncoder.encode(server_name.trim()) + "&user_id="+URLEncoder.encode(user_id.trim());
        logger.info("悦玩穿山甲签名原字符串："+vSign);
        vSign = MD5Util.md5(vSign).toLowerCase(Locale.ROOT);

        JSONObject result = new JSONObject();
        result.put("ret", 0);
        result.put("msg","fai");
        result.put("content",new ArrayList<>());

        logger.info("vSignkey:" + vSign);
        if (!StringUtil.isEmpty(vSign) & vSign.equals(sign)) {
            AdPo adPo = new AdPo();
            adPo.setCreate_time(System.currentTimeMillis());
            adPo.setPid(app_role_id);
            adPo.setPlatform("YWANCSJ");
            adPo.setReward_name(code_id);
            adPo.setUid(user_id);
            adPo.setSid(server_id);
            adPo.setTrans_id(channel_order_id);
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(adPo);
            result.put("ret", 1);
            result.put("msg","");
            JSONObject content = new JSONObject();
            content.put("app_order_id",channel_order_id);
            result.put("content",content);
        }
        sender.sendAndDisconnect(result.toJSONString());
    }

    @Cmd("/ad/ylh/cb")
    public void ylh(User sender, JSONObject params) {
        String sign = params.getString("sig");
        String user_id = params.getString("userid");
        String trans_id = params.getString("transid");
        String extra = params.getString("extrainfo");
        JSONObject oriJson = JSONObject.parseObject(extra);
        Integer sid = oriJson.getInteger("sid");
        Integer pid = oriJson.getInteger("pid");
        String reward_name = oriJson.getString("reward_name");
        String advertisement_id = params.getString("pid");
        logger.info("param:" + params.toJSONString());
        Constants.PlatformOption.YLHSDK YLHsdk = GlobalCache.getPfConfig("YLHSDK");
        String appSecurityKey = YLHsdk.apps.get(advertisement_id);
        logger.info("signkey:" + appSecurityKey);
        String vSign = "";
        JSONObject result = new JSONObject();
        result.put("isValid", false);
        String str = trans_id + ":" + appSecurityKey;
        vSign = getSHA256StrJava(str);
        logger.info("vSignkey:" + vSign);
        if (!StringUtil.isEmpty(vSign) & vSign.equals(sign)) {
            AdPo adPo = new AdPo();
            adPo.setCreate_time(System.currentTimeMillis());
            adPo.setPid(pid);
            adPo.setPlatform("YLH");
            adPo.setReward_name(reward_name);
            adPo.setUid(user_id);
            adPo.setSid(sid);
            adPo.setTrans_id(trans_id);
            boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(adPo);
            logger.info("isSychnSuccess[" + isSychnSuccess + "]");
            result.put("isValid", true);
        }
        sender.sendAndDisconnect(result.toJSONString());
    }

    /***
     * 优点手q看视频广告得奖励
     * @param sender
     * @param params
     */
    @Cmd("/ad/YDSQ/cb")
    public void YDSQ(User sender, JSONObject params) {
        logger.info("param:" + params.toJSONString());
        JSONObject result = new JSONObject();
        result.put("isValid", true);

        AdPo adPo = new AdPo();
        try {
            adPo.setCreate_time(System.currentTimeMillis());
            adPo.setPid(Integer.valueOf(params.getString("pid")));
            adPo.setPlatform("YDSQ");
            adPo.setReward_name(params.getString("reward_name"));
            adPo.setUid(params.getString("user_id"));
            adPo.setSid(Integer.valueOf(params.getString("sid")));
            adPo.setTrans_id(params.getString("trans_id"));
        } catch (Exception e) {
            logger.info("传入参数异常",e);
            result.put("isValid", false);
            sender.sendAndDisconnect(result.toJSONString());
            return;
        }
        boolean delivery = PaymentToDeliveryUnified.delivery(adPo);
        sender.sendAndDisconnect(result.toJSONString());
    }

    /**
     * 　　* 利用java原生的摘要实现SHA256加密
     * 　　* @param str 加密后的报文
     * 　　* @return
     */
    public static String getSHA256StrJava(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
     * 　　* 将byte转为16进制
     * 　　* @param bytes
     * 　　* @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

}

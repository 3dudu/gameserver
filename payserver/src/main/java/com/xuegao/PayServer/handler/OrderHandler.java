package com.xuegao.PayServer.handler;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.util.NumberUtil;
import com.xuegao.PayServer.logs.ThinKingData;
import com.xuegao.PayServer.po.*;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.vo.SMSDKNotifyData;
import com.xuegao.core.util.Misc;
import com.xuegao.core.util.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.Constants.PlatformOption.AnySdk;
import com.xuegao.PayServer.data.Constants.PlatformOption.GoogleInPay;
import com.xuegao.PayServer.data.Constants.PlatformOption.XGSDK;
import com.xuegao.PayServer.data.Constants.PlatformOption.XPSDK;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.slaveServer.DataProto.Q_TopUp;
import com.xuegao.PayServer.slaveServer.GlobalSlaveManage;
import com.xuegao.PayServer.slaveServer.ScConnector;
import com.xuegao.PayServer.slaveServer.SlaveServerVo;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.util.HelperJson;
import com.xuegao.PayServer.util.HelperRandom;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.util.RSAUtils;
import com.xuegao.PayServer.vo.GoogleinPayNotifyData;
import com.xuegao.PayServer.vo.XGSDKNotifyData;
import com.xuegao.PayServer.vo.XPSDKNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;

import io.netty.util.CharsetUtil;
import sun.security.rsa.RSASignature;

import javax.imageio.stream.ImageInputStream;

public class OrderHandler {
    static Logger logger = Logger.getLogger(OrderHandler.class);

    /**
     * 统一下单
     * get:/api/open/buy?s=1&p=1001&idx=1&c
     * serverId/playerId/proIdx/扩展参数<不能有特殊符号只支持下划线>/money
     * post:
     */
    @Cmd("/p/buy")
    public void buy(User sender, JSONObject params) {

        String serverId = params.getString("s");
        String playerId = params.getString("p");
        String proIdx = params.getString("idx");
        String ext = params.getString("c");
        String money = params.getString("money");
        String userId = params.getString("u");
        String platform = params.getString("wc");
        String order_type = params.getString("aTest");
        String channel = params.getString("chl");// 客户端在中心配置获取 然后传入到这边
        String channelCode = params.getString("channelCode"); //channelcode

        if (!StringUtils.isNumeric(serverId) || !StringUtils.isNumeric(playerId) || !StringUtils.isNumeric(proIdx) || !StringUtils.isNumeric(userId)) {
            logger.info("参数为必须为数字serverId:" + serverId + ",playerId:" + playerId + ",proIdx:" + proIdx + ",userId:" + userId);
            JSONObject rs = MsgFactory.getErrorMsg(-1, "参数错误");
            sender.sendAndDisconnect(rs);
            return;
        }

        //如果客户端或GM代充传递的channelCode为空,就从UserPo表里取
        if (StringUtils.isBlank(channelCode)) {
            UserPo userPo = UserPo.findByUserId(Long.valueOf(userId));
            if (null == userPo) {
                logger.info("该用户不存在:" + userId);
                JSONObject rs = MsgFactory.getDefaultErrorMsg("该用户不存在:" + userId);
                sender.sendAndDisconnect(rs);
                return;
            }
            logger.info("UserPo:" + userPo.toString());
            channelCode = userPo.getChannelCode();
        }

        String ordId = HelperRandom.randSecendId(System.currentTimeMillis(), 8, playerId.hashCode());
        // 入库
        OrderPo ordPO = OrderPo.findByOrdId(ordId);
        while (null != ordPO) {// 说明存在重复订单号
            // 重新生成订单号
            ordId = HelperRandom.randSecendId(System.currentTimeMillis(), 8, playerId.hashCode());
            ordPO = OrderPo.findByOrdId(ordId);
        }
        //  直接入库
        ordPO = new OrderPo();
        ordPO.setOrder_id(ordId);
        ordPO.setOrder_type(order_type);
        ordPO.setChannelCode(channelCode);
        ordPO.setPid(Integer.parseInt(playerId));
        ordPO.setSid(Integer.parseInt(serverId));
        ordPO.setPro_idx(Integer.parseInt(proIdx));
        ordPO.setUser_id(Long.parseLong(userId));
        ordPO.setChannel(channel);// 客户端传入
        if (!StringUtils.isEmpty(money)) {
            float pay_price = Float.parseFloat(money);
            ordPO.setPay_price(pay_price);
        }
        ordPO.setExt(ext);
        ordPO.setStatus(0);
        ordPO.setCreate_time(System.currentTimeMillis());
        ordPO.insertToDB();
        //苹果档位映射
        String product_id = "";
        String callBackUrl = "";
        if (null!=platform&&platform.equals("100")){
            Constants.PlatformOption.IOSPay iosPay = GlobalCache.getPfConfig("IOSPay");
            if (null!=iosPay) {
                product_id=iosPay.apps.get(channelCode).products.get(proIdx);
                product_id=product_id.substring(product_id.lastIndexOf("_")+1);
                if (NumberUtil.isNumber(product_id)) {
                    ordPO.setPro_idx(Integer.valueOf(product_id));
                } else {
                    Matcher m = Pattern.compile("[^0-9]").matcher(product_id);
                    ordPO.setPro_idx(Integer.valueOf(m.replaceAll("").trim()));
                }
            }
        }
        //谷歌充值档位映射
        if (null!=platform&&platform.equals("3")){
            Constants.PlatformOption.GoogleInPay googleInPay = GlobalCache.getPfConfig("GoogleInPay");
            if (null!=googleInPay) {
                product_id=googleInPay.apps.get(channelCode).products.get(proIdx);
                product_id=product_id.substring(product_id.lastIndexOf("_")+1);
                String[] strs = product_id.split("-");
                if (strs.length>1) {
                    if (NumberUtil.isNumber(strs[1])) {
                        ordPO.setPro_idx(Integer.valueOf(product_id));
                    }else {
                        Matcher m = Pattern.compile("[^0-9]").matcher(product_id);
                        ordPO.setPro_idx(Integer.valueOf(m.replaceAll("").trim()));
                    }
                }
            }
        }
        //华为充值档位映射
        if(null != platform && platform.equals("9")){
            Constants.PlatformOption.HuaWeiSDK huaWeiSDK = GlobalCache.getPfConfig("HuaWeiSDK");
            if (null !=huaWeiSDK ) {
                Map<String, String> products = huaWeiSDK.apps.get(channelCode).products;
                ordPO.setExt(huaWeiSDK.apps.get(channelCode).payPublicKey);
                if (products.isEmpty()) {
                    ordPO.setPro_idx(Integer.valueOf(proIdx));
                }else {
                    product_id = products.get(proIdx);
                    String  productId =product_id.substring(product_id.lastIndexOf("_")+1);
                    ordPO.setPro_idx(Integer.valueOf(productId));
                }
            }
        }
        //赞钛充值回调
        if (null!=platform&&platform.equals("61")){
            Constants.PlatformOption.ZTSDK ztsdk = GlobalCache.getPfConfig("ZTSDK");
            if (null!=ztsdk) {
                callBackUrl = ztsdk.apps.get(channelCode).callBackUrl;
            }

        }
        //掌娱趣
        if (null!=platform&&platform.equals("317")){
            Constants.PlatformOption.ZhangYuQuSDK zyq = GlobalCache.getPfConfig("ZhangYuQuSDK");
            if (null!=zyq) {
                callBackUrl = zyq.apps.get(channelCode).call_back_url;
            }
        }
        //gosu
        if (null!=platform&&platform.equals("77")){
            Constants.PlatformOption.GOSUSDK gosusdk = GlobalCache.getPfConfig("GOSUSDK");
            if (null !=gosusdk ) {
                Map<String, String> products = gosusdk.apps.get(channelCode).products;
                if (products.isEmpty()) {
                    ordPO.setPro_idx(Integer.valueOf(proIdx));
                }else {
                    product_id = products.get(proIdx);
                }
            }
        }
        //玩心ios充值档位映射
        if(null != platform && platform.equals("62")){
            Constants.PlatformOption.WanXinH5 wanXinH5 = GlobalCache.getPfConfig("WanXinH5");
            if (null !=wanXinH5 ) {
                Map<String, String> products = wanXinH5.apps.get(channelCode).products;
                if (products.isEmpty()) {
                    ordPO.setPro_idx(Integer.valueOf(proIdx));
                }else {
                    product_id = products.get(proIdx);
                    ordPO.setPro_idx(Integer.valueOf(product_id));
                }
            }
        }
        //quick7751 ios充值档位映射
        if(null != platform && platform.equals("68")){
            Constants.PlatformOption.Quick7751SDK quick7751SDK = GlobalCache.getPfConfig("Quick7751SDK");
            if(null !=quick7751SDK ) {
                Map<String, String> products = quick7751SDK.products;
                if (products.isEmpty()) {
                    ordPO.setPro_idx(Integer.valueOf(proIdx));
                }else {
                    product_id = products.get(proIdx);
                    ordPO.setPro_idx(Integer.valueOf(product_id));
                }
            }
        }
        //yuenansdk 档位映射
        if(null != platform && platform.equals("69")){
            Constants.PlatformOption.YueNanSDK yueNanSDK = GlobalCache.getPfConfig("YueNanSDK");
            if(null !=yueNanSDK ) {
                Map<String, String> products = yueNanSDK.products;
                if(products.isEmpty()) {
                    ordPO.setPro_idx(Integer.valueOf(proIdx));
                }else {
                    product_id = products.get(proIdx);
                    ordPO.setPro_idx(Integer.valueOf(proIdx));
                }
            }
        }
        //samsung 档位映射
        if(null != platform && platform.equals("70")){
            Constants.PlatformOption.Samsung samsung = GlobalCache.getPfConfig("Samsung");
            if (null!=samsung) {
                product_id=samsung.products.get(proIdx);
                ordPO.setPro_idx(Integer.valueOf(proIdx));
            }
        }
        //玩心-可以 ios充值档位映射
        if(null != platform && platform.equals("66")){
            Constants.PlatformOption.WanXinKeYi wanXinKeYi = GlobalCache.getPfConfig("WanXinKeYi");
            if (null !=wanXinKeYi ) {
                Map<String, String> products = wanXinKeYi.apps.get(channelCode).products;
                if (products.isEmpty()) {
                    ordPO.setPro_idx(Integer.valueOf(proIdx));
                }else {
                    product_id = products.get(proIdx);
                    ordPO.setPro_idx(Integer.valueOf(proIdx));
                }
            }
        }
        //玩心-荣耀 充值档位映射
        if(null != platform && platform.equals("72")){
            Constants.PlatformOption.WanXinRongYao wanXinRongYao = GlobalCache.getPfConfig("WanXinRongYao");
            if (null !=wanXinRongYao ) {
                Map<String, String> products = wanXinRongYao.apps.get(channelCode).products;
                if (products.isEmpty()) {
                    ordPO.setPro_idx(Integer.valueOf(proIdx));
                }else {
                    product_id = products.get(proIdx);
                    ordPO.setPro_idx(Integer.valueOf(proIdx));
                }
            }
        }
        //菲凡sdk挡位映射
        if (null!=platform&&platform.equals("56")){
            Constants.PlatformOption.FFSDK ffsdk = GlobalCache.getPfConfig("FFSDK");
            if (null!=ffsdk) {
                product_id=ffsdk.apps.get(channelCode).products.get(proIdx);
                JSONObject callback = new JSONObject();
                callback.put("callbackurl",ffsdk.callBackUrl);
                callback.put("sign", Misc.md5(ffsdk.callBackUrl+ffsdk.signkey));
                callBackUrl = new String(Base64.encodeBase64(callback.toJSONString().getBytes(StandardCharsets.UTF_8)));
            }

        }
        //悦玩sdk挡位映射
        if (null!=platform&&platform.equals("6")){
            Constants.PlatformOption.YWan yWan = GlobalCache.getPfConfig("YWan");
            if (null!=yWan) {
               Map<String,String> products=yWan.apps.get(ext).products;
                if (products.isEmpty()) {
                    product_id = proIdx;
                }
                else {product_id = products.get(proIdx);}
            }

        }
        //酷玩sdk挡位映射
        if (null!=platform&&platform.equals("59")){
            Constants.PlatformOption.KWSDK kwsdk = GlobalCache.getPfConfig("KWSDK");
            if (null!=kwsdk) {
                Map<String,String> products=kwsdk.products;
                if (products.isEmpty()) {
                    product_id = proIdx;
                }
                else {product_id = products.get(proIdx);}
            }

        }
        //oneStore充值档位映射
        if (null!=platform&&platform.equals("105")){
            Constants.PlatformOption.OneStore oneStore = GlobalCache.getPfConfig("OneStore");
            if (null!=oneStore) {
                product_id=oneStore.apps.get(channelCode).products.get(proIdx);
                String[] strs = product_id.split("_");
                if (strs.length>1) {
                    ordPO.setPro_idx(Integer.valueOf(strs[1]));
                }
            }
        }
        //EyouSDK挡位映射
        if (null!=platform&&platform.equals("57")){
            Constants.PlatformOption.EyouSDK eyouSDK = GlobalCache.getPfConfig("EyouSDK");
            if (null!=eyouSDK) {
                product_id = eyouSDK.apps.get(channelCode).products.get(proIdx);
                callBackUrl = eyouSDK.callBackUrl;
            }
        }
        //优点SDK苹果充值档位映射
        if (null!=platform&&platform.equals("53IOS")){
            Constants.PlatformOption.YDSDKApple ydsdkApple = GlobalCache.getPfConfig("YDSDKApple");
            if (null!=ydsdkApple) {
                product_id=ydsdkApple.apps.get(channelCode).products.get(proIdx);
                product_id=product_id.substring(product_id.lastIndexOf("_")+1);
                if (NumberUtil.isNumber(product_id)) {
                    ordPO.setPro_idx(Integer.valueOf(product_id));
                } else {
                    Matcher m = Pattern.compile("[^0-9]").matcher(product_id);
                    ordPO.setPro_idx(Integer.valueOf(m.replaceAll("").trim()));
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        JSONObject data = (JSONObject)JSONObject.toJSON(ordPO);
        data.put("product_id",product_id);
        data.put("callBackUrl",callBackUrl);
        jsonObject.put("ret", 0);
        jsonObject.put("msg", "success");
        jsonObject.put("data", data);
        if ("open".equals( Constants.THINKING_OPEN) ) {
            //向数数科技上报数据
            ThinKingData thinKingData = new ThinKingData();
            thinKingData.ta.setSuperProperties(thinKingData.setSuperProperties(userId, serverId, channelCode));

            Map<String, Object> properties = new HashMap<String, Object>();
            //设置事件属性
            properties.put("order_id", ordPO.getOrder_id());
            properties.put("gear_id", proIdx);
            properties.put("order_money_amount", ordPO.getPay_price());
            properties.put("order_type", order_type);
            properties.put("entrance", ordPO.getPlatform());
            properties.put("item_id", product_id);
            try {
                logger.info("设置create_order 事件参数为：" + properties);
                thinKingData.ta.track(userId + "-" + serverId, "", "create_order", properties);
                thinKingData.ta.flush();
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("----- 数数数据上报 请求错误 ---- 错误信息" + e);
            }
        }
        sender.sendAndDisconnect(jsonObject);
    }

    /**
     * 雪糕SDK支付回调
     * get:/p/平台/cb
     * 例如:p/anySDK/cb
     * post: json
     *
     * @throws UnsupportedEncodingException
     */
    @Cmd("/p/xuegaoSDK/cb")
    public void callbackPay(User sender, HttpRequestJSONObject params) throws UnsupportedEncodingException {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }

        logger.info("xgSDk:json:" + json);
        XGSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), XGSDKNotifyData.class);
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.out_trade_no, notifyData.out_trade_no);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:out_trade_no:" + notifyData.out_trade_no);
            sender.sendAndDisconnect("fail");
            return;
        } else {
            redis.expire(notifyData.out_trade_no, 30);
        }

        OrderPo payTrade = OrderPo.findByOrdId(notifyData.out_trade_no);
        if (payTrade == null) {
            sender.sendAndDisconnect("fail");
            logger.error("订单号:" + notifyData.out_trade_no + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("success");
            return;
        }
        XGSDK pfconfig = GlobalCache.getPfConfig("XGSDK");
        StringBuffer buff = new StringBuffer();
        buff.append("trade_no=").append(notifyData.trade_no).
                append("&out_trade_no=").append(notifyData.out_trade_no).
                append("&gameId=").append(notifyData.gameId).
                append("&amount=").append(String.format("%.2f", notifyData.amount)).
                append("&ts=").append(notifyData.ts).
                append("&ext=").append(notifyData.ext).
                append("&appKey=").append(pfconfig.apps.get(String.valueOf(notifyData.gameId)).gameKey);
        String sign = HelperEncrypt.encryptionMD5(buff.toString()).toLowerCase();
        payTrade.setThird_trade_no(notifyData.trade_no);
        payTrade.setPay_price(Float.valueOf(notifyData.amount));
        payTrade.setUnits("CNY");
        payTrade.setPlatform("xgSDK");

        if (!sign.equals(notifyData.sign)) {
            logger.error("验签失败");
            sender.sendAndDisconnect("fail");
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        // 到这个位置说明:  订单支付钱已经确认正常
        sender.sendAndDisconnect("success");

        payTrade.setStatus(2);//2.收到回调验证成功
        payTrade.updateToDB();
        boolean  isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "IOSPay");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
        // 加一个 支付统计 [新增和活跃付费,以及Ltv统计]
//        payTrade.payFlowStatitic();
    }

    //----------------------------------------------------------

    /**
     * 喜扑SDK支付回调
     * get:/p/平台/cb
     * 例如:p/anySDK/cb
     * post: json
     *
     * @throws UnsupportedEncodingException
     */
    @Cmd("/p/xipuSDK/cb")
    public void xipuSdkCallBack(User sender, HttpRequestJSONObject params) throws UnsupportedEncodingException {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }

        logger.info("xpSDk:json:" + json);
        XPSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), XPSDKNotifyData.class);
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.orderId, notifyData.orderId);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:orderId:" + notifyData.orderId);
            sender.sendAndDisconnect("fail");
            return;
        } else {
            redis.expire(notifyData.orderId, 30);
        }
        //TODO callbackInfo 透传参数
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.callbackInfo);
        if (payTrade == null) {
            sender.sendAndDisconnect("fail");
            logger.error("订单号:" + notifyData.callbackInfo + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("success");
            return;
        }
        XPSDK pfconfig = GlobalCache.getPfConfig("XPSDK");
        StringBuffer buff = new StringBuffer();
        buff.append(notifyData.appId).append(notifyData.orderId).append(notifyData.openId).
                append(notifyData.serverId).append(notifyData.roleId).
                append(notifyData.money).append(notifyData.amount).
                append(notifyData.callbackInfo).append(pfconfig.appServerKey);
        String sign = MD5Util.md5(buff.toString()).toLowerCase();
//        logger.info("newSign=" + notifyData.newsign);
//        logger.info("sign=" + sign);

        //TODO 第三方订单号
        payTrade.setThird_trade_no(notifyData.orderId);
        //TODO 订单金额
        payTrade.setPay_price(Float.valueOf(notifyData.money)/100F);
        payTrade.setUnits("CNY");
        payTrade.setPlatform("xpSDK");
        //TODO 订单状态是否成功
        if (notifyData.orderStatus != 1) {
            logger.error("订单状态不成功");
            return;
        }
        //TODO 校验newSign
        if (!sign.equals(notifyData.newsign)) {
            logger.error("验签失败");
            sender.sendAndDisconnect("fail");
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        // 到这个位置说明:  订单支付钱已经确认正常
        sender.sendAndDisconnect("success");
        payTrade.setStatus(2);//2.收到回调验证成功
        payTrade.updateToDB();
        boolean  isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "IOSPay");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
        // 加一个 支付统计 [新增和活跃付费,以及Ltv统计]
//        payTrade.payFlowStatitic();
    }


    /**
     * AnySdk 回调
     * <p>
     * POST form表单方式 回调
     */
    @Cmd("/p/anySDK/cb")
    public void anySdkCallBack(User sender, JSONObject params) {
        logger.info("params:" + params.toString());
        Map<String, String> in_param = new HashMap<>();//报错所有参数
        String enhanced_sign = params.getString("enhanced_sign");//增强签名串
        String pay_status = params.getString("pay_status");//支付状态，1 为成功，非1则为其他异常状态，游服请在成功的状态下发货
        String amount = params.getString("amount");//支付金额，单位元 值根据不同渠道的要求可能为浮点类型
        String channel_number = params.getString("channel_number");//渠道编号
        String user_id = params.getString("user_id");//用户id
        String order_type = params.getString("order_type");//渠道标识符谷歌：106
        String private_data = params.getString("private_data");//自定义参数，调用客户端支付函数时传入的EXT参数，透传给游戏服务器
        String currency_type = params.getString("currency_type");//自定义参数，调用客户端支付函数时传入的EXT参数，透传给游戏服务器
        logger.info("enhanced_sign:" + enhanced_sign + ",pay_status:" + pay_status + ",amount:" + amount +
                ",channel_number:" + channel_number + ",private_data:" + private_data + ",currency_type:" + currency_type);
        if (null == enhanced_sign || null == pay_status || null == channel_number || null == private_data) {
            logger.error("回调参数为空");
            sender.sendAndDisconnect("fail");
            return;
        }
        Set<String> setKeys = params.keySet();
        Iterator<String> iter = setKeys.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            in_param.put(key, (String) params.get(key));
        }
        // 防止高并发  同一个订单多次处理
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(private_data, private_data);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:private_data:" + private_data);
            sender.sendAndDisconnect("fail");
            return;
        } else {
            redis.expire(private_data, 30);
        }
        logger.debug("in_param:" + in_param.toString());
        String signString = HelperJson.builderSignSortValues(in_param,
                new String[]{"sign", "enhanced_sign"}).toString();

        AnySdk anysdkConfig = GlobalCache.getPfConfig("AnySdk");
        logger.debug("merchantKey:" + anysdkConfig.merchantKey + ",enhancedMerchantKey:" + anysdkConfig.enhancedMerchantKey);
        signString = HelperEncrypt.encryptionMD5(signString).toLowerCase()
                + anysdkConfig.enhancedMerchantKey;
        signString = HelperEncrypt.encryptionMD5(signString).toLowerCase();
        logger.debug("enhanced_sign:" + enhanced_sign);

        OrderPo payTrade = OrderPo.findByOrdId(private_data);
        if (null == payTrade) {
            logger.error("订单号:" + private_data + ",不存在");
            sender.sendAndDisconnect("fail");
            return;
        }
        logger.debug("payTrade:" + payTrade.toString());
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("ok");
            return;
        }
        if (null != currency_type) {// 货币类型
            payTrade.setUnits(currency_type);
        } else {//默认 人民币支付
            logger.info("anysdk 货比 类型 字段 为 空");
            payTrade.setUnits("CNY");
        }
        payTrade.setPlatform("AnySdk");

        if (in_param.containsKey("source")) {//渠道服务器通知 AnySDK 时请求的参数
            payTrade.setSource(in_param.get("source"));
        }
        if (channel_number.equals("160270")) {
            String MyCardJsonData = in_param.get("source");
            Map<String, String> data = HelperJson.decodeMap(MyCardJsonData);

            if (data.containsKey("MyCardTradeNo")) {
                payTrade.setThird_trade_no(data.get("MyCardTradeNo"));
            }
        }
        payTrade.setPay_price(Float.valueOf(amount));

        if (enhanced_sign.equals(signString)) {// 验证签名正确
            // 到这个位置说明:  订单支付钱已经确认正常
            sender.sendAndDisconnect("ok");
            payTrade.setStatus(2);//2.收到回调验证成功
            payTrade.updateToDB();
            UserPo userPo = UserPo.findByUserId(payTrade.getUser_id());
            PlayerPo playerPo = PlayerPo.findByUidAndSid(payTrade.getUser_id(), payTrade.getSid());
            logger.info("user_id:"+user_id+",uid"+userPo.getPf_user()+",pid"+playerPo.getPid());
            if (order_type.equals("106")){
            } else if(!user_id.equals(userPo.getPf_user())  || playerPo.getPid() != payTrade.getPid()){
                return;
            }
            boolean  isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "IOSPay");
            logger.info("isSychnSuccess["+isSychnSuccess+"]");

            // 加一个 支付统计 [新增和活跃付费,以及Ltv统计]
//            payTrade.payFlowStatitic();
        } else {
            logger.error("private_data:" + private_data + ",channel_number:" + channel_number + ",验签失败..");
            payTrade.setStatus(1);//2.收到回调验证成功
            sender.sendAndDisconnect("fail");
            payTrade.updateToDB();
        }
    }

    /***
     * google in pay
     *
     *
     * */
    @Cmd("/p/googleInPay/cb")
    public void googleInPayCallBack(User sender, HttpRequestJSONObject params) throws Exception {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        //{'packageName':xxx,'sign':yyy,'ordid':zzz,'total_fee':aaaa,'trade_status':bbbb}
        logger.info("googleInPayCallBack:" + json);
        GoogleinPayNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), GoogleinPayNotifyData.class);
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.ordid, notifyData.ordid);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:out_trade_no:" + notifyData.ordid);
            sender.sendAndDisconnect("fail");
            return;
        } else {
            redis.expire(notifyData.ordid, 30);
        }

        OrderPo payTrade = OrderPo.findByOrdId(notifyData.ordid);
        if (payTrade == null) {
            sender.sendAndDisconnect("fail");
            logger.error("订单号:" + notifyData.ordid + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("success");
            return;
        }

//		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtOuOeOSjepkaggsJ244bj/6YSfKJSvyMXXn22SHTYX/ueIAshSHJq0JWkqU/0Qk5qkqI5kP7FNpOrCqA2CPQiLiDNs/ZTwCG3drvlf7/LVixUDSK4Gq5e+R7lliKq1z8SniIbS8VgjAnwmf5b+FY0ZtRsWUxpu+ATDiqaXOrGH358p90iapBbHrZ5uUMVhzKBv1WgKRSToI0f3BzbWAqC8CwW5lkVij8CVmnQ+1iqdr8OdaDJFzAqKZSLbx/1kL7iDTdGE3zHrbhklO7ZgDC73qAJaDfR8SbR0esBHvpGbKQ/Cgg87ExZzb0clL5mXG9ySwqKoSFWghX6WodMpeOcwIDAQAB";
        GoogleInPay googleConfig = GlobalCache.getPfConfig("GoogleInPay");
        JSONObject oriJson = JSONObject.parseObject(notifyData.oriJson);
        logger.info("oriJson:"+oriJson.toString());
        //获取档位谷歌返回的productId
        String product_id=oriJson.getString("productId");
        //谷歌订单号 在oriJson里
        String third_order_id=oriJson.getString("orderId");
   /*     //删除谷歌 oriJson 里的developerPayload
        oriJson.remove("developerPayload");*/
        //根据后台配置的channelCode和订单中的pro_idx找出productId与谷歌返回的productId进行比对
        String productValue=googleConfig.apps.get(payTrade.getChannelCode()).products.get(String.valueOf(payTrade.getPro_idx()));
        logger.info("谷歌返回的product_id:"+product_id+"========"+"后台配置的productId:"+productValue);
        if (StringUtil.isEmpty(productValue)||!product_id.equals(productValue)) {
            logger.info("充值档位未配置或充值档位不匹配");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("充值档位未配置或充值档位不匹配"));
            return;
        }
        // 1 验签
        logger.info("谷歌代签名参数 oriJson ：" +notifyData.oriJson + ",签名的key为 ： "+googleConfig.apps.get(payTrade.getChannelCode()).publicKey);
            if (RSAUtils.verify(notifyData.oriJson.getBytes(), googleConfig.apps.get(payTrade.getChannelCode()).publicKey, notifyData.sign)) {
            logger.info("到这个位置说明:  订单支付钱已经确认正常");
            // 到这个位置说明:  订单支付钱已经确认正常
            sender.sendAndDisconnect("success");
            payTrade.setThird_trade_no(third_order_id);
            payTrade.setPay_price(Float.valueOf(notifyData.total_fee));
            payTrade.setUnits("单位待定");
            payTrade.setPlatform("googleInpay");
            payTrade.setStatus(2);//2.收到回调验证成功
            payTrade.updateToDB();

            boolean  isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "IOSPay");
            logger.info("isSychnSuccess["+isSychnSuccess+"]");
        }

    }

    //---------------------------------------------
    /**
     * 手盟SDK支付回调
     * get:/p/平台/cb
     * 例如:p/shoumengSDK/cb
     * post: json
     *
     * @throws UnsupportedEncodingException
     */
    @Cmd("/p/SMengSDK/cb")
    public void SMengSDKCallBack(User sender, HttpRequestJSONObject params) throws UnsupportedEncodingException {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }

        logger.info("SMengSDK:json:" + json);
        SMSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), SMSDKNotifyData.class);
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");

        logger.info("SMSDKNotifyData:"+notifyData.toString());
        long isSaveSuc = redis.STRINGS.setnx(notifyData.coOrderId, notifyData.coOrderId);
        if (isSaveSuc == 0L) {
            logger.error("同一订单多次请求,无须重复处理:orderId:" + notifyData.coOrderId);
            sender.sendAndDisconnect("fail");
            return;
        } else {
            redis.expire(notifyData.coOrderId, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.coOrderId); // TODO 原厂订单号

        if (payTrade == null) {
            sender.sendAndDisconnect("fail");
            logger.error("订单号:" + notifyData.coOrderId + ",不存在");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.error("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("success");
            return;
        }
        int success = notifyData.success;
        if (success == 1) {
            logger.error("订单状态="+success+",失败");
            return;
        }
        Float pay_price = payTrade.getPay_price();
        if (!pay_price.equals((float) notifyData.amount)) {
            sender.sendAndDisconnect("fail");
            logger.error("充值金额=" + notifyData.amount+",订单金额="+pay_price+",充值金额与订单金额不一致");
            return;
        }
        Constants.PlatformOption.SMengSDK pfconfig = GlobalCache.getPfConfig("SMengSDK");
        StringBuffer buff = new StringBuffer();
        String secret = pfconfig.Secret;
//        sign=md5(orderId=1406282005595592&uid=1258813
//                &amount=100&coOrderId=abcdefghijk &success=0
//                &secret=SM********************************SM)
        buff.append("orderId=").append(notifyData.orderId).
                append("&uid=").append(notifyData.uid).
                append("&amount=").append(notifyData.amount).
                append("&coOrderId=").append(notifyData.coOrderId).
                append("&success=").append(notifyData.success).
                append("&secret=").append(secret);
        String sign = HelperEncrypt.encryptionMD5(buff.toString()).toLowerCase();

        payTrade.setThird_trade_no(notifyData.orderId); //TODO 手盟订单号
        payTrade.setPay_price(Float.valueOf(notifyData.amount));
        payTrade.setUnits("CNY");
        payTrade.setPlatform("SMengSDK");

        if (!sign.equals(notifyData.sign)) {
            logger.error("验签失败");
            sender.sendAndDisconnect("fail");
            payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        // 到这个位置说明:  订单支付钱已经确认正常
        sender.sendAndDisconnect("SUCCESS"); // TODO 大写SUCCESS

        payTrade.setStatus(2);//2.收到回调验证成功
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "SMengH5");
        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
        // 加一个 支付统计 [新增和活跃付费,以及Ltv统计]
//          payTrade.payFlowStatitic();
    }
}

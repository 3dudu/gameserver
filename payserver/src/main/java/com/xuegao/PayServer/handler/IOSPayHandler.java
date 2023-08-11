package com.xuegao.PayServer.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.IosVerifyUtil;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.StringUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.Logger;
import com.xuegao.PayServer.data.Constants.PlatformOption.IOSPay;

import java.util.Arrays;
import java.util.List;

/**
 * IOS内购
 */
public class IOSPayHandler {

    static Logger logger = Logger.getLogger(IOSPayHandler.class);


    @Cmd("/p/IOSPay/cb")
    public void IOSPay(User sender, JSONObject params) {

        String receipt = params.getString("r");
        String orderId = params.getString("o");
        boolean sanbox = false;
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(orderId, orderId);
        if (isSaveSuc == 0L) {
            logger.info("同一订单多次请求,无须重复处理:orderId:" + orderId);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("同一订单多次请求,无须重复处理:orderId:" + orderId));
            return;
        } else {
            redis.expire(orderId, 30);
        }
        String verifyResult = IosVerifyUtil.buyAppVerify(receipt, 1);//先进行线上测试
        if (null==verifyResult) {
            logger.info("无收据信息!!");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("无收据信息!!"));
            return;
        } else {
            JSONObject job = JSONObject.parseObject(verifyResult);
            logger.info("线上环境，苹果平台返回:"+job.toJSONString());
            int status = job.getIntValue("status");
            if (21007==status) {// TODO 21007---receipt是沙盒测试,但却发送至线上测试的验证服务
                verifyResult = IosVerifyUtil.buyAppVerify(receipt, 0);//再进行沙盒测试
                job = JSONObject.parseObject(verifyResult);
                logger.info("沙盒环境，苹果平台返回:"+job.toJSONString());
                sanbox = true;
                status = job.getIntValue("status");
            }
            //前端所提供的收据是有效的,验证成功
            if (0==status) {
                String r_receipt = job.getString("receipt");
                JSONObject returnJson = JSONObject.parseObject(r_receipt);
                String in_app = returnJson.getString("in_app");
                String product_id = "";
                String transaction_id = "";
                String bundle_id = "";
                String cause = "";
                //兼容旧版本苹果返回格式
                if ("".equals(in_app)||null==in_app) {
                    product_id = returnJson.getString("product_id");
                    bundle_id = returnJson.getString("bid");
                    transaction_id = returnJson.getString("transaction_id");

                    if ("".equals(transaction_id)||null==transaction_id) {
                        cause = "transaction_id为空";
                        logger.info(cause);
                        sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
                        return;
                    }
                    //查询transaction_id是否存在
                    OrderPo orderPo = OrderPo.findByThirdId(transaction_id);
                    if (null!=orderPo) {
                        cause = "transaction_id已存在";
                        logger.info(cause);
                        sender.sendAndDisconnect(MsgFactory.getIOSErrorMsg(cause));
                        return;
                    }
                    OrderPo payTrade = OrderPo.findByOrdId(orderId);
                    if (null==payTrade) {
                        cause = "订单号不存在";
                        logger.info(cause);
                        sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
                        return;
                    }
                    logger.info("OrderPo:"+payTrade.toString());
                    IOSPay iosPay=GlobalCache.getPfConfig("IOSPay");
                    if (null==iosPay) {
                        cause ="IOSPay:未配置";
                        logger.info(cause);
                        sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
                        return;
                    }
                    //校验bundle_id是否配置
                    String productValue="";
                    List<String> bundleIds=iosPay.apps.get(payTrade.getChannelCode()).bundleId;
                    if (CollUtil.contains(bundleIds,bundle_id)) {
                        productValue = iosPay.apps.get(payTrade.getChannelCode()).products.get(String.valueOf(payTrade.getPro_idx()));
                        logger.info("product_id:"+product_id+"========"+"productValue:"+productValue);
                    }else{
                        cause ="bundleId:"+bundle_id+"未配置";
                        logger.info(cause);
                        sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
                        return;
                    }
                    //校验productId
                    if (StringUtil.isEmpty(productValue)||!product_id.equals(productValue)) {
                        cause ="充值档位未配置或充值档位不匹配";
                        logger.info("充值档位未配置或充值档位不匹配");
                        sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
                        return;
                    }
                    //成功
                    sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
                    payTrade.setThird_trade_no(transaction_id);
                    //保存原始票据
                    payTrade.setSource(receipt);
                    payTrade.setPlatform("IOSPay");
                    payTrade.updateToDB();

                    //校验通过,调用发货接口
                    boolean  isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "IOSPay",sanbox);
                    logger.info("isSychnSuccess["+isSychnSuccess+"]");
                }else {
                    JSONArray jsonArray = JSONObject.parseArray(in_app);
                    for (Object o : jsonArray) {
                        //in_app数组可能有多个票据,第一个元素就是最新的
                        JSONObject in_appJson = (JSONObject) o;
                        if (ObjectUtil.isEmpty(in_appJson)) {
                            logger.info("in_app数组为空");
                            continue;
                        }
                        product_id = in_appJson.getString("product_id");
                        transaction_id = in_appJson.getString("transaction_id");
                        bundle_id = returnJson.getString("bundle_id");
                        if ("".equals(transaction_id) || null == transaction_id) {
                            cause = "第三方订单 transaction_id 为空";
                            logger.info(cause);
                            continue;
                        }
                        //查询transaction_id是否存在
                        OrderPo orderPo = OrderPo.findByThirdId(transaction_id);
                        if (null != orderPo) {
                            cause = "第三方订单 transaction_id 已存在";
                            logger.info(cause);
                            continue;
                        }
                        OrderPo payTrade = OrderPo.findByOrdId(orderId);
                        if (null == payTrade) {
                            cause = " 雪糕订单号 " + orderId + "不存在";
                            logger.info(cause);
                            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
                            return;
                        }
                        logger.info("OrderPo:" + payTrade.toString());
                        IOSPay iosPay = GlobalCache.getPfConfig("IOSPay");
                        if (null == iosPay) {
                            cause = "IOSPay:未配置";
                            logger.info(cause);
                            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg(cause));
                            return;
                        }
                        //校验bundle_id是否配置
                        String productValue = "";
                        List<String> bundleIds = iosPay.apps.get(payTrade.getChannelCode()).bundleId;
                        if (CollUtil.contains(bundleIds, bundle_id)) {
                            productValue = iosPay.apps.get(payTrade.getChannelCode()).products.get(String.valueOf(payTrade.getPro_idx()));
                            logger.info(" 第三方订单"+ transaction_id  +"档位信息： product_id:" + product_id + "========" + "productValue:" + productValue);
                        } else {
                            cause =" 第三方订单"+ transaction_id  + " bundleId:" + bundle_id + "未配置";
                            logger.info(cause);
                            continue;
                        }
                        //校验productId
                        if (StringUtil.isEmpty(productValue) || !product_id.equals(productValue)) {
                            cause = "第三方订单" +transaction_id  +" 充值档位未配置或充值档位不匹配";
                            logger.info(cause);
                            continue;
                        }
                        //成功
                        sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
                        payTrade.setThird_trade_no(transaction_id);
                        //保存原始票据
                        payTrade.setSource(receipt);
                        payTrade.setPlatform("IOSPay");
                        payTrade.updateToDB();

                        //校验通过,调用发货接口
                        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "IOSPay", sanbox);
                        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
                        break;
                    }
                }
            } else {
                logger.info("receipt数据有问题,返回状态码="+status);
                sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("receipt数据有问题,返回状态码="+status));
                return;
            }
        }
    }

}

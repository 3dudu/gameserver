package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.IosVerifyUtil;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCardSDKServiceHandle {
    static Logger logger = Logger.getLogger(MyCardSDKServiceHandle.class);
    private static final String AuthGlobalUrl = "http://testb2b.mycard520.com.tw/MyBillingPay/v1.1/AuthGlobal";//向MyCard要求交易授權碼url(测试环境)
    private static final String TradeQueryUrl = "http://testb2b.mycard520.com.tw/MyBillingPay/v1.1/TradeQuery";//驗證MyCard交易結果url(测试环境)
    private static final String PaymentConfirmUrl = "http://testb2b.mycard520.com.tw/MyBillingPay/v1.1/PaymentConfirm";//確認MyCard交易並進行請款url(测试环境)


    /**
     * mycard 差异对比功能
     * req参数1模板 StartDateTime  EndDateTime 批量查询
     * req参数1模板MyCardTradeNo 单个查询
     **/
    @Cmd("/p/MyCardCompare")
    public void MyCardCompare(User sender, JSONObject params) throws Exception {
        String packTxt = "";// 返回报文
        Map<String, String> reqMap = new HashMap<>();
        Set<String> setKeys = params.keySet();
        Iterator<String> iter = setKeys.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            reqMap.put(key, (String) params.get(key));
        }
        if (reqMap.containsKey("StartDateTime")) {
            String StartDateTime = reqMap.get("StartDateTime");
            String EndDateTime = reqMap.get("EndDateTime");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StartDateTime = StartDateTime.replace('T', ' ');
            Date date = simpleDateFormat.parse(StartDateTime);
            long startTime = date.getTime();
            EndDateTime = EndDateTime.replace('T', ' ');
            date = simpleDateFormat.parse(EndDateTime);
            long endTime = date.getTime();
            packTxt = new OrderPo().MyCardTradeNoCompare(startTime, endTime + 1000);

        } else if (reqMap.containsKey("MyCardTradeNo")) {
            String MyCardTradeNo = reqMap.get("MyCardTradeNo");
            packTxt = new OrderPo().MyCardTradeNo(MyCardTradeNo);
        }
        sender.sendAndDisconnect(packTxt);
        return;
    }


    /**
     * 获取MyCard交易授权码（AuthCode字段）
     * @param sender
     * @param params
     * @throws Exception
     */
    @Cmd("/p/MyCardSDKAuthGlobal/cb")
    public void MyCardSDKAuthGlobal(User sender, HttpRequestJSONObject params) throws Exception {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject data = JSONObject.parseObject(json);
        Constants.PlatformOption.MyCardSDK myCardSDK = GlobalCache.getPfConfig("MyCardSDK");
        String FacServiceId=myCardSDK.FacServiceID;
        String FacTradeSeq=data.getString("FacTradeSeq");//厂商交易序列号 为订单资料的key
        String TradeType=data.getString("TradeType");//交易模式 Andeoid SDK手游通用 |  WEB
        String CustomerId =data.getString("CustomerId");//会员代号 为主键
        String ProductName= data.getString("ProductName");//产品名称 ProductName=180元寶＝TWD90
        String Amount = data.getString("Amount");//交易金额
        String Currency = data.getString("Currency");//货币类别
        String SandBoxMode  = data.getString("SandBoxMode");//是否为测试环境
        //生成验证码
        ProductName = URLEncoder.encode(ProductName, "utf-8");//180%E5%85%83%E5%AF%B6%EF%BC%9DTWD90
        Pattern pattern = Pattern.compile("%\\w{2}");
        Matcher matcher = pattern.matcher(ProductName);
        while (matcher.find()){
            ProductName = ProductName.replace(matcher.group(0),matcher.group(0).toLowerCase());
        }
        String preHashValue = URLEncoder.encode(FacServiceId,"utf-8")+ URLEncoder.encode(FacTradeSeq,"utf-8" ) +
                URLEncoder.encode(TradeType,"utf-8" )  + URLEncoder.encode(CustomerId,"utf-8" ) +
                ProductName + URLEncoder.encode(Amount,"utf-8" ) +
                URLEncoder.encode(Currency,"utf-8" ) + URLEncoder.encode(SandBoxMode,"utf-8" ) +
                URLEncoder.encode(myCardSDK.appServerKey,"utf-8" );
        String hash= AdHandler.getSHA256StrJava(preHashValue);
        String post="FacServiceId="+FacServiceId+"&FacTradeSeq="+FacTradeSeq+"&TradeType="+TradeType+"&CustomerId="+CustomerId+"&ProductName="+ProductName+
                "&Amount="+Amount+"&Currency="+Currency+"&SandBoxMode="+SandBoxMode+"&Hash="+hash;
        String result= IosVerifyUtil.https(myCardSDK.AuthGlobalUrl+"?"+post);
        logger.info("请求MyCard获取交易授权码接口:"+myCardSDK.AuthGlobalUrl+",请求参数为:"+post+",返回结果为:"+result);
        if(result!=null){
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject jsondata =new JSONObject();
            if(jsonObject.getString("ReturnCode").equals("1")){
                OrderPo orderPo = OrderPo.findByOrdId(FacTradeSeq);
                orderPo.setSource(jsonObject.getString("TradeSeq")+"|"+jsonObject.getString("AuthCode"));
                orderPo.updateToDB();
                jsondata.put("ret", 0);
                jsondata.put("msg", "success");
                jsondata.put("AuthCode",jsonObject.getString("AuthCode"));
                sender.sendAndDisconnect(jsondata);
            }
        }
    }

    /**
     *
     * 验证交易结果并发货
     *
     * @param sender
     * @param params
     */
    @Cmd("/p/MyCardSDKTradeQuery/cb")
    public void MyCardSDKTradeQuery(User sender, HttpRequestJSONObject params){
        String json =null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject data = JSONObject.parseObject(json);
        Constants.PlatformOption.MyCardSDK myCardSDK = GlobalCache.getPfConfig("MyCardSDK");
        String authCode=data.getString("AuthCode");
        String postData="AuthCode="+authCode;
        String result=IosVerifyUtil.https(myCardSDK.TradeQueryUrl+"?"+postData);
        logger.info("MyCard查询交易结果返回数据为:"+result);
        if(result!=null){
            JSONObject jsonObject = JSONObject.parseObject(result);
            //交易成功為3,交易失敗為0
            if (jsonObject.getString("ReturnCode").equals("1")&&jsonObject.getString("PayResult").equals("3")) {
                String facTradeSeq = jsonObject.getString("FacTradeSeq");
                String amout = jsonObject.getString("Amount");
                String myCardTradeNo = jsonObject.getString("MyCardTradeNo");
                JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
                long isSaveSuc = redis.STRINGS.setnx(facTradeSeq, facTradeSeq);
                if (isSaveSuc == 0L) {
                    logger.info("同一订单多次请求,无须重复处理:order_id:" + facTradeSeq);
                    sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("同一订单多次请求,无须重复处理:order_id:" + facTradeSeq));
                    return;
                } else {
                    redis.expire(facTradeSeq, 30);
                }
                OrderPo payTrade = OrderPo.findByOrdId(facTradeSeq);
                if (payTrade == null) {
                    logger.info("订单号:" + facTradeSeq + ",不存在");
                    sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单号:" + facTradeSeq + ",不存在"));
                    return;
                }
                if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
                    logger.info("订单已经处理完毕,无须重复处理");
                    sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单已经处理完毕,无须重复处理"));
                    return;
                }
                //校验金额
                if (!payTrade.getPay_price().equals(Float.valueOf(amout))){
                    logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "支付金额:" + Float.valueOf(amout));
                    sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单金额:" + payTrade.getPay_price() + "不匹配" + "支付金额:" + Float.valueOf(amout)));
                    return;
                }
                //成功
                sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
                payTrade.setThird_trade_no(myCardTradeNo);
                //订单金额以实际接受到返回金额为准
                payTrade.setPay_price(Float.valueOf(amout));
                payTrade.setPlatform("MyCardSDK");
                payTrade.setSource(payTrade.getSource()+"|"+jsonObject.getString("PaymentType"));
                payTrade.setStatus(2);
                payTrade.updateToDB();
                //发钻石
                boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "MyCardSDK");
                logger.info("isSychnSuccess[" + isSychnSuccess + "]");
                if (isSychnSuccess) {
                    String cresult=IosVerifyUtil.https(myCardSDK.PaymentConfirmUrl+"?"+postData);
                    logger.info("发货成功后通知MyCard请款接口,结果为："+cresult);
                }
            }
        }
    }


    /**
     * 补储接口
     *
     * @param sender
     * @param params eg: {"DATA":"{\"ReturnCode\":\"1\",\"ReturnMsg\":\"QueryOK\",\"FacServiceId\":\"xuefs2\",\"TotalNum\":1,\"FacTradeSeq\":[\"0000016cf0ba4254cdbf85bc0017ed5e\"]}"}
     */
    @Cmd("/p/MyCardSDKForStorage/cb")
    public void MycardSDKForStorage(User sender, JSONObject params) {
        String data= params.getString("DATA");
        logger.info("获取到的json字符串为:" + data);
        JSONObject json = JSONObject.parseObject(data);
        String ReturnCode = json.getString("ReturnCode");
        String ReturnMsg = json.getString("ReturnMsg");
        String FacServiceId = json.getString("FacServiceId");
        int TotalNum = json.getInteger("TotalNum");
        List FacTradeSeq = json.getJSONArray("FacTradeSeq");
        //遍历获取到的订单集
        for (int i = 0; i < TotalNum; i++) {
            String facTradeSeq = (String) FacTradeSeq.get(i);
            logger.info("订单号为：" + facTradeSeq);
            OrderPo payTrade = OrderPo.findByOrdId(facTradeSeq);
            logger.info("订单的详细信息为："+payTrade.toString());
            String Source = payTrade.getSource();
            String[] AuthCodeArr = Source.split("\\|");
            String AuthCode = AuthCodeArr[1];
            logger.info("查询到的AuthCode为"+AuthCode);
            Integer status = payTrade.getStatus();
            //根据订单状态判断是否需要补储
            if (status ==0 || status ==1 || status ==2 || status ==3) {
                Constants.PlatformOption.MyCardSDK myCardSDK = GlobalCache.getPfConfig("MyCardSDK");
                String postData="AuthCode="+AuthCode;
                logger.info("请求的参数为"+postData +"url路径为："+myCardSDK.TradeQueryUrl);
                String result = IosVerifyUtil.https(myCardSDK.TradeQueryUrl+"?"+postData);
                logger.info("补储MyCard查询交易结果返回数据为:" + result);
                if (result != null) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    //交易成功為3,交易失敗為0
                    if (jsonObject.getString("ReturnCode").equals("1") && jsonObject.getString("PayResult").equals("3")) {
                        String amout = jsonObject.getString("Amount");
                        String myCardTradeNo = jsonObject.getString("MyCardTradeNo");
                        //校验金额
                        if (!payTrade.getPay_price().equals(Float.valueOf(amout))) {
                            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "支付金额:" + Float.valueOf(amout));
                            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单金额:" + payTrade.getPay_price() + "不匹配" + "支付金额:" + Float.valueOf(amout)));
                            return;
                        }
                        //成功
                        sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
                        payTrade.setThird_trade_no(myCardTradeNo);
                        //订单金额以实际接受到返回金额为准
                        payTrade.setPay_price(Float.valueOf(amout));
                        payTrade.setPlatform("MyCardSDK");
                        payTrade.setSource(payTrade.getSource()+"|"+jsonObject.getString("PaymentType"));
                        payTrade.setStatus(2);
                        payTrade.updateToDB();
                        //发钻石
                        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "MyCardSDK");
                        logger.info("isSychnSuccess[" + isSychnSuccess + "]");
                        if (isSychnSuccess) {
                            String cresult = IosVerifyUtil.https(myCardSDK.PaymentConfirmUrl+"?"+postData);
                            logger.info("补储发货成功后通知MyCard请款接口,结果为：" + cresult);
                        }
                    }
                }
            }
        }
    }
}

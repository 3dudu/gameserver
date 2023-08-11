package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.yyb.SnsSigCheck;
import com.xuegao.PayServer.vo.YSDKNotifyData;
import com.xuegao.PayServer.util.yyb.OpensnsException;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.RequestUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;


import java.util.HashMap;
import java.util.Map;


public class YSDKServiceHandle {

    static Logger logger = Logger.getLogger(YSDKServiceHandle.class);


    private static final String cb_xw_url = "https://ysdk.qq.com/mpay/get_balance_m";//查询余额接口
    private static final String cb_sx_url = "https://ysdktest.qq.com/mpay/get_balance_m";
    private static final String d_xw_url = "https://ysdk.qq.com/mpay/pay_m";//消耗游戏币接口
    private static final String d_sx_url = "https://ysdktest.qq.com/mpay/pay_m";
    private static final String c_xw_url = "https://ysdk.qq.com/mpay/cancel_pay_m";
    private static final String c_sx_url = "https://ysdktest.qq.com/mpay/cancel_pay_m";

    /**
     * 查询余额接口(balance字段)
     * @param sender
     * @param params
     * @throws OpensnsException
     */
    @Cmd("/p/YSDKCheckBalance/cb")
    public void YSDKCheckBalance(User sender, HttpRequestJSONObject params) throws OpensnsException {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject jsonObject = JSON.parseObject(json);
        long ts = System.currentTimeMillis() / 1000L;//UNIX时间戳
        String openid = jsonObject.getString("openid");//从手Q登录态或微信登录态中获取的openid的值
        String openkey = jsonObject.getString("openkey");//手Q登陆时传手Q登陆回调里获取的paytoken值
        String appid = jsonObject.getString("appid");//offerid即支付结算页面里的应用id，用于支付接口
        String pf = jsonObject.getString("pf");//平台来源，登录获取的pf值
        String pfkey = jsonObject.getString("pfkey");//登录获取的pfkey值
        String zoneid = jsonObject.getString("zoneid");//账户分区ID_角色ID
        String platform = jsonObject.getString("platform");
        Constants.PlatformOption.YSDK ysdk = GlobalCache.getPfConfig("YSDK");
        if (ysdk.apps.get(appid).PayAppkey == null) {
            logger.info("PayAppkey未配置：" + ysdk.apps.get(appid).PayAppkey);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("PayAppkey未配置"));
            return;
        }
        String method = "POST";
        String url_path = "/v3/r/mpay/get_balance_m";
        HashMap<String, String> param = new HashMap<>();
        param.put("appid", appid);
        param.put("openid", openid);
        param.put("openkey", openkey);
        param.put("pf", pf);
        param.put("pfkey", pfkey);
        param.put("ts", String.valueOf(ts));
        param.put("zoneid", zoneid);
        String secret = ysdk.apps.get(appid).PayAppkey + "&";
        String sig = SnsSigCheck.makeSig(method, url_path, param, secret);
        String postData = "appid=" + SnsSigCheck.encodeUrl(appid) + "&openid=" + SnsSigCheck.encodeUrl(openid) +
                "&openkey=" + SnsSigCheck.encodeUrl(openkey) + "&pf=" + SnsSigCheck.encodeUrl(pf) + "&pfkey=" + SnsSigCheck.encodeUrl(pfkey) +
                "&ts=" + SnsSigCheck.encodeUrl(String.valueOf(ts)) + "&sig=" + SnsSigCheck.encodeUrl(sig) + "&zoneid=" + SnsSigCheck.encodeUrl(zoneid);
        Map<String, String> map = new HashMap<>();
        String CookieValue = "";
        switch (Integer.valueOf(platform)) {
            case 1:
                CookieValue = "session_id=openid;session_type=kp_actoken";
                break;
            case 2:
                CookieValue = "session_id=hy_gameid;session_type=wc_actoken";
                break;
        }
        CookieValue += ";org_loc=/mpay/get_balance_m";
        map.put("Cookie", CookieValue);
        String result = RequestUtil.requestSetRp(ysdk.apps.get(appid).getBalanceUrl, map, "POST", postData.getBytes());
        logger.info("应用宝查询余额 post:" + ysdk.apps.get(appid).getBalanceUrl + ",postData=" + postData + ",result=" + result);
        if (result != null) {
            JSONObject jsonObj = JSONObject.parseObject(result);
            if (jsonObj.getIntValue("ret") == 0) {
                JSONObject data = new JSONObject();
                data.put("ret", 0);
                data.put("msg", "success");
                data.put("balance", jsonObj.getString("balance"));
                sender.sendAndDisconnect(data);
            }
        }
    }


    /**
     * 扣款接口,扣款成功后执行发货
     * @param sender
     * @param params
     * @throws Exception
     */
    @Cmd("/p/YSDKDeduct/cb")
    public void YSDKDeduct(User sender, HttpRequestJSONObject params) throws Exception {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        YSDKNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), YSDKNotifyData.class);
        long ts = System.currentTimeMillis() / 1000L;//UNIX时间戳
        String appid = notifyData.appid;
        String openid = notifyData.openid;
        String openkey = notifyData.openkey;
        String pf = notifyData.pf;
        String pfkey = notifyData.pf_key;
        String zoneid = notifyData.zoneid;
        String amt = notifyData.amt;//扣款金额
        String billno = notifyData.billno;//订单号
        String platform = notifyData.platform;//平台

        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.billno, notifyData.billno);
        if (isSaveSuc == 0L) {
            logger.info("同一订单多次请求,无须重复处理:order_id:" + notifyData.billno);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("同一订单多次请求,无须重复处理:order_id:" + notifyData.billno));
            return;
        } else {
            redis.expire(notifyData.billno, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.billno);
        if (payTrade == null) {
            logger.info("订单号:" + notifyData.billno + ",不存在");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单号:" + notifyData.billno + ",不存在"));
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单已经处理完毕,无须重复处理"));
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(notifyData.amt)/10F)){
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配" + "客户端请求扣款金额:" + Float.valueOf(notifyData.amt)/10F);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单金额:" + payTrade.getPay_price() + "不匹配" + "客户端请求的扣款金额:" + Float.valueOf(notifyData.amt)/10F));
            return;
        }
        Constants.PlatformOption.YSDK ysdk = GlobalCache.getPfConfig("YSDK");
        String method = "POST";
        String url_path = "/v3/r/mpay/pay_m";
        HashMap<String, String> param = new HashMap<>();
        param.put("appid", appid);
        param.put("amt", amt);
        param.put("billno", billno);
        param.put("openid", openid);
        param.put("openkey", openkey);
        param.put("pf", pf);
        param.put("pfkey", pfkey);
        param.put("ts", String.valueOf(ts));
        param.put("zoneid", zoneid);
        String sig = SnsSigCheck.makeSig(method, url_path, param, ysdk.apps.get(appid).PayAppkey + "&");
        String postData = "appid=" + appid + "&openid=" + openid + "&amt=" + amt + "&billno=" + billno + "&openkey=" + openkey + "&pf=" + pf + "&pfkey=" + pfkey +
                "&ts=" + ts + "&sig=" + sig + "&zoneid=" + zoneid;
        Map<String, String> map = new HashMap<>();
        String CookieValue = "";
        switch (Integer.valueOf(platform)) {
            case 1:
                CookieValue = "session_id=openid;session_type=kp_actoken";
                break;
            case 2:
                CookieValue = "session_id=hy_gameid;session_type=wc_actoken";
                break;
        }
        CookieValue += ";org_loc=/mpay/pay_m";
        map.put("Cookie", CookieValue);
        String result = RequestUtil.requestSetRp(ysdk.apps.get(appid).payUrl, map, "POST", postData.getBytes());
        logger.info("应用宝扣款接口 post:" + ysdk.apps.get(appid).payUrl + ",postData=" + postData + ",result=" + result);
        if (result!=null) {
            JSONObject obj = JSON.parseObject(result);
            if (obj.getIntValue("ret") == 0) {
                //执行到这里,说明扣款成功 执行发货
                sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
                payTrade.setPay_price(Float.valueOf(notifyData.amt)/10F);
                payTrade.setPlatform("YSDK");
                payTrade.setStatus(2);
                payTrade.updateToDB();
                //发钻石
                boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "YSDK");
                logger.info("isSychnSuccess[" + isSychnSuccess + "]");
            }
        }
    }


}

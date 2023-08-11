package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.wxpay.HmacSHA1And256EncryptionWx;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.RequestUtil;
import com.xuegao.core.util.StringUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;


public class WxXcxHandler {

    public static Logger logger = Logger.getLogger(WxXcxHandler.class);

    private static final String cb_xw_url = "https://api.weixin.qq.com/cgi-bin/midas/getbalance";//查询余额接口
    private static final String cb_sx_url = "https://api.weixin.qq.com/cgi-bin/midas/sandbox/getbalance";
    private static final String d_xw_url = "https://api.weixin.qq.com/cgi-bin/midas/pay";//消耗游戏币接口
    private static final String d_sx_url = "https://api.weixin.qq.com/cgi-bin/midas/sandbox/pay";
    private static final String get_AccessToken_Url = "https://api.weixin.qq.com/cgi-bin/token";

    private static final String code2_Session_Url = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 扣款接口,扣款成功后执行发货
     * @param sender
     * @param params
     * @throws Exception
     */
    @Cmd("/p/WxXcxDeduct/cb")
    public void WxXcxDeduct(User sender, JSONObject params){

        long ts = System.currentTimeMillis() / 1000L;//UNIX时间戳
        String appid = params.getString("appid");
        String openid = params.getString("openid");
        String pf =params.getString("pf");
        String zoneid =params.getString("zondid");
        String amt =params.getString("amt");//扣款金额
        String billno = params.getString("billno");//订单号
        String session_key =params.getString("session_key");//mp_sig签名时需要用的key
        String sandbox =params.getString("sandbox");
        logger.info("传的参数为："+appid+"-----------"+session_key );
        //appid=wx769a9a8262984b13&openid=oFbiB4pvHEdVax_Qbo2vCpAjzvGw&pf=wx&zondid=1&amt=10&billno=000001700f54516da97a682c02cc51ef&session_key=U71abD3RLGgdgl9aV6wDQQ==
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(billno,billno);
        if (isSaveSuc == 0L) {
            logger.info("同一订单多次请求,无须重复处理:order_id:" + billno);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("同一订单多次请求,无须重复处理:order_id:" + billno));
            return;
        } else {
            redis.expire(billno, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(billno);
        if (payTrade == null) {
            logger.info("订单号:" + billno + ",不存在");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单号:" + billno + ",不存在"));
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单已经处理完毕,无须重复处理"));
            return;
        }
        //校验金额
        if (!payTrade.getPay_price().equals(Float.valueOf(amt)/10)){
            logger.info("订单金额:" + payTrade.getPay_price() + "不匹配," + "客户端请求扣款金额:" + Float.valueOf(amt)/10F);
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("订单金额:" + payTrade.getPay_price() + "不匹配" + "客户端请求的扣款金额:" + Float.valueOf(amt)/10F));
            return;
        }
        Constants.PlatformOption.WxXcxSDK wxXcxSDK = GlobalCache.getPfConfig("WxXcxSDK");
        if (wxXcxSDK.apps.get(appid) == null) {
            logger.info("米大师参数未配置 appId未配置 :"+appid);
            return;
        }
        String offer_id = wxXcxSDK.apps.get(appid).offerId;
        String method = "POST";
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", appid);
        map.put("openid", openid);
        map.put("pf", "android");
        map.put("ts", String.valueOf(ts));
        map.put("zone_id", zoneid);
        map.put("offer_id",offer_id);
        map.put("bill_no",billno);
        map.put("amt",amt);



        String stringA = sortStrByMap(map);
        // 请求方法
        // 指定OpenApi Cgi名字
        String scriptName ="0".equals(sandbox) ?"/cgi-bin/midas/sandbox/pay": "/cgi-bin/midas/pay";
        String url_mothed_key="&org_loc="+scriptName+"&method="+method+"&secret="+wxXcxSDK.apps.get(appid).appKey;
        //stringA+"&org_loc=/cgi-bin/midas/getbalance&method=POST&session_key=V7Q38/i2KXaqrQyl2Yx9Hg=="
        String stringSignTemp = stringA.concat(url_mothed_key);
        logger.info("stringSignTemp_sig:"+stringSignTemp+"签名时用到的key："+wxXcxSDK.apps.get(appid).appKey);
        //1. sig签名
        String sig=null;
        try {
            sig = HmacSHA1And256EncryptionWx.GENHMACSHA256(stringSignTemp,wxXcxSDK.apps.get(appid).appKey);
            logger.info("生成的签名为："+sig);
        } catch (Exception e) {
            e.printStackTrace();

        }
        map.put("sig", sig);

        JedisUtil redis_token = JedisUtil.getInstance("PayServerRedis");
        String accessToken = redis_token.STRINGS.get(appid + "_accessToken");
        logger.info("accessToken:" + accessToken);
        //如果accessToken已过期则重新获取
        if (StringUtil.isEmpty(accessToken)) {
            String requestData = get_AccessToken_Url + "?grant_type=client_credential" + "&appid=" + appid + "&secret=" + wxXcxSDK.apps.get(appid).appSecret;
            String result = RequestUtil.request3Times(requestData);
            if (result != null) {
                JSONObject obj = JSON.parseObject(result);
                logger.info("请求get_token_url返回:" + obj.toString());
                accessToken = obj.getString("access_token");
                //存入redis
                redis.STRINGS.set(appid + "_accessToken", accessToken);
                //设置过期时间为7000S
                redis.expire(appid + "_accessToken", 7000);
            }
        }
        logger.info("获取到的accessToken："+accessToken);
        // 2. mp_sig签名
        String mp_sig=null;
        map.put("access_token", accessToken);

        stringA = sortStrByMap(map);
        url_mothed_key="&org_loc="+scriptName+"&method="+method+"&session_key="+session_key;
        stringSignTemp= stringA.concat(url_mothed_key);
        logger.info("stringSignTemp_mp_sig:"+stringSignTemp);

        try {
            mp_sig = HmacSHA1And256EncryptionWx.GENHMACSHA256(stringSignTemp,session_key);
        } catch (Exception e) {
            e.printStackTrace();

        }

        String postData="{\"amt\":"+Integer.parseInt(amt)+",\"appid\":\""+appid+"\",\"bill_no\":\""+billno+"\",\"mp_sig\":\""+mp_sig+"\",\"openid\":\""+openid+"\",\"offer_id\":\""+offer_id+"\",\"pf\":\""+"android"+"\",\"sig\":\""+sig+"\",\"ts\":"+ts+",\"zone_id\":\""+zoneid+"\"}";


        JSONObject jsonObject = new JSONObject();
        String url ="0".equals(sandbox)
                ?d_sx_url+"?access_token="+accessToken// 沙盒
                :d_xw_url+"?access_token="+accessToken;// 正式
        String result = RequestUtil.post(url,postData);
        logger.info("微信小程序 post:" + url+ ",postData=" + postData + ",result=" + result);
        if (result!=null) {
            JSONObject obj = JSON.parseObject(result);
            if (obj.getIntValue("errcode") == 0) {
                //执行到这里,说明扣款成功 执行发货
                sender.sendAndDisconnect(MsgFactory.getDefaultSuccessMsg());
                payTrade.setPay_price(Float.valueOf(amt)/10);
                payTrade.setPlatform("WxXcxSDK");
                payTrade.setStatus(2);
                payTrade.updateToDB();
                //发钻石
                boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "WxXcxSDK");
                logger.info("isSychnSuccess[" + isSychnSuccess + "]");
            }
            if (obj.getIntValue("errcode") == 90013) {
                JSONObject jsonO = JSON.parseObject(result);
                sender.sendAndDisconnect(jsonO);
            }
        }

    }

    /**
     *
     * @param params
     * @return
     */
    public static String sortStrByMap( HashMap<String,String> params ){

        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);
        StringBuilder buffer= new StringBuilder();
        for(int i=0;i<keys.length;i++){
            buffer.append(keys[i]).append("=").append(params.get(keys[i]));
            if (i!=keys.length-1)
            {
                buffer.append("&");
            }
        }
        return buffer.toString();
    }

    @Cmd("/p/code2accessToken/cb")
    public void code2accessToken(User sender, HttpRequestJSONObject params){
        logger.info("---------------进入方法---------------");
        Constants.PlatformOption.WxXcxSDK config = GlobalCache.getPfConfig("WxXcxSDK");
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        JSONObject jsonObject = JSON.parseObject(json);
        String appId  =jsonObject.getString("appId");
        String code = jsonObject.getString("code");
        String result = RequestUtil.request3Times(code2_Session_Url + "?" + "appid=" + appId + "&secret=" + config.apps.get(appId).appSecret + "&js_code=" + code + "&grant_type=authorization_code");
        logger.info("請求獲取到的的result："+result);
        if (result != null) {
            JSONObject obj = JSON.parseObject(result);
            logger.info("获取到的数据为:" + obj.toString());
            sender.sendAndDisconnect(obj);
        }

    }

}



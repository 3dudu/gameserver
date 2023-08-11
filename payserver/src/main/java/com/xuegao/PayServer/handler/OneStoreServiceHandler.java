package com.xuegao.PayServer.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.vo.OneStoreNotifyData;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.StringUtil;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class OneStoreServiceHandler {
    static Logger LOGGER = Logger.getLogger(OneStoreServiceHandler.class);


    public final static String SANDBOX = "sbpp.onestore.co.kr";
    public final static String PRODUCT = "apis.onestore.co.kr";

    /**
     * oneStore验证
     *
     * @param sender
     * @param params
     */
    @Cmd("/p/OneStoreNotify/cb")
    public void OneStoreNotify(User sender, HttpRequestJSONObject params) throws Exception {
        String json = null;
        if (!params.isEmpty()) {
            json = params.toJSONString();
        } else {
            json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
        }
        OneStoreNotifyData notifyData = JSONObject.toJavaObject(JSON.parseObject(json), OneStoreNotifyData.class);
        JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(notifyData.developerPayload, notifyData.developerPayload);
        if (isSaveSuc == 0L) {
            LOGGER.info("同一订单多次请求,无须重复处理:order_id:" + notifyData.developerPayload);
            sender.sendAndDisconnect("fail");
            return;
        } else {
            redis.expire(notifyData.developerPayload, 30);
        }
        OrderPo payTrade = OrderPo.findByOrdId(notifyData.developerPayload);
        if (payTrade == null) {
            LOGGER.info("订单号:" + notifyData.developerPayload + ",不存在");
            sender.sendAndDisconnect("fail");
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            LOGGER.info("订单已经处理完毕,无须重复处理");
            sender.sendAndDisconnect("fail");
            return;
        }
        Constants.PlatformOption.OneStore oneStore = GlobalCache.getPfConfig("OneStore");
        String cause = "";
        //获取配置参数
        String client_id=oneStore.apps.get(payTrade.getChannelCode()).client_id;
        String client_secret=oneStore.apps.get(payTrade.getChannelCode()).client_secret;
        String url=oneStore.apps.get(payTrade.getChannelCode()).sanboxOrProduct;
        String packageName = notifyData.packageName;
        String productId = notifyData.productId;
        String purchaseId = notifyData.purchaseId;
        PurchaseDetail detail = getPurchaseDetails(purchaseId, packageName, productId,client_id,client_secret,url);
        if (null==detail) {
            cause = "OneStore发起查询购买商品详情请求返回Null";
            LOGGER.info(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        int consumptionState = detail.consumptionState;
        //已购商品消耗状态（0：未消耗，1：消耗）
        if (consumptionState!=1) {
            cause = "已购商品消耗状态为:0-未消耗";
            LOGGER.info(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        //校验客户端传递的订单与oneStore返回的订单是否一致
        if (!notifyData.developerPayload.equals(detail.developerPayload)) {
            cause = "客户端传递的订单与oneStore返回的订单不一致,客户端传递的订单为:"+notifyData.developerPayload+",oneStore返回的订单为:"+detail.developerPayload;
            LOGGER.info(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        //校验档位
        String product_id = oneStore.apps.get(payTrade.getChannelCode()).products.get(payTrade.getPro_idx()+"");
        LOGGER.info("product_id:" + product_id + "========" + "回调productId:" + notifyData.productId);
        if (StringUtil.isEmpty(product_id) || !product_id.equals(notifyData.productId)) {
            cause = "充值档位未配置或充值档位不匹配";
            LOGGER.info(cause);
            sender.sendAndDisconnect("fail");
            return;
        }
        //成功
        sender.sendAndDisconnect("0");
        payTrade.setThird_trade_no(notifyData.orderId);
        payTrade.setPlatform("OneStore");
        payTrade.setStatus(2);
        payTrade.updateToDB();
        //发钻石
        boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "OneStore");
        LOGGER.info("isSychnSuccess[" + isSychnSuccess + "]");
    }


    /**
     * 2.2 接口: getPurchaseDetailsByProductId (查询购买商品详情)
     * URI: https://{host}/v2/purchase/details-by-productid/{purchaseId}/{packageName}/{productId}
     * Method: GET
     *
     * @throws Exception
     */
    public PurchaseDetail getPurchaseDetails(String purchaseId, String packageName, String productId,String client_id,String client_secret,String reqUrl) throws Exception {

        URL url = new URL("https://" + reqUrl + "/v2/purchase/details-by-productid/" + purchaseId + "/" + packageName + "/" + productId);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        //get请求的话默认就行了，post请求需要setDoOutput(true)，这个默认是false的。
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        String access_token = fetchAccessTokenV2(client_id,client_secret,reqUrl);
        urlConnection.setRequestProperty("Authorization", "Bearer " + access_token);
        urlConnection.connect();
        int code = urlConnection.getResponseCode();
        LOGGER.info("code=" + code);
        //开始获取数据
        BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        byte[] arr = new byte[1024];
        while ((len = bis.read(arr)) != -1) {
            bos.write(arr, 0, len);
            bos.flush();
        }
        bos.close();
        String rslt = bos.toString("utf-8");
        LOGGER.info("rslt:" + rslt);
        PurchaseDetail purchaseDetail = JSON.parseObject(rslt, PurchaseDetail.class);
        LOGGER.info("purchaseDetail:" + purchaseDetail);
        return purchaseDetail;

    }

    static class PurchaseDetail {
        int consumptionState;//Integer	1	true	已购商品消耗状态（0：未消耗，1：消耗）
        String developerPayload;//String	200	true	开发者提供的支付唯一序列号
        int purchaseState;//Integer	1	true	购买状态（0：完成购买，1：完成取消）
        long purchaseTime;//Integer	13	true	购买时间（ms）

        public int getConsumptionState() {
            return consumptionState;
        }

        public void setConsumptionState(int consumptionState) {
            this.consumptionState = consumptionState;
        }

        public String getDeveloperPayload() {
            return developerPayload;
        }

        public void setDeveloperPayload(String developerPayload) {
            this.developerPayload = developerPayload;
        }

        public int getPurchaseState() {
            return purchaseState;
        }

        public void setPurchaseState(int purchaseState) {
            this.purchaseState = purchaseState;
        }

        public long getPurchaseTime() {
            return purchaseTime;
        }

        public void setPurchaseTime(long purchaseTime) {
            this.purchaseTime = purchaseTime;
        }

        @Override
        public String toString() {
            return "PurchaseDetail [consumptionState=" + consumptionState + ", developerPayload=" + developerPayload
                    + ", purchaseState=" + purchaseState + ", purchaseTime=" + purchaseTime + "]";
        }
    }

    /**
     * 每次调用获取一次AccessToken
     */
    public String fetchAccessTokenV2(String client_id,String client_secret,String reqUrl) {
        String access_token = null;
        try {
            URL url = new URL("https://" + reqUrl + "/v2/oauth/token");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Proxy-Connection", "Keep-Alive");
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            StringBuffer params = new StringBuffer();
            // 表单参数与get形式一样
            params.append("grant_type=client_credentials").append("&").append("client_id").append("=").append(client_id)
                    .append("&").append("client_secret").append("=").append(client_secret);

            out.write(params.toString());
            out.flush();
            out.close();
            // 开始获取数据
            BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            byte[] arr = new byte[1024];
            while ((len = bis.read(arr)) != -1) {
                bos.write(arr, 0, len);
                bos.flush();
            }
            bos.close();
            String rslt = bos.toString("utf-8");
            LOGGER.info("v2:rslt:" + rslt);
            AccessTokenInfo accTokenInfo = JSON.parseObject(rslt, AccessTokenInfo.class);
            // 获取请求对象
            LOGGER.info("v2:accTokenInfo:" + accTokenInfo.toString());
            if ("SUCCESS".equals(accTokenInfo.status) || "success".equals(accTokenInfo.status)) {
                access_token = accTokenInfo.access_token;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return access_token;
    }

/*    protected String fetchAccessToken() {

        JedisUtil jedis = JedisUtil.getInstance("PayServerRedis");
        // AccessToken的有效时间为3600秒，如果有效时间到期或只剩下600秒以下，再次调用 getAccessToken()，可获取新令牌。
        // redis 设置 3000 秒有效期
        String access_token = jedis.STRINGS.get("access_token_onestore");
        try {
            if (null == access_token) { // access_token 过期了

                URL url = new URL("https://" + SANDBOX + "/v2/oauth/token");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Proxy-Connection", "Keep-Alive");
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                StringBuffer params = new StringBuffer();
                String client_id = ONESTORE_CONFIGURE.getClient_id();
                String client_secret = ONESTORE_CONFIGURE.getClient_secret();
                // 表单参数与get形式一样
                params.append("grant_type=client_credentials").append("&")
                        .append("client_id").append("=").append(client_id).append("&")
                        .append("client_secret").append("=").append(client_secret);

                out.write(params.toString());
                out.flush();
                out.close();
                //开始获取数据
                BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int len;
                byte[] arr = new byte[1024];
                while ((len = bis.read(arr)) != -1) {
                    bos.write(arr, 0, len);
                    bos.flush();
                }
                bos.close();
                String rslt = bos.toString("utf-8");
                LOGGER.info("rslt:" + rslt);
                AccessTokenInfo accTokenInfo = JSON.parseObject(rslt, AccessTokenInfo.class);
                // 获取请求对象
                LOGGER.info("accTokenInfo:" + accTokenInfo.toString());
                if ("SUCCESS".equals(accTokenInfo.status) || "success".equals(accTokenInfo.status)) {
                    access_token = accTokenInfo.access_token;
                    // 将 key 的值设为 value ，当且仅当 key 不存在。
                    long isSaveSuc = jedis.STRINGS.setnx("access_token_onestore", access_token);
                    if (isSaveSuc == 0L) {// 说明有人已经在写了 那么延迟15毫秒之后再次获取
                        try {
                            Thread.sleep(15);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        access_token = jedis.STRINGS.get("access_token_onestore");
                    }
                    // 设置key生存时间，当key过期时，它会被自动删除。
                    jedis.expire("access_token_onestore", 3000);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.returnJedis(jedis.getJedis());
        }

        return access_token;
    }*/


    //	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
//		String  json = "{\"status\":\"SUCCESS\",\"client_id\":\"com.sanguozhirpg.fungames.onestore\",\"access_token\":\"bee48437-e776-4569-ab4b-16eca84887d6\",\"token_type\":\"bearer\",\"expires_in\":3109,\"scope\":\"/v2/purchase/*,/api/recurringpurchase/*,/api/purchase/*\"}";
//		 AccessTokenInfo accTokenInfo = JSON.parseObject(json, AccessTokenInfo.class);
//         System.out.println(accTokenInfo.toString());
//	}
    static class AccessTokenInfo {

        String status;//String	7	访问令牌获取结果
        String client_id;//	String	255	OAuth认证 client_id
        String access_token;//	String	36
        String token_type;//	String	6	bearer 只提供 bearer方式
        int expires_in;//Integer	10	令牌有效期，单位为秒（second）
        String scope;//String	1024	令牌使用范围

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getClient_id() {
            return client_id;
        }

        public void setClient_id(String client_id) {
            this.client_id = client_id;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getToken_type() {
            return token_type;
        }

        public void setToken_type(String token_type) {
            this.token_type = token_type;
        }

        public int getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(int expires_in) {
            this.expires_in = expires_in;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        @Override
        public String toString() {
            return "AccessTokenInfo [status=" + status + ", client_id=" + client_id + ", access_token=" + access_token
                    + ", token_type=" + token_type + ", expires_in=" + expires_in + ", scope=" + scope + "]";
        }

    }
}

package com.xuegao.PayServer.util.wxpay;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.xuegao.PayServer.data.Constants;
import org.apache.log4j.Logger;

import com.xuegao.PayServer.data.Constants.PlatformOption.WxPayParam;
import com.xuegao.PayServer.data.GlobalCache;

public class WxOrderQueryUtil {
	static Logger logger=Logger.getLogger(WxOrderQueryUtil.class);
	public static String WEIXIN_ORDER_FIND_URL="https://api.mch.weixin.qq.com/pay/orderquery";
	public static String APPID="wxca9f09e1e3f7e4db";
	public   static String MCHID="1442949202";
	public static String KEY="auZbXVCHXo7FDFknMkuzrkWPiocgvS4w";

	public static void main(String[] args) {
		String [] ords=new String[44];

		String odrs ="VTrHqK1801131014ce006cc34e8e188c,VTrHqK18011413238d8e0191e672aab4,VTrHqK18011421497227dd47c0c2ebe7,VTrHqK1801142154373f7b7c9411df47,VTrHqK18011510581e74780245efa6b3,VTrHqK18011517376d7455ea34c0607b,VTrHqK18011519164aed480906a95352,VTrHqK1801161150aa858d5d6ddab761,VTrHqK180116115817adb2d67b46656d,VTrHqK1801161159fb015fd0c15c8191,VTrHqK1801161200bbe62469bdd9ed88,VTrHqK1801161226d8cc02c9de8e671e,VTrHqK180116210170d5ee24de87ac9c,VTrHqK180117100184310f26f997976a,VTrHqK1801171007d6a32060adff04a0,VTrHqK1801171256cdecfdf7561d1702,VTrHqK18011713580cd280bc3ff64f30,VTrHqK18011714297a4bdad5713af9f6,VTrHqK180117152633e4a0fec382f641,VTrHqK1801171527a56250e1ad08b774,VTrHqK180117175708208fe41e099c20,VTrHqK18011721341dcb61aca67f9c05,VTrHqK18011721352b1298b3b432a642,VTrHqK1801172135730f2d091cc47f26,VTrHqK1801172135e4aad88c9b30ae71,VTrHqK18011721368730409401b28d56,VTrHqK1801172136967935340bc6b5b9,VTrHqK1801172136aea2098ea8a427b1,VTrHqK180118001492cb48259b457240,VTrHqK1801180014a58ba913bc421115,VTrHqK180118001511ef7562e8e96e56,VTrHqK180118001542a9c1c01c0368bb,VTrHqK1801180015527fa03a82400f82,VTrHqK180118001583e920bbb5a04214,VTrHqK1801180015f27bca1052ac996d,VTrHqK1801181526cb21ae937ad5755a,VTrHqK1801181816c6959ce5ea4ff03a,VTrHqK18011823172314e69a3d230742,VTrHqK18011922554af507014013d189,VTrHqK180120011597a615bebc0b0a1c,VTrHqK1801200116994a268a5245263a,VTrHqK180120011766bde3678e3af4ea,VTrHqK18012002599799f94fd32914a3,VTrHqK1801200634f0ffb9e3ecbb27a2,VTrHqK1801200635cd096782491a1a07,VTrHqK18012011011f0d7d20c321a179,VTrHqK180120110187c547c2109025e3,VTrHqK180121022027d53f47d1bcb39b,VTrHqK18012102256b76bb33186d0f1b,VTrHqK180121023907efc9967c40a010,VTrHqK18012102397ca90bef9a35e0f0,VTrHqK18012110523b6f230940857070,VTrHqK180121111086b56b4910ef5252,VTrHqK180121111641dc0462a1d86609,VTrHqK18012111169eea9b28f6fccf0f,VTrHqK1801211143d3fd7746a99eb664,VTrHqK180121123395a2bddadac4d2dd,VTrHqK1801211233bfbd868d72fd97af,VTrHqK180121130328034a179995a7d9,VTrHqK18012113035f5f6e4b90da51bd,VTrHqK1801211303a00fc7c701458c4c,VTrHqK1801211516787759e44515c464,VTrHqK180121162903d7bcccbfb3225a,VTrHqK18012120054dd7161c0648119d,VTrHqK18012208433af16640f38fb2b1,VTrHqK1801220843786f80ba84ad3222,VTrHqK18012218120d5dc7686daa7347,VTrHqK180123090191d839332f353d35,VTrHqK1801230953675c36d68d002a43,VTrHqK180123185426934eeab054497c,VTrHqK1801240710a2721a5407ca0a6f,VTrHqK18012413169a84ae45c1125759,VTrHqK18012416462ec6203fc595fec3,VTrHqK180125074993c80490adf593fb,VTrHqK1801261438f8bb9b01c502e1a8,VTrHqK180126183052363473c087e593,VTrHqK180127015260398c8ca98b40db,VTrHqK180128110818f9ebbb17d3ed73,VTrHqK1801281155496d48a4b6ac0384,VTrHqK1801291536ba37ca88fb168b08,VTrHqK1801292052c9a4555052663383,VTrHqK18013013115dd0d9568256a40f,VTrHqK1801301325fd64f38dd9ad9f91,VTrHqK180130160933b9cfab6676e00c,VTrHqK1801301959b52e8a0587521560,VTrHqK18013023466d9fa9facc85a15a";
		String splOrds []= odrs.split(",");
		int len =  splOrds.length;


//		for(String outOrd:splOrds){
//			new WxOrderQueryUtil().wxGZHOrderFind(outOrd);
//		}

	}
	 /**
     * 微信账单查询
     * @return 订单存在 返回 true
     */
    public static Map<String, String>  wxGZHOrderFind(String  outTradeNo,String channel_code) {
    	long startTime = System.currentTimeMillis();
        Map<String, String> data = null;
        try {
            String xml = WXGZHOrderGenerate(outTradeNo,channel_code);
            String res = httpsRequest(WEIXIN_ORDER_FIND_URL, "POST", xml);
            data = XMLUtil.doXMLParse(res);
            logger.info("微信账单查询:" + data.toString());
            // 通信异常
            if (!data.get("return_code").equals("SUCCESS")) {
//               throw new TradeException(GatewayCode.CommunicationError);
            	System.out.println("GatewayCode.CommunicationError");
            	 data = null;
            }
            if (!data.get("result_code").equals("SUCCESS")) {
//               throw new TradeException(GatewayCode.OrderFindErrror);
            	System.out.println("GatewayCode.OrderFindErrror");
            	 data = null;
            }
            if (!data.get("trade_state").equals("SUCCESS")) {
//               throw new TradeException(GatewayCode.OrderPayError);
            	System.out.println("GatewayCode.OrderPayError");
            	 data = null;
            }

        } catch (Exception e) {
//            throw new TradeException(GatewayCode.PayXmlError);
        	 System.out.println("GatewayCode.PayXmlError");
        	 e.printStackTrace();
        	 data = null;
        }
        System.out.println(System.currentTimeMillis()-startTime);
        return data;
    }

    /**
     * 微信H5查询订单
     * @param outTradeNo
     * @return
     */
    public static Map<String, String>  wxH5OrderFind(String  outTradeNo ,String channel_code) {
        long startTime = System.currentTimeMillis();
        Map<String, String> data = null;
        try {
            String xml = WXH5OrderGenerate(outTradeNo,channel_code);
            String res = httpsRequest(WEIXIN_ORDER_FIND_URL, "POST", xml);
            data = XMLUtil.doXMLParse(res);
            logger.info("微信账单查询:" + data.toString());
            // 通信异常
            if (!data.get("return_code").equals("SUCCESS")) {
//               throw new TradeException(GatewayCode.CommunicationError);
                System.out.println("GatewayCode.CommunicationError");
                data = null;
            }
            if (!data.get("result_code").equals("SUCCESS")) {
//               throw new TradeException(GatewayCode.OrderFindErrror);
                System.out.println("GatewayCode.OrderFindErrror");
                data = null;
            }
            if (!data.get("trade_state").equals("SUCCESS")) {
//               throw new TradeException(GatewayCode.OrderPayError);
                System.out.println("GatewayCode.OrderPayError");
                data = null;
            }

        } catch (Exception e) {
//            throw new TradeException(GatewayCode.PayXmlError);
            System.out.println("GatewayCode.PayXmlError");
            e.printStackTrace();
            data = null;
        }
        System.out.println(System.currentTimeMillis()-startTime);
        return data;
    }



    /**
     * 微信公众号生成订单。
     *
     * @param orderOutId
     * @param
     * @return
     * @throws Exception
     */
    public  static String WXGZHOrderGenerate(String orderOutId,String channel_code) throws Exception {

    	WxPayParam wxconfig = GlobalCache.getPfConfig("WxPayParam");
        Map<String, String> param = new HashMap<String, String>();
        String partnerId = "";
        //如果回调里的appid在后台没找到就是old_parameter这套参数
        if (wxconfig.apps.get(channel_code)==null) {
            channel_code = "old_parameter";
        }
        param.put("appid", wxconfig.apps.get(channel_code).appId);//wxpayparam.getAppid());//APPID
        param.put("mch_id",wxconfig.apps.get(channel_code).partnerId);//wxpayparam.getPartnerId());//MCHID
        param.put("nonce_str", generateNonceStr());
        param.put("out_trade_no", orderOutId);
        String sign =generateSignature(param, wxconfig.apps.get(channel_code).apiKey,"MD5");//KEY  wxpayparam.getApiKey()
        param.put("sign", sign);
        return GetMapToXML(param);
    }

    /**
     * 微信H5生成订单
     * @param orderOutId
     * @return
     * @throws Exception
     */
    public  static String WXH5OrderGenerate(String orderOutId,String channel_code) throws Exception {

        Constants.PlatformOption.WxPayH5Pay wxconfig = GlobalCache.getPfConfig("WxPayH5Pay");
        String mch_id=wxconfig.apps.get(channel_code).mch_id;
        String apiKey=wxconfig.apps.get(channel_code).apiKey;
        Map<String, String> param = new HashMap<String, String>();
        param.put("appid", wxconfig.apps.get(channel_code).app_id);//APPID
        param.put("mch_id",mch_id);//MCHID
        param.put("nonce_str", generateNonceStr());
        param.put("out_trade_no", orderOutId);
        String sign =generateSignature(param, apiKey,"MD5");//KEY  wxpayparam.getApiKey()
        param.put("sign", sign);
        return GetMapToXML(param);
    }


    /**
     * 获取随机字符串 Nonce Str
     *
     * @return String 随机字符串
     */
    public static String generateNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }
    /**
     * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
     *
     * @param data 待签名数据
     * @param key API密钥
     * @param signType 签名方式
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String key, String signType) throws Exception {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals("sign")) {
                continue;
            }
            if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("key=").append(key);
        if ("MD5".equals(signType)) {
            return MD5(sb.toString()).toUpperCase();
        }
        else if ("HmacSHA256".equals(signType)) {
            return HMACSHA256(sb.toString(), key);
        }
        else {
            throw new Exception(String.format("Invalid sign_type: %s", signType));
        }
    }

    /**
     * 生成 MD5
     *
     * @param data 待处理数据
     * @return MD5结果
     */
    public static String MD5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }
    /**
     * 生成 HMACSHA256
     * @param data 待处理数据
     * @param key 密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String HMACSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }
    //发起微信支付请求
    public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setReadTimeout(5*1000);// 读超时 设置为5秒
            conn.setConnectTimeout(5*1000);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            return buffer.toString();
        } catch (ConnectException ce) {
            ce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //Map转xml数据
    public static String GetMapToXML(Map<String,String> param){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        for (Map.Entry<String,String> entry : param.entrySet()) {
            sb.append("<"+ entry.getKey() +">");
            sb.append(entry.getValue());
            sb.append("</"+ entry.getKey() +">");
        }
        sb.append("</xml>");
        return sb.toString();
    }
}

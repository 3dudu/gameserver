package com.xuegao.PayServer.util.wxpay;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.Constants.PlatformOption.WxPayParam;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.util.MD5Util;

/***
 *
 * 参看:http://blog.csdn.net/gbguanbo/article/details/50915333
 *
 */
public class WxPayCommonUtil {

	static Logger logger=Logger.getLogger(WxPayCommonUtil.class);
	// 微信参数配置 <仅仅支持默认通话挂机参数>
	// public static String API_KEY = "auZbXVCHXo7FDFknMkuzrkWPiocgvS4w";
	// public static String APPID = "wxca9f09e1e3f7e4db";
	// public static String MCH_ID = "1442949202";

	// 随机字符串生成
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	// 请求xml组装
	public static String getRequestXml(SortedMap<String, Object> parameters) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		Set es = parameters.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			if ("attach".equalsIgnoreCase(key) || "body".equalsIgnoreCase(key) || "sign".equalsIgnoreCase(key)) {
				sb.append("<" + key + ">" + "<![CDATA[" + value + "]]></" + key + ">");
			} else {
				sb.append("<" + key + ">" + value + "</" + key + ">");
			}
		}
		sb.append("</xml>");
		return sb.toString();
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
			if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + API_KEY);
		String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
		return sign;
	}

	// 请求方法
	public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		try {

			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
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
			logger.error("连接超时：{}" + ce);
			ce.printStackTrace();
		} catch (Exception e) {
			logger.error("https请求异常：{}" + e);
			e.printStackTrace();
		}
		return null;
	}


	// xml解析
	public static Map doXMLParse(String strxml) throws JDOMException, IOException {
		strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

		if (null == strxml || "".equals(strxml)) {
			return null;
		}

		Map m = new HashMap();

		InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			String k = e.getName();
			String v = "";
			List children = e.getChildren();
			if (children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = getChildrenText(children);
			}

			m.put(k, v);
		}

		// 关闭流
		in.close();

		return m;
	}

	public static String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if (!children.isEmpty()) {
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if (!list.isEmpty()) {
					sb.append(getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}

		return sb.toString();
	}

	/**
	 * 订单No sdk 订单号 totalAmount 订单金额 description 充值 描述
	 */
	public static Map<String, String> weixinPrePay(String ordId, BigDecimal totalAmount, String description,
			String notify_url, String client_iP, String trade_type,String channel_code) {
		WxPayParam wxconfig = GlobalCache.getPfConfig("WxPayParam");
		SortedMap<String, Object> parameterMap = new TreeMap<String, Object>();
		parameterMap.put("appid", wxconfig.apps.get(channel_code).appId);// APPID);//
		parameterMap.put("mch_id", wxconfig.apps.get(channel_code).partnerId);// MCH_ID);//
		parameterMap.put("nonce_str", WxPayCommonUtil.getRandomString(32));
		// body 商品描述交易字段格式根据不同的应用场景按照以下格式：APP——需传入应用市场上的APP名字-实际商品名称，天天爱消除-游戏充值。
		parameterMap.put("body",
				StringUtils.abbreviate(description.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 600));
		parameterMap.put("out_trade_no", ordId);
		parameterMap.put("fee_type", "CNY");

		BigDecimal total = totalAmount.multiply(new BigDecimal(100));
		java.text.DecimalFormat df = new java.text.DecimalFormat("0");
		parameterMap.put("total_fee", df.format(total));
		parameterMap.put("spbill_create_ip", Constants.WEBIP);
		parameterMap.put("notify_url", notify_url);
		// parameterMap.put("trade_type", "APP");
		parameterMap.put("trade_type", trade_type);
		logger.info("parameterMap:"+parameterMap);
		String sign = WxPayCommonUtil.createSign("UTF-8", parameterMap,wxconfig.apps.get(channel_code).apiKey);// API_KEY);//

		parameterMap.put("sign", sign);
		String requestXML = WxPayCommonUtil.getRequestXml(parameterMap);
		logger.info("requestXML:"+requestXML);
//		if(wxconfig.isSandBox){
//			requnifiedorderUrl = "https://api.mch.weixin.qq.com/sandboxnew/pay/unifiedorder";
//		}else{
//			requnifiedorderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
//		}
		String requnifiedorderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		String result = WxPayCommonUtil.httpsRequest(requnifiedorderUrl, "POST",
				requestXML);
		logger.info("result:"+ result);
		Map<String, String> map = null;
		try {
			map = WxPayCommonUtil.doXMLParse(result);
		} catch (JDOMException e) {
			logger.error("JDOMException异常：{}" + e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException异常：{}" + e);
			e.printStackTrace();
		}
		logger.info("weixinPrePay:"+map);
		return map;
	}

	/**
	 * 调起支付接口 请求参数:
	 * https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12&index=2
	 *
	 * @throws UnsupportedEncodingException
	 *
	 */
	public static Map<String, Object> createSignAgain(OrderPo payment, String client_iP,String  proDesc,String channel_code)
			throws UnsupportedEncodingException {

		WxPayParam wxconfig = GlobalCache.getPfConfig("WxPayParam");
		logger.info("wxconfig:"+wxconfig.toString());
		Map<String, String> map = weixinPrePay(payment.getOrder_id(), new BigDecimal(payment.getPay_price()),
				proDesc + "", wxconfig.apps.get(channel_code).wxpayNotify, // Constants.WX_NOTIFY_URL
				client_iP, "APP",channel_code); // 如果返回 invalid spbill_create_ip 修改:
									// 使用localhost访问会出现此问题，使用127.0.0.1访问不会出现此问题
		logger.info("map:"+map.toString());
		// JSONObject jsonObject = new JSONObject();
		SortedMap<String, Object> parameterMap = new TreeMap<String, Object>();
		parameterMap.put("appid", wxconfig.apps.get(channel_code).appId); // APPID);//
		parameterMap.put("partnerid", wxconfig.apps.get(channel_code).partnerId); // MCH_ID);//
		parameterMap.put("prepayid", map.get("prepay_id"));
		parameterMap.put("package", "Sign=WXPay");
		parameterMap.put("noncestr", WxPayCommonUtil.getRandomString(32));
		parameterMap.put("timestamp", System.currentTimeMillis());
		String sign = WxPayCommonUtil.createSign("UTF-8", parameterMap, wxconfig.apps.get(channel_code).apiKey); // API_KEY);//
		parameterMap.put("sign", sign);
		// jsonObject.put("parameterMap",parameterMap);
		return parameterMap;
	}





	//---------------一下是微信 Uitl--------------------------
	/**
	 * 微信的验签
	 */
	public static boolean checkSign(String xmlString,String apiKey) {
		Map<String, String> map = null;
		try {
			map = XMLUtil.doXMLParse(xmlString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String signFromAPIResponse = map.get("sign").toString();
		if (signFromAPIResponse == "" || signFromAPIResponse == null) {
			System.out.println("API返回的数据签名数据不存在，有可能被第三方篡改!!!");
			return false;
		}
		logger.info("服务器回包里面的签名是:" +signFromAPIResponse);
		// 清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
		map.put("sign", "");
		// 将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
		String signForAPIResponse = getSign(map,apiKey);
		if (!signForAPIResponse.equals(signFromAPIResponse)) {
			// 签名验不过，表示这个API返回的数据有可能已经被篡改了
			System.out.println("API返回的数据签名验证不通过，有可能被第三方篡改!!! signForAPIResponse生成的签名为" + signForAPIResponse);
			return false;
		}
		logger.info("恭喜，API返回的数据签名验证通过!!!");
		return true;

	}
    public static String getSign(Map<String, String> map,String apiKey) {
        SortedMap<String, String> signParams = new TreeMap<String, String>();
        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            signParams.put(stringStringEntry.getKey(), stringStringEntry.getValue());
        }
        signParams.remove("sign");
        String sign = PayCommonUtil.createSign("UTF-8", signParams, apiKey);
        return sign;
    }
	private String returnXML(String return_code) {
		return "<xml><return_code><![CDATA["
				+ return_code
				+ "]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
	}

	/**
	 * 获取沙盒 sandbox_signkey
	 *
	 * @author yclimb
	 * @date 2018/9/18
	 */
//	public static void doGetSandboxSignKey() throws Exception {
//		WxPayParam wxconfig = GlobalCache.getPfConfig("WxPayParam");
//		SortedMap<String, Object> data = new TreeMap<String, Object>();
//	    // 商户号
//	    data.put("mch_id", wxconfig.partnerId);
//	    // 获取随机字符串
//	    data.put("nonce_str",WxPayCommonUtil.getRandomString(32));
//	    // 生成签名
//	    String sign = WxPayCommonUtil.createSign("UTF-8", data, wxconfig.apiKey); // API_KEY);//
//	    data.put("sign", sign);
//	    String requestXML = WxPayCommonUtil.getRequestXml(data);
//	    // 得到 sandbox_signkey
//	    String result = WxPayCommonUtil.httpsRequest("https://api.mch.weixin.qq.com/sandboxnew/pay/getsignkey", "POST",
//	    		requestXML);
//
//	    logger.info(result);
//	    Map<String, String> map = null;
//		try {
//			map = WxPayCommonUtil.doXMLParse(result);
//		} catch (JDOMException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	    wxconfig.apiKey= map.get("sandbox_signkey");
//
//
//	}

}

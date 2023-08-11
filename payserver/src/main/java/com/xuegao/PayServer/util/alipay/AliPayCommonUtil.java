package com.xuegao.PayServer.util.alipay;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.Constants.PlatformOption.AliPayParam;
import com.xuegao.PayServer.data.Constants.PlatformOption.WxPayParam;
import com.xuegao.PayServer.handler.AliPayHandler;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.util.wxpay.NumUtil;

/**
 * 参看:https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.J0RL3t&treeId=54&articleId=106370&docType=1
 *
 * */
public class AliPayCommonUtil {
	static Logger logger=Logger.getLogger(AliPayCommonUtil.class);

	public  static final String  CHARSET ="utf-8";


	/**支付宝新版本创建
	 * 支付宝订单
	 * JAVA服务端SDK生成APP支付订单信息示例
	 * https://docs.open.alipay.com/54/106370/
	 * */
	public static String  doWapPost( OrderPo payment,String proDesc,String appId) throws Exception {

        Constants.PlatformOption.AliPayH5Param aliconfigH5 = GlobalCache.getPfConfig("AliPayH5Param");

		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",aliconfigH5.apps.get(appId).appId,
                aliconfigH5.apps.get(appId).merchantKey,
				"json","UTF-8",
                aliconfigH5.apps.get(appId).aliPublicKey
				,"RSA2");   //获得初始化的AlipayClient

		//订单编号，必填
		String out_trade_no = payment.getOrder_id();//new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"),"UTF-8");

		//订单名称，必填[商品描述]
		String subject = proDesc;//new String(request.getParameter("WIDsubject").getBytes("ISO-8859-1"),"UTF-8");

		//付款金额，必填
		String total_fee =String.valueOf(NumUtil.formatBigDecimal(new BigDecimal(payment.getPay_price()), 2));// new String(request.getParameter("WIDtotal_fee").getBytes("ISO-8859-1"),"UTF-8");
		AlipayTradeWapPayRequest alipayRequest=new AlipayTradeWapPayRequest();//创建API对应的request
		alipayRequest.setReturnUrl(aliconfigH5.apps.get(appId).return_url);
		alipayRequest.setNotifyUrl(aliconfigH5.apps.get(appId).alipayNotify );//在公共参数中设置回跳和通知地址
//		alipayRequest.setBizContent("{" +
//		"    \"out_trade_no\":" +out_trade_no+","+
//		"    \"total_amount\":" +total_fee+","+
//		"    \"subject\":" +subject+","+
//		"    \"product_code\":\"QUICK_WAP_WAY\"" +
//		"  }");//填充业务参数

		// 封装请求支付信息
		AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
		model.setOutTradeNo(out_trade_no);
		model.setSubject(subject);
		model.setTotalAmount(total_fee);
		model.setBody("");
		model.setTimeoutExpress("2m");// 2分钟
		model.setProductCode("QUICK_WAP_WAY");
		alipayRequest.setBizModel(model);

		logger.info("AlipayTradeWapPayRequest [udfParams=" + alipayRequest.getTextParams().toString() + ", apiVersion=" + alipayRequest.getApiVersion()
				+ ", bizContent="+ alipayRequest.getBizContent() + ", terminalType=" + alipayRequest.getTerminalType() + ", terminalInfo=" + alipayRequest.getTerminalInfo()
				+ ", prodCode="+ alipayRequest.getProdCode() + ", notifyUrl=" + alipayRequest.getNotifyUrl() + ", returnUrl=" + alipayRequest.getReturnUrl()
				+ ", needEncrypt=" + alipayRequest.isNeedEncrypt()
				+ ", bizModel=" + alipayRequest.getBizModel() + "]");
		try{
			String form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
			//这里和普通的接口调用不同，使用的是sdkExecute
			//AlipayTradeAppPayResponse response = alipayClient.sdkExecute(alipayRequest);
//	        logger.info(cbcontext);//就是orderString 可以直接给客户端请求，无需再做处理。
			//		httpResponse.setContentType("text/html;charset=" + AlipayServiceEnvConstants.CHARSET);
			//		httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
			//		httpResponse.getWriter().flush();
			logger.info("alipayOrderCallback:"+form);
			return form;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**支付宝新版本创建
	 * 支付宝订单
	 * JAVA服务端SDK生成APP支付订单信息示例
	 * https://docs.open.alipay.com/54/106370/
	 * */
	public static String  doPost( OrderPo payment,String proDesc,String appId) throws Exception {
		AliPayParam aliconfig = GlobalCache.getPfConfig("AliPayParam");
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",aliconfig.apps.get(appId).appId,
				aliconfig.apps.get(appId).merchantKey,
				"json","UTF-8",
				aliconfig.apps.get(appId).aliPublicKey
				,"RSA2");   //获得初始化的AlipayClient

		//订单编号，必填
		String out_trade_no = payment.getOrder_id();//new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"),"UTF-8");

		//订单名称，必填[商品描述]
		String subject = proDesc;//new String(request.getParameter("WIDsubject").getBytes("ISO-8859-1"),"UTF-8");

		//付款金额，必填
		String total_fee =String.valueOf(NumUtil.formatBigDecimal(new BigDecimal(payment.getPay_price()), 2));// new String(request.getParameter("WIDtotal_fee").getBytes("ISO-8859-1"),"UTF-8");

		AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();//创建API对应的request

		alipayRequest.setReturnUrl(aliconfig.apps.get(appId).return_url);
		alipayRequest.setNotifyUrl(aliconfig.apps.get(appId).alipayNotify );//在公共参数中设置回跳和通知地址
//		alipayRequest.setBizContent("{" +
//		"    \"out_trade_no\":" +out_trade_no+","+
//		"    \"total_amount\":" +total_fee+","+
//		"    \"subject\":" +subject+","+
//		"    \"product_code\":\"QUICK_WAP_WAY\"" +
//		"  }");//填充业务参数

		// 封装请求支付信息
		AlipayTradeAppPayModel model=new AlipayTradeAppPayModel();
		model.setOutTradeNo(out_trade_no);
		model.setSubject(subject);
		model.setTotalAmount(total_fee);
		model.setBody("");
		model.setTimeoutExpress("2m");// 2分钟
		model.setProductCode("UICK_MSECURITY_PAY");
		alipayRequest.setBizModel(model);

		logger.info("AlipayTradeWapPayRequest [udfParams=" + alipayRequest.getTextParams().toString() + ", apiVersion=" + alipayRequest.getApiVersion()
				+ ", bizContent="+ alipayRequest.getBizContent() + ", terminalType=" + alipayRequest.getTerminalType() + ", terminalInfo=" + alipayRequest.getTerminalInfo()
				+ ", prodCode="+ alipayRequest.getProdCode() + ", notifyUrl=" + alipayRequest.getNotifyUrl() + ", returnUrl=" + alipayRequest.getReturnUrl()
				+ ", needEncrypt=" + alipayRequest.isNeedEncrypt()
				+ ", bizModel=" + alipayRequest.getBizModel() + "]");
		try{
//			String form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
			//这里和普通的接口调用不同，使用的是sdkExecute
			AlipayTradeAppPayResponse response = alipayClient.sdkExecute(alipayRequest);
			String  cbcontext = response.getBody();
//	        logger.info(cbcontext);//就是orderString 可以直接给客户端请求，无需再做处理。
			//		httpResponse.setContentType("text/html;charset=" + AlipayServiceEnvConstants.CHARSET);
			//		httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
			//		httpResponse.getWriter().flush();
			return cbcontext;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static void writeToWeb(String message, String type, HttpServletResponse response) throws IOException{
	        response.setHeader("Pragma", "No-cache");
	        response.setHeader("Cache-Control", "no-cache");
	        response.setContentType("text/" + type +"; charset=utf-8");
	        response.getWriter().write(message);
	        response.getWriter().close();
	    }

	 public static void main(String[] args) throws Exception {

	}

}

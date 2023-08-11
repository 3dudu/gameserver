package com.xuegao.PayServer.util.alipay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.Constants.PlatformOption.AliPayParam;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.Constants.PlatformOption.AliPayH5Param;

/* *
 *类名：AlipayNotify
 *功能：支付宝通知处理类
 *详细：处理支付宝各接口通知返回
 *版本：3.3
 *日期：2012-08-17
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考

 *************************注意*************************
 *调试通知返回时，可查看或改写log日志的写入TXT里的数据，来检查通知返回是否正常
 */
public class AlipayNotifyByAbis {
	static Logger logger=Logger.getLogger(AlipayNotifyByAbis.class);

	/**
	 * 获取远程服务器ATN结果,验证返回URL
	 *
	 * @param notifyId
	 *            通知校验ID
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	public static boolean verifyNotifyId(String notifyId,String app_id,String payType) {

		String partner = "";

		if (StringUtils.isEmpty(notifyId)||StringUtils.isEmpty(payType)) {
			logger.info("参数必须都不为空:notifyId="+notifyId+","+"payType="+payType);
			return false;
		}

		if ("APP".equals(payType)) {
			AliPayParam param = GlobalCache.getPfConfig("AliPayParam");
			if (StringUtils.isEmpty(app_id)) {
				partner = param.apps.get("old_parameter").partnerId;
			} else {
				partner = param.apps.get(app_id).partnerId;
			}
		} else if ("H5".equals(payType)) {
			AliPayH5Param param = GlobalCache.getPfConfig("AliPayH5Param");
			if (StringUtils.isEmpty(app_id)) {
				partner = param.apps.get("old_parameter").partnerId;
			} else {
				partner = param.apps.get(app_id).partnerId;
			}
		}

		// 获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
		String veryfy_url = Constants.HTTPS_VERIFY_URL +"service=notify_verify&" + "partner=" + partner + "&notify_id=" + notifyId;
		logger.info("veryfy_url:"+veryfy_url);
		notifyId = responseLine(veryfy_url);
		logger.info("notifyId:"+notifyId);
		return notifyId != null && notifyId.equals("true");
	}

	public static void main(String[] args) {
//		System.out.println(verifyNotifyId("0bb1459155bfb94cede12cdee95d599kxu"));
	}
	/**
	 * 获取远程服务器ATN结果
	 *
	 * @param aliUrl
	 *            指定URL路径地址
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	private static String responseLine(String aliUrl) {
		try {
			URL url = new URL(aliUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			return in.readLine().toString();

		} catch (Exception e) {
			return null;
		}
	}
}

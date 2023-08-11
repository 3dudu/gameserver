package com.xuegao.PayServer.util.alipay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.xuegao.PayServer.data.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
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
public class AlipayH5Notify {
	private static Logger logger = Logger.getLogger(AlipayH5Notify.class);


    /**USE
     * 根据反馈回来的信息，生成签名结果
     * @param Params 通知返回来的参数数组
     * @param sign 比对的签名结果

     * @return 生成的签名结果
     */
	public static boolean getSignVeryfy(Map<String, String> Params, String sign , String  sign_type,String app_id,String payType) {
    	//过滤空值、sign与sign_type参数
    	Map<String, String> sParaNew = AlipayCore.paraFilter(Params);
        //获取待签名字符串
    	String preSignStr = "";

    	preSignStr = AlipayCore.createLinkString(sParaNew);

    	logger.info("preSignStr:"+preSignStr);
        //获得签名验证结果
        boolean isSign = false;

		String appId = "";
		String aliPublicKey = "";
		if ("APP".equals(payType)) {
			AliPayParam param = GlobalCache.getPfConfig("AliPayParam");
			if (StringUtils.isEmpty(app_id)) {
				aliPublicKey = param.apps.get("old_parameter").aliPublicKey;
			} else {
				aliPublicKey= param.apps.get(app_id).aliPublicKey;
			}
		} else if ("H5".equals(payType)) {
			AliPayH5Param param = GlobalCache.getPfConfig("AliPayH5Param");
			if (StringUtils.isEmpty(app_id)) {
				aliPublicKey = param.apps.get("old_parameter").aliPublicKey;
			} else {
				aliPublicKey= param.apps.get(app_id).aliPublicKey;
			}
		}
        if(sign_type.equals("RSA2")){// 使用 rsa2 验签
        	try {
				isSign = AlipaySignature.rsaCheckV1(Params,aliPublicKey,"UTF-8","RSA2");
			} catch (AlipayApiException e) {
				e.printStackTrace();
			}
        	logger.info("RSA2: isSign:"+isSign+",Params:"+Params+",pubKey:"+ aliPublicKey);
        }
        return isSign;
    }

}

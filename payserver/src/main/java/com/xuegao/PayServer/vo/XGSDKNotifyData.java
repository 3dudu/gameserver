package com.xuegao.PayServer.vo;
/**
 * @author absir
 *
 */
public  class XGSDKNotifyData {
	public String   trade_no;//sdk订单号
	public String 	out_trade_no; //原厂订单号
	public int      gameId;
	public float 	amount;//成功充值金额(单位:元)  
	public int      ts;//时间戳
	public String 	ext; //商户扩展参数
	public String 	sign;//参数签名(用于签名比对)
	
	public String payment_type;//<0未付款 1 苹果沙盒支付 2 支付宝 3 微信4 AppStore支付 >

	@Override
	public String toString() {
		return "XGSDKNotifyData [trade_no=" + trade_no + ", out_trade_no=" + out_trade_no + ", gameId=" + gameId
				+ ", amount=" + amount + ", ts=" + ts + ", ext=" + ext + ", sign=" + sign + ", payment_type="
				+ payment_type + "]";
	}

	
	
}

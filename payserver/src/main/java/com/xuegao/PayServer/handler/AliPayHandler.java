package com.xuegao.PayServer.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.po.OrderSynchPo;
import com.xuegao.PayServer.po.SlaveServerListPo;
import com.xuegao.PayServer.slaveServer.DataProto.Q_TopUp;
import com.xuegao.PayServer.slaveServer.GlobalSlaveManage;
import com.xuegao.PayServer.slaveServer.ScConnector;
import com.xuegao.PayServer.slaveServer.SlaveServerVo;
import com.xuegao.PayServer.util.HelperRandom;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.util.alipay.AliPayCommonUtil;
import com.xuegao.PayServer.util.alipay.AlipayH5Notify;
import com.xuegao.PayServer.util.alipay.AlipayNotifyByAbis;
import com.xuegao.PayServer.util.wxpay.DoubleCompare;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;

import io.netty.util.CharsetUtil;

public class AliPayHandler {
	static Logger logger = Logger.getLogger(AliPayHandler.class);



	// 创建支付宝预支付订单
	@Cmd("/p/aliwapPrePlaceOrder")
	public void aliwapPrePlaceOrder(User sender, HttpRequestJSONObject params) {
		String trade_no = params.getString("trade_no");
		String proDesc = params.getString("proDesc");
		String packageName = params.getString("packageName");
		String appId = params.getString("appId");
		//兼容老包单套参数 老包这个appId是不传递或者是appId空值
        if (appId==null||appId.equals("")||appId.equals("null")) {
            appId = "old_parameter";
        }
		logger.info("trade_no:" + trade_no+",proDesc="+proDesc+",packageName="+packageName+",appId="+appId);
		OrderPo payTrade = OrderPo.findByOrdId(trade_no);
		JSONObject rs = new JSONObject();
		Map<String, Object> orderMap = new HashMap<>();
		if(null==payTrade){
			logger.info("trade_no:"+trade_no+",订单不存在");
			rs.put("status", Constants.FAIL);
			sender.sendAndDisconnect(orderMap);
			return;
		}
		if(null==proDesc){
			logger.info("proDesc:"+proDesc+",不能为空");
			rs.put("status", Constants.FAIL);
			sender.sendAndDisconnect(orderMap);
			return;
		}
		String orderStr = null;
		try {
			orderStr = AliPayCommonUtil.doWapPost(payTrade,proDesc,appId);
//			logger.info("new:orderStr:" + orderStr);
			if (null == orderStr) {
				logger.error("创建网页支付 界面失败");
				rs.put("status", Constants.FAIL);
				sender.sendAndDisconnect(rs);
				return ;
			}
//			logger.info("支付宝:orderStr->" + orderStr);


			rs.put("orderStr", orderStr);
			rs.put("status", Constants.SUCCESS);
			sender.sendAndDisconnect(rs);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 创建支付宝预支付订单
		@Cmd("/p/aliWebPrePlaceOrder")
		public void aliWebPrePlaceOrder(User sender, HttpRequestJSONObject params) {
			String trade_no = params.getString("trade_no");
			String proDesc = params.getString("proDesc");
			String packageName = params.getString("packageName");
			String appId = params.getString("appId");
            //兼容老包单套参数 老包这个appId是不传递或者是appId空值
            if (appId==null||appId.equals("")||appId.equals("null")) {
                appId = "old_parameter";
            }
			logger.info("trade_no:" + trade_no+",proDesc="+proDesc+",packageName="+packageName+",appId="+appId);
			OrderPo payTrade = OrderPo.findByOrdId(trade_no);
			JSONObject rs = new JSONObject();
			Map<String, Object> orderMap = new HashMap<>();
			if(null==payTrade){
				logger.info("trade_no:"+trade_no+",订单不存在");
				rs.put("status", Constants.FAIL);
				sender.sendAndDisconnect(orderMap);
				return;
			}
			if(null==proDesc){
				logger.info("proDesc:"+proDesc+",不能为空");
				rs.put("status", Constants.FAIL);
				sender.sendAndDisconnect(orderMap);
				return;
			}
			String orderStr = null;
			try {
				logger.info(payTrade.toString());
				orderStr = AliPayCommonUtil.doPost(payTrade,proDesc,appId);
//				logger.info("new:orderStr:" + orderStr);
				if (null == orderStr) {
					logger.error("创建网页支付 界面失败");
					rs.put("status", Constants.FAIL);
					sender.sendAndDisconnect(rs);
					return ;
				}
//				logger.info("支付宝:orderStr->" + orderStr);


				rs.put("orderStr", orderStr);
				rs.put("status", Constants.SUCCESS);
				sender.sendAndDisconnect(rs);
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	// 支付宝 回调
	// ------------------支付宝网页支付 回调 验证-----------------------
	/**
	 * 网页支付 接受 支付宝 验证方式 MD5<和RSA> 同时 也支持 RSA 验签
	 *
	 * @param
	 */
	@Cmd("/p/alipayRSA2Notify/cb")
	public void aliPaycallbackRSA2Notify(User sender, HttpRequestJSONObject params) throws Exception {

		// 通知的参数
		// Map<String, String> notifyParamMap = parseReqParam(request);
		// logger.debug("alipayNotify请求参数:["+notifyParamMap.toString()+"]");

		logger.info("alipayMd5/RSANotify请求参数:[" + params.toJSONString() + "]");
		String json = null;
		if (!params.isEmpty()) {
			json = params.toJSONString();
		} else {
			json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
		}
		logger.info("aliPayData:" + json);
		JSONObject jsonObj = JSON.parseObject(json);
		Map parammap = JSON.parseObject(json, Map.class);
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		// 商户订单号<sdk订单号 由客户端传入给支付宝的>
		String out_trade_no = jsonObj.getString("out_trade_no");
		String app_id = jsonObj.getString("app_id");
		// 支付宝交易号
		String trade_no = jsonObj.getString("trade_no");
		// 交易状态
		String trade_status = jsonObj.getString("trade_status");
		// 异步通知ID
		String notify_id = jsonObj.getString("notify_id");
		// sign
		String sign = jsonObj.getString("sign");
		String sign_type = jsonObj.getString("sign_type");
		// // 本次交易支付的订单金额，单位为人民币（元）
		String total_fee = params.get("total_fee") == null ? jsonObj.getString("total_amount")
				: jsonObj.getString("total_fee");
		String result = "success";
		OrderPo paytradeByDb = OrderPo.findByOrdId(out_trade_no);
		if (null == paytradeByDb) {
			sender.sendAndDisconnect("failure");
			return;// 订单不存在
		}
		if (paytradeByDb.getStatus() >= 2) {
			sender.sendAndDisconnect("success");
			return ;
		}
		if (notify_id != "" && notify_id != null) {
			if (AlipayNotifyByAbis.verifyNotifyId(notify_id,app_id,"APP")) {// AlipayNotify.verifyNotifyId(notify_id)判断成功之后使用getResponse方法判断是否是支付宝发来的异步通知。
				// AliPayParam alipayparam =
				// payparamServiceImp.getAliParam(paytradeByDb.getGid());
				logger.info("服务器ATN结果 验证结果:true (" + notify_id + ")");
				String CHARSET = "utf-8";
				if (AlipayH5Notify.getSignVeryfy(parammap, sign, sign_type,app_id,"APP"))// 使用RSA2验签
				{
					logger.info("支付 验签通过:");
					if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
						// 业务处理
						int rs = DoubleCompare.compareNum(total_fee, paytradeByDb.getPay_price().toString());
						if (rs < 2) {// rs =0 相等 1 第一个数字大 2 第二个数字大
							paytradeByDb.setStatus(2);// 2.验证成功
							paytradeByDb.setPlatform("AliPay");// platform支付宝
							paytradeByDb.setThird_trade_no(trade_no);// 支付宝订单号
							//订单金额以实际接受到返回金额为准 【支付宝返回元单位】
							paytradeByDb.setPay_price(Float.valueOf(total_fee));
							paytradeByDb.updateToDB();

							//发钻石
							boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(paytradeByDb, "AnFengH5");
							logger.info("isSychnSuccess["+isSychnSuccess+"]");
							// TODO 验签成功后
							// 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure

							// 加一个 支付统计 [新增和活跃付费,以及Ltv统计]
//							paytradeByDb.payFlowStatitic();
						}
						sender.sendAndDisconnect("success");
						return ;
					} else// 验证签名失败
					{
						sender.sendAndDisconnect("sign fail");
						return ;
					}
				} else// 验证是否来自支付宝的通知失败
				{
					sender.sendAndDisconnect("response fail");
					return ;
				}
			} else {
				sender.sendAndDisconnect("no notify message");
				return ;
			}
		}
	}

	/**
	 * 支付宝H5回调
	 * @param sender
	 * @param params
	 * @throws Exception
	 */
	@Cmd("/p/alipayH5RSA2Notify/cb")
	public void alipayH5RSA2Notify(User sender, HttpRequestJSONObject params) throws Exception {

		// 通知的参数
		// Map<String, String> notifyParamMap = parseReqParam(request);
		// logger.debug("alipayNotify请求参数:["+notifyParamMap.toString()+"]");

		logger.info("alipayMd5/RSANotify请求参数:[" + params.toJSONString() + "]");
		String json = null;
		if (!params.isEmpty()) {
			json = params.toJSONString();
		} else {
			json = params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
		}
		logger.info("aliPayData:" + json);
		JSONObject jsonObj = JSON.parseObject(json);
		Map parammap = JSON.parseObject(json, Map.class);
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		// 商户订单号<sdk订单号 由客户端传入给支付宝的>
		String out_trade_no = jsonObj.getString("out_trade_no");
		String app_id = jsonObj.getString("app_id");
		// 支付宝交易号
		String trade_no = jsonObj.getString("trade_no");
		// 交易状态
		String trade_status = jsonObj.getString("trade_status");
		// 异步通知ID
		String notify_id = jsonObj.getString("notify_id");
		// sign
		String sign = jsonObj.getString("sign");
		String sign_type = jsonObj.getString("sign_type");
		// // 本次交易支付的订单金额，单位为人民币（元）
		String total_fee = params.get("total_fee") == null ? jsonObj.getString("total_amount")
				: jsonObj.getString("total_fee");
		String result = "success";
		OrderPo paytradeByDb = OrderPo.findByOrdId(out_trade_no);
		if (null == paytradeByDb) {
			sender.sendAndDisconnect("failure");
			return;// 订单不存在
		}
		if (paytradeByDb.getStatus() >= 2) {
			sender.sendAndDisconnect("success");
			return ;
		}
		boolean isSychnSuccess = true;
		if (notify_id != "" && notify_id != null) {
			if (AlipayNotifyByAbis.verifyNotifyId(notify_id,app_id,"H5")) {// AlipayNotify.verifyNotifyId(notify_id)判断成功之后使用getResponse方法判断是否是支付宝发来的异步通知。
				// AliPayParam alipayparam =
				// payparamServiceImp.getAliParam(paytradeByDb.getGid());
				logger.info("服务器ATN结果 验证结果:true (" + notify_id + ")");
				String CHARSET = "utf-8";
				if (AlipayH5Notify.getSignVeryfy(parammap, sign, sign_type,app_id,"H5"))// 使用RSA2验签
				{
					logger.info("支付 验签通过:");
					if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
						// 业务处理
						int rs = DoubleCompare.compareNum(total_fee, paytradeByDb.getPay_price().toString());
						if (rs < 2) {// rs =0 相等 1 第一个数字大 2 第二个数字大
							paytradeByDb.setStatus(2);// 2.验证成功
							paytradeByDb.setPlatform("AliPay");// platform支付宝
							paytradeByDb.setThird_trade_no(trade_no);// 支付宝订单号
							//订单金额以实际接受到返回金额为准 【支付宝返回元单位】
							paytradeByDb.setPay_price(Float.valueOf(total_fee));
							paytradeByDb.updateToDB();

							// 无须验证金额 直接传入给slave 发钻石
							SlaveServerListPo slaveServer = GlobalSlaveManage
									.fecthSlaveServerByServerId(paytradeByDb.getSid());// 服务列表信息
							SlaveServerVo slaveV0 = null;
							if (null != slaveServer) {
								String slave_ip = slaveServer.getIp();
								slaveV0 = GlobalSlaveManage.connectSlave(slave_ip,slaveServer.getPort());
							}
							if (null == slaveV0 || !slaveV0.isActive()) {// 连接
								// 还未建立
								// 或者连接断开了
								logger.error("serverId:" + paytradeByDb.getSid() + ",连接不存在");
								paytradeByDb.setStatus(3);
								isSychnSuccess = false;
								paytradeByDb.updateToDB();
							} else {
								Q_TopUp reqData = new Q_TopUp();
								reqData.ext = "ext"
										+ HelperRandom.randSecendId(System.currentTimeMillis(), 8, reqData.hashCode());
								reqData.playerId = (long) (paytradeByDb.getPid());
								reqData.serverId = paytradeByDb.getSid();
								reqData.proIdx = paytradeByDb.getPro_idx();
								reqData.proPrice = paytradeByDb.getPay_price();
								reqData.platform = "AliPay";
								reqData.trade_no = paytradeByDb.getOrder_id();
								StringBuffer sb = new StringBuffer();
								sb.append(reqData.playerId).append(reqData.serverId).append(reqData.proIdx)
										.append(reqData.proPrice).append(reqData.ext).append(Constants.SYCHPASSWORD);
								logger.debug("sb.:" + sb.toString());
								reqData.sign = MD5Util.md5(sb.toString()).toLowerCase();
								logger.debug("reqData.sign:" + reqData.sign);
								slaveV0.SC_CONNECTOR.sc.qn("/" + reqData.getClass().getSimpleName(),
										(JSONObject) JSON.toJSON(reqData));
							}
							if (!isSychnSuccess) {
								// 同步列表不存在 则 加入同步列表中
								if (null == OrderSynchPo.getOrderSyncPoByOrdId(paytradeByDb.getOrder_id())) {
									OrderSynchPo ordSynchPo = new OrderSynchPo();
									ordSynchPo.setOrder_id(paytradeByDb.getOrder_id());
									ordSynchPo.setSych_cnt(0);
									ordSynchPo.setSych_status(1); // 同步状态 1
									// 定时器正在同步等待通知,2同步成功,3同步失败
									ordSynchPo.setCreate_time(new Date());
									ordSynchPo.insertToDB();
								}
							}
							// TODO 验签成功后
							// 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure

							// 加一个 支付统计 [新增和活跃付费,以及Ltv统计]
//							paytradeByDb.payFlowStatitic();
						}
						sender.sendAndDisconnect("success");
						return ;
					} else// 验证签名失败
					{
						sender.sendAndDisconnect("sign fail");
						return ;
					}
				} else// 验证是否来自支付宝的通知失败
				{
					sender.sendAndDisconnect("response fail");
					return ;
				}
			} else {
				sender.sendAndDisconnect("no notify message");
				return ;
			}
		}
	}

	public static void main(String[] args) {
		String  json= "{\"code\": 104,\"RS_RoleLogin.ext\":\"1232\"}";
		Map parammap = JSON.parseObject(json, Map.class);
		System.out.println(parammap.toString());

	}
}

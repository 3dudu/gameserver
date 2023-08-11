package com.xuegao.PayServer.handler;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.Constants.PlatformOption.WxPayParam;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.po.OrderSynchPo;
import com.xuegao.PayServer.po.SlaveServerListPo;
import com.xuegao.PayServer.slaveServer.GlobalSlaveManage;
import com.xuegao.PayServer.slaveServer.ScConnector;
import com.xuegao.PayServer.slaveServer.SlaveServerVo;
import com.xuegao.PayServer.slaveServer.DataProto.Q_TopUp;
import com.xuegao.PayServer.util.HelperRandom;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.PayServer.util.wxpay.DoubleCompare;
import com.xuegao.PayServer.util.wxpay.NumUtil;
import com.xuegao.PayServer.util.wxpay.WxOrderQueryUtil;
import com.xuegao.PayServer.util.wxpay.WxPayCommonUtil;
import com.xuegao.PayServer.util.wxpay.XMLUtil;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;
import com.xuegao.core.util.DateUtil;

import io.netty.buffer.ByteBuf;
import org.jdom2.JDOMException;

public class WxPayHandler {
	static Logger logger=Logger.getLogger(WxPayHandler.class);

    /**
     * 微信H5支付接口
     * @param sender
     * @param params
     * @throws Exception
     */
    @Cmd("/p/wxPayH5/cb")
    public void wxPayH5(User sender,HttpRequestJSONObject params)  throws Exception{
        Map<String, Object> orderMap = new HashMap<>();
        String trade_no = params.getString("trade_no");//订单号
        String channel_code = params.getString("channel_code");//app_id
        String body = params.getString("body");//充值描述

        logger.info("trade_no:"+trade_no+",channel_code="+channel_code+",body="+body);
        OrderPo payTrade = OrderPo.findByOrdId(trade_no);
        if(null==payTrade){
            logger.info("trade_no:"+trade_no+",订单不存在");
            orderMap.put("status", Constants.FAIL);
            sender.sendAndDisconnect(JSON.toJSON(orderMap));
            return;
        }
        logger.info("trade_no:"+trade_no+",payTrade:"+payTrade.toString());
        Constants.PlatformOption.WxPayH5Pay wxconfig = GlobalCache.getPfConfig("WxPayH5Pay");
        logger.info("wxconfig:"+wxconfig.toString());
		if(channel_code == null || "null".equals(channel_code)){
			channel_code = wxconfig.apps.keySet().iterator().next();
			logger.info("channel_code为null,默认使用第一个配置:"+channel_code);
		}
        //校验前端传的channel_Code在后台是否有配置
        if (null==wxconfig||null==wxconfig.apps.get(channel_code)) {
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("微信H5支付参数channel_code未配置"));
        }
        //校验该channel_Code的mch_id在后台是否有配置
        if (null==wxconfig.apps.get(channel_code).mch_id) {
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("微信H5支付参数mch_id未配置"));
        }
        //校验该channel_code的支付回调地址在后台是否有配置
        if (null==wxconfig.apps.get(channel_code).wxpayNotify) {
            sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("微信H5支付回调地址未配置"));
        }
        try {
            SortedMap<String, Object> parameterMap = new TreeMap<String, Object>();
            parameterMap.put("appid",wxconfig.apps.get(channel_code).app_id);//APPID
            parameterMap.put("mch_id",wxconfig.apps.get(channel_code).mch_id);//MCH_ID
            parameterMap.put("nonce_str",WxPayCommonUtil.getRandomString(32));
            // body 商品描述交易字段格式根据不同的应用场景按照以下格式：APP——需传入应用市场上的APP名字-实际商品名称，天天爱消除-游戏充值。
            parameterMap.put("body",
                    StringUtils.abbreviate(body.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 600));
            parameterMap.put("out_trade_no",trade_no);
            parameterMap.put("fee_type", "CNY");
            parameterMap.put("total_fee",NumUtil.yuanToFen(payTrade.getPay_price()));//微信单位为分
            parameterMap.put("spbill_create_ip", sender.getRemoteIp());
            parameterMap.put("notify_url",wxconfig.apps.get(channel_code).wxpayNotify);//微信H5支付回调地址
            parameterMap.put("trade_type","MWEB");//H5支付标识
            logger.info("parameterMap:"+parameterMap);
            String sign = WxPayCommonUtil.createSign("UTF-8", parameterMap,wxconfig.apps.get(channel_code).apiKey);//API_KEY
            parameterMap.put("sign", sign);
            String requestXML = WxPayCommonUtil.getRequestXml(parameterMap);
            logger.info("requestXML:"+requestXML);
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
            logger.info("微信H5返回:"+map.toString());
            //result_code为SUCCESS return_code一定也为SUCCESS
            if (null != map.get("result_code") && "SUCCESS".equals(map.get("result_code"))) {
                map.put("status", Constants.SUCCESS);
				if (!"".equals(wxconfig.apps.get(channel_code).referer_url)) {
					map.put("referer_url", wxconfig.apps.get(channel_code).referer_url );
				}
                sender.sendAndDisconnect(JSON.toJSON(map));
            }else{
                //返回客户端信息
                map.put("status",Constants.FAIL);
                sender.sendAndDisconnect(JSON.toJSON(map));
            }
        }catch (Exception e) {
            e.printStackTrace();
            orderMap.put("status", Constants.FAIL);
            sender.sendAndDisconnect(JSON.toJSON(orderMap));
        }

    }

    /**
     * 微信H5支付回调接口
     * @param sender
     * @param params
     * @throws Exception
     */
    @Cmd("/p/wxPayH5callbackNotify/cb")
    public void wxPayH5callbackNotify(User sender,HttpRequestJSONObject params)  throws Exception{
        ByteBuf reqdData = params.getHttpBodyBuf();
        String xmlString  = convertByteBufToString(reqdData);
        logger.info("----wxPayH5callbackNotify接收到的数据如下:" +xmlString);
        Map<String, String> map = new HashMap<String, String>();
        String result_code = "";
        String return_code = "";
        String out_trade_no = "";
        map = XMLUtil.doXMLParse(xmlString);
        result_code = map.get("result_code");
        out_trade_no = map.get("out_trade_no");
        return_code = map.get("return_code");

        //微信支付订单号
        String  transaction_id= map.get("transaction_id");
        String total_amount_fen = map.get("total_fee");
        String result="success";
        OrderPo paytradeByDb = OrderPo.findByOrdId(out_trade_no);
        if (null == paytradeByDb) {// 订单号不存在
            logger.error("订单:[out_trade_no:"+out_trade_no+"] 不存在");
            sender.sendAndDisconnect("failure");
            return;
        }
        if(paytradeByDb.getStatus()>=2){
            sender.sendAndDisconnect("success");
            return;
        }
        // 2 回调 游戏服务器
        // 仅仅支付成功 才会通知游戏服务端
        if (null != result_code && "SUCCESS".equals(result_code)) {
            // 加强验证
            // 重新发起 订单查询  为了检测订单是否正常交易完成
            Map<String, String> data = WxOrderQueryUtil.wxH5OrderFind(out_trade_no,paytradeByDb.getChannelCode());
            if(null==data){
                logger.error("微信后台 订单:[out_trade_no:"+out_trade_no+"] 不存在");
                sender.sendAndDisconnect("failure");
                return;
            }else{
                // 1.比较时间   手动支付操作时间 不可能和 微信后台支付成功时间 相同的   微信订单时间 一定大于SDk订单创建时间
                String time=data.get("time_end");//支付完成时间
                Date ordCompleteDate=null;
                SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmss");
                ordCompleteDate=formatter.parse(time);
                logger.info("ordCompleteDate微信订单时间:"+ordCompleteDate+",SDk订单创建时间"+new Date(paytradeByDb.getCreate_time()));
                long diff=DateUtil.diffDays(new Date(paytradeByDb.getCreate_time()), ordCompleteDate);
				  if(diff<0){
					  logger.error("微信后台 订单:[out_trade_no:"+out_trade_no+"] 订单重复,可能涉及刷单,需人工处理");
					  sender.sendAndDisconnect("fail");
					  return;
				  }
                // 2.比较交易订单号是否相同
                if(!transaction_id.equals(data.get("transaction_id"))){
                    logger.error("微信后台 订单:[transaction_id:"+transaction_id+"],与回调支付的订单号不符合:transaction_id:"+transaction_id+",需人工处理");
                    sender.sendAndDisconnect("fail");
                    return;
                }
            }
            Constants.PlatformOption.WxPayH5Pay wxconfig = GlobalCache.getPfConfig("WxPayH5Pay");
            // 调用SDK验证签名
            if (WxPayCommonUtil.checkSign(xmlString,wxconfig.apps.get(paytradeByDb.getChannelCode()).apiKey)) {//使用微信回调的app_id去后台取出对应的apiKey
                String  amount =paytradeByDb.getPay_price().toString();
                String  fen = NumUtil.yuanToFen(Float.valueOf(amount));
                int rs = DoubleCompare.compareNum(total_amount_fen,fen);
                if (rs<2) {// rs =0 相等 1 第一个数字大 2 第二个数字大
                    paytradeByDb.setStatus(2);//2.验证成功
                    paytradeByDb.setPlatform("WxH5Pay");
                    paytradeByDb.setThird_trade_no(transaction_id);
                    //订单金额以实际接受到返回金额为准 【微信返回分单位】
                    paytradeByDb.setPay_price(Float.valueOf(total_amount_fen)/100F);
                    paytradeByDb.updateToDB();
                    //发钻石
                    boolean isSychnSuccess = PaymentToDeliveryUnified.delivery(paytradeByDb, "SMengH5");
                    logger.info("isSychnSuccess[" + isSychnSuccess + "]");
                } else {// 实际支付金额和订单金额不一致
                    logger.error("wx_paytransaction_id:"+transaction_id);
                    logger.error("Wx_trade_no:"+out_trade_no+"实际支付金额:"+total_amount_fen+"和订单金额"+fen+"不一致!");
                    result= "failure";
                }
                // TODO 验签成功后
                // 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure
            } else {
                // TODO 验签失败则记录异常日志，并在response中返回failure.
                String failCtx=  returnXML("FAIL");
                sender.sendAndDisconnect(failCtx);
                return;
            }
        } else {
            sender.sendAndDisconnect("failure");
            return;
        }
        sender.sendAndDisconnect(result);
        return;
    }

	// 创建微信预支付订单
	@Cmd("/p/wxWebPrePlaceOrder")
	public void  wxWebPrePlaceOrder(User sender,HttpRequestJSONObject params){
//		try {
//			WxPayCommonUtil.doGetSandboxSignKey();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		 Map<String, Object> orderMap = new HashMap<>();
		String trade_no = params.getString("trade_no");
		String proDesc = params.getString("proDesc");
		String packageName = params.getString("packageName");
		String channel_code = params.getString("channel_code");
        //兼容老包单套参数 老包这个appId是不传递或者是appId空值
        if (channel_code==null||channel_code.equals("")||channel_code.equals("null")) {
			channel_code = "old_parameter";
        }
		logger.info("trade_no:"+trade_no+",proDesc="+proDesc+",packageName="+packageName+",channel_code="+channel_code);
		OrderPo payTrade = OrderPo.findByOrdId(trade_no);
		if(null==payTrade){
			logger.info("trade_no:"+trade_no+",订单不存在");
			orderMap.put("status", Constants.FAIL);

			sender.sendAndDisconnect(JSON.toJSON(orderMap));
			return;
		}
//		if(null==packageName){
//			logger.info("packageName:"+packageName+",不能为空");
//			orderMap.put("status", Constants.FAIL);
//			sender.sendAndDisconnect(JSON.toJSON(orderMap));
//			return;
//		}
		logger.info("trade_no:"+trade_no+",payTrade:"+payTrade.toString());
		 Map<String, Object> rs =null;
		 String  orderStr=null;
		try {
            WxPayParam wxPayParam =  GlobalCache.getPfConfig("WxPayParam");
			if (wxPayParam.apps.get(channel_code) == null) {
				logger.info("微信：channel_code->" +channel_code+"未配置");
			}
			rs = WxPayCommonUtil.createSignAgain(payTrade,params.getRemoteIp(),proDesc,channel_code);
		    orderStr="weixin://app/"+rs.get("appid")+"/pay/?"
		 		+ "nonceStr="+rs.get("noncestr")
		 		+"&package="+URLEncoder.encode(rs.get("package").toString(),"UTF-8")
		 		+ "&partnerId="+rs.get("partnerid")
		 		+ "&prepayId="+rs.get("prepayid")
		 		+"&timeStamp="+rs.get("timestamp")
		 		+"&sign="+rs.get("sign");
		 logger.info("微信:orderStr->:"+orderStr);
		} catch (Exception e) {
			e.printStackTrace();
			orderMap.put("status", Constants.FAIL);
			sender.sendAndDisconnect(JSON.toJSON(orderMap));
			return;
		}
		orderMap.put("orderStr", orderStr);
		orderMap.putAll(rs);
		 orderMap.put("status", Constants.SUCCESS);
		logger.info("webPrePlaceOrder 返回:"+orderMap);
		sender.sendAndDisconnect(JSON.toJSON(orderMap));
		return;
	}


	// 支付回调
	/**
	 * 微信平台 支付回调 通知接口
	 *
	 */
	@Cmd("/p/wxPaycallbackNotify/cb")
	public void wxPaycallbackNotify(User sender,HttpRequestJSONObject params)  throws Exception{
		ByteBuf  reqdData = params.getHttpBodyBuf();
		String xmlString  = convertByteBufToString(reqdData);
		logger.info("----wxpayNotify接收到的数据如下:" +xmlString);
		Map<String, String> map = new HashMap<String, String>();
		String result_code = "";
		String return_code = "";
		String out_trade_no = "";
		map = XMLUtil.doXMLParse(xmlString);
		result_code = map.get("result_code");
		out_trade_no = map.get("out_trade_no");
		return_code = map.get("return_code");
		//微信支付订单号
		String  transaction_id= map.get("transaction_id");
		String total_amount_fen = map.get("total_fee").toString();

		String result="success";
		OrderPo paytradeByDb = OrderPo.findByOrdId(out_trade_no);
		String channel_code = paytradeByDb.getChannelCode();
		if (null == paytradeByDb) {// 订单号不存在
			logger.error("订单:[out_trade_no:"+out_trade_no+"] 不存在");
			sender.sendAndDisconnect("failure");
			return;
		}
		if(paytradeByDb.getStatus()>=2){
			sender.sendAndDisconnect("success");
			return;
		}
		boolean isSychnSuccess=true;
		// 2 回调 游戏服务器
		// 仅仅支付成功 才会通知游戏服务端
		if (null != result_code && "SUCCESS".equals(result_code)) {

			// 加强验证
			// 重新发起 订单查询  为了检测订单是否正常交易完成
			Map<String, String> data = WxOrderQueryUtil.wxGZHOrderFind(out_trade_no,channel_code);
			if(null==data){
				logger.error("微信后台 订单:[out_trade_no:"+out_trade_no+"] 不存在");
				sender.sendAndDisconnect("failure");
				return;
			}else{
				// 1.比较时间   手动支付操作时间 不可能和 微信后台支付成功时间 相同的   微信订单时间 一定大于SDk订单创建时间
				  String time=data.get("time_end");//支付完成时间
				  Date ordCompleteDate=null;
				  SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmss");
				  ordCompleteDate=formatter.parse(time);
				  long diff=DateUtil.diffDays(new Date(paytradeByDb.getCreate_time()), ordCompleteDate);
				  if(diff<0){
					  logger.error("微信后台 订单:[out_trade_no:"+out_trade_no+"] 订单重复,可能涉及刷单,需人工处理");
					  sender.sendAndDisconnect("fail");
					  return;
				  }
				  // 2.比较交易订单号是否相同
				  if(!transaction_id.equals(data.get("transaction_id"))){
					  logger.error("微信后台 订单:[transaction_id:"+transaction_id+"],与回调支付的订单号不符合:transaction_id:"+transaction_id+",需人工处理");
					  sender.sendAndDisconnect("fail");
					  return;
				  }
			}

			WxPayParam wxconfig = GlobalCache.getPfConfig("WxPayParam");
            if (wxconfig.apps.get(channel_code)==null) {
                channel_code = "old_parameter";
            }

			// 调用SDK验证签名
			if (WxPayCommonUtil.checkSign(xmlString,wxconfig.apps.get(channel_code).apiKey)) {

					String  amount =paytradeByDb.getPay_price().toString();
					String  fen = NumUtil.yuanToFen(amount);
					int rs = DoubleCompare.compareNum(total_amount_fen,fen);
					if (rs<2) {// rs =0 相等 1 第一个数字大 2 第二个数字大
//					if (fen.equals(total_amount_fen)) {// 支付金额
						paytradeByDb.setStatus(2);//2.验证成功
						paytradeByDb.setPlatform("WXPay");//platform:0 预付单	1 苹果2 支付宝3 微信
						paytradeByDb.setThird_trade_no(transaction_id);
						//订单金额以实际接受到返回金额为准 【微信返回分单位】
						paytradeByDb.setPay_price(Float.valueOf(total_amount_fen)/100F);
						paytradeByDb.updateToDB();
						// 无须验证金额 直接传入给slave 发钻石
						SlaveServerListPo slaveServer= GlobalSlaveManage.fecthSlaveServerByServerId(paytradeByDb.getSid());// 服务列表信息
						SlaveServerVo 	slaveV0 = 	slaveServer == null ? null : GlobalSlaveManage.connectSlave(slaveServer.getIp(),slaveServer.getPort());
						if(null==slaveV0||!slaveV0.isActive()){// 连接 还未建立 或者连接断开了
							logger.error("serverId:"+paytradeByDb.getSid()+",连接不存在");
							paytradeByDb.setStatus(3);
							isSychnSuccess=false;
							paytradeByDb.updateToDB();
						}else{
							Q_TopUp  reqData =  new Q_TopUp();
							reqData.ext="ext"+HelperRandom.randSecendId(System.currentTimeMillis(), 8,reqData.hashCode());
							reqData.playerId= (long)(paytradeByDb.getPid());
							reqData.serverId=paytradeByDb.getSid();
							reqData.proIdx= paytradeByDb.getPro_idx();
							reqData.proPrice= paytradeByDb.getPay_price();
							reqData.platform="WxPay";
							reqData.trade_no=paytradeByDb.getOrder_id();
							StringBuffer  sb =  new StringBuffer();
							sb.append(reqData.playerId).append(reqData.serverId).append(reqData.proIdx).append(reqData.proPrice)
							.append(reqData.ext).append(Constants.SYCHPASSWORD);
							logger.debug("sb.:"+sb.toString());
							reqData.sign=MD5Util.md5(sb.toString()).toLowerCase();
							logger.debug("reqData.sign:"+reqData.sign);
							slaveV0.SC_CONNECTOR.sc.qn("/"+reqData.getClass().getSimpleName(),(JSONObject)JSON.toJSON(reqData));
						}
						if(!isSychnSuccess){
							// 同步列表不存在 则 加入同步列表中
							if(null==OrderSynchPo.getOrderSyncPoByOrdId(paytradeByDb.getOrder_id())){
								OrderSynchPo ordSynchPo= new  OrderSynchPo();
								ordSynchPo.setOrder_id(paytradeByDb.getOrder_id());
								ordSynchPo.setSych_cnt(0);
								ordSynchPo.setSych_status(1); //同步状态 1 定时器正在同步等待通知,2同步成功,3同步失败
								ordSynchPo.setCreate_time(new Date());
								ordSynchPo.insertToDB();
							}
						}

						// 加一个 支付统计 [新增和活跃付费,以及Ltv统计]
//						paytradeByDb.payFlowStatitic();

					} else {// 实际支付金额和订单金额不一致
						logger.error("wx_paytransaction_id:"+transaction_id);
						logger.error("Wx_trade_no:"+out_trade_no+"实际支付金额:"+total_amount_fen+"和订单金额"+fen+"不一致!");
						result= "failure";
					}
				// TODO 验签成功后
				// 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure
			} else {
				// TODO 验签失败则记录异常日志，并在response中返回failure.
				String failCtx=  returnXML("FAIL");
				sender.sendAndDisconnect(failCtx);
				 return;
			}
		} else {
			sender.sendAndDisconnect("failure");
			return;
		}
		sender.sendAndDisconnect(result);
		return;
	}
	private String returnXML(String return_code) {
		return "<xml><return_code><![CDATA["

				+ return_code
				+ "]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
	}
	//https://blog.csdn.net/o1101574955/article/details/81024102
	public String convertByteBufToString(ByteBuf buf) {
	    String str;
	    if(buf.hasArray()) { // 处理堆缓冲区
	        str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
	    } else { // 处理直接缓冲区以及复合缓冲区
	        byte[] bytes = new byte[buf.readableBytes()];
	        buf.getBytes(buf.readerIndex(), bytes);
	        str = new String(bytes, 0, buf.readableBytes());
	    }
	    return str;
	}
}

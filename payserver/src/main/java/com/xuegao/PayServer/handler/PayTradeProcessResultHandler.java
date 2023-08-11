package com.xuegao.PayServer.handler;

import com.xuegao.PayServer.logs.LogBase;
import com.xuegao.PayServer.logs.ThinKingData;
import com.xuegao.PayServer.po.AdPo;
import com.xuegao.PayServer.po.UserPo;
import com.xuegao.PayServer.slaveServer.DataProto;
import com.xuegao.core.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.executor.PayExecutor;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.po.OrderSynchPo;
import com.xuegao.PayServer.slaveServer.DataProto.Q_TopUpRslt;
import com.xuegao.PayServer.slaveServer.IScMsgHandler;
import com.xuegao.PayServer.slaveServer.SC;
import com.xuegao.PayServer.slaveServer.ScJSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 专门负责处理 slave订单执行完毕之后 返回给你payserver
 * 继续处理 更新订单状态
 * **/
public class PayTradeProcessResultHandler implements IScMsgHandler {
	public static Logger logger = Logger.getLogger(PayTradeProcessResultHandler.class);
	public static Logger elkLogger = Logger.getLogger("elk");
	public static final String KIND_ID="1008";
	public static final String KIND_NAME="付费成功日志";
	public static String PROJRCT = PropertiesUtil.get("project");

	static int  conut=0;
	/**
	 * @param  scReqMsg 来自于 slave 返回json内容
	 *
	 * */
	@Override
	public void handle(SC sc, ScJSONObject scReqMsg) {
		conut++;
		logger.info("slave:respose:"+scReqMsg);
		if("/Q_TopUpRslt".equals(scReqMsg.sc_cmd)){
			if(null!=scReqMsg){//响应成功

				JSONObject respDat = scReqMsg.sc_data;
				Q_TopUpRslt resp=JSON.toJavaObject(respDat, Q_TopUpRslt.class);
				String trade_no=resp.trade_no;
				OrderPo payTrade = OrderPo.findByOrdId(resp.trade_no);
				logger.info(payTrade.toString());
				if (payTrade == null){
					logger.error("订单号:"+trade_no+",不存在");
					return ;
				}
				if(resp.status==0){
					payTrade.setStatus(4);
					payTrade.setFinish_time(System.currentTimeMillis());
				}else{
					logger.error("其他错误信息...暂未定义");
				}
				payTrade.updateToDB_WithSync();
				if("syncPayTradeCallbackSuccess".equals(resp.ext)){

					OrderSynchPo orderSync = OrderSynchPo.getOrderSyncPoByOrdId(trade_no);
					orderSync.setStatus_msg(resp.msg);
					orderSync.setSych_status(2);

					orderSync.updateToDB_WithSync();
				}
				//tapdb支付上报
				PayExecutor payExecutor = new PayExecutor();
				LogBase log = new LogBase();
				log.setKindId(KIND_ID);
				log.setKindName(KIND_NAME);
				log.setTimestamp(System.currentTimeMillis());
				log.setUid(payTrade.getUser_id() == null ? "0" : payTrade.getUser_id().toString());
				log.setPlayerId(String.valueOf(payTrade.getPid()));
				log.setObject(payTrade);
				log.setProject(PROJRCT);
				elkLogger.info((JSONObject)JSON.toJSON(log));
				//tapdb统计
				if(Constants.tapdb_open) {
					payExecutor.tapdbPayReport(String.valueOf(payTrade.getUser_id()	), trade_no, Float.valueOf(payTrade.getPay_price())*100,payTrade.getUnits());
				}
				//数数上报
				if ("open".equals( Constants.THINKING_OPEN) ) {
					//充值完成上报数数科技
					ThinKingData thinKingData = new ThinKingData();
					thinKingData.ta.setSuperProperties(thinKingData.setSuperProperties(String.valueOf(payTrade.getUser_id()), String.valueOf(payTrade.getSid()), payTrade.getChannelCode()));
					Map<String, Object> properties = new HashMap<String, Object>();
					Map<String, Object> userSetOnceProperties = new HashMap<String, Object>();
					Map<String, Object> userSetProperties = new HashMap<String, Object>();
					Map<String, Object> userAddProperties = new HashMap<String, Object>();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					//设置事件属性
					properties.put("order_id", payTrade.getOrder_id());
					properties.put("gear_id", payTrade.getPro_idx());
					properties.put("order_money_amount", payTrade.getPay_price());
					properties.put("order_type", payTrade.getOrder_type());
					properties.put("entrance", payTrade.getPlatform());
					properties.put("item_id", payTrade.getPro_idx());
					//设置用户属性只设置一次
					userSetOnceProperties.put("first_charge_time", sdf.format(payTrade.getCreate_time()));
					userSetOnceProperties.put("first_charge_amount", payTrade.getPay_price());
					//设置用户最后支付时间
					userSetProperties.put("last_charge_time", sdf.format(payTrade.getCreate_time()));
					//设置累充金额 注：新项目可用
					userAddProperties.put("total_charge_amount", payTrade.getPay_price());
					try {
						thinKingData.ta.track(payTrade.getUser_id() + "-" + payTrade.getSid(), "", "order_complete", properties);

						thinKingData.ta.user_setOnce(payTrade.getUser_id() + "-" + payTrade.getSid(), "", userSetOnceProperties);
						thinKingData.ta.user_set(payTrade.getUser_id() + "-" + payTrade.getSid(), "", userSetProperties);
						thinKingData.ta.user_add(payTrade.getUser_id() + "-" + payTrade.getSid(), "", userAddProperties);

						thinKingData.ta.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//订单上报归因服务器
                try {
                    if (StringUtils.isNotEmpty(Constants.adv_appId)) {
                        payExecutor.advPayReport(String.valueOf(payTrade.getUser_id()), trade_no, Float.valueOf(payTrade.getPay_price()),
                                payTrade.getUnits(),payTrade.getSid(),payTrade.getPid(),payTrade.getStatus().equals(4)?1:0,
                                payTrade.getChannelCode(),Constants.adv_appId,payTrade.getFinish_time());
                    }
                } catch (Exception e) {
                    logger.info("订单上报归因服务器失败,订单号为:"+payTrade.getOrder_id());
                }
			}
		}
		if("/Q_AdvertisementRslt".equals(scReqMsg.sc_cmd)){
			if(null!=scReqMsg) {//响应成功
				JSONObject respDat = scReqMsg.sc_data;
				DataProto.Q_AdvertisementRslt resp=JSON.toJavaObject(respDat, DataProto.Q_AdvertisementRslt.class);
				AdPo adPo= AdPo.findByTid(resp.tId);
				if (adPo==null){
					logger.info("广告流水号:"+resp.tId+"不存在");
					return;
				}
				adPo.setStatu(resp.status);
				adPo.updateToDB();
			}
		}
		logger.info("callback conut:"+conut);
	}
}

package com.xuegao.PayServer.schedule_task;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.handler.PayTradeProcessResultHandler;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.po.OrderSynchPo;
import com.xuegao.PayServer.po.SlaveServerListPo;
import com.xuegao.PayServer.slaveServer.DataProto.Q_TopUp;
import com.xuegao.PayServer.slaveServer.GlobalSlaveManage;
import com.xuegao.PayServer.slaveServer.ScConnector;
import com.xuegao.PayServer.slaveServer.SlaveServerVo;
import com.xuegao.PayServer.util.MD5Util;

public class PayTradeSynchJob extends  AbsScheduleTask{

	@Override
	public int fetchInitialDelaySec() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public int fetchPeriodSec() {
		// TODO Auto-generated method stub
		return 5*60;
	}

	@Override
	public void run() {
		List<OrderSynchPo> pendingList = OrderSynchPo.getAllPendingWorking();
		
		for(OrderSynchPo ordSyn:pendingList){
			String ord_id = ordSyn.getOrder_id();
			OrderPo payTrade =OrderPo.findByOrdId(ord_id);
			if(null==payTrade){
				logger.info("ord_id:"+ord_id+",不存在");
				continue;
			}
			int serverId= payTrade.getSid();
			SlaveServerListPo slaveServer= GlobalSlaveManage.fecthSlaveServerByServerId(serverId);
			SlaveServerVo 	slaveVo = 	null;
			if(null!=slaveServer){
				String slave_ip= slaveServer.getIp();
				slaveVo = GlobalSlaveManage.connectSlave(slave_ip,slaveServer.getPort());
			}
			if(null==slaveVo){
				logger.info("serverId:"+serverId+"对应的连接已经断开");
				ordSyn.setStatus_msg("serverId:"+serverId+",对应区服连接已经断开,无法发送钻石");
				ordSyn.setSych_cnt(ordSyn.getSych_cnt()+1);
				if(ordSyn.getSych_cnt()>=5){
					ordSyn.setSych_status(3);
					payTrade.setStatus(3);//  订单状态也发生改变
					payTrade.setFinish_time(System.currentTimeMillis());
					payTrade.updateToDB();
				}
				ordSyn.updateToDB();
				continue;
			}
			if(!slaveVo.isActive()){ // 连接已经 未激活
				logger.info("serverId:"+serverId+",slaveIp:"+slaveVo.slaveIp+",连接未激活状态");
				ordSyn.setStatus_msg("serverId:"+serverId+",对连接未激活状态,无法发送钻石,slaveIp:"+slaveVo.slaveIp);
				ordSyn.setSych_cnt(ordSyn.getSych_cnt()+1);
				if(ordSyn.getSych_cnt()>=5){
					ordSyn.setSych_status(3);
					payTrade.setStatus(3); //订单状态也发生改变
					payTrade.setFinish_time(System.currentTimeMillis());
					payTrade.updateToDB();
				}
				ordSyn.updateToDB();
				continue;
			}
			// 拼接数据 发送请求:
			Q_TopUp  reqData =  new Q_TopUp();
			reqData.ext="syncPayTradeCallbackSuccess";
			reqData.playerId= (long)(payTrade.getPid());
			reqData.serverId=payTrade.getSid();
			reqData.proIdx= payTrade.getPro_idx();
			reqData.proPrice= payTrade.getPay_price();
			reqData.trade_no=payTrade.getOrder_id();
			StringBuffer  sb =  new StringBuffer();
			sb.append(reqData.playerId).append(reqData.serverId).append(reqData.proIdx).append(reqData.proPrice)
			.append(reqData.ext).append(Constants.SYCHPASSWORD);
			reqData.sign=MD5Util.md5(sb.toString());
			slaveVo.SC_CONNECTOR.sc.qn("/"+reqData.getClass().getSimpleName(),(JSONObject)JSON.toJSON(reqData));
			ordSyn.setSych_cnt(ordSyn.getSych_cnt()+1);
		
			ordSyn.updateToDB();
			
		}
	}
}

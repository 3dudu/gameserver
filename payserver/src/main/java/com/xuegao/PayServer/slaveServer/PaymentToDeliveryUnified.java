package com.xuegao.PayServer.slaveServer;

import java.util.Date;

import com.xuegao.PayServer.po.AdPo;
import com.xuegao.PayServer.vo.EyouGift;
import com.xuegao.PayServer.vo.FFSDKGift;
import com.xuegao.core.util.StringUtil;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.handler.PayTradeProcessResultHandler;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.po.OrderSynchPo;
import com.xuegao.PayServer.po.SlaveServerListPo;
import com.xuegao.PayServer.slaveServer.DataProto.Q_TopUp;
import com.xuegao.PayServer.util.HelperRandom;
import com.xuegao.PayServer.util.MD5Util;

/**
 * 款到发货统一处理接口
 * */
public class PaymentToDeliveryUnified {
	static Logger logger = Logger.getLogger(PaymentToDeliveryUnified.class);
	/**
	 * @param  payTrade 订单
	 * @param  platform 支付平台名称
	 *
	 * */
	public static boolean delivery( OrderPo payTrade,String platform){
		 boolean isSychnSuccess = true;
		 // 无须验证金额 直接传入给slave 发钻石
        SlaveServerListPo slaveServer = GlobalSlaveManage.fecthSlaveServerByServerId(payTrade.getSid());// 服务列表信息
        SlaveServerVo slaveV0 = null;
        if (null != slaveServer) {
            String slave_ip = slaveServer.getIp();
            slaveV0 = GlobalSlaveManage.connectSlave(slave_ip,slaveServer.getPort());
        }
        if (null == slaveV0 || !slaveV0.isActive()) {// 连接 还未建立 或者连接断开了
            logger.info("serverId:" + payTrade.getSid() + ",连接不存在");
            payTrade.setStatus(3);
            isSychnSuccess = false;
            payTrade.updateToDB();
        } else {
            Q_TopUp reqData = new Q_TopUp();
            reqData.ext = StringUtil.isEmpty(payTrade.getExt())? "ext" + HelperRandom.randSecendId(System.currentTimeMillis(), 8, reqData.hashCode()):payTrade.getExt();
            reqData.playerId = (long) (payTrade.getPid());
            reqData.serverId = payTrade.getSid();
            reqData.proIdx = payTrade.getPro_idx();
            reqData.proPrice = payTrade.getPay_price(); //TODO 订单金额
            reqData.platform = platform; //TODO 平台名
            reqData.trade_no = payTrade.getOrder_id();
            reqData.order_type = payTrade.getOrder_type();
            StringBuffer sb = new StringBuffer();
            sb.append(reqData.playerId).append(reqData.serverId).append(reqData.proIdx).append(reqData.proPrice)
                    .append(reqData.ext).append(Constants.SYCHPASSWORD);
            logger.info("sb.:" + sb.toString());
            reqData.sign = MD5Util.md5(sb.toString()).toLowerCase();
            logger.info("reqData.sign:" + reqData.sign);
            slaveV0.SC_CONNECTOR.sc.qn("/" + reqData.getClass().getSimpleName(), (JSONObject) JSON.toJSON(reqData));
        }
        if (!isSychnSuccess) {
            // 同步列表不存在 则 加入同步列表中
            if (null == OrderSynchPo.getOrderSyncPoByOrdId(payTrade.getOrder_id())) {//TODO 透传参数
                OrderSynchPo ordSynchPo = new OrderSynchPo();
                ordSynchPo.setOrder_id(payTrade.getOrder_id());//TODO 透传参数
                ordSynchPo.setSych_cnt(0);
                ordSynchPo.setSych_status(1); //同步状态 1 定时器正在同步等待通知,2同步成功,3同步失败
                ordSynchPo.setCreate_time(new Date());
                ordSynchPo.insertToDB();
            }
        }
		return isSychnSuccess;
	}

    /**
     * @param  payTrade 订单
     * @param  platform 支付平台名称
     *
     * */
    public static boolean eyoudelivery( OrderPo payTrade,String platform){
        boolean isSychnSuccess = true;
        // 无须验证金额 直接传入给slave 发钻石
        SlaveServerListPo slaveServer = GlobalSlaveManage.fecthSlaveServerByServerId(payTrade.getSid());// 服务列表信息
        SlaveServerVo slaveV0 = null;
        if (null != slaveServer) {
            String slave_ip = slaveServer.getIp();
            slaveV0 = GlobalSlaveManage.connectSlave(slave_ip,slaveServer.getPort());
        }
        if (null == slaveV0 || !slaveV0.isActive()) {// 连接 还未建立 或者连接断开了
            logger.info("serverId:" + payTrade.getSid() + ",连接不存在");
            payTrade.setStatus(3);
            isSychnSuccess = false;
            payTrade.updateToDB();
        } else {
            Q_TopUp reqData = new Q_TopUp();
            reqData.ext = payTrade.getExt();
            reqData.playerId = (long) (payTrade.getPid());
            reqData.serverId = payTrade.getSid();
            reqData.proIdx = payTrade.getPro_idx();
            reqData.proPrice = payTrade.getPay_price(); //TODO 订单金额
            reqData.platform = platform; //TODO 平台名
            reqData.trade_no = payTrade.getOrder_id();
            reqData.order_type = payTrade.getOrder_type();
            reqData.bonus = payTrade.bonus;
            StringBuffer sb = new StringBuffer();
            sb.append(reqData.playerId).append(reqData.serverId).append(reqData.proIdx).append(reqData.proPrice)
                    .append(reqData.ext).append(Constants.SYCHPASSWORD);
            logger.info("sb.:" + sb.toString());
            reqData.sign = MD5Util.md5(sb.toString()).toLowerCase();
            logger.info("reqData.sign:" + reqData.sign);
            slaveV0.SC_CONNECTOR.sc.qn("/" + reqData.getClass().getSimpleName(), (JSONObject) JSON.toJSON(reqData));
        }
        if (!isSychnSuccess) {
            // 同步列表不存在 则 加入同步列表中
            if (null == OrderSynchPo.getOrderSyncPoByOrdId(payTrade.getOrder_id())) {//TODO 透传参数
                OrderSynchPo ordSynchPo = new OrderSynchPo();
                ordSynchPo.setOrder_id(payTrade.getOrder_id());//TODO 透传参数
                ordSynchPo.setSych_cnt(0);
                ordSynchPo.setSych_status(1); //同步状态 1 定时器正在同步等待通知,2同步成功,3同步失败
                ordSynchPo.setCreate_time(new Date());
                ordSynchPo.insertToDB();
            }
        }
        return isSychnSuccess;
    }
    /**
     * @param gift 礼包
     *
     * */
    public static boolean delivery(FFSDKGift gift){
        boolean isSychnSuccess = true;
        // 无须验证金额 直接传入给slave 发钻石
        SlaveServerListPo slaveServer = GlobalSlaveManage.fecthSlaveServerByServerId(gift.serverid);// 服务列表信息
        SlaveServerVo slaveV0 = null;
        if (null != slaveServer) {
            String slave_ip = slaveServer.getIp();
            slaveV0 = GlobalSlaveManage.connectSlave(slave_ip,slaveServer.getPort());
        }
        if (null == slaveV0 || !slaveV0.isActive()) {// 连接 还未建立 或者连接断开了
            logger.info("serverId:" + gift.serverid + ",连接不存在");
        } else {
            DataProto.Q_SendAward reqData= new DataProto.Q_SendAward();
            reqData.playerId = (long)gift.roleid;
            reqData.serverId = gift.serverid;
            reqData.platform = "FFSDK"; //TODO 平台名
            reqData.rewardId = String.valueOf(gift.cp_gift) ;
            reqData.ext = gift.ext;
            StringBuffer sb = new StringBuffer();
            sb.append(reqData.playerId).append(reqData.serverId).append(reqData.rewardId).append(reqData.ext)
                    .append(Constants.SYCHPASSWORD);
            logger.info("sb.:" + sb.toString());
            reqData.sign = MD5Util.md5(sb.toString()).toLowerCase();
            logger.info("reqData.sign:" + reqData.sign);
            slaveV0.SC_CONNECTOR.sc.qn("/" + reqData.getClass().getSimpleName(), (JSONObject) JSON.toJSON(reqData));
        }
        return true;
    }

    /**
     * eyou礼包发放
     * @param gift 礼包
     *
     * */
    public static boolean delivery(EyouGift gift){
        boolean isSychnSuccess = true;
        // 无须验证金额 直接传入给slave 发钻石
        SlaveServerListPo slaveServer = GlobalSlaveManage.fecthSlaveServerByServerId(gift.s_id);// 服务列表信息
        SlaveServerVo slaveV0 = null;
        if (null != slaveServer) {
            String slave_ip = slaveServer.getIp();
            slaveV0 = GlobalSlaveManage.connectSlave(slave_ip,slaveServer.getPort());
        }
        if (null == slaveV0 || !slaveV0.isActive()) {// 连接 还未建立 或者连接断开了
            logger.info("serverId:" + gift.s_id + ",连接不存在");
        } else {
            DataProto.Q_SendAward reqData= new DataProto.Q_SendAward();
            reqData.playerId = (long)gift.role_id;
            reqData.serverId = gift.s_id;
            reqData.platform = "EyouSDK";
            reqData.rewardId = String.valueOf(gift.pid) ;
            reqData.ext = "";
            reqData.type = gift.type;
            reqData.is_all = gift.is_all;
            reqData.content = gift.content;
            reqData.title = gift.title;
            StringBuffer sb = new StringBuffer();
            sb.append(reqData.playerId).append(reqData.serverId).append(reqData.rewardId).append(reqData.ext)
                    .append(Constants.SYCHPASSWORD);
            logger.info("sb.:" + sb.toString());
            reqData.sign = MD5Util.md5(sb.toString()).toLowerCase();
            logger.info("reqData.sign:" + reqData.sign);
            slaveV0.SC_CONNECTOR.sc.qn("/" + reqData.getClass().getSimpleName(), (JSONObject) JSON.toJSON(reqData));
        }
        return true;
    }
    public static boolean delivery( OrderPo payTrade,String platform ,boolean sanbox){
        boolean isSychnSuccess = true;
        // 无须验证金额 直接传入给slave 发钻石
        SlaveServerListPo slaveServer = GlobalSlaveManage.fecthSlaveServerByServerId(payTrade.getSid());// 服务列表信息
        SlaveServerVo slaveV0 = null;
        if (null != slaveServer) {
            String slave_ip = slaveServer.getIp();
            slaveV0 = GlobalSlaveManage.connectSlave(slave_ip,slaveServer.getPort());
        }
        if (null == slaveV0 || !slaveV0.isActive()) {// 连接 还未建立 或者连接断开了
            logger.info("serverId:" + payTrade.getSid() + ",连接不存在");
            payTrade.setStatus(3);
            isSychnSuccess = false;
            payTrade.updateToDB();
        } else {
            Q_TopUp reqData = new Q_TopUp();
            reqData.ext = "ext" + HelperRandom.randSecendId(System.currentTimeMillis(), 8, reqData.hashCode());
            reqData.playerId = (long) (payTrade.getPid());
            reqData.serverId = payTrade.getSid();
            reqData.proIdx = payTrade.getPro_idx();
            reqData.proPrice = payTrade.getPay_price(); //TODO 订单金额
            reqData.platform = platform; //TODO 平台名
            reqData.trade_no = payTrade.getOrder_id();
            reqData.order_type = payTrade.getOrder_type();
            reqData.sanbox = sanbox;
            StringBuffer sb = new StringBuffer();
            sb.append(reqData.playerId).append(reqData.serverId).append(reqData.proIdx).append(reqData.proPrice)
                    .append(reqData.ext).append(Constants.SYCHPASSWORD);
            logger.info("sb.:" + sb.toString());
            reqData.sign = MD5Util.md5(sb.toString()).toLowerCase();
            logger.info("reqData.sign:" + reqData.sign);
            slaveV0.SC_CONNECTOR.sc.qn("/" + reqData.getClass().getSimpleName(), (JSONObject) JSON.toJSON(reqData));
        }
        if (!isSychnSuccess) {
            // 同步列表不存在 则 加入同步列表中
            if (null == OrderSynchPo.getOrderSyncPoByOrdId(payTrade.getOrder_id())) {//TODO 透传参数
                OrderSynchPo ordSynchPo = new OrderSynchPo();
                ordSynchPo.setOrder_id(payTrade.getOrder_id());//TODO 透传参数
                ordSynchPo.setSych_cnt(0);
                ordSynchPo.setSych_status(1); //同步状态 1 定时器正在同步等待通知,2同步成功,3同步失败
                ordSynchPo.setCreate_time(new Date());
                ordSynchPo.insertToDB();
            }
        }
        return isSychnSuccess;
    }
    public static boolean delivery(AdPo adPo ){
        // 无须验证金额 直接传入给slave 发钻石
        SlaveServerListPo slaveServer = GlobalSlaveManage.fecthSlaveServerByServerId(adPo.getSid());// 服务列表信息
        SlaveServerVo slaveV0 = null;
        if (null != slaveServer) {
            String slave_ip = slaveServer.getIp();
            slaveV0 = GlobalSlaveManage.connectSlave(slave_ip,slaveServer.getPort());
        }
        if (null == slaveV0 || !slaveV0.isActive()) {// 连接 还未建立 或者连接断开了
            logger.info("serverId:" + adPo.getSid() + ",连接不存在");
        } else {
            DataProto.Q_Advertisement reqData = new DataProto.Q_Advertisement();
            reqData.playerId = (long) (adPo.getPid());
            reqData.serverId = adPo.getSid();
            reqData.platform = adPo.getPlatform(); //TODO 平台名
            reqData.tId = adPo.getTrans_id();
            reqData.rewardId= adPo.getReward_name();
            StringBuffer sb = new StringBuffer();
            sb.append(reqData.playerId).append(reqData.serverId).append(reqData.tId).append(reqData.rewardId)
                    .append(Constants.SYCHPASSWORD);
            logger.info("sb.:" + sb.toString());
            reqData.sign = MD5Util.md5(sb.toString()).toLowerCase();
            logger.info("reqData.sign:" + reqData.sign);
            slaveV0.SC_CONNECTOR.sc.qn("/" + reqData.getClass().getSimpleName(), (JSONObject) JSON.toJSON(reqData));
            adPo.setStatu(0);
            adPo.insertToDB();
        }
        return true;
    }
    /**
     * @param  payTrade 订单
     * @param  platform 支付平台名称
     *
     * */
    public static boolean rebate( OrderPo payTrade,String platform){
        boolean isSychnSuccess = true;
        // 无须验证金额 直接传入给slave 发钻石
        SlaveServerListPo slaveServer = GlobalSlaveManage.fecthSlaveServerByServerId(payTrade.getSid());// 服务列表信息
        SlaveServerVo slaveV0 = null;
        if (null != slaveServer) {
            String slave_ip = slaveServer.getIp();
            slaveV0 = GlobalSlaveManage.connectSlave(slave_ip,slaveServer.getPort());
        }
        if (null == slaveV0 || !slaveV0.isActive()) {// 连接 还未建立 或者连接断开了
            logger.info("serverId:" + payTrade.getSid() + ",连接不存在");
            payTrade.setStatus(3);
            isSychnSuccess = false;
            payTrade.updateToDB();
        } else {
            DataProto.Q_Rebate reqData = new DataProto.Q_Rebate();
            reqData.ext = StringUtil.isEmpty(payTrade.getExt())? "ext" + HelperRandom.randSecendId(System.currentTimeMillis(), 8, reqData.hashCode()):payTrade.getExt();
            reqData.playerId = (long) (payTrade.getPid());
            reqData.serverId = payTrade.getSid();
            reqData.platform = platform; //TODO 平台名
            reqData.trade_no = payTrade.getOrder_id();
            reqData.order_type = payTrade.getOrder_type();
            reqData.bonus = payTrade.getBonus();
            StringBuffer sb = new StringBuffer();
            sb.append(reqData.bonus).append(reqData.playerId).append(reqData.serverId)
                    .append(reqData.ext).append(Constants.SYCHPASSWORD);
            logger.info("sb.:" + sb.toString());
            reqData.sign = MD5Util.md5(sb.toString()).toLowerCase();
            logger.info("reqData.sign:" + reqData.sign);
            slaveV0.SC_CONNECTOR.sc.qn("/" + reqData.getClass().getSimpleName(), (JSONObject) JSON.toJSON(reqData));
        }
        if (!isSychnSuccess) {
            // 同步列表不存在 则 加入同步列表中
            if (null == OrderSynchPo.getOrderSyncPoByOrdId(payTrade.getOrder_id())) {//TODO 透传参数
                OrderSynchPo ordSynchPo = new OrderSynchPo();
                ordSynchPo.setOrder_id(payTrade.getOrder_id());//TODO 透传参数
                ordSynchPo.setSych_cnt(0);
                ordSynchPo.setSych_status(1); //同步状态 1 定时器正在同步等待通知,2同步成功,3同步失败
                ordSynchPo.setCreate_time(new Date());
                ordSynchPo.insertToDB();
            }
        }
        return isSychnSuccess;
    }
}

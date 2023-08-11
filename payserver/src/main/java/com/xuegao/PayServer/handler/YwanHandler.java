package com.xuegao.PayServer.handler;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants.PlatformOption.YWan;
import com.xuegao.PayServer.data.Constants.PlatformOption.YWan.YWanData;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.YwResultVo;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.slaveServer.PaymentToDeliveryUnified;
import com.xuegao.PayServer.util.HelperEncrypt;
import com.xuegao.PayServer.util.wxpay.DoubleCompare;
import com.xuegao.PayServer.util.wxpay.NumUtil;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.redis.JedisUtil;

public class YwanHandler {
	static Logger logger = Logger.getLogger(YwanHandler.class);
	
	/***
	 * 支付结果通知---回调应用服务器接口
	 * 【从接口文档上来看只有gameid 和 pay_sign参数内容 不一样，其他 字段完全一样】
	 * 同时支持 Android和IOS 支付 回调。
	 * */
	@Cmd("/p/52YwanPayNotify/cb")
	public void yWanPayNotify(User sender, JSONObject params){
		
		String app_order_id = params.getString("app_order_id");
        String app_role_id = params.getString("app_role_id");
        String is_sandbox = params.getString("is_sandbox");
        String j_game_id = params.getString("j_game_id");
        String product_id= params.getString("product_id");
        String order_id = params.getString("order_id");//悦玩的订单ID
        String server_id = params.getString("server_id");
        String total_fee = params.getString("total_fee");// 单位为分
        String sign = params.getString("sign");
        String user_id = params.getString("user_id");
        String time = params.getString("time");
        
        YWan yWanConfig = GlobalCache.getPfConfig("YWan");
        if(null==yWanConfig){
        	String cause ="YWan:未配置" ;
            logger.info(cause);
            YwResultVo failVo= new YwResultVo("",cause,0);
	        String jsonStr = JSON.toJSONString(failVo);
	       	sender.sendAndDisconnect(jsonStr);
            return;
        }
        YWanData ywData=  yWanConfig.apps.get(j_game_id);
        if(null==ywData){
        	String cause ="j_game_id:未配置" ;
        	logger.info(cause);
            YwResultVo failVo= new YwResultVo("",cause,0);
 	        String jsonStr = JSON.toJSONString(failVo);
 	       	sender.sendAndDisconnect(jsonStr);
            return;
        }
        logger.info("ywData:"+ywData.toString());
		String j_pay_sign =ywData.j_pay_sign;
		
		JedisUtil redis = JedisUtil.getInstance("PayServerRedis");
        long isSaveSuc = redis.STRINGS.setnx(app_order_id, app_order_id);
        if (isSaveSuc == 0L) {
            String cause ="同一订单多次请求,无须重复处理:app_order_id:" + app_order_id;
            logger.info(cause);
            YwResultVo failVo= new YwResultVo("",cause,1);
	        String jsonStr = JSON.toJSONString(failVo);
	       	sender.sendAndDisconnect(jsonStr);
            return;
        } else {
            redis.expire(app_order_id, 30);
        }

        OrderPo payTrade = OrderPo.findByOrdId(app_order_id);
        if (payTrade == null) {
        	String cause="订单号:" + app_order_id + ",不存在";
        	YwResultVo failVo= new YwResultVo("",cause,0);
	        String jsonStr = JSON.toJSONString(failVo);
	       	sender.sendAndDisconnect(jsonStr);
            logger.info(cause);
            return;
        }
        if (payTrade.getStatus() >= 2) { //2.请求回调验证成功
            logger.info("订单已经处理完毕,无须重复处理");
            YwResultVo successVo= new YwResultVo("","success",1);
            String jsonStr = JSON.toJSONString(successVo);
        	sender.sendAndDisconnect(jsonStr);
            return;
        }
        payTrade.setExt("1".equals(is_sandbox)?"沙盒支付":"正式支付");
        //签名 约定
        //md5(app_order_id=xx&app_role_id=xx&is_sandbox=xx&j_game_id=xx&j_pay_sign=xx&product_id=xx&server_id=xx&time=xx&total_fee=xx&user_id=xx)
        StringBuffer  preSignStr =new StringBuffer();
        preSignStr.append("app_order_id=").append(app_order_id).append("&app_role_id=").append(app_role_id)
        .append("&is_sandbox=").append(is_sandbox).append("&j_game_id=").append(j_game_id)
        .append("&j_pay_sign=").append(j_pay_sign)
        .append("&product_id=").append(product_id).append("&server_id=").append(server_id)
        .append("&time=").append(time).append("&total_fee=").append(total_fee).append("&user_id=").append(user_id);
        logger.info("52Ywanparamn=="+preSignStr.toString());
        String signStr = HelperEncrypt.encryptionMD5(preSignStr.toString()).toLowerCase();
        if(!sign.equalsIgnoreCase(signStr)){// 
        	logger.info("sign not  right["+signStr+"]"+"["+sign+"]，app_order_id:"+app_order_id);
	       	String cause="sign not  right!";
	        YwResultVo failVo= new YwResultVo("",cause,0);
	        String jsonStr = JSON.toJSONString(failVo);
	       	sender.sendAndDisconnect(jsonStr);
	       	payTrade.setStatus(1);//1.收到回调验证失败
            payTrade.updateToDB_WithSync();
            return;
        }
        
        String  amount =payTrade.getPay_price().toString();
		String  fen = NumUtil.yuanToFen(amount);// 实际游戏订单金额
		int rs = DoubleCompare.compareNum(total_fee,fen);// total_fee 实际支付的金额
		if (rs > 1) {// rs =0 相等 1 第一个数字大 2 第二个数字大
			logger.info("Incorrect amount["+fen+"]"+"["+total_fee+"]，app_order_id:"+app_order_id);
	       	String cause="Incorrect amount!";
	        YwResultVo failVo= new YwResultVo("",cause,0);
	        String jsonStr = JSON.toJSONString(failVo);
	       	sender.sendAndDisconnect(jsonStr);
            return;
		}
        
        YwResultVo successVo= new YwResultVo("","success",1);
        String jsonStr = JSON.toJSONString(successVo);
    	sender.sendAndDisconnect(jsonStr);
    	
    	payTrade.setThird_trade_no(order_id);
//    	if(null==payTrade.getPay_price()){
    	//订单金额以实际接受到返回金额为准 【悦玩返回分单位】
    	payTrade.setPay_price(Float.valueOf(total_fee)/100F);
//    	}
        payTrade.setUnits("CNY");
        payTrade.setPlatform("52Ywan");
    	
	    payTrade.setStatus(2);//2.收到回调验证成功
        payTrade.updateToDB();
    	// 调用  发货接口
        boolean  isSychnSuccess = PaymentToDeliveryUnified.delivery(payTrade, "52Ywan");
        logger.info("isSychnSuccess["+isSychnSuccess+"]");
        
        // 加一个 支付统计 [新增和活跃付费,以及Ltv统计]
        // 这个暂时不上【留待测试服验证之后再上0417】
//        payTrade.payFlowStatitic();
    	
	}
}

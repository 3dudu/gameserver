package com.xuegao.PayServer.handler;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.OrderPo;
import com.xuegao.PayServer.po.OrderSynchPo;
import com.xuegao.PayServer.po.SlaveServerListPo;
import com.xuegao.PayServer.slaveServer.DataProto.Q_TopUp;
import com.xuegao.PayServer.slaveServer.DataProto.RQ_TopUp;
import com.xuegao.PayServer.slaveServer.DataProto.RS_TopUp;
import com.xuegao.PayServer.slaveServer.GlobalSlaveManage;
import com.xuegao.PayServer.slaveServer.ScConnector;
import com.xuegao.PayServer.slaveServer.SlaveServerVo;
import com.xuegao.PayServer.util.HelperRandom;
import com.xuegao.PayServer.util.MD5Util;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.redis.JedisUtil;
import com.xuegao.core.util.RequestUtil;

public class PayHandler {
	public static Logger logger = Logger.getLogger(PayHandler.class);
	public ExecutorService excutorService = Executors.newScheduledThreadPool(8);
	private AtomicInteger count = new AtomicInteger(0);//统计执行次数
	private int reqCnt= 0;
	
	@Cmd("/reqCnt")
	public  void  reqCnt(User sender, JSONObject params){
		
		// 返回
		JSONObject rs = MsgFactory.getDefaultSuccessMsg();
		rs.put("reqCnt", reqCnt);
		sender.sendAndDisconnect(rs); 
	}
	/**
	 * 接收客户端参数
	 * 
	 * @param 开始角色Id
	 * @param 结束角色Id
	 * @param serverId
	 * @param 档位Idx
	 * @param 价格
	 *            例如 6.0
	 * @throws InterruptedException 
	 * @callback 支付回调选择 0 雪糕SDK回调,1 anysdk回调
	 *           s=1&p_beg=1001&p_end=1001&idx=0&cb=0&amount=6.0
	 */
	@Cmd("/testPatchPay")
	public void receviePatchTopup(User sender, JSONObject params) throws InterruptedException {
		reqCnt++;
		long startTime = System.currentTimeMillis();
		final String serverId = params.getString("s");
		Integer playerId_beg = Integer.valueOf(params.getString("p_beg"));
		Integer playerId_end = Integer.valueOf(params.getString("p_end"));
		final	String proIdx = params.getString("idx");
		final String cb = params.getString("cb");// 回调方式 0 雪糕SDK回调,1 anysdk回调
		final String ext = "0".equals(cb) ? "压测雪糕SDK" : "压测Ansydk";
		final String amount = params.getString("amount");
		int loopCnt = Integer.valueOf(params.getString("loopCnt"));
		final String httpServerIp = params.getString("httpServerIp");
		int total = loopCnt*(playerId_end-playerId_beg+1);
		final CountDownLatch countDownLatch=new CountDownLatch(total);
		for (int j = 0; j < loopCnt; j++) {

			for (int i = playerId_beg; i <= playerId_end; i++) {

				String ordId = HelperRandom.randSecendId(System.currentTimeMillis(), 8, (i+"").hashCode());;
				// 入库
				OrderPo ordPO= OrderPo.findByOrdId(ordId);
				while(null!=ordPO){
					// 重新生成订单号
					ordId = HelperRandom.randSecendId(System.currentTimeMillis(), 8, (i+"").hashCode());
					ordPO= OrderPo.findByOrdId(ordId);
				}
				//  直接入库
				ordPO = new OrderPo();
				ordPO.setOrder_id(ordId);
				ordPO.setPid(Integer.parseInt((i+"")));
				ordPO.setSid(Integer.parseInt(serverId));
				ordPO.setPro_idx(Integer.parseInt(proIdx));
				ordPO.setExt(ext);
				ordPO.setStatus(0);
				ordPO.setCreate_time(System.currentTimeMillis());
				ordPO.insertToDB();

				final String trade_no =  ordId;
				// 支付回调
				// 彬发回调
				excutorService.execute(new Runnable(){
					@Override
					public void run() {
						try{
							logger.info("去支付2:");
							String cbUrl = "";
							String cbData = "";
							if ("0".equals(cb)) {// SDK 支付回调
								cbUrl = "/p/xuegaoSDK/cb";
								cbData = "trade_no=xxxx&out_trade_no=" + trade_no + "&gameId=100219&amount=6.0&payment_type=1";
								boolean isSychnSuccess=true;
				
								JedisUtil	redis=JedisUtil.getInstance("PayServerRedis");
								long isSaveSuc= redis.STRINGS.setnx(trade_no,  trade_no);
								if(isSaveSuc==0L){
									logger.error("同一订单多次请求,无须重复处理:out_trade_no:"+trade_no);
								}else{
									redis.expire(trade_no, 30);
								}
								
								OrderPo payTrade = OrderPo.findByOrdId(trade_no);
								if (payTrade == null){
									logger.error("订单号:"+trade_no+",不存在");
								}
								if(payTrade.getStatus()>=2){ //2.请求回调验证成功 
									logger.error("订单已经处理完毕,无须重复处理");
									return;
								}

								payTrade.setThird_trade_no(trade_no);
								payTrade.setPay_price(Float.valueOf(amount));
								payTrade.setUnits("CNY");
								payTrade.setPlatform("xgSDK");

								payTrade.setStatus(2);//2.收到回调验证成功
								
								// 无须验证金额 直接传入给slave 发钻石
								SlaveServerListPo slaveServer= GlobalSlaveManage.fecthSlaveServerByServerId(payTrade.getSid());
								SlaveServerVo 	slaveV0 = 	null;
								if(null!=slaveServer){
									String slave_ip= slaveServer.getIp();
									slaveV0 = GlobalSlaveManage.connectSlave(slave_ip,slaveServer.getPort());
								}
								if(null==slaveV0||!slaveV0.isActive()){// 连接 还未建立 或者连接断开了
									logger.error("serverId:"+payTrade.getSid()+",连接不存在");
									payTrade.setStatus(3);
									isSychnSuccess=false;
								}else{
									RQ_TopUp  reqData =  new RQ_TopUp();
									reqData.ext="";
									reqData.playerId= (long)(payTrade.getPid());
									reqData.serverId=payTrade.getSid();
									reqData.proIdx= payTrade.getPro_idx();
									reqData.proPrice= Float.valueOf(amount);
									StringBuffer  sb =  new StringBuffer();
									sb.append(reqData.playerId).append(reqData.serverId).append(reqData.proIdx).append(reqData.proPrice)
									.append(reqData.ext).append(Constants.SYCHPASSWORD);
									logger.debug("sb.:"+sb.toString());
									reqData.sign=MD5Util.md5(sb.toString()).toLowerCase();
									logger.debug("reqData.sign:"+reqData.sign);
									JSONObject respose = slaveV0.SC_CONNECTOR.sc.q("/"+reqData.getClass().getSimpleName(),(JSONObject)JSON.toJSON(reqData));
									logger.debug("slave:respose:"+respose);
									if(null!=respose){//响应成功
										RS_TopUp resp=JSON.toJavaObject(respose, RS_TopUp.class);
										if(resp.status==0){
											payTrade.setStatus(4);
											payTrade.setFinish_time(System.currentTimeMillis());
										}else{
											logger.error("其他错误信息...暂未定义");
										}
									}else if(null==respose){// 超时无返回
										logger.error("trade_no:"+trade_no+",发钻石超时无返回");
										payTrade.setStatus(3);
										isSychnSuccess=false;
									}
								}
								if(!isSychnSuccess){
									// 同步列表不存在 则 加入同步列表中
									if(null==OrderSynchPo.getOrderSyncPoByOrdId(trade_no)){
										OrderSynchPo ordSynchPo= new  OrderSynchPo();
										ordSynchPo.setOrder_id(trade_no);
										ordSynchPo.setSych_cnt(0);
										ordSynchPo.setSych_status(1); //同步状态 1 定时器正在同步等待通知,2同步成功,3同步失败
										ordSynchPo.setCreate_time(new Date());
										ordSynchPo.insertToDB();
									}
								}
								payTrade.updateToDB();
							
							} else {
								logger.info("anysdk 回调:");
								cbUrl = "/p/anySDK/cb";
								cbData = "sercer_id=1&game_user_id=1007";
								cbData = "server_id=1&game_user_id=1007&product_id=appBuyItem_1&private_data=" + trade_no
										+ "&product_name=60元宝＝￥6&channel_order_id=61529&pay_time=2018-07-30 19:25:58&order_id=PB619718073019255789065&enhanced_sign=ec3ca5083b3c214f745548f8e4ed1de5&pay_status=1&sign=564f0e0326d57f07da2691102500532b&amount="
										+ amount
										+ "&product_count=1&source=abc&channel_number=999&channel_product_id=99&plugin_id=0&game_id=619718137&user_id=18596&";
								boolean isSychnSuccess=true;
								String channel_number = "999";
								String currency_type=null;

								String private_data = trade_no;
								OrderPo payTrade =OrderPo.findByOrdId(private_data);
								if(null==payTrade){
									logger.error("订单号:"+private_data+",不存在");
									return;
								}
								logger.info("payTrade:"+payTrade.toString());
								if(payTrade.getStatus()>=2){ //2.请求回调验证成功 
									logger.info("订单已经处理完毕,无须重复处理");
									return;
								}
								if(null!=currency_type){// 货币类型
									payTrade.setUnits(currency_type);
								}else{//默认 人民币支付
									payTrade.setUnits("CNY");
								}
								payTrade.setPlatform("AnySdk");


								payTrade.setPay_price(Float.valueOf(amount));
								if (1==1) {// 验证签名正确enhanced_sign.equals(signString)
										// 到这个位置说明:  订单支付钱已经确认正常
										payTrade.setStatus(2);//2.收到回调验证成功
										// 无须验证金额 直接传入给slave 发钻石
										SlaveServerListPo slaveServer= GlobalSlaveManage.fecthSlaveServerByServerId(payTrade.getSid());
										SlaveServerVo 	slaveV0 = 	null;
										if(null!=slaveServer){
											String slave_ip= slaveServer.getIp();
											slaveV0 = GlobalSlaveManage.connectSlave(slave_ip,slaveServer.getPort());
										}
										if(null==slaveV0||!slaveV0.isActive()){// 连接 还未建立 或者连接断开了
											logger.info("serverId:"+payTrade.getSid()+",连接不存在");
											payTrade.setStatus(3);
											isSychnSuccess=false;
										}else{
											Q_TopUp  reqData =  new Q_TopUp();
											reqData.playerId= (long)(payTrade.getPid());
											reqData.serverId=payTrade.getSid();
											reqData.proIdx= payTrade.getPro_idx();
											reqData.proPrice=payTrade.getPay_price();
											reqData.ext=payTrade.getExt()==null?"":payTrade.getExt();
											reqData.platform="anysdk";
											reqData.trade_no=trade_no;
											StringBuffer  sb =  new StringBuffer();
											sb.append(reqData.playerId).append(reqData.serverId).append(reqData.proIdx).append(reqData.proPrice)
											.append(reqData.ext).append(Constants.SYCHPASSWORD);
											reqData.sign=MD5Util.md5(sb.toString()).toLowerCase();
											logger.info("reqData:"+reqData.toString());
											slaveV0.SC_CONNECTOR.sc.qn("/"+reqData.getClass().getSimpleName(),(JSONObject)JSON.toJSON(reqData));
										}
										if(!isSychnSuccess){//  发钻石失败;
											// 发钻石失败 添加到定时任务中继续执行
											OrderSynchPo ordSynchPo= new  OrderSynchPo();
											ordSynchPo.setOrder_id(payTrade.getOrder_id());
											ordSynchPo.setSych_cnt(0);
											ordSynchPo.setSych_status(1); //同步状态 1 定时器正在同步等待通知,2同步成功,3同步失败
											ordSynchPo.setCreate_time(new Date());
											ordSynchPo.insertToDB();
										}
									}else{
										logger.error("private_data:"+private_data+",channel_number:"+channel_number+",验签失败..");
										payTrade.setStatus(1);//2.收到回调验证成功
									}
									payTrade.updateToDB();
							}
							
						}finally{
							
							countDownLatch.countDown();
						}
					}
				});
			}
		}
		countDownLatch.await();
		
		logger.info("testPatchPay:spend time:"+(System.currentTimeMillis()-startTime));
		
		// 返回
		JSONObject rs = MsgFactory.getDefaultSuccessMsg();
		rs.put("cost", System.currentTimeMillis()-startTime);
		sender.sendAndDisconnect(rs);
	}
	
	/**
	 * 创建订单
	 * 并发执行
	 * @throws InterruptedException 
	 * 
	 * */
	@Cmd("/testPatchMakeOrder")
	public void receviePatchMakeOrder(User sender, JSONObject params) throws InterruptedException {
		long startTime = System.currentTimeMillis();
		String serverId = params.getString("s");
		Integer playerId_beg = Integer.valueOf(params.getString("p_beg"));
		Integer playerId_end = Integer.valueOf(params.getString("p_end"));
		String proIdx = params.getString("idx");
		final String cb = params.getString("cb");// 回调方式 0 雪糕SDK回调,1 anysdk回调
		String ext = "0".equals(cb) ? "压测雪糕SDK" : "压测Ansydk";
		final String amount = params.getString("amount");
		int loopCnt = Integer.valueOf(params.getString("loopCnt"));
		final String httpServerIp = params.getString("httpServerIp");
		
		int total = loopCnt*(playerId_end-playerId_beg+1);
		logger.info("total:"+total);
		final CountDownLatch countDownLatch=new CountDownLatch(total);
		
		for (int j = 0; j < loopCnt; j++) {

			for (int i = playerId_beg; i <= playerId_end; i++) {

				String createOrdCmd = "/p/buy?s=" + serverId + "&p=" + i + "&idx=" + proIdx + "&c=" + ext;
				// url=
				final String placeOrderurl = "http://"+httpServerIp+":9030" + createOrdCmd;
				
				// 并发创建
				excutorService.execute(new Runnable(){
					@Override
					public void run() {
						try{
						// 创建订单
						String result = RequestUtil.request3Times(placeOrderurl);
						logger.info("callBack:"+result);
						}finally{
							countDownLatch.countDown();
						}
					}
				});
			}
		}
		countDownLatch.await();
		logger.info("testPatchMakeOrder:spend time:"+(System.currentTimeMillis()-startTime));
		// 返回
		JSONObject rs = MsgFactory.getDefaultSuccessMsg();
		rs.put("cost", System.currentTimeMillis()-startTime);
		sender.sendAndDisconnect(rs);
	}
	
	
	
	
	
	
	
	
	/**
	 * 接收客户端参数
	 * 
	 * @param 开始角色Id
	 * @param 结束角色Id
	 * @param serverId
	 * @param 档位Idx
	 * @param 价格
	 *            例如 6.0
	 * @throws InterruptedException 
	 * @callback 支付回调选择 0 雪糕SDK回调,1 anysdk回调
	 *           s=1&p_beg=1001&p_end=1001&idx=0&cb=0&amount=6.0
	 */
	@Cmd("/makeTradeAndPay")
	public void PlaceAndPay(User sender, JSONObject params) throws InterruptedException {
		reqCnt++;
		long startTime = System.currentTimeMillis();
		final String serverId = params.getString("s");
		final Integer pid = Integer.valueOf(params.getString("pid"));
		final	String proIdx = params.getString("idx");
		final String amount = params.getString("amount");
//		final CountDownLatch countDownLatch=new CountDownLatch(1);

			//创建订单和支付
			// 支付回调
				excutorService.execute(new Runnable(){
					@Override
					public void run() {
						try{
							
							String ordId = HelperRandom.randSecendId(System.currentTimeMillis(), 8, serverId.hashCode());;
							// 入库
							OrderPo ordPO= OrderPo.findByOrdId(ordId);
							while(null!=ordPO){
								// 重新生成订单号
								ordId = HelperRandom.randSecendId(System.currentTimeMillis(), 8, (proIdx+"").hashCode());
								ordPO= OrderPo.findByOrdId(ordId);
							}
							//  直接入库
							ordPO = new OrderPo();
							ordPO.setOrder_id(ordId);
							ordPO.setPid(pid);
							ordPO.setSid(Integer.parseInt(serverId));
							ordPO.setPro_idx(Integer.parseInt(proIdx));
							ordPO.setExt("");
							ordPO.setStatus(0);
							ordPO.setCreate_time(System.currentTimeMillis());
							ordPO.insertToDB();

							String trade_no =  ordId;
							
							logger.info("去支付2:");
							String cbUrl = "";
							String cbData = "";
								logger.info("anysdk 回调:");
								cbUrl = "/p/anySDK/cb";
								cbData = "sercer_id=1&game_user_id=1007";
								cbData = "server_id=1&game_user_id=1007&product_id=appBuyItem_1&private_data=" + trade_no
										+ "&product_name=60元宝＝￥6&channel_order_id=61529&pay_time=2018-07-30 19:25:58&order_id=PB619718073019255789065&enhanced_sign=ec3ca5083b3c214f745548f8e4ed1de5&pay_status=1&sign=564f0e0326d57f07da2691102500532b&amount="
										+ amount
										+ "&product_count=1&source=abc&channel_number=999&channel_product_id=99&plugin_id=0&game_id=619718137&user_id=18596&";
								boolean isSychnSuccess=true;
								String channel_number = "999";
								String currency_type=null;

								String private_data = trade_no;
								OrderPo payTrade =OrderPo.findByOrdId(private_data);
								if(null==payTrade){
									logger.error("订单号:"+private_data+",不存在");
									return;
								}
//								logger.info("payTrade:"+payTrade.toString());
								if(payTrade.getStatus()>=2){ //2.请求回调验证成功 
									logger.info("订单已经处理完毕,无须重复处理");
									return;
								}
								if(null!=currency_type){// 货币类型
									payTrade.setUnits(currency_type);
								}else{//默认 人民币支付
									payTrade.setUnits("CNY");
								}
								payTrade.setPlatform("AnySdk");

								payTrade.setPay_price(Float.valueOf(amount));
								if (1==1) {// 验证签名正确enhanced_sign.equals(signString)
										// 到这个位置说明:  订单支付钱已经确认正常
										payTrade.setStatus(2);//2.收到回调验证成功
										// 无须验证金额 直接传入给slave 发钻石
										SlaveServerListPo slaveServer= GlobalSlaveManage.fecthSlaveServerByServerId(payTrade.getSid());
										SlaveServerVo 	slaveV0 = 	null;
										if(null!=slaveServer){
											String slave_ip= slaveServer.getIp();
											slaveV0 = GlobalSlaveManage.connectSlave(slave_ip,slaveServer.getPort());
										}
										if(null==slaveV0||!slaveV0.isActive()){// 连接 还未建立 或者连接断开了
											logger.info("serverId:"+payTrade.getSid()+",连接不存在");
											payTrade.setStatus(3);
											isSychnSuccess=false;
										}else{
											Q_TopUp  reqData =  new Q_TopUp();
											reqData.playerId= (long)(payTrade.getPid());
											reqData.serverId=payTrade.getSid();
											reqData.proIdx= payTrade.getPro_idx();
											reqData.proPrice=payTrade.getPay_price();
											reqData.ext=payTrade.getExt()==null?"":payTrade.getExt();
											reqData.platform="anysdk";
											reqData.trade_no=trade_no;
											StringBuffer  sb =  new StringBuffer();
											sb.append(reqData.playerId).append(reqData.serverId).append(reqData.proIdx).append(reqData.proPrice)
											.append(reqData.ext).append(Constants.SYCHPASSWORD);
											reqData.sign=MD5Util.md5(sb.toString()).toLowerCase();
											logger.info("reqData:"+reqData.toString());
											slaveV0.SC_CONNECTOR.sc.qn("/"+reqData.getClass().getSimpleName(),(JSONObject)JSON.toJSON(reqData));
										}
										if(!isSychnSuccess){//  发钻石失败;
											// 发钻石失败 添加到定时任务中继续执行
											OrderSynchPo ordSynchPo= new  OrderSynchPo();
											ordSynchPo.setOrder_id(payTrade.getOrder_id());
											ordSynchPo.setSych_cnt(0);
											ordSynchPo.setSych_status(1); //同步状态 1 定时器正在同步等待通知,2同步成功,3同步失败
											ordSynchPo.setCreate_time(new Date());
											ordSynchPo.insertToDB();
										}
									}else{
										logger.error("private_data:"+private_data+",channel_number:"+channel_number+",验签失败..");
										payTrade.setStatus(1);//2.收到回调验证成功
									}
									payTrade.updateToDB();
						}finally{
							
//							countDownLatch.countDown();
						}
					}
				});
//		countDownLatch.await();
		
		logger.info("testPatchPay:spend time:"+(System.currentTimeMillis()-startTime));
		
		// 返回
		JSONObject rs = MsgFactory.getDefaultSuccessMsg();
		rs.put("cost", System.currentTimeMillis()-startTime);
		sender.sendAndDisconnect(rs);
	}
	
	
	
	
	
	
	
	
	
	
	
}

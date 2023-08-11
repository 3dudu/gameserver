package com.xuegao.PayServer.slaveServer;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ScConnectorLengthFrameHandler  extends SimpleChannelInboundHandler<ByteBuf>{
		
		private static final Logger logger = Logger.getLogger(ScConnectorLengthFrameHandler.class);
		
		public ScConnector scConnector = null;
		
		public static final String ATTR_SC = "ATTR_SC";
		
		
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			logger.info("ScConnector session removed:"+ctx.channel().toString());
			super.channelInactive(ctx);
			scConnector.sc.ctx=null;
			scConnector.status=3;
			
			
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			logger.info("ScConnector session created:"+ctx.channel().toString());
			super.channelActive(ctx);
			
			scConnector.sc.ctx=ctx;
			scConnector.status=4;
			new Thread(new Runnable() {
				@Override
				public void run() {
					List<Runnable> list=scConnector.activeListeners;
					for(Runnable runnable:list){
						try {
							runnable.run();
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
						}
					}
				}
			}).start();
		}
		
		@Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    	logger.error(cause.getMessage());
	    }
		
		@Override
	 	protected void messageReceived(ChannelHandlerContext ctx, ByteBuf frame) throws Exception {
			try {
				String request = frame.toString(Charset.forName("UTF-8"));
				if("HEART_BEAT".equals(request)){
					return;
				}
				logger.info("-------ScConnector received msg:"+request+"-----------");
				final ScJSONObject scReqMsg=JSON.toJavaObject(JSON.parseObject(request), ScJSONObject.class);
				if(scReqMsg.sc_type==1){
		        	//response消息
		        	//根据sc_reqResId找到之前执行的线程，并唤醒他，并将数据放置公共区域
					scConnector.sc.rsMap.put(scReqMsg.sc_reqResId, scReqMsg);
	        		//唤醒请求线程
	        		CountDownLatch countDownLatch=scConnector.sc.countDownLatchMap.get(scReqMsg.sc_reqResId);
	        		if(countDownLatch!=null){
	        			countDownLatch.countDown();
	        		}
		        }else{
		    		//request消息和notify消息
		    		if(scConnector.msgHandler==null){
		    			logger.info("scConnector.msgHandler==null");
		    			//notify消息无需响应
//		        		if(scReqMsg.sc_type==0){
//		        			JSONObject object=ScMsgFactory.getErrorMsg(ScMsgFactory.ERRORCODE_NOHANDLER, "MsgHandler未注册");
//				    		scConnector.sc.sendRS(scReqMsg.sc_reqResId, object);
//		        		}
		        	}else{
		        		//放入消息池中执行
		        		scConnector.msgThreadPool.execute(new Runnable() {
							@Override
							public void run() {
								try {
									scConnector.msgHandler.handle(scConnector.sc, scReqMsg);
								} catch (Exception e) {
									logger.error(e.getMessage(),e);
								}
							}
						});
		        	}
		    		
		        }
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			
	    }

		public ScConnectorLengthFrameHandler(ScConnector scConnector) {
			super();
			this.scConnector=scConnector;
		}
		
		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
				throws Exception {
			super.userEventTriggered(ctx, evt);
			if (evt instanceof IdleStateEvent) {
				IdleStateEvent e = (IdleStateEvent) evt;
				if (e.state() == IdleState.READER_IDLE) {
					logger.info("------ScConnector 触发 IdleStateEvent,主动断开连接----");
					ctx.close();
				}
			}
		}
}

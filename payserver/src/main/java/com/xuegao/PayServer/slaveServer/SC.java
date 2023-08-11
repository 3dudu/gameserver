package com.xuegao.PayServer.slaveServer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * 远程通信产生的连接对象
 * @author ccx
 * @date 2017-8-15 下午5:22:02
 */
public class SC {
	static Logger logger=Logger.getLogger(SC.class);
	//唯一的连接ID，1个ScConnector与1个ScAcceptor之间只会存在一个SC.
	public String sc_id;
	
	public ChannelHandlerContext ctx;
	
	public Map<String, ScJSONObject> rsMap=new ConcurrentHashMap<String, ScJSONObject>();
	public Map<String, CountDownLatch> countDownLatchMap=new ConcurrentHashMap<String, CountDownLatch>();
	
	public SC(ChannelHandlerContext ctx) {
		super();
		this.ctx = ctx;
		this.sc_id=UUID.randomUUID().toString();
	}
	
	public JSONObject q(String cmd,JSONObject params){
		long time1=System.currentTimeMillis();
		ScJSONObject scReqMsg=ScJSONObject.newRequestMsg(cmd, params);
		logger.info("scReqMsg:"+scReqMsg.toString());
		try {
			if(this.isActive()){
				CountDownLatch countDownLatch=new CountDownLatch(1);
				countDownLatchMap.put(scReqMsg.sc_reqResId, countDownLatch);
				ByteBuf buf=ctx.alloc().heapBuffer();
				buf.writeBytes(scReqMsg.toString().getBytes());
				this.ctx.channel().writeAndFlush(buf);
				//等待响应数据
				countDownLatch.await(3, TimeUnit.SECONDS);
				ScJSONObject res=rsMap.get(scReqMsg.sc_reqResId);
				if(res!=null&&res.sc_type==1){
					return res.sc_data;
				}else{
					//超时
					throw new Exception("SC request over 3000ms.<cmd="+cmd+">,<params="+params+">");
				}
			}else{
				//当前连接断开
				logger.error("SC is not active.cmd="+cmd+",params="+params);
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}finally{
			rsMap.remove(scReqMsg.sc_reqResId);
			countDownLatchMap.remove(scReqMsg.sc_reqResId);
			long time2=System.currentTimeMillis();
			long diff=time2-time1;
			logger.info("-----SC request cost time:"+diff+"ms,cmd="+cmd+",params="+params+"-----");
		}
	}
	/**推送-通知 模式
	 * 无须返回
	 * */
	public void qn(String cmd,JSONObject params){
		ScJSONObject scReqMsg=ScJSONObject.newNotifyMsg(cmd, params);
		logger.info("qn:scReqMsg:"+scReqMsg);
		try {
			if(this.isActive()){
				ByteBuf buf=ctx.alloc().heapBuffer();
				buf.writeBytes(scReqMsg.toString().getBytes());
				this.ctx.channel().writeAndFlush(buf);
			}else{
				//当前连接断开
				logger.error("SC is not active.cmd="+cmd+",params="+params);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public void qn(byte[] params){
		
		try {
			if(this.isActive()){
				ByteBuf buf=ctx.alloc().heapBuffer();
				buf.writeBytes(params);
				this.ctx.channel().writeAndFlush(buf);
			}else{
				//当前连接断开
				logger.error("SC is not active,<cmd="+ new String(params)+">");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	
	public boolean isActive(){
		return this.ctx!=null&&this.ctx.channel().isActive();
	}
	
	public void setAttribute(String key,Object value){
		ctx.attr(AttributeKey.valueOf(key)).set(value);
	}
	@SuppressWarnings("unchecked")
	public <T>T getAttribute(String key){
		return (T) this.ctx.attr(AttributeKey.valueOf(key)).get();
	}
	
	/** 处理完rq逻辑后,需调用此函数返回rs消息*/
	public void sendRS(String sc_reqResId,JSONObject rsData){
		try {
//			ScJSONObject scResMsg=ScJSONObject.newResponseMsg(sc_reqResId, rsData);
//	        ByteBuf buf=this.ctx.alloc().heapBuffer();
//    		buf.writeBytes(scResMsg.toString().getBytes());
//    		this.ctx.channel().writeAndFlush(buf);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
}

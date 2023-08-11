package com.xuegao.core.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import com.xuegao.core.netty.http.HttpUploadServerHandler;

public class User {
	static Logger logger=Logger.getLogger(User.class);
	ChannelHandlerContext ctx;
	private int totalSendTime=0;
	ISendResponse sendResponse;
	ChannelFuture sendMsgChannelFuture=null;
	private boolean needEncrypt = false;
	
	public void setAttribute(String key,Object value){
		ctx.attr(AttributeKey.valueOf(key)).set(value);
	}
	public User(ChannelHandlerContext ctx,ISendResponse sr){
		this.ctx=ctx;
		this.sendResponse=sr;
	}
	@SuppressWarnings("unchecked")
	public <T>T getAttribute(String key){
		return (T) this.ctx.attr(AttributeKey.valueOf(key)).get();
	}
	
	public void sendAndDisconnect(Object rs){
	    if(this.sendResponse != null && this.sendResponse instanceof HttpUploadServerHandler){
	        send(rs,false);
	    }else{
	        send(rs,true);
	    }
	}
	
//	public void sendAndDisconnect(String cmd,JSONObject rs){
//		if(cmd==null||rs==null){
//			send(rs,true);
//		}else {
//			rs.put("cmd", cmd);
//			send(rs,true);
//		}
//	}
//	
//	public void send(String cmd,JSONObject rs){
//		if(cmd==null||rs==null){
//			send(rs,false);
//		}else {
//			rs.put("cmd", cmd);
//			send(rs,false);
//		}
//	}
	public void send(Object rs){
		send(rs,false);
	}
	
	private void send(Object rs,boolean isDisconnect){
		if(logger.isDebugEnabled()){
			logger.debug(ctx.channel().toString()+"返回客户端消息==>"+(rs==null?"null":rs.toString())+"");
		}
		if(isDisconnect){
			sendResponse.sendMsg(this, rs).addListener(ChannelFutureListener. CLOSE);
		}else {
			sendMsgChannelFuture=sendResponse.sendMsg(this, rs);
		}
		totalSendTime++;
	}
	
	public void disconnect(){
		if(sendMsgChannelFuture==null||sendMsgChannelFuture.isDone()){
			ctx.channel().close();
			return;
		}
		sendMsgChannelFuture.addListener(ChannelFutureListener. CLOSE);
	}
	
	public long aliveTime(){
		return System.currentTimeMillis()-(Long)getAttribute("ctx_start_time");
	}
	
	
	public boolean isActive(){
		return ctx.channel().isActive();
	}
	
	public Channel getChannel(){
		return ctx.channel();
	}
	public int getTotalSendTime() {
		return totalSendTime;
	}
	public ChannelHandlerContext getCtx() {
		return ctx;
	}
	
	public String getRemoteIp(){
		String ip=((InetSocketAddress)(this.getChannel().remoteAddress())).getHostString();
		return ip;
	}
	
	public String getRemoteIpAndPort(){
		InetSocketAddress inetSocketAddress=((InetSocketAddress)(this.getChannel().remoteAddress()));
		return inetSocketAddress.getHostString()+":"+inetSocketAddress.getPort();
	}
	
	public boolean isNeedEncrypt() {
		return needEncrypt;
	}
	public void setNeedEncrypt(boolean needEncrypt) {
		this.needEncrypt = needEncrypt;
	}
	
}

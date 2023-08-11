/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.xuegao.core.netty.websocket;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.netty.CmdHandler;
import com.xuegao.core.netty.ISendResponse;
import com.xuegao.core.netty.User;

public class CustomTextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> implements ISendResponse{
	

	//--------------ccx user begin-----------
	private static final Logger logger = Logger.getLogger(CustomTextFrameHandler.class);
	
	public CmdHandler cmdHandler;
	
	public static final String CTX_USER="ctx_user";
	public static final String CTX_START_TIME="ctx_start_time";
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.debug("session removed:"+ctx.channel().toString());
		User sender=(User)ctx.attr(AttributeKey.valueOf(CTX_USER)).get();
		cmdHandler.sessionRemoved(sender,this);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.debug("session created:"+ctx.channel().toString());
		User sender=new User(ctx,this);
		ctx.attr(AttributeKey.valueOf(CTX_USER)).set(sender);
		ctx.attr(AttributeKey.valueOf(CTX_START_TIME)).set(System.currentTimeMillis());
		cmdHandler.sessionCreated(sender,this);
		super.channelActive(ctx);
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	cmdHandler.caughtDecodeException(getUser(ctx), cause);
    }
	
	public User getUser(ChannelHandlerContext ctx){
		return (User)ctx.attr(AttributeKey.valueOf(CTX_USER)).get();
	}
	//------------ccx user end------------	
	
	
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        String request = frame.text();
//        if(!isJsonStr(request)){
//        	//Base64解码
//        	byte[] zipbs=MigBase64.decode(request);
//        	//解压
//        	byte[] bs=ZipUtil.unzip(zipbs);
//        	request=new String(bs,"UTF-8");
//        }
        JSONObject object=JSON.parseObject(request);
        String cmd=object.getString("cmd");
        cmdHandler.handleRequest(getUser(ctx), cmd, object);
        
    }
    
    public static boolean isJsonStr(String str){
    	if(str!=null&&(str.startsWith("{")||str.startsWith("["))){
    		return true;
    	}
    	return false;
    }

	public CustomTextFrameHandler(CmdHandler cmdHandler) {
		super();
		this.cmdHandler = cmdHandler;
	}

	@Override
	public ChannelFuture sendMsg(User user, Object rs) {
		String msg=rs.toString();
//		if(msg.length()>=300){
//			//压缩
//			byte[] bs=ZipUtil.zip(msg);
//			//Base64编码
//			msg=MigBase64.encodeToString(bs, false);
//		}
//		return user.getCtx().channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg.getBytes())));
		return user.getCtx().channel().writeAndFlush(new TextWebSocketFrame(msg));
	}
}

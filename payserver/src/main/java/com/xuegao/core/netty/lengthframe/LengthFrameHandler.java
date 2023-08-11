package com.xuegao.core.netty.lengthframe;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.netty.CmdHandler;
import com.xuegao.core.netty.ISendResponse;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.websocket.CustomTextFrameHandler;

public class LengthFrameHandler  extends SimpleChannelInboundHandler<ByteBuf> implements ISendResponse{
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
	 	protected void messageReceived(ChannelHandlerContext ctx, ByteBuf frame) throws Exception {
			String request = frame.toString(Charset.forName("UTF-8"));
			JSONObject object=JSONObject.parseObject(request);
	        String cmd=object.getString("cmd");
	        cmdHandler.handleRequest(getUser(ctx), cmd, object);
	    }

		public LengthFrameHandler(CmdHandler cmdHandler) {
			super();
			this.cmdHandler = cmdHandler;
		}

		@Override
		public ChannelFuture sendMsg(User user, Object rs) {
			ByteBuf buf=user.getCtx().alloc().heapBuffer();
			buf.writeBytes(rs.toString().getBytes());
			return user.getCtx().channel().writeAndFlush(buf);
		}
		
}

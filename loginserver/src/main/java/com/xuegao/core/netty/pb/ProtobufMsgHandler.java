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
package com.xuegao.core.netty.pb;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import org.apache.log4j.Logger;

import com.google.protobuf.ExtensionLite;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.MessageLite;
import com.xuegao.core.netty.CmdHandler;
import com.xuegao.core.netty.ISendResponse;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.websocketproto.PbJsonObject;
import com.xuegao.core.netty.websocketproto.PbMsg;
import com.xuegao.core.netty.websocketproto.PbMsgDefine;


public class ProtobufMsgHandler extends SimpleChannelInboundHandler<PbMsg.BaseData> implements ISendResponse{

    private static final Logger logger = Logger.getLogger(
            ProtobufMsgHandler.class.getName());

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void channelRead0(ChannelHandlerContext ctx, PbMsg.BaseData baseData) throws Exception {
		PbJsonObject pbJsonObject=new PbJsonObject(baseData);
		GeneratedExtension extention=PbMsgDefine.fetchExtensionByMsgCode(baseData.getCode());
		MessageLite extensionData=(MessageLite) baseData.getExtension((ExtensionLite) extention);
		pbJsonObject.setExtensionData(extensionData);
		String cmd="/"+extensionData.getClass().getSimpleName();
    	cmdHandler.handleRequest(getUser(ctx), cmd, pbJsonObject);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public ProtobufMsgHandler(CmdHandler cmdHandler) {
		super();
		this.cmdHandler = cmdHandler;
	}

    //--------------ccx user begin-----------
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
	public ChannelFuture sendMsg(User user, Object rs) {
		if(rs instanceof PbJsonObject){
			PbJsonObject pbJsonObject=(PbJsonObject)rs;
			if(pbJsonObject.getBaseData()!=null){
				ChannelFuture future=user.getCtx().channel().writeAndFlush(pbJsonObject.getBaseData());
				return future;
			}
		}
		return null;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		super.userEventTriggered(ctx, evt);
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.READER_IDLE) {
				logger.info("客户端6分钟无响应，连接断开!");
				ctx.close();
			}
		}
	}

}

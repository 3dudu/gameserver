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
package com.xuegao.core.netty.websocketproto;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpMethod.GET;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ExtensionLite;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.googlecode.protobuf.format.JsonFormat;
import com.xuegao.core.netty.CmdHandler;
import com.xuegao.core.netty.ISendResponse;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.websocketproto.PbMsg.BaseData;

/**
 * Handles handshakes and messages
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> implements ISendResponse{
    private static final Logger logger = Logger.getLogger(WebSocketServerHandler.class);

    private static final String WEBSOCKET_PATH = "/websocket";

    private WebSocketServerHandshaker handshaker;


  	public CmdHandler cmdHandler;


  	public WebSocketServerHandler(CmdHandler cmdHandler) {
		super();
		this.cmdHandler = cmdHandler;
	}

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


    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // Handle a bad request.
        if (!req.getDecoderResult().isSuccess()) {
            sendMsg(getUser(ctx), "BAD_REQUEST");
            return;
        }
        // Allow only GET methods.
        if (req.getMethod() != GET) {
        	sendMsg(getUser(ctx), "Allow only GET methods.");
            return;
        }
        if(WEBSOCKET_PATH.equals(req.getUri())){
        	//upgrade websocket
        	WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    getWebSocketLocation(req), req.headers().get("Sec-WebSocket-Protocol"), false);
            handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), req);
            }
        }else{
        	//normal http request
        	sendMsg(getUser(ctx), "Allow only websocket protocol!");
            return;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
    	if(frame instanceof BinaryWebSocketFrame){
        	BinaryWebSocketFrame bf=(BinaryWebSocketFrame)frame;
        	ByteBuf byteBuf=bf.content();
        	byte[] bs = new byte[byteBuf.readableBytes()];
        	byteBuf.readBytes(bs);
        	try {
				BaseData baseData=PbMsg.BaseData.parseFrom(bs,PbMsgDefine.extensionRegistry);
				PbJsonObject pbJsonObject=new PbJsonObject(baseData);
				GeneratedExtension extention=PbMsgDefine.fetchExtensionByMsgCode(baseData.getCode());
				MessageLite extensionData=(MessageLite) baseData.getExtension((ExtensionLite) extention);
				pbJsonObject.setExtensionData(extensionData);
				String cmd="/"+extensionData.getClass().getSimpleName();
				cmdHandler.handleRequest(getUser(ctx), cmd, pbJsonObject);
			} catch (InvalidProtocolBufferException e) {
				logger.error("------无法解析的proto msg,连接断开------"+bf+",length="+bs.length);
				cmdHandler.caughtDecodeException(getUser(ctx),e);
			}
        }
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
        	throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
                    .getName()));
        }
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

    private static String getWebSocketLocation(FullHttpRequest req) {
        return "ws://" + req.headers().get(HOST) + WEBSOCKET_PATH;
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ChannelFuture sendMsg(User user, Object rs) {
		//1.normal http request
		//2.binary frame
		ByteBuf buf =null;
		String text="";
		if(rs == null){
		}else if(rs instanceof MessageLite){
			MessageLite messageLite=(MessageLite)rs;
			int code=PbMsgDefine.fetchCodeByExtensionType(messageLite.getClass());
			GeneratedExtension extension=PbMsgDefine.fetchExtensionByMsgCode(code);
			BaseData baseData=BaseData.newBuilder().setCode(code).setExtension(extension,messageLite).build();
			buf=Unpooled.wrappedBuffer(baseData.toByteArray());
			int size=buf.readableBytes();
			BinaryWebSocketFrame binaryWebSocketFrame=new BinaryWebSocketFrame(buf);
			ChannelFuture future=user.getCtx().channel().writeAndFlush(binaryWebSocketFrame);
			logger.info("-------发送消息["+user.getRemoteIpAndPort()+"]["+size+"bytes]:"+JsonFormat.printToString(baseData)+"-------");
			return future;
		}else if(rs instanceof JSONObject){
			text=JSON.toJSONString(rs, true);
		}else{
			text=rs.toString();
		}
		logger.info("-------发送非proto消息:"+text+"-------");
		buf=copiedBuffer(text, CharsetUtil.UTF_8);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

        response.headers().set(CONTENT_TYPE, "text/plain;charset=UTF-8");
		response.headers().set("Access-Control-Allow-Origin", "*");
		response.headers().set("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
		response.headers().set(CONNECTION, Values.CLOSE);
		response.headers().set(CONTENT_LENGTH, buf.readableBytes());

        // Write the response.
        ChannelFuture future=user.getCtx().channel().writeAndFlush(response).addListener(ChannelFutureListener. CLOSE);
		return future;
	}

	public static void main(String[] args) {
	}
}

package com.xuegao.core.netty.websocket;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.Map;

import com.xuegao.core.netty.CmdHandler;
import com.xuegao.core.netty.Server;
import com.xuegao.core.netty.User;
/**
 * Http服务器
 * @author ccx
 */
public class WebSocketServer extends Server{
	Map<String, User> users;
	public WebSocketServer(int port, CmdHandler cmdHandler) {
		super(port, cmdHandler);
	}

	@Override
	public void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(
                new HttpRequestDecoder(),
                new HttpObjectAggregator(65536),
                new HttpResponseEncoder(),
                new ChunkedWriteHandler(),
                new WebSocketServerProtocolHandler("/websocket"),
               	new CustomTextFrameHandler(cmdHandler));
	}
}

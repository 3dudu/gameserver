package com.xuegao.core.netty.websocketproto;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import com.xuegao.core.netty.CmdHandler;
import com.xuegao.core.netty.Server;

public class WebSocketProtoServer  extends Server{

	public WebSocketProtoServer(int port, CmdHandler cmdHandler) {
		super(port, cmdHandler);
		PbMsgDefine.init();
	}
	
	@Override
	public void initPipeline(ChannelPipeline pipeline) {
		pipeline.addLast(new IdleStateHandler(6*60*60, 0, 0, TimeUnit.SECONDS));
		pipeline.addLast("codec-http", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("handler", new WebSocketServerHandler(this.cmdHandler));
	}
}

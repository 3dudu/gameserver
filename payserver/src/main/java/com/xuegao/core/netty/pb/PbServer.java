package com.xuegao.core.netty.pb;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import com.xuegao.core.netty.CmdHandler;
import com.xuegao.core.netty.Server;
import com.xuegao.core.netty.websocketproto.PbMsg;
import com.xuegao.core.netty.websocketproto.PbMsgDefine;

public class PbServer extends Server{
	public PbServer(int port, CmdHandler cmdHandler) {
		super(port, cmdHandler);
	}
	
	
	@Override
	public void initPipeline(ChannelPipeline pipeline) {
		pipeline.addLast(new IdleStateHandler(360, 0, 0, TimeUnit.SECONDS));
		
		pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65536, 0, 2, 0, 2));
//		pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
		pipeline.addLast("protobufDecoder", new ProtobufDecoder(PbMsg.BaseData.getDefaultInstance(),PbMsgDefine.extensionRegistry));

		pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
//		pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
		pipeline.addLast("protobufEncoder", new ProtobufEncoder());

		pipeline.addLast("handler", new ProtobufMsgHandler(this.cmdHandler));
	}
	
}

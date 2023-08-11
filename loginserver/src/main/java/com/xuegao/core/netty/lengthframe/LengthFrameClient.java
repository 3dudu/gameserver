package com.xuegao.core.netty.lengthframe;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import com.xuegao.core.netty.Client;
import com.xuegao.core.netty.CmdHandler;

public class LengthFrameClient extends Client{

	public LengthFrameClient(String host, int port, CmdHandler cmdHandler) {
		super(host, port, cmdHandler);
	}

	@Override
	public void initPipeline(ChannelPipeline pipeline) {
		pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(100*1024*1024, 0, 4,0,4));
        pipeline.addLast("encoder", new LengthFieldPrepender(4));

        pipeline.addLast("handler", new LengthFrameHandler(cmdHandler));
	}
	
}

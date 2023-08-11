package com.xuegao.core.netty.lengthframe;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import com.xuegao.core.netty.CmdHandler;
import com.xuegao.core.netty.Server;
/**
 * LengthFrame服务器
 * @author ccx
 */
public class LengthFrameServer extends Server{

	public LengthFrameServer(int port, CmdHandler cmdHandler) {
		super(port, cmdHandler);
	}

	@Override
	public void initPipeline(ChannelPipeline pipeline) {
		
        pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(100*1024*1024, 0, 4,0,4));
        pipeline.addLast("encoder", new LengthFieldPrepender(4));

        pipeline.addLast("handler", new LengthFrameHandler(cmdHandler));
	}
}

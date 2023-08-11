package com.xuegao.core.netty.http;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import com.xuegao.core.netty.CmdHandler;
import com.xuegao.core.netty.Server;
/**
 * Http服务器
 * @author ccx
 */
public class HttpServer extends Server{

	public HttpServer(int port, CmdHandler cmdHandler) {
		super(port, cmdHandler);
	}

	@Override
	public void initPipeline(ChannelPipeline pipeline) {
	    pipeline.addLast(new IdleStateHandler(10*60, 10*60, 0, TimeUnit.SECONDS));
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());

        // Remove the following line if you don't want automatic content
        // compression.
        // pipeline.addLast("deflater", new HttpContentCompressor());

        pipeline.addLast("handler", new HttpUploadServerHandler(cmdHandler));
	}
}

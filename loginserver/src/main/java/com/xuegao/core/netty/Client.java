package com.xuegao.core.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.apache.log4j.Logger;

import com.xuegao.core.netty.http.HttpServer;

public abstract class Client {
	private String host;
	private int port;
	protected CmdHandler cmdHandler;
	private static Logger logger=Logger.getLogger(HttpServer.class);
	
	public Client(String host,int port,CmdHandler cmdHandler){
		this.host=host;
		this.port=port;
		this.cmdHandler=cmdHandler;
	}
	
	public CmdHandler getCmdHandler() {
		return cmdHandler;
	}
	

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public void setCmdHandler(CmdHandler cmdHandler) {
		this.cmdHandler = cmdHandler;
	}

	public void connect() throws Exception{
		logger.info("------建立连接中..."+host+":"+port+"-------");
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel sc) throws Exception {
							ChannelPipeline p = sc.pipeline();
							initPipeline(p);
						}
					});
			// Make the connection attempt.
			ChannelFuture f = b.connect(host, port).sync();
			logger.info("=================="+this.getClass().getSimpleName()+" connected to "+host+":"+port+"======================");
			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			throw e;
		}finally{
			group.shutdownGracefully();
		}
	}
	
	public abstract void initPipeline(ChannelPipeline pipeline);
}

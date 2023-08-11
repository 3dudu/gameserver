package com.xuegao.core.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.apache.log4j.Logger;

public abstract class Server {
	private int port;
	protected CmdHandler cmdHandler;
	private static Logger logger=Logger.getLogger(Server.class);
	
	private EventLoopGroup bossGroup=null;
	private EventLoopGroup workerGroup=null;
	
	public Server(int port,CmdHandler cmdHandler){
		this.port=port;
		this.cmdHandler=cmdHandler;
	}
	
	public CmdHandler getCmdHandler() {
		return cmdHandler;
	}
	
	public int getPort() {
		return port;
	}

	public void setCmdHandler(CmdHandler cmdHandler) {
		this.cmdHandler = cmdHandler;
	}



	public void run() throws Exception{
		EventLoopGroup bossGroup=new NioEventLoopGroup();
		EventLoopGroup workerGroup=new NioEventLoopGroup();
		this.bossGroup=bossGroup;
		this.workerGroup=workerGroup;
		try {
			ServerBootstrap bootstrap=new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel sc) throws Exception {
					ChannelPipeline p = sc.pipeline();
					initPipeline(p);
				}
			})
			.option(ChannelOption.SO_BACKLOG, 1024)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			ChannelFuture f=bootstrap.bind(port).sync();
			
			logger.info("=================="+this.getClass().getSimpleName()+" started at port "+port+"======================");
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public void shutdown(){
		if(this.workerGroup!=null){
			this.workerGroup.shutdownGracefully();
		}
		if(this.bossGroup!=null){
			this.bossGroup.shutdownGracefully();
		}
	}
	
	public abstract void initPipeline(ChannelPipeline pipeline);

}

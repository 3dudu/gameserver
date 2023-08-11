package com.xuegao.PayServer.slaveServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.xuegao.PayServer.util.ByteConvert;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
/**
 * SC客户端
 * @author ccx
 * @date 2017-8-17 下午4:08:29
 */
public class ScConnector {
	static Logger logger=Logger.getLogger(ScConnector.class);
	
	public IScMsgHandler msgHandler = null;
	
	//心跳包定时检测线程
	private ScheduledExecutorService scheduledExecutorService;
	//connector 连接线程
	private ExecutorService executorService=Executors.newSingleThreadExecutor();
	
	//消息处理线程池:4线程
	public ExecutorService msgThreadPool= Executors.newScheduledThreadPool(4);
			
	
	public String host;
	public int port;
	
	public int status = 0;	//0:未开始连接 1:已连接未响应 2:连接抛出异常 3:连接inActive 4.连接成功
	
	public List<Runnable> activeListeners=new ArrayList<Runnable>();
	
	public ScConnector(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}
	
	private static class ConnectRunnable implements Runnable{
		private ScConnector scConnector;
		public ConnectRunnable(ScConnector scConnector) {
			super();
			this.scConnector=scConnector;
		}

		@Override
		public void run() {
			scConnector.status=1;
			logger.info("------ScConnector建立连接中..."+scConnector.host+":"+scConnector.port+"-------");
			EventLoopGroup group = new NioEventLoopGroup();
			
			try {
				Bootstrap b = new Bootstrap();
				b.group(group).channel(NioSocketChannel.class)
						.option(ChannelOption.TCP_NODELAY, true)
						.handler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel sc) throws Exception {
								ChannelPipeline pipeline = sc.pipeline();
								
								pipeline.addLast(new IdleStateHandler(20, 0, 0, TimeUnit.SECONDS));
								pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(100*1024*1024, 0, 4,0,4));
						        pipeline.addLast("encoder", new LengthFieldPrepender(4));
						        
						        pipeline.addLast("handler", new ScConnectorLengthFrameHandler(scConnector));
							}
						});
				// Make the connection attempt.
				ChannelFuture f = b.connect(scConnector.host, scConnector.port).sync();
				logger.info("==================ScConnector connected to "+scConnector.host+":"+scConnector.port+"======================");
				// Wait until the connection is closed.
				f.channel().closeFuture().sync();
			} catch (Exception e) {
				scConnector.status=2;
				logger.error(e.getMessage(),e);
			}finally{
				group.shutdownGracefully();
			}
		}
	}
	
	public SC sc=new SC(null);
	/**
	 * 发起与远程服务器建立连接
	 * 分配一个独立线程,不在连接状态则尝试建立连接，在连接状态则发送心跳消息
	 * 多次调用无效
	 * @author ccx
	 * @date 2017-8-15 下午5:24:18
	 */
	public void connect(){
		//检查是否已执行过
		//定时器开始定时执行
		//重新连接判定规则:连接程序抛出异常 或者 连接已断开 或者连接程序未开始过
		//发送心跳规则:sc.channel().isActive()并且sc处于ready状态
		if(scheduledExecutorService!=null){
			return;
		}
		final ScConnector scConnector = this;
		scheduledExecutorService=Executors.newScheduledThreadPool(1);
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				if(scConnector.status==0||scConnector.status==2||scConnector.status==3){
					//重新连接
					scConnector.executorService.execute(new ConnectRunnable(scConnector));
				}else if(scConnector.status==4&&scConnector.sc.isActive()){
					//发送心跳包
					ByteBuf buf=scConnector.sc.ctx.alloc().heapBuffer();
					 ByteConvert c = new ByteConvert();
					buf.writeBytes("HEART_BEAT".getBytes());
					scConnector.sc.ctx.channel().writeAndFlush(buf);
				}else {
					//等待连接中,不执行任何操作
				}
			}
		}, 0, 3, TimeUnit.SECONDS);//3秒一次定时任务
	}
	/**
	 * 断开连接，结束线程定时任务
	 * @author ccx
	 * @date 2017-8-15 下午5:28:37
	 */
	public void disconnect(){
		logger.info("-------SC Connector closing-------");
		try {
			if(scheduledExecutorService!=null){
				scheduledExecutorService.shutdown();
			}
			if(executorService!=null){
				executorService.shutdown();
			}
			if(sc!=null&&sc.ctx!=null){
				sc.ctx.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
	}
	
	public void registMsgHandler(IScMsgHandler scMsgHandler){
		this.msgHandler=scMsgHandler;
	}
	/**
	 * 在连接建立后触发
	 * @author ccx
	 * @date 2017-8-17 下午3:29:24
	 */
	public void addActiveListener(Runnable doSth){
		activeListeners.add(doSth);
	}
	@Override
	public String toString() {
		return "ScConnector [msgHandler=" + msgHandler + ", scheduledExecutorService=" + scheduledExecutorService
				+ ", executorService=" + executorService + ", msgThreadPool=" + msgThreadPool + ", host=" + host
				+ ", port=" + port + ", status=" + status + ", activeListeners=" + activeListeners + ", sc=" + sc + "]";
	}
	
	
	
}

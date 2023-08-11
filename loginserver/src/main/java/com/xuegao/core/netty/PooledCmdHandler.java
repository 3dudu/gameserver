package com.xuegao.core.netty;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
/**
 * 使用线程池任务队列方式处理cmd,线程池线程个数自动扩容
 * @author ccx
 */
public abstract class PooledCmdHandler extends CmdHandler{
	private volatile long curTaskCount=0;
	private int max_task_count;//总的最大任务数
	public static final int CPU_CORE_SIZE=Runtime.getRuntime().availableProcessors();
	private int max_thread_count;
	
	private volatile int curThreadPoolSize;
	
	
	public static Logger logger=Logger.getLogger(PooledCmdHandler.class);
	private ThreadPoolExecutor threadPoolExecutor=null;
	
	public PooledCmdHandler(int threadCount,int maxThreadCount,int maxOverFlowTaskCount){
		curThreadPoolSize=threadCount;
		threadPoolExecutor=new ThreadPoolExecutor(curThreadPoolSize, curThreadPoolSize, 0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
		
		if(maxThreadCount>=curThreadPoolSize){
			max_thread_count=maxThreadCount;
		}else {
			max_thread_count=curThreadPoolSize;
			logger.warn("PooledCmdHandler(),max_thread_count set too low,ignore.");
		}
		
		max_task_count=maxOverFlowTaskCount;
		logger.info(this.getClass().getSimpleName()+"线程池:size="+curThreadPoolSize+",允许最大线程数="+max_thread_count+",允许最大积压任务数="+max_task_count);
	}
	
	@Override
	public void handleRequest(final User sender, final String cmd, final JSONObject params) {
		if(logger.isDebugEnabled()){
			logger.debug(sender.getChannel().remoteAddress() +"------>handle:"+cmd+",param:"+params.toString());
		}
		checkThreadCount();
		if(curTaskCount>max_task_count){
			//超出任务队列上线，抛出去给子类实现
			handleOutOfMaxTaskSize(sender, cmd, params);
			if(params instanceof Releasable){
				((Releasable)params).release();
			}
			return;
		}
		curTaskCount++;
		//没有超出上限，加入任务队列
		threadPoolExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					
					exec(sender, cmd, params);
				} catch (Throwable e) {
					//一般到不到这里，异常已被上层捕获到，并抛给子类实现了
					logger.error(e.getMessage(),e);
				}finally{
					curTaskCount--;
				}
			}
		});
	}
	/**
	 * 线程池中积压的任务超过设定上限 时的处理
	 * @param sender
	 * @param cmd
	 * @param params
	 */
	public abstract void handleOutOfMaxTaskSize(User sender, String cmd, JSONObject params);
	
	
	/**
	 * 检查当前累计任务数量，如果必须，则加大线程池线程数量
	 */
	private synchronized void checkThreadCount(){
		//加线程策略：1.当前积压任务数量>当前线程数量*100时 2.当前线程数量 小于 最大线程数量时 3.线程数量调整为两倍，如果超过最大限制，则设置为最大线程数
		if(curTaskCount>curThreadPoolSize*100&&curThreadPoolSize<max_thread_count){
			curThreadPoolSize=curThreadPoolSize*2;
			if(curThreadPoolSize>max_thread_count){
				curThreadPoolSize=max_thread_count;
			}
			//线程池扩容
			logger.info(this.getClass().getSimpleName()+"线程池扩容,当前任务数="+curTaskCount+",当前线程个数更改为="+curThreadPoolSize);
			threadPoolExecutor.setMaximumPoolSize(curThreadPoolSize);
			threadPoolExecutor.setCorePoolSize(curThreadPoolSize);
		}
	}

	public long getCurTaskCount() {
		return curTaskCount;
	}

	public int getCurThreadPoolSize() {
		return curThreadPoolSize;
	}
	
}

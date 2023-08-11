package com.xuegao.core.netty;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
/**
 * 可指定线程 执行的消息处理类
 * @author ccx
 */
public abstract class AssignableThreadCmdHandler<T> extends CmdHandler{
	private volatile long curTaskCount=0;
	private int max_task_count;//总的最大任务数
	public static final int CPU_CORE_SIZE=Runtime.getRuntime().availableProcessors();
	
	
	public static Logger logger=Logger.getLogger(AssignableThreadCmdHandler.class);
	private ExecutorService[] executors=null;
	private Random random=new Random();
	
	public AssignableThreadCmdHandler(int threadCount,int maxOverFlowTaskCount){
		this.executors=new ExecutorService[threadCount];
		for(int i=0;i<threadCount;i++){
			this.executors[i]=Executors.newSingleThreadExecutor();
		}
		this.max_task_count=maxOverFlowTaskCount;
		logger.info(this.getClass().getSimpleName()+"线程池:size="+threadCount+",允许最大积压任务数="+max_task_count);
	}
	
	/**
	 * 从消息中获取对象T，以指定专用线程执行  如果返回null，则随机一线程
	 * @author ccx
	 * @date 2017-4-12 上午11:39:54
	 */
	public abstract T fetchTFromMsg(User sender, String cmd, JSONObject params);
	
	
	@Override
	public void handleRequest(final User sender, final String cmd, final JSONObject params) {
		if(logger.isDebugEnabled()){
			logger.debug(sender.getChannel().remoteAddress() +"------>handle:"+cmd+",param:"+params.toString());
		}
		if(curTaskCount>max_task_count){
			//超出任务队列上线，抛出去给子类实现
			handleOutOfMaxTaskSize(sender, cmd, params);
			if(params instanceof Releasable){
				((Releasable)params).release();
			}
			return;
		}
		curTaskCount++;
		//拿到hashcode，获取指定的线程执行该任务
		T t=fetchTFromMsg(sender, cmd, params);
		ExecutorService executorService=fetchExecutThreadForT(t);
		//没有超出上限，加入任务队列
		executorService.execute(new Runnable() {
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
	 * 获取对象专用执行线程
	 * @author ccx
	 * @date 2017-4-12 上午11:53:09
	 */
	public ExecutorService fetchExecutThreadForT(T t){
		int code = 0;
		if(t!=null){
			code=t.hashCode();
		}else{
			code=random.nextInt(this.executors.length);
		}
		int index=code%this.executors.length;
		if(index<0)index=-index;
		ExecutorService executorService=this.executors[index];
		return executorService;
	}

	public long getCurTaskCount() {
		return curTaskCount;
	}

	public int getCurThreadPoolSize() {
		return this.executors.length;
	}
	
}

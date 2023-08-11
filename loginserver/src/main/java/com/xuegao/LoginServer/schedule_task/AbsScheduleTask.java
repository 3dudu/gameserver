package com.xuegao.LoginServer.schedule_task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public abstract class AbsScheduleTask {
	static Logger logger=Logger.getLogger(AbsScheduleTask.class);
	
	private ScheduledExecutorService executorService=Executors.newScheduledThreadPool(1);
	
	/**
	 * the time to delay first execution 
	 * 单位:秒
	 * @author ccx
	 * @date 2017-2-16 下午3:16:46
	 */
	public abstract int fetchInitialDelaySec();
	
	/**
	 * the period between successive executions
	 * 单位:秒
	 * @author ccx
	 * @date 2017-2-16 下午3:17:11
	 */
	public abstract int fetchPeriodSec();
	/**
	 * the task to execute
	 * @author ccx
	 * @date 2017-2-16 下午3:17:32
	 */
	public abstract void run();
	
	//运行状态
	private volatile int status = 0;
	
	private long curStatisticsCostTime=0;
	private int curStatisticsCount=0;
	//统计时间
	private int statisticsTurnTime=10*60;
	private int statisticsTurnCount=0;
	public synchronized void start(){
		if(status==0){
			status=1;
			statisticsTurnCount=statisticsTurnTime/fetchPeriodSec();
			final AbsScheduleTask task = this;
			int sec=fetchInitialDelaySec();
			executorService.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					long start=System.currentTimeMillis();
					try {
						task.run();
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
					long end=System.currentTimeMillis();
					curStatisticsCostTime+=end-start;
					curStatisticsCount++;
					long cost=end-start;
					if(cost>=500){
						//1次逻辑执行超过500ms,打印警告
						logger.info("------------task "+task.getClass().getSimpleName()+" cost "+cost+"ms by 1 time,please attention---------");
					}
					if(curStatisticsCount>=statisticsTurnCount){
						//打印统计数据
						logger.info("------------task "+task.getClass().getSimpleName()+" cost "+curStatisticsCostTime+"ms by "+curStatisticsCount+" time,average="+(curStatisticsCostTime/curStatisticsCount)+"ms---------");
						//重置统计轮回
						curStatisticsCostTime=0;
						curStatisticsCount=0;
					}
				}
			}, sec, fetchPeriodSec(), TimeUnit.SECONDS);
			logger.info("------------任务"+this.getClass().getSimpleName()+",将于"+sec+"秒后执行--------");
		}else{
			logger.info("------the task "+this.getClass().getSimpleName()+" is already running!-----");
		}
	}
	
	public void shutdown(){
		this.executorService.shutdown();
	}
	
}

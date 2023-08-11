package com.xuegao.core.db.po;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class BasePoSyncPool {
	static Logger logger=Logger.getLogger(BasePoSyncPool.class);
	private static Set<BasePo> set=null;
	private static ScheduledExecutorService executorService=null;
	private static int syncThreadSize=1;
	private static volatile boolean isRun=false;
	static{
		set=new HashSet<BasePo>();
		executorService=Executors.newScheduledThreadPool(syncThreadSize);
		startSync();
	}
	
	private static void startSync(){
		if(isRun){
			return;
		}
		isRun=true;
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				syncToDB();
			}
		}, 1, 5, TimeUnit.SECONDS);
	}
	
	/**
	 * 放入池子
	 * @param basePo
	 */
	public synchronized static void push(BasePo basePo){
		if(basePo!=null){
			set.add(basePo);
		}
	}
	//周期取数据
	private synchronized static Set<BasePo> fetchFromPool(){
		if(set.size()>0){
			Set<BasePo> newSet=new HashSet<>();
			Set<BasePo> oldSet=set;
			set=newSet;
			return oldSet;
		}else {
			return null;
		}
	}
	public static void syncToDB(){
		Set<BasePo> set=fetchFromPool();
		if(set==null){
			return;
		}
		long t1=System.currentTimeMillis();
		for(BasePo po:set){
			try {
				po.updateToDB();
			} catch (Exception e) {
				logger.error("同步数据库异常:"+e.getMessage(),e);
			}
			logger.info("------sync to db:"+po.getClass().getSimpleName()+",id="+po.getId()+"--------");
		}
		set.clear();
		set=null;
		long t2=System.currentTimeMillis();
		logger.info("-------同步完成:耗时"+(t2-t1)+"ms"+"-----");
	}
	
	public static void main(String[] args) {
		
	}
}

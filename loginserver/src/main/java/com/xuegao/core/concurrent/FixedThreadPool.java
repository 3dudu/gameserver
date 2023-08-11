package com.xuegao.core.concurrent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
/**
 * 可顺序执行的线程池
 * @author Administrator
 *
 */
public class FixedThreadPool {
	static Logger logger = Logger.getLogger(FixedThreadPool.class);
	public int threadNum = 0;
	private ExecutorService pool;
	
	private Map<Long, LinkedList<Runnable>> taskMap=new HashMap<Long, LinkedList<Runnable>>();
	public FixedThreadPool(int threadNum) {
		super();
		this.threadNum = threadNum;
		pool=Executors.newFixedThreadPool(threadNum);
	}
	
	
	/**
	 * 指定线程索引执行任务
	 * @param runnable
	 * @param threadIndex 线程索引，相同线程索引的任务，将会按照先后顺序执行。不同线程索引的任务会并发执行
	 */
	public void execute(Runnable runnable,long threadIndex){
		LinkedList<Runnable> taskList = null;
		synchronized (taskMap) {
			taskList=taskMap.get(threadIndex);
			if(taskList==null){
				taskList = new LinkedList<>();
				taskMap.put(threadIndex, taskList);
			}
		}
		synchronized (taskList) {
			taskList.add(runnable);
			if(taskList.size()==1){
				executeNext(taskList);
			}
		}
	}
	/**
	 * 开始执行任务列表中的第一个
	 * @param taskList
	 */
	private void executeNext(final LinkedList<Runnable> taskList){
		final Runnable runnable=taskList.getFirst();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}finally{
					synchronized (taskList) {
						taskList.removeFirst();
						if(taskList.size()>0){
							executeNext(taskList);
						}
					}
				}
			}
		});
	}
}

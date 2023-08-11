package com.xuegao.core.netty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class SessionManager {
	static Logger logger=Logger.getLogger(SessionManager.class);
	private static Map<String, User> userMap=new ConcurrentHashMap<String, User>();
	private static volatile long totalUserCount=0;
	public static void addUser(User sender){
		totalUserCount++;
		String key=sender.getChannel().id().asLongText();
		userMap.put(key, sender);
	}
	public static void removeUser(User sender){
		userMap.remove(sender.getChannel().id().asLongText());
	}
	public static Map<String, User> getUserMap() {
		return userMap;
	}
	private  static ThreadLocal<User> currentRoleThreadLocal=new ThreadLocal<>();
	
	public static void setCurrentUser(User sender){
		currentRoleThreadLocal.set(sender);
	}
	
	public static User getCurrentUser(){
		return currentRoleThreadLocal.get();
	}
	
	public static void printStatus(){
		try {
			StringBuffer sb=new StringBuffer();
			int size=userMap.size();
			sb.append("当前连接保持数---->"+size+",详细如下:");
			Map<Long, Integer> timeMap=new TreeMap<Long,Integer>();
			List<String> disconnectKeys=new ArrayList<String>();
			for(Entry<String,User> entry:userMap.entrySet()){
				String key=entry.getKey();
				User value=entry.getValue();
				if(!value.isActive()){
					disconnectKeys.add(key);
					continue;
				}
				long alivetime = value.aliveTime()/1000;
				Integer count=timeMap.get(alivetime);
				if(count==null){
					count=0;
				}
				timeMap.put(alivetime, count+1);
				if(alivetime>=5){
					logger.warn("检测到连接保持时间>=5s-------------->"+value.getChannel().remoteAddress().toString());
				}
			}
			for(String k:disconnectKeys){
				userMap.remove(k);
			}
			for(Entry<Long, Integer> entry:timeMap.entrySet()){
				Long second=entry.getKey();
				Integer count=entry.getValue();
				sb.append("\r\n"+second+"s---->"+count);
			}
			logger.info(sb.toString());
		} catch (Exception e) {
			logger.error("打印连接状态异常",e);
		}
	}
	
	private static ScheduledExecutorService scheduledExecutorService=null;
	private static long lastUserCount=0;
	public static void beginPrintTask(final int periodSeconds){
		if(scheduledExecutorService!=null){
			logger.warn("SessionManager beginPrintTask 已经启动,请勿重复启动!");
			return;
		}
		lastUserCount=totalUserCount;
		scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				long nowcount=getTotalUserCount();
				long addcount=nowcount-lastUserCount;
				lastUserCount=nowcount;
				logger.info(""+periodSeconds+"s内新增连接数----->"+addcount);
				SessionManager.printStatus();
			}
		}, periodSeconds, periodSeconds, TimeUnit.SECONDS);
	}
	
	public static long getTotalUserCount() {
		return totalUserCount;
	}
	
	
}

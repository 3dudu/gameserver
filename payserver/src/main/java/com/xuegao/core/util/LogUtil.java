package com.xuegao.core.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Appender;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LogUtil {
	private static Map<String, Logger> loggerMap=new ConcurrentHashMap<String, Logger>();
	
	public static Logger getLog(){
		return getOrCreateLogger("info");
	}
	
	/**
	 * 获取logger，如果没有则创建
	 * @param loggerName
	 * @return
	 */
	public static Logger getOrCreateLogger(String loggerName){
		loggerName=""+loggerName;
		Logger logger=loggerMap.get(loggerName);
		if(logger!=null){
			return logger;
		}else {
			synchronized (loggerMap) {
				logger=loggerMap.get(loggerName);
				if(logger==null){
					logger=createNewLogger(loggerName);
					loggerMap.put(loggerName, logger);
				}
			}
			return logger;
		}
	}
	/**
	 * 创建新的logger
	 * @param loggerName
	 * @return
	 */
	private static Logger createNewLogger(String loggerName){
		loggerName=(""+loggerName).trim();	
		//构建dailyfile appender
		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%d [%t] [%m] [%l] %n");
		Appender fileappender;
		try {
			fileappender = new DailyRollingFileAppender(layout, "./logs/"+loggerName+".log",".yyyy-MM-dd");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		//构建异步appender
		AsyncAppender asyncAppder=new AsyncAppender();
		asyncAppder.activateOptions();
		asyncAppder.setBufferSize(1000000);
		asyncAppder.setBlocking(false);
		asyncAppder.removeAllAppenders();
		asyncAppder.addAppender(fileappender);
		
		//构建logger
		Logger logger = Logger.getLogger(loggerName);
		logger.removeAllAppenders();
		logger.setAdditivity(false);//设置继承输出root
		logger.addAppender(asyncAppder);
		logger.setLevel(Level.DEBUG);
		return logger;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		//构建dailyfile appender
		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%d [%t] [%m] [%l] %n");
		Appender fileappender = new DailyRollingFileAppender(layout, "./logs/aaa.log",".yyyy-MM-dd");
		
		//构建异步appender
		AsyncAppender asyncAppder=new AsyncAppender();
		asyncAppder.activateOptions();
		asyncAppder.setBufferSize(1000000);
		asyncAppder.setBlocking(false);
		asyncAppder.removeAllAppenders();
		asyncAppder.addAppender(fileappender);
		
		//构建logger
		Logger logger = Logger.getLogger("aa");
		logger.removeAllAppenders();
		logger.setAdditivity(false);//设置继承输出root
		logger.addAppender(asyncAppder);
		logger.setLevel(Level.INFO);
		for(int i=0;i<10;i++){
			System.out.println(i);
			logger.info(""+i+"-->4测试"+System.currentTimeMillis());
			
		}
		Thread.sleep(1000);
		
	}
	
	
}

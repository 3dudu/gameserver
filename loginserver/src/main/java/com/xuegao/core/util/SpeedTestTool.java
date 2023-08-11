package com.xuegao.core.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
/**
 * 计算每秒运行次数，并打印到控制台
 * @author ccx
 */
public class SpeedTestTool {
	private static Logger logger=Logger.getLogger(SpeedTestTool.class);
	private static Map<String, NumberOfThisTime> map=new HashMap<String, NumberOfThisTime>();
	public static boolean isWork=true;
	public static synchronized  void addOneTime(String key){
		if(!isWork){
			return;
		}
		NumberOfThisTime n=map.get(key);
		long thisSec=System.currentTimeMillis()/1000;
		if(n!=null){
			long delaySec=thisSec-n.getLastSec();
			int num=n.getNum()+1;
			if(delaySec!=0){
				logger.info("------------<"+key+">:speed="+(num/delaySec)+"/s-----------");
				n.setNum(0);
				n.setLastSec(thisSec);
			}else {
				n.setNum(num);
			}
		}else {
			NumberOfThisTime nw=new NumberOfThisTime();
			nw.setLastSec(thisSec);
			nw.setNum(1);
			map.put(key, nw);
		}
		
		
	}
	
	public static void setSwitchOpen(){
		isWork=true;
	}
	public static void setSwitchClose(){
		isWork=false;
	}
}

class NumberOfThisTime{
	int num;
	long lastSec;
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public long getLastSec() {
		return lastSec;
	}
	public void setLastSec(long lastSec) {
		this.lastSec = lastSec;
	}
	
}

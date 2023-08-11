package com.xuegao.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	/**
	 * 获取第二个日期，与第一个日期的天数间距 如2月1日 与 2月2日 间距为1
	 * @author ccx
	 * @date 2017-2-14 下午2:25:56
	 */
	public static int diffDays(Date date1,Date date2){
		Calendar calendar1=Calendar.getInstance();
		calendar1.setTime(date1);
		calendar1.set(Calendar.HOUR_OF_DAY, 0);
		calendar1.set(Calendar.MINUTE,0);
		calendar1.set(Calendar.SECOND,0);
		calendar1.set(Calendar.MILLISECOND,0);
		
		Calendar calendar2=Calendar.getInstance();
		calendar2.setTime(date2);
		calendar2.set(Calendar.HOUR_OF_DAY, 0);
		calendar2.set(Calendar.MINUTE,0);
		calendar2.set(Calendar.SECOND,0);
		calendar2.set(Calendar.MILLISECOND,0);
		
		long diffMs=calendar2.getTimeInMillis()-calendar1.getTimeInMillis();
		long days=(diffMs/(1000*60*60*24));
		return (int) days;
	}
	
	/**
	 * 格式化: yyyy-MM-dd
	 * */
	public static String format(Date date){
		if(date==null){
			return null;
		}
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
	
}

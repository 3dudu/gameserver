package com.xuegao.core.util;

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
	 * 获取今日还剩的毫秒数（晚上12点)
	 * @author ccx
	 * @date 2017-7-10 下午4:27:51
	 */
	public static long todayLeftMiliSeconds(){
		long now=System.currentTimeMillis();
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DATE, 1);
		return calendar.getTimeInMillis()-now;
	}
	/**
	 * 获取本周1,0点那一刻
	 * @author ccx
	 * @date 2018-2-1 下午3:31:49
	 */
	public static Calendar fetchMondayZeroTimeThisWeek(){
		//1.找出本周1 0点整
		Calendar calendar=Calendar.getInstance();
		int dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK);
		if(dayOfWeek==Calendar.SUNDAY){
			//如果今天是星期天，应该提前7天
			calendar.add(Calendar.DAY_OF_MONTH, -7);
		}
		//设置到星期1的0点
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND, 0);		
		return calendar;
	}
	/**
	 * 获取指定日期的那一周的星期一0点时刻
	 * @author ccx
	 * @date 2018-2-1 下午3:31:49
	 */
	public static Calendar fetchMondayZeroTimeByDate(Date date){
		//1.找出本周1 0点整
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK);
		if(dayOfWeek==Calendar.SUNDAY){
			//如果今天是星期天，应该提前7天
			calendar.add(Calendar.DAY_OF_MONTH, -7);
		}
		//设置到星期1的0点
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND, 0);		
		return calendar;
	}
}

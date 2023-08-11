package com.icegame.framework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @Description: 此类为和时间相关的转换类
 * @Package com.icegame.framework.utils
 * @author chesterccw
 * @date 2018年1月19日
 */
public class TimeUtils {
	
	public static final long DAY_TIME = 24 * 3600000;
	
	public static final long MINUTE_TIME = 60 * 1000;
	
	//把时间字符串转化为Date类型
	public static Date turnTime(String loginDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(loginDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//日期向后推迟7天
	public static String addDate(String addDate){   
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;   
        int addDay = 7;
        try {   
            date = format.parse(addDate);   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        }   
        Calendar cal = Calendar.getInstance();   
        cal.setTime(date);   
        cal.add(Calendar.DAY_OF_MONTH,addDay);   
        date = cal.getTime();   
        return format.format(date);   
        
    }
	
	//日期向后推迟7天
		public static String addDateDetail(String addDate){   
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date date = null;   
	        int addDay = 7;
	        try {   
	            date = format.parse(addDate);   
	        } catch (Exception ex) {   
	            ex.printStackTrace();   
	        }   
	        Calendar cal = Calendar.getInstance();   
	        cal.setTime(date);   
	        cal.add(Calendar.DAY_OF_MONTH,addDay);   
	        date = cal.getTime();   
	        return format.format(date);   
	        
	    }
	
	//日期向前推迟七天
	public static String subDate(String addDate){   
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;   
        int addDay = 7;
        try {   
            date = format.parse(addDate);   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        }   
        Calendar cal = Calendar.getInstance();   
        cal.setTime(date);   
        cal.add(Calendar.DAY_OF_MONTH,-addDay);   
        date = cal.getTime();   
        return format.format(date);   
        
    }
	
	//日期向前推迟七天
		public static String subDateDetail(String addDate){   
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date date = null;   
	        int addDay = 7;
	        try {   
	            date = format.parse(addDate);   
	        } catch (Exception ex) {   
	            ex.printStackTrace();   
	        }   
	        Calendar cal = Calendar.getInstance();   
	        cal.setTime(date);   
	        cal.add(Calendar.DAY_OF_MONTH,-addDay);   
	        date = cal.getTime();   
	        return format.format(date);   
	        
	    }
	
	//日期向前推迟七天
	public static String subDateOne(){   
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();   
        int addDay = 1;
        try {   
            date = format.parse(format.format(date));   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        }   
        Calendar cal = Calendar.getInstance();   
        cal.setTime(date);   
        cal.add(Calendar.DAY_OF_MONTH,-addDay);   
        date = cal.getTime();   
        return format.format(date);   
        
    }

	/**
	 * 日趋转化为时间字符串（包含日期）
	 * @author chsterccw  
	 * @date 2018年4月2日
	 */
	public static String dateToString(Date date){ 
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		return df.format(date);
	}
	
	/**
	 * 获取当前日期
	 * @author chsterccw  
	 * @date 2018年4月2日
	 */
	public static String getDate(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		return df.format(new Date());	
	}

	/**
	 * 获取当前日期（包含时间）
	 * @author chsterccw  
	 * @date 2018年4月2日
	 */
	public static String getDateDetail(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		return df.format(new Date());
	}
	
	/**
	 * @Description: 对接收到的两个日期字符串进行判断,四种情况 
	 * @author chesterccw
	 * @date 2018年1月19日
	 */
	public static Map<String,String> checkDate(String startDate,String endDate){
		Map<String,String> dateMap = new HashMap<String,String>();
		if(startDate.equals("") && endDate.equals("")){
			endDate = TimeUtils.getDate();
			startDate = TimeUtils.subDate(endDate);
			dateMap.put("startDate",startDate);
			dateMap.put("endDate", endDate);
			return dateMap;
		}
		else if(startDate.equals("")){
			startDate = TimeUtils.subDate(endDate);
			dateMap.put("startDate",startDate);
			dateMap.put("endDate", endDate);
			return dateMap;
		}
		else if(endDate.equals("")){
			endDate = TimeUtils.addDate(startDate);
			dateMap.put("startDate",startDate);
			dateMap.put("endDate", endDate);
			return dateMap;
		}else{
			dateMap.put("startDate",startDate);
			dateMap.put("endDate", endDate);
			return dateMap;
		}
	}
	
	/**
	 * @Description: 对接收到的两个日期字符串进行判断,四种情况 
	 * @author chesterccw
	 * @date 2018年1月19日
	 */
	public static Map<String,String> checkDateDetail(String startDate,String endDate){
		Map<String,String> dateMap = new HashMap<String,String>();
		if(startDate.equals("") && endDate.equals("")){
			endDate = TimeUtils.getDateDetail();
			startDate = TimeUtils.subDateDetail(endDate);
			dateMap.put("startDate",startDate);
			dateMap.put("endDate", endDate);
			return dateMap;
		}
		else if(startDate.equals("")){
			startDate = TimeUtils.subDateDetail(endDate);
			dateMap.put("startDate",startDate);
			dateMap.put("endDate", endDate);
			return dateMap;
		}
		else if(endDate.equals("")){
			endDate = TimeUtils.addDateDetail(startDate);
			dateMap.put("startDate",startDate);
			dateMap.put("endDate", endDate);
			return dateMap;
		}else{
			dateMap.put("startDate",startDate);
			dateMap.put("endDate", endDate);
			return dateMap;
		}
	}
	
	/**
	 * 字符串日期转化为timestamp类型（去掉毫秒数）
	 * @author chsterccw  
	 * @date 2018年4月2日
	 */
	public static String stringToTimeStamp (String str){
	    SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd"); 
	    Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    return String.valueOf(date.getTime()/1000);
	}
	
	/**
	 * 时间转化为时间戳，带毫秒数
	 * @author chsterccw  
	 * @date 2018年4月3日
	 */
	public static String dateToStamp(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
		try {
			date = simpleDateFormat.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }
	
	/**
	 * 时间转化为时间戳，带毫秒数
	 * @author chsterccw  
	 * @date 2018年4月3日
	 */
	public static String dateToStampWithDetail(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
		try {
			date = simpleDateFormat.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

	/**
	 * 时间转化为时间戳，带毫秒数
	 * @author chsterccw
	 * @date 2019年6月15日
	 */
	public static String stampToDateWithMill(Long s){
		String str = String.valueOf(s);
		return stampToDateWithMill(str);
	}
	
	//日期向后推迟1天
	public static String beforeOneDay(String addDate){   
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;   
        int addDay = -1;
        try {   
            date = format.parse(addDate);   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        }   
        Calendar cal = Calendar.getInstance();   
        cal.setTime(date);   
        cal.add(Calendar.DAY_OF_MONTH,addDay);   
        date = cal.getTime();   
        return format.format(date);   
    }
	
	/**
	 * 日期向前推59天
	 * @author chsterccw  
	 * @date 2018年3月7日
	 */
	public static String subFifNineDay(){   
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();   
        int addDay = 61;
        try {   
            date = format.parse(format.format(date));   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        }   
        Calendar cal = Calendar.getInstance();   
        cal.setTime(date);   
        cal.add(Calendar.DAY_OF_MONTH,-addDay);   
        date = cal.getTime();   
        return format.format(date);      
    }
	
	
	public static String getDate(int i) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = TimeUtils.getYestodayDate();   
        //int addDay = -i;
        switch(i) {
        	case 13:i=-19;break;
        	case 14:i=-28;break;
        	case 15:i=-58;break;
        	default :i=-i;break;
        }
        try {   
            date = format.parse(format.format(date));   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        }   
        Calendar cal = Calendar.getInstance();   
        cal.setTime(date);   
        cal.add(Calendar.DAY_OF_MONTH,i);   
        date = cal.getTime();   
        return format.format(date);
	}
	
	
	//日期向后推迟1天
	public static Date getYestodayDate(){   
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();   
        int addDay = -1;
        try {   
            date = format.parse(format.format(date));   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        }   
        Calendar cal = Calendar.getInstance();   
        cal.setTime(date);   
        cal.add(Calendar.DAY_OF_MONTH,addDay);   
        date = cal.getTime();   
        return date;  
    }
	
	/**
	 * 去毫秒数之后的时间戳转化为时间
	 * @author chsterccw  
	 * @date 2018年4月9日
	 */
	public static String stampToDate(String s){
		s = String.valueOf(Long.parseLong(s)*1000);
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
	
	/**
	 * 去毫秒数之后的时间戳转化为时间
	 * @author chsterccw  
	 * @date 2018年4月9日
	 */
	public static String stampToDateWithMill(String s){
		s = String.valueOf(Long.parseLong(s));
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
	
	/**
	 * 去掉年份
	 * @author chsterccw  
	 * @date 2018年4月9日
	 */
	public static String stampToDateDelYYYY(String s){
		s = String.valueOf(Long.parseLong(s)*1000);
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
	
	/**
	 * 一天的开始时间
	 * @return
	 */
	public static String getStartTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return sdf.format(todayStart.getTime());
	}
 
	/**
	 * 一天的结束时间
	 * @return
	 */
	public static String getEndTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.set(Calendar.HOUR_OF_DAY, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		return sdf.format(todayEnd.getTime());
	}


	/**
	 * 每天早上9点的时间戳
	 * @return
	 */
	public static String getMorningNine() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR_OF_DAY, 9);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return sdf.format(todayStart.getTime());
	}

	/**
	 * 每天晚上9点的时间戳
	 * @return
	 */
	public static String getEveningNine() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR_OF_DAY, 21);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return sdf.format(todayStart.getTime());
	}
	
	public static void main(String[] args) {
		String s = "2018-8-9 19:11:47";
		System.out.println(getMorningNine());
		System.out.println(getEveningNine());
		System.out.println(Long.parseLong(dateToStampWithDetail(getEveningNine())));
		System.out.println(Long.parseLong(dateToStampWithDetail(getMorningNine()))+86400000);
		//System.out.println(dateToStampWithDetail(s));
	}

	public static String getCurTime(){
		return String.valueOf(System.currentTimeMillis());
	}


	public static boolean isOnWork(Long createTime) {
		Long todayEvenintNine = Long.parseLong(dateToStampWithDetail(getEveningNine())); //今天晚上9点
		Long tomorrowMorningNine = Long.parseLong(dateToStampWithDetail(getMorningNine()))+86400000;  ////明天早上9点

		Long todayMorningNine = Long.parseLong(dateToStampWithDetail(getMorningNine()));
		Long lastEvenintNine = Long.parseLong(dateToStampWithDetail(getEveningNine()))-86400000;

		if((createTime >= todayEvenintNine && createTime <tomorrowMorningNine)
				|| (createTime < todayMorningNine && createTime >=lastEvenintNine))
			return false;
		else
			return true;
	}
	
}

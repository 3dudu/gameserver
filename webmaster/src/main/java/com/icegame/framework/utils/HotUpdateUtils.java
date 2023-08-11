package com.icegame.framework.utils;

public class HotUpdateUtils {
	
	public static int getRandomNum(int a) {
		return (int)(Math.random()*a);
	}
	
	public static String getPackageUrl(String url) {
		String arr[] = url.split(",");
		if(arr.length==0)
			return "";
		else
			return arr[0].trim()+","+arr[1].trim();
	}
	
	public static String getPackageUrl1(String url) {
		String arr[] = url.split(",");
		if(arr.length==0)
			return "";
		else
			return arr[0].trim();
	}
	
	public static String getPackageUrl1(String url,String pkgUrl) {
		String arr[] = url.split(",");
		if(arr.length==0 || pkgUrl.equals("off"))
			return "";
		else
			return arr[0].trim();
	}
	
	public static String getPackageUrl2(String url) {
		String arr[] = url.split(",");
		if(arr.length==0)
			return "";
		else
			return arr[1].trim();
	}
	
	public static String getPackageUrl2(String url,String pkgUrl) {
		String arr[] = url.split(",");
		if(arr.length==0 || pkgUrl.equals("off"))
			return "";
		else
			return arr[1].trim();
	}
	
	public static String getPackageUrl(String url1,String url2) {
		url1 = url1.length()==0?"":url1.trim();
		url2 = url2.length()==0?"":url2.trim(); 
		return url1+","+url2+",1";
	}
	

}

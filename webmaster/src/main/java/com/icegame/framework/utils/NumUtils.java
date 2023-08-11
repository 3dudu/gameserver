package com.icegame.framework.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumUtils {
	
	/*public static String getNum(String str,int num) {
		double temp = Double.parseDouble(str);
		double arr = temp/num;
		NumberFormat nt = NumberFormat.getPercentInstance();
		nt.setMinimumFractionDigits(2);
		return nt.format(arr);
	}*/
	
	public static String getNum(String str,int num) {
		double temp = Double.parseDouble(str);
		double arr = temp/num;
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(arr);
	}
	
	public static String getNumWithPercent(String str,int num) {
		double temp = Double.parseDouble(str);
		double arr = temp/num;
		NumberFormat nt = NumberFormat.getPercentInstance();
		nt.setMinimumFractionDigits(2);
		return nt.format(arr);
	}
	
	public static String getNumWithPercent(String str) {
		double temp = Double.parseDouble(str);
		NumberFormat nt = NumberFormat.getPercentInstance();
		nt.setMinimumFractionDigits(2);
		return nt.format(temp);
	}
	
	public static String getNum(String str,String str1) {
		double num1 = Double.parseDouble(StringUtils.isNotNull(str)?str:"0");
		double num2 = Double.parseDouble(StringUtils.isNotNull(str1)?str1:"0");
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(num1/num2);
	}

	public static double getNum(BigDecimal bd,int num) {
		double temp = Double.parseDouble(bd.toString());
		double arr = temp/num;
		NumberFormat nt = NumberFormat.getPercentInstance();
		nt.setMinimumFractionDigits(2);
		return arr;
	}
	
	public static double getNum(int newPlayer,int loginActive) {
		double dnewPlayer = newPlayer;
		double dloginActive = loginActive;
		double result = dnewPlayer/dloginActive;
		NumberFormat nt = NumberFormat.getPercentInstance();
		nt.setMinimumFractionDigits(2);
		return result;
	}

}

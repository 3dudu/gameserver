package com.xuegao.PayServer.util.wxpay;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DoubleCompare {
	/**
	 * return   0 一样大
	 * 			1  第一一个参数大
	 * 			2 第二个参数大
	 * */
	public static int compare(BigDecimal val1, BigDecimal val2) {
		int  rs =0;
		String result = "";
		if (val1.compareTo(val2) < 0) {
			result = "第二位数大！";
			rs=2;
		}
		if (val1.compareTo(val2) == 0) {
			result = "两位数一样大！";
			rs=0;
		}
		if (val1.compareTo(val2) > 0) {
			result = "第一位数大！";
			rs=1;
		}
		return rs;
	}

	public  static  int compareNum(String  val1,String val2){
		
	    Double val1D = Double.parseDouble(val1);//6.2041    这个是转为double类型  
	    DecimalFormat df = new DecimalFormat("0.00");   
	    String val1DStr = df.format(val1D); //6.20   这个是字符串，但已经是我要的两位小数了  
	    Double dob1 = Double.parseDouble(val1DStr); //6.20  
	    
	    Double val2D = Double.parseDouble(val2);//6.2041    这个是转为double类型  
	    String val2DStr = df.format(val2D); //6.20   这个是字符串，但已经是我要的两位小数了  
	    Double dob2 = Double.parseDouble(val2DStr); //6.20  
	    
	    BigDecimal data1 = new BigDecimal(dob1);
		BigDecimal data2 = new BigDecimal(dob2);
		return DoubleCompare.compare(data1, data2);
		
	}
	
	
	public static void main(String[] args) {
		double a = 0.001;
		double b = 0.011;
		BigDecimal data1 = new BigDecimal(a);
		BigDecimal data2 = new BigDecimal(b);
		System.out.println(new DoubleCompare().compare(data1, data2));
		
		
	    Double val1D = Double.parseDouble("111199.023132");//6.2041    这个是转为double类型  
	    DecimalFormat df = new DecimalFormat("0.00");   
	    String val1DStr = df.format(val1D); //6.20   这个是字符串，但已经是我要的两位小数了  
	    Double cny = Double.parseDouble(val1DStr); //6.20 
	    System.out.println(cny);
	    
	    System.out.println(compareNum("10000.012","10000.01"));
	}
}
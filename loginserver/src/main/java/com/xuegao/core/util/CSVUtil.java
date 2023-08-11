package com.xuegao.core.util;

import java.util.ArrayList;
import java.util.List;

public class CSVUtil {
	
	public static List<String> createCsvStr(List<String> heads,List<Object[]> contents){
		List<String> list=new ArrayList<>();
		StringBuffer sb=new StringBuffer();
		for(String head:heads){
			sb.append(dealObjectToString(head));
		}
		list.add(sb.toString());
		for(Object[] line:contents){
			sb=new StringBuffer();
			for(Object obj:line){
				sb.append(dealObjectToString(obj));
			}
			list.add(sb.toString());
		}
		return list;
	}
	/**
	 * 出现逗号或者双引号：1.将双引号变为 两个双引号 2.在两边加上双引号
	 * @author ccx
	 * @date 2017-7-4 上午11:36:26
	 */
	private static String dealObjectToString(Object object){
		if(object==null){
			return "";
		}
		String str=String.valueOf(object);
		boolean containsYinhao=str.indexOf("\"")!=-1;
		boolean containsDouhao=str.indexOf(",")!=-1;
		if(containsYinhao||containsDouhao){
			str=str.replaceAll("\"", "\"\"");
			str="\""+str+"\"";
		}
		str+=",";
		return str;
	}
}

package com.icegame.framework.utils;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Pattern;

import cn.hutool.core.lang.Console;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * 字符串非空判断
 * @Description: TODO
 * @Package com.icegame.framework.utils
 * @author chesterccw
 * @date 2018年1月22日
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

	public static boolean isNull(String str) {
		return null == str || str.length() == 0 || "".equals(str)
				|| str.matches("\\s*");
	}

	public static boolean isNull(int num) {
		return null == (Integer)num;
	}

	public static boolean isNull(Long str) {
		return null == str || "".equals(str);
	}

	public static boolean isNullArr(String[] str) {
		return null == str || str.length == 0 || "".equals(str);
	}

	public static boolean isNull(BigDecimal str) {
		return null == str || "".equals(str);
	}

	public static boolean equalsNull(String str) {
		return null == str;
	}

	/**
	 * 非空判断
	 */
	public static boolean isNotNull(String str){
		return !isNull(str);
	}

	public static boolean isNotNull(int num){
		return !isNull(num);
	}

	public static boolean isNotNull(Long str){
		return !isNull(str);
	}

	public static boolean isNotNull(BigDecimal str){
		return !isNull(str);
	}

	public static String str(String url){
    	return url.substring(0,url.lastIndexOf("/"));
    }

	public static JSONArray splitSidPid(String str){
		JSONArray sidPidArr = new JSONArray();
		String split = "\\r\\n";
		String[] sidPidStrArray = str.split(split);
		for(int i = 0 ; i < sidPidStrArray.length ; i++) {
			JSONObject sidPidObj = new JSONObject();
			String[] sidPidLongArray = sidPidStrArray[i].trim().split(":");
			sidPidObj.put(Long.valueOf(sidPidLongArray[0].trim()), String.valueOf(sidPidLongArray[1].trim()));
			sidPidArr.add(sidPidObj);
		}
		return sidPidArr;
	}

	public static String[] splitSid(String str){
		String split = "\\r\\n";
		String[] sidStrArray = str.split(split);
		return sidStrArray;
	}

	public static String awardStrformart(String str) {
		return str.replaceAll("\\r\\n", ",");
	}

	public static int getId(String str,String type){
		int i = 0;
		str = str.substring(str.length() - 2, str.length());
		String[] ids = str.split("");
		switch (type){
			case "question" : i = Integer.valueOf(ids[0]); break;
			case "option" : i = Integer.valueOf(ids[1]); break;
		}
		return i;
	}


	public static void main(String[] args){
		String aa = "[slaveId=4]#http://1.1.1.1:6003/sync/maintainserver";
		String[] ids = aa.split("");
		System.out.println(ids[0]);
		System.out.println(ids[1]);
		Console.log(getHost(aa));
	}

	/**
	 * 获取 URL 中的id地址
	 * @param string 源地址
	 * @return String
	 */
	public static String getHost(String string){
		if(isEmpty(string)){
			return null;
		}
		string = string.substring(string.indexOf("//") + 2);
		return string.split("\\:")[0];
	}

	public static String getDownloadZipName(String path){
		path = path.substring(0,path.lastIndexOf(File.separator));
		path = path.substring(path.lastIndexOf(File.separator),path.length());
		path = path.substring(1,path.length());
		return path + ".zip";
	}

	public static String multFormat(String multChannel){
		StringBuffer sb = new StringBuffer();
		String[] strings = multChannel.split("\r\n");

		if(strings.length > 0){
			sb.append("(");
			for(String s : strings){
				sb.append("'").append(s).append("'").append(",");
			}
			sb = sb.deleteCharAt(sb.length() - 1);
			sb.append(")");
		}
		return sb.toString();
	}

	public static String multFormat(String multChannel, String format){
		StringBuffer sb = new StringBuffer();
		String[] strings = multChannel.split(format);

		if(strings.length > 0){
			sb.append("(");
			for(String s : strings){
				sb.append("'").append(s).append("'").append(",");
			}
			sb = sb.deleteCharAt(sb.length() - 1);
			sb.append(")");
		}
		return sb.toString();
	}

	public static String multFormatComma(String multChannel){
		StringBuffer sb = new StringBuffer();
		String[] strings = multChannel.split(",");

		if(strings.length > 0){
			sb.append("(");
			for(String s : strings){
				sb.append("'").append(s).append("'").append(",");
			}
			sb = sb.deleteCharAt(sb.length() - 1);
			sb.append(")");
		}
		return sb.toString();
	}

	/**
	 * 主要用于把对象转化为json对象出现的格式问题(\t || \n ||  \)
	 * @param str
	 * @return
	 */
	public static String jsonFormat(String str){
		return StringEscapeUtils.unescapeJavaScript(str).replaceAll("[ \t\r\n]","");
	}

	/**
	 * 取一段字符串前面的数字
	 * @param str
	 * @return
	 */

	public static  Long getNumInStr(String str){
		Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
		String[] strs = str.split("");
		String strNum = "";
		for (int i = 0; i < strs.length; i++) {
			if(pattern.matcher(strs[i]).matches()){
				strNum += strs[i];
			}
			else {
				break;
			}
		}
		return Long.valueOf(strNum);
	}

	public static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}


	public static String formatMapToString(Map<String, Object> map){
		if (map == null){
			return null;
		}
		StringBuffer str = new StringBuffer();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			str.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		str.deleteCharAt(str.length() - 1);
		return str.toString();
	}

	public static String awardStrFormat(String str) {
		return str.replaceAll("\\r\\n", ",");
	}

}

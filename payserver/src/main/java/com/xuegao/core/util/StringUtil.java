package com.xuegao.core.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;


public class StringUtil {
	
	public static String[] split(String str, String tag) {
		
		List<String> list = new ArrayList<String>();
		int i = str.indexOf(tag);
		int len=tag.length();
		while (i != -1) {
			list.add(str.substring(0, i));
			str = str.substring(i + len);
			i = str.indexOf(tag);
		}
		list.add(str);
		return list.toArray(new String[0]);
	}

	public static String deal(String str) {
		str = replace(str, "\\", "\\\\");
		str = replace(str, "'", "\\'");
		str = replace(str, "\r", "\\r");
		str = replace(str, "\n", "\\n");
		str = replace(str, "\"", "\\\"");
		return str;
	}

	
	public static String replace(String strSource, String strFrom, String strTo) {
		if(strSource==null){
			return null;
		}
		int i = 0;
		if ((i = strSource.indexOf(strFrom, i)) >= 0) {
			char[] cSrc = strSource.toCharArray();
			char[] cTo = strTo.toCharArray();
			int len = strFrom.length();
			StringBuffer buf = new StringBuffer(cSrc.length);
			buf.append(cSrc, 0, i).append(cTo);
			i += len;
			int j = i;
			while ((i = strSource.indexOf(strFrom, i)) > 0) {
				buf.append(cSrc, j, i - j).append(cTo);
				i += len;
				j = i;
			}
			buf.append(cSrc, j, cSrc.length - j);
			return buf.toString();
		}
		return strSource;
	}

	
	public static String getSqlString(String sql, String tag, String... params) {
		int i = sql.indexOf(tag);
		int count = 0;
		while (i != -1) {
			String pa = params[count++];
			pa = pa == null ? pa : ("'" + pa + "'");
			sql = sql.substring(0, i) + pa + sql.substring(i + tag.length());
			i = sql.indexOf(tag);
		}
		return sql;
	}

	
	public static int getTagLength(String srcStr, String tag) {
		int count = 0;
		if (srcStr != null) {
			int index = srcStr.indexOf(tag);
			while (index != -1) {
				srcStr = srcStr.substring(index + 1);
				count++;
				index = srcStr.indexOf(tag);
			}
		}
		return count;
	}

	
	public static boolean isNumber(String str) {
		if(null==str||str.length()==0){
			return false;
		}
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isPhoneNumber(String str) {
		if(str==null||str.length()!=11){
			return false;
		}
		if(!(str.startsWith("13")||str.startsWith("15"))){
			return false;
		}
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static String trim(String src) {
		String dest = null;
		if(src!=null) {
			String s = "[^\\p{Graph}]";
			dest = src.split(s)[0];
		}
		return dest;
	}
	private static String hexString="0123456789ABCDEF";

	
	public static String GBToHex(String content) {
		try {
			byte[] bytes = content.getBytes("GBK");
			StringBuffer sb =new StringBuffer(bytes.length*2);
			for(int i=0;i<bytes.length;i++){
				sb.append(hexString.charAt((bytes[i]&0xf0)>>4));
				sb.append(hexString.charAt((bytes[i]&0x0f)>>0));
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static String UnicodeToHex(String content) {
			char[] chars = content.toCharArray();
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<chars.length;i++) {
				String s = Integer.toHexString(chars[i]);
				sb.append(s.substring(2));
				sb.append(s.substring(0,2));
			}
			return sb.toString();
	}
	
	public static String HexToGB(String bytes) {
		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream(bytes.length()/2);
		for(int i=0;i<bytes.length();i+=2) {
			byteArrayOutputStream.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1))));
		}
		return byteArrayOutputStream.toString();
	}
	public static String getMiddleString(String source,String front,String back){
		int a=source.indexOf(front);
		if(a!=-1){
			source=source.substring(a+front.length());
			int b=source.indexOf(back);
			if(b!=-1){
				source=source.substring(0,b);
				return source;
			}
		}
		return null;
	}
	public static String toString(List<?> list){
		if(list!=null){
			StringBuffer sb=new StringBuffer();
			int size=list.size();
			for(int i=0;i<size;i++){
				Object o=list.get(i);
				sb.append(o);
				if(i!=size-1){
					sb.append("\n");
				}
			}
			return sb.toString();
		}else {
			return null;
		}
	}
	
	public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";

	 

	public static String encodeURIComponent(String input) {

		if (input == null || "".equals(input)) {

			return input;

		}

 

		int l = input.length();

		StringBuilder o = new StringBuilder(l * 3);

		try {

			for (int i = 0; i < l; i++) {

				String e = input.substring(i, i + 1);

				if (ALLOWED_CHARS.indexOf(e) == -1) {

					byte[] b = e.getBytes("utf-8");

					o.append(getHex(b));

					continue;

				}

				o.append(e);

			}

			return o.toString();

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

		}

		return input;

	}

 

	private static String getHex(byte buf[]) {

		StringBuilder o = new StringBuilder(buf.length * 3);

		for (int i = 0; i < buf.length; i++) {

			int n = (int) buf[i] & 0xff;

			o.append("%");

			if (n < 0x10) {

				o.append("0");

			}

			o.append(Long.toString(n, 16).toUpperCase());

		}

		return o.toString();

	}

 

	public static String decodeURIComponent(String encodedURI) {

		char actualChar;

 

		StringBuffer buffer = new StringBuffer();

 

		int bytePattern, sumb = 0;

 

		for (int i = 0, more = -1; i < encodedURI.length(); i++) {

			actualChar = encodedURI.charAt(i);

 

			switch (actualChar) {

			case '%': {

				actualChar = encodedURI.charAt(++i);

				int hb = (Character.isDigit(actualChar) ? actualChar - '0'

						: 10 + Character.toLowerCase(actualChar) - 'a') & 0xF;

				actualChar = encodedURI.charAt(++i);

				int lb = (Character.isDigit(actualChar) ? actualChar - '0'

						: 10 + Character.toLowerCase(actualChar) - 'a') & 0xF;

				bytePattern = (hb << 4) | lb;

				break;

			}

			case '+': {

				bytePattern = ' ';

				break;

			}

			default: {

				bytePattern = actualChar;

			}

			}

 

			if ((bytePattern & 0xc0) == 0x80) { // 10xxxxxx

				sumb = (sumb << 6) | (bytePattern & 0x3f);

				if (--more == 0)

					buffer.append((char) sumb);

			} else if ((bytePattern & 0x80) == 0x00) { // 0xxxxxxx

				buffer.append((char) bytePattern);

			} else if ((bytePattern & 0xe0) == 0xc0) { // 110xxxxx

				sumb = bytePattern & 0x1f;

				more = 1;

			} else if ((bytePattern & 0xf0) == 0xe0) { // 1110xxxx

				sumb = bytePattern & 0x0f;

				more = 2;

			} else if ((bytePattern & 0xf8) == 0xf0) { // 11110xxx

				sumb = bytePattern & 0x07;

				more = 3;

			} else if ((bytePattern & 0xfc) == 0xf8) { // 111110xx

				sumb = bytePattern & 0x03;

				more = 4;

			} else { // 1111110x

				sumb = bytePattern & 0x01;

				more = 5;

			}

		}

		return buffer.toString();
		
	}
	/**
	 * 将数组变成字符串
	 * @param glue
	 * @param pieces
	 * @return
	 */
	public static String implode(String glue,Object[] pieces){
		if(pieces==null||pieces.length==0){
			return "";
		}
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<pieces.length;i++){
			sb.append(pieces[i]);
			if(i!=pieces.length-1){
				sb.append(glue);
			}
		}
		return sb.toString();
	}
	/**
	 * 将数组变成字符串 [a,b] prefix=` suffix=` ==>`a`,`b`   
	 * @param glue
	 * @param pieces
	 * @param prefix 前缀
	 * @param suffiix 后缀
	 * @return
	 */
	public static String implode(String glue,Object[] pieces,String prefix,String suffix){
		if(pieces!=null){
			String[] pieces2=new String[pieces.length];
			for(int i=0;i<pieces.length;i++){
				pieces2[i]=prefix+pieces[i]+suffix;
			}
			pieces=pieces2;
		}
		return implode(glue, pieces);
		
	}
	
	/**
     * emoji表情替换
     *
     * @param source 原字符串
     * @param slipStr emoji表情替换成的字符串                
     * @return 过滤后的字符串
     */
    public static String filterEmoji(String source,String slipStr) {
        if(source!=null&&source.length()>0){
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr);
        }else{
            return source;
        }
    }
    
    public static String fetchUniqStr_32(){
    	String uuid=UUID.randomUUID().toString();
    	uuid=StringUtil.replace(uuid, "-", "");
    	return uuid;
    }
    
    /**
     * 排序并拼接url参数，值为null=空字符串
     * @param objects
     * @return
     */
    public static String fetchUrlParamStr(Object[] objects,boolean encodeValue){
    	StringBuilder sb=new StringBuilder();
    	
    	int size=objects.length/2;
    	TreeMap<String, Object> treeMap=new TreeMap<>();
    	for(int i=0;i<size;i++){
    		Object name=objects[2*i];
    		Object value=objects[2*i+1];
    		treeMap.put(name.toString(), value);
    	}
    	for(Entry<String, Object> entry:treeMap.entrySet()){
    		sb.append(entry.getKey());
    		sb.append("=");
    		if(encodeValue){
    			sb.append(entry.getValue()==null?"":URLEncoder.encode(""+entry.getValue()));
    		}else{
    			sb.append(entry.getValue()==null?"":""+entry.getValue());
    		}
    		sb.append("&");
		}
    	if(sb.length()>0){
    		return sb.substring(0, sb.length()-1);
    	}else{
    		return sb.toString();
    	}
    }
    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public static String fetchUrlParamStr(Map map,boolean encodeValue){
    	StringBuilder sb=new StringBuilder();
    	TreeMap<String, Object> treeMap=new TreeMap<>();
    	treeMap.putAll(map);
    	for(Entry<String, Object> entry:treeMap.entrySet()){
    		sb.append(entry.getKey());
    		sb.append("=");
    		if(encodeValue){
    			sb.append(entry.getValue()==null?"":URLEncoder.encode(""+entry.getValue()));
    		}else{
    			sb.append(entry.getValue()==null?"":""+entry.getValue());
    		}
    		sb.append("&");
		}
    	if(sb.length()>0){
    		return sb.substring(0, sb.length()-1);
    	}else{
    		return sb.toString();
    	}
    }
    
    /**
     * 首字母大写，其他小写
     * @param word
     * @return
     */
    public static String toUpperCaseFirstword(String word){
    	if(word.length()<2){
    		return word.toUpperCase();
    	}
    	return word.substring(0,1).toUpperCase()+word.substring(1).toLowerCase();
    }
    /**
     * 移除列表中重复的元素
     * @author ccx
     * @date 2017-3-14 下午2:53:40
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public   static   void  removeDuplicateWithOrder(List list)   { 
    	if(list==null)return;
		Set set = new HashSet<>();
		List newList = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		list.clear();
		list.addAll(newList);
	} 
    
    /**
	    * 获取定长的字符串
	    * @param str 原始字符串
	    * @param len 固定长度
	    * @param c 不够填充的字符
	    * @return 固定长度的字符串
	    */
	public static String getFixedLenString(String str, int len, char c) {
     if (str == null || str.length() == 0){
         str = "";
     }
     if (str.length() == len){
         return str;
     }
     if (str.length() > len){
         return str.substring(0,len);
     }
     StringBuilder sb = new StringBuilder(str);
     while (sb.length() < len){
         sb.insert(0, c);
     }
     return sb.toString();
 }
	
	 /**
     * 判断字符串是否为空
     * @param param
     * @return true 空  false 非空
     */
    public static boolean isEmpty(String param) {
    	return param==null||param.length()<=0;
    }
    
}

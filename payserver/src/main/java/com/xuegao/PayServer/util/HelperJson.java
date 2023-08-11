package com.xuegao.PayServer.util;

import java.io.IOException;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;

public class HelperJson {
	/** OBJECT_MAPPER */
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	static {
		// OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	/**
	 * @param map
	 * @param filterKeys
	 * @return
	 */
	public static StringBuilder builderSignSortValues(Map<String, ?> map, String[] filterKeys) {
		List<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		StringBuilder stringBuilder = new StringBuilder();
		for (String key : keys) {
			if (filterKeys != null && ArrayUtils.contains(filterKeys, key)) {
				continue;
			}

			stringBuilder.append(map.get(key));
		}

		return stringBuilder;
	}
	
	/**
	 * @param string
	 * @return
	 */
	public static Map decodeMap(String string) {
		return decodeNull(string, Map.class);
	}
	/**
	 * @param string
	 * @param toClass
	 * @return
	 */
	public static <T> T decodeNull(String string, Class<T> toClass) {
		if (KernelString.isEmpty(string)) {
			return null;
		}
		try {
			return decode(string, toClass);

		} catch (Exception e) {
				e.printStackTrace();
		}
		return null;
	}
	/**
	 * @param string
	 * @param toClass
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static <T> T decode(String string, Class toClass) throws IOException {
		ObjectReader reader = OBJECT_MAPPER.reader(toClass);
		return reader.readValue(string);
	}
	/**
	 * 解析出url参数中的键值对
	 * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
	 *
	 * @param URL url地址
	 * @return url请求参数部分
	 */
	public static Map<String, String> URLRequest(String URL) {
		Map<String, String> mapRequest = new HashMap<String, String>();

		String[] arrSplit = null;

		String strUrlParam = TruncateUrlPage(URL);
		if (strUrlParam == null) {
			return mapRequest;
		}
		//每个键值为一组 www.2cto.com
		arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = null;
			arrSplitEqual = strSplit.split("[=]");

			//解析出键值
			if (arrSplitEqual.length > 1) {
				//正确解析
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

			} else {
				if (arrSplitEqual[0] != "") {
					//只有参数没有值，不加入
					mapRequest.put(arrSplitEqual[0], "");
				}
			}
		}
		return mapRequest;
	}

	/**
	 * 去掉url中的路径，留下请求参数部分
	 *
	 * @param strURL url地址
	 * @return url请求参数部分
	 */
	private static String TruncateUrlPage(String strURL) {
		String strAllParam = null;
		String[] arrSplit = null;

		strURL = strURL.trim();

		arrSplit = strURL.split("[?]");
		if (strURL.length() > 1) {
			if (arrSplit.length > 1) {
				if (arrSplit[1] != null) {
					strAllParam = arrSplit[1];
				}

			}
			else { strAllParam = arrSplit[0];}
		}

		return strAllParam;
	}
}

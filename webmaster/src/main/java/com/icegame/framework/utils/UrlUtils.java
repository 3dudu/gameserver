package com.icegame.framework.utils;

import java.io.InputStream;
import java.net.URL;

public class UrlUtils {

	public static void main(String[] args) {
		System.out.println(UrlUtils.isUseful("http://www.baidu.com"));
	}
	
	@SuppressWarnings("finally")
	public static boolean isUseful(String insertUrl) {
		@SuppressWarnings("unused")
		InputStream in;
		URL url;
		boolean flag = false;
		try {
			url = new URL(insertUrl);
			in = url.openStream();
			flag = true;
		} catch (Exception e) {
			flag = false;
			url = null;
		}finally {
			return flag;
		}
	}
}

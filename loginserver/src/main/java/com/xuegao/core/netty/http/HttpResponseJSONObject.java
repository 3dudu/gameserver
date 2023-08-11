package com.xuegao.core.netty.http;

import io.netty.handler.codec.http.Cookie;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class HttpResponseJSONObject extends JSONObject{
	
	private static final long serialVersionUID = 1L;
	private boolean isFile=false;
	private boolean text=false;
	private String textContent;
	private File file;
	private String fileName;
	List<Cookie> cookies=new ArrayList<Cookie>();
	
	public boolean isFile() {
		return isFile;
	}
	public void setIsFile(boolean isFile) {
		this.isFile = isFile;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<Cookie> getCookies() {
		return cookies;
	}
	public boolean isText() {
		return text;
	}
	public void setText(boolean text) {
		this.text = text;
	}
	public String getTextContent() {
		return textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	
}

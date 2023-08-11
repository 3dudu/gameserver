package com.xuegao.core.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.netty.Releasable;


public class HttpRequestJSONObject extends JSONObject implements Releasable{
	
	String httpUri;
	ByteBuf httpBodyBuf;
	Map<String, String> httpHeaders=new HashMap<String, String>();
	Map<String, String> httpCookies=new HashMap<String, String>();
	String httpMethod;
	List<FileUpload> httpFileUploads=new ArrayList<FileUpload>();
	HttpPostRequestDecoder decoder=null;
	
	boolean hasInHandler=false;
	
	public String getHttpUri() {
		return httpUri;
	}
	public void setHttpUri(String httpUri) {
		this.httpUri = httpUri;
	}
	public ByteBuf getHttpBodyBuf() {
		return httpBodyBuf;
	}
	public void setHttpBodyBuf(ByteBuf httpBodyBuf) {
		this.httpBodyBuf = httpBodyBuf;
	}
	public Map<String, String> getHttpHeaders() {
		return httpHeaders;
	}
	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}
	public String getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	public List<FileUpload> getHttpFileUploads() {
		return httpFileUploads;
	}
	public void setHttpFileUploads(List<FileUpload> httpFileUploads) {
		this.httpFileUploads = httpFileUploads;
	}
	@Override
	public void release() {
		if(httpBodyBuf!=null){
			if(httpBodyBuf.refCnt()>0) {
				httpBodyBuf.release();
			}
			httpBodyBuf=null;
		}
		if(decoder!=null){
			decoder.destroy();
			decoder=null;
		}

	}
	public HttpPostRequestDecoder getDecoder() {
		return decoder;
	}
	public void setDecoder(HttpPostRequestDecoder decoder) {
		this.decoder = decoder;
	}
	public boolean isInHandler() {
		return hasInHandler;
	}
	public void setHasInHandler(boolean hasInHandler) {
		this.hasInHandler = hasInHandler;
	}
	public Map<String, String> getHttpCookies() {
		return httpCookies;
	}
	
	public String getRemoteIp(){
		String forwardstr=this.getHttpHeaders().get("X-Forwarded-For");
		String ip=null;
		if(forwardstr!=null){
			if(forwardstr.indexOf(",")!=-1){
				ip=forwardstr.substring(0,forwardstr.indexOf(","));
			}else if(forwardstr.length()>=7){
				ip=forwardstr;
			}
		}
		return ip;
	}
	
}

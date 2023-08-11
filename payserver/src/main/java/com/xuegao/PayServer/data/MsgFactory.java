package com.xuegao.PayServer.data;

import com.alibaba.fastjson.JSONObject;


public class MsgFactory {

	public static JSONObject getDefaultErrorMsg(String msg){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("ret", -1);
		jsonObject.put("msg", msg);
		return jsonObject;
	}
	public static JSONObject getIOSErrorMsg(String msg){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("ret", 2);
		jsonObject.put("msg", msg);
		return jsonObject;
	}
	public static JSONObject getErrorMsg(int error_code,String msg){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("ret", error_code);
		jsonObject.put("msg", msg);
		return jsonObject;
	}

	public static JSONObject getDefaultSuccessMsg(){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("ret", 0);
		return jsonObject;
	}
	public static JSONObject getMsg(int code,String msg){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("code", code);
		jsonObject.put("msg", msg);
		return jsonObject;
	}
	public static JSONObject getStatus(int status ,String msg){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("status", status);
		jsonObject.put("msg", msg);
		return jsonObject;
	}

}

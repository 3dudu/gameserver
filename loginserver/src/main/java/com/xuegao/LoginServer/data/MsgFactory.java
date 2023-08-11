package com.xuegao.LoginServer.data;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants.ErrorCode;
import com.xuegao.LoginServer.vo.X7DataVo;

import java.util.List;


public class MsgFactory {
	
	public static JSONObject getDefaultErrorMsg(String msg){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("ret", -1);
		jsonObject.put("msg", msg);
		return jsonObject;
	}
	
	public static JSONObject getErrorMsg(ErrorCode error_code){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("ret", error_code.type);
		jsonObject.put("msg", error_code.toString());
		return jsonObject;
	}
	public static JSONObject getDefaultErrorMsg(){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("ret", -1);
		return jsonObject;
	}
	
	public static JSONObject getDefaultSuccessMsg(){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("ret", 0);
		return jsonObject;
	}

	public static JSONObject getX7ApiErrorMsg(String errorno, String errormsg, List<X7DataVo> x7DataList){
		JSONObject errorMsg=new JSONObject();
		errorMsg.put("errorno",errorno);
		errorMsg.put("errormsg",errormsg);
		errorMsg.put("user_str",x7DataList);
		return errorMsg;
	}

	public static JSONObject getGOSUApiErrorMsg(int status, Object msg){
		JSONObject errorMsg=new JSONObject();
		errorMsg.put("msg",msg);
		errorMsg.put("status",status);
		return errorMsg;
	}
}

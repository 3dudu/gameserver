package com.xuegao.PayServer.slaveServer;

import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * SC通信的数据结构对象
 * @author ccx
 * @date 2017-8-17 下午4:09:18
 */
public class ScJSONObject {
	public String sc_reqResId = null;
	public int sc_type = 0;	//0:request消息      1:response消息    2:notify消息(无需响应)
	public String sc_cmd = null;
	public JSONObject sc_data = null;
	
	
	public ScJSONObject() {
		super();
	}
	
	public static ScJSONObject newRequestMsg(String cmd,JSONObject data){
		ScJSONObject scJSONObject=new ScJSONObject();
		scJSONObject.sc_type=0;
		scJSONObject.sc_cmd=cmd;
		scJSONObject.sc_data=data;
		scJSONObject.sc_reqResId=UUID.randomUUID().toString();
		return scJSONObject;
	}
	
	public static ScJSONObject newResponseMsg(String sc_reqResId,JSONObject data){
		ScJSONObject scJSONObject=new ScJSONObject();
		scJSONObject.sc_type=1;
		scJSONObject.sc_reqResId=sc_reqResId;
		scJSONObject.sc_cmd=null;
		scJSONObject.sc_data=data;
		return scJSONObject;
	}
	
	public static ScJSONObject newNotifyMsg(String cmd,JSONObject data){
		ScJSONObject scJSONObject=new ScJSONObject();
		scJSONObject.sc_type=2;
		scJSONObject.sc_cmd=cmd;
		scJSONObject.sc_data=data;
		scJSONObject.sc_reqResId=UUID.randomUUID().toString();
		return scJSONObject;
	}
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}

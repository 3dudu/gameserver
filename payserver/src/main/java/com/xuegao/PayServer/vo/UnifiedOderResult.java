package com.xuegao.PayServer.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xuegao.PayServer.po.OrderPo;

/**
 * 统一下单 对象
 * */
public  class  UnifiedOderResult {
	
	public int ret;
	public String msg;
	public OrderPo data;
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public OrderPo getData() {
		return data;
	}
	public void setData(OrderPo data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "UnifiedOderResult [ret=" + ret + ", msg=" + msg + ", data=" + data + "]";
	}
	
	
}
package com.xuegao.LoginServer.po;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.db.DBWrapper;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.PoInfoVo;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;

@Table(proxoolAlias="proxool.GateWay",tableName="pf_options")
public class PfOptionPo extends BasePo{
	
	@Column
	private String key;//键(各平台接入参数)
	@Column
	private String value;//值(json配置)
	@Column
	private int close;//关闭该渠道充值要求客户端不再调起支付,关闭状态下订单直接返回false不允许支付
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getClose() {
		return close;
	}
	public void setClose(int close) {
		this.close = close;
	}

	public static PfOptionPo findEntityByKeyFromDB(String key){
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(PfOptionPo.class);
		String sql="select * from `"+poInfoVo.getTableName()+"` where `key`=?";
		JSONObject object=DBWrapper.getInstance(poInfoVo.getProxoolAlias()).queryForBean(sql, key);
		PfOptionPo basePo=JSON.toJavaObject(object, PfOptionPo.class);
		return basePo;
	}
	
	
}

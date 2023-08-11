package com.xuegao.PayServer.po;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.db.DBWrapper;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.PoInfoVo;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;

@Table(tableName = "pf_options", proxoolAlias = "proxool.PayServer")
public class PfOptionsPo extends BasePo {

	@Column
	private String key;// 键(各平台接入参数)',
	@Column
	private String value;// '值(json配置)'
	@Column
	private int close;// '关闭该渠道充值要求客户端不再调起支付,关闭状态下订单直接返回false不允许支付',

	
	@SuppressWarnings("unchecked")
	public static PfOptionsPo findEntityByKeyFromDB(String key){
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(PfOptionsPo.class);
		String sql="select * from `"+poInfoVo.getTableName()+"` where `key`=?";
		JSONObject object=DBWrapper.getInstance(poInfoVo.getProxoolAlias()).queryForBean(sql, key);
		PfOptionsPo basePo=JSON.toJavaObject(object, PfOptionsPo.class);
		return basePo;
	}
	
	
	@Override
	public String toString() {
		return "PfOptionsPo [key=" + key + ", value=" + value + ", close=" + close + "]";
	}
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

}

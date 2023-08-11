package com.xuegao.PayServer.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.db.DBWrapper;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.PoInfoVo;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;

@Table(tableName = "pay_order_syn", proxoolAlias = "proxool.PayServer")
public class OrderSynchPo extends BasePo{
	@Column
	private String order_id;//
	@Column
	private int sych_status;// 同步状态 1 正在同步等待通知',
	@Column
	private int sych_cnt;// 同步次数',
	@Column
	private String status_msg;// 状态描述',
	@Column
	private Date create_time;// '新增时间',
	
	public static List<OrderSynchPo> getAllPendingWorking(){
		
		DBWrapper dbWrapper = BasePo.fetchDBWrapper(OrderSynchPo.class);
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(OrderSynchPo.class);
		String sql="select * from pay_order_syn where sych_cnt<=5 and sych_status=1";
		JSONArray array=DBWrapper.getInstance(poInfoVo.getProxoolAlias()).queryForList(sql);
		List<OrderSynchPo> list=new ArrayList<>();
		for(int i=0;i<array.size();i++){
			JSONObject object=array.getJSONObject(i);
			BasePo basePo=JSON.toJavaObject(object, OrderSynchPo.class);
			list.add((OrderSynchPo) basePo);
		}
		return list;
		
	}
	
	public static  OrderSynchPo getOrderSyncPoByOrdId(String ord_id){
		
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(OrderSynchPo.class);
		String sql="select * from `"+poInfoVo.getTableName()+"` where `order_id`=?";
		JSONObject object=DBWrapper.getInstance(poInfoVo.getProxoolAlias()).queryForBean(sql, ord_id);
		OrderSynchPo basePo=JSON.toJavaObject(object, OrderSynchPo.class);
		return basePo;
		
	}
	/**
	 * 根据主键 更新为 订单完成 
	 * */
	public  static int updateOrderSyncPoById(int id){
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(OrderSynchPo.class);
		String updateSql="update pay_order_syn set sych_status=2,status_msg='success' where id=?";
		int rs = DBWrapper.getInstance(poInfoVo.getProxoolAlias()).execute(updateSql,id);
		return rs;
	}
	
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public int getSych_status() {
		return sych_status;
	}
	public void setSych_status(int sych_status) {
		this.sych_status = sych_status;
	}
	public int getSych_cnt() {
		return sych_cnt;
	}
	public void setSych_cnt(int sych_cnt) {
		this.sych_cnt = sych_cnt;
	}
	public String getStatus_msg() {
		return status_msg;
	}
	public void setStatus_msg(String status_msg) {
		this.status_msg = status_msg;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	@Override
	public String toString() {
		return "OrderSynchPo [order_id=" + order_id + ", sych_status=" + sych_status + ", sych_cnt=" + sych_cnt
				+ ", status_msg=" + status_msg + ", create_time=" + create_time + "]";
	}

	
}

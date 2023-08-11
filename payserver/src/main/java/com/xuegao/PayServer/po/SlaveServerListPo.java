package com.xuegao.PayServer.po;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.db.DBWrapper;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.PoInfoVo;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;
import com.xuegao.core.util.StringUtil;

@Table(tableName = "server_list", proxoolAlias = "proxool.PayServer")
public class SlaveServerListPo  extends BasePo{

	@Column
	private int server_id;// COMMENT '区服ID',
	@Column
	private String name;// '区服名称',
	@Column
	private String ip;// 'ip地址',
	@Column
	private int port;// '端口号',
	@Column
	private String channel;// '渠道号',

	private String nip;// 'ip地址',



	public int getServer_id() {
		return server_id;
	}
	public void setServer_id(int server_id) {
		this.server_id = server_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getNip() {
		return nip;
	}
	public void setNip(String nip) {
		this.nip = nip;
	}

	@SuppressWarnings("unchecked")
	public static SlaveServerListPo findEntityByServerIdFromDB(String server_id){
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(SlaveServerListPo.class);
		String sql="select * from `"+poInfoVo.getTableName()+"` where `server_id`=?";
		JSONObject object=DBWrapper.getInstance(poInfoVo.getProxoolAlias()).queryForBean(sql, server_id);
		SlaveServerListPo basePo=JSON.toJavaObject(object, SlaveServerListPo.class);
		return basePo;
	}

	/**
	 * 根据主键 更新
	 * */
	public  static int updateServerListByServerId(SlaveServerListPo po){
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(OrderSynchPo.class);
		String updateSql="update server_list set `name`=?,`ip`=?,`port`=?,`channel`=?   where `server_id`=?";
		int rs = DBWrapper.getInstance(poInfoVo.getProxoolAlias()).execute(updateSql,po.name, po.ip,po.port,po.channel,po.server_id);
		return rs;
	}
	/**
	 * 根据主键 更新
	 * */
	public  static int delServerListByServerId(SlaveServerListPo po){
		PoInfoVo poInfoVo=PoInfoVo.getPoInfo(OrderSynchPo.class);
		String updateSql="delete from server_list  where `server_id`=?";
		int rs = DBWrapper.getInstance(poInfoVo.getProxoolAlias()).execute(updateSql,po.server_id);
		return rs;
	}

	@Override
	public String toString() {
		return "SlaveServerListPo [server_id=" + server_id + ", name=" + name + ", ip=" + ip + ", port=" + port
				+ ", channel=" + channel + "]";
	}



}

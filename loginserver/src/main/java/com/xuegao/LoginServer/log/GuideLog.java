package com.xuegao.LoginServer.log;

import com.xuegao.LoginServer.data.Constants;

public class GuideLog extends Logs{

	private String typeId;
	
	private String typeName;
	
	private String ip;
	
	private String imei;
	
	private String idfa;
	
	private String deviceId;
	
	public GuideLog(String typeId,String typeName,String ip) {
		super(Constants.KindLog.GuideLog);
		this.typeId=typeId;
		this.typeName = typeName;
		this.ip = ip;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getIdfa() {
		return idfa;
	}

	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}

package com.xuegao.PayServer.slaveServer;

import java.util.Date;

/**管理 :slave_ip 和  具体的 slave 连接 对应关系
 * 
 * */
public class SlaveServerVo {
	public  ScConnector SC_CONNECTOR;
	public String slaveIp;	// slave的Ip地址
	public boolean isActive(){
		return SC_CONNECTOR.sc!=null&&SC_CONNECTOR.sc.isActive();
	}
	
	@Override
	public String toString() {
		return "SlaveServerVo [SC_CONNECTOR=" + SC_CONNECTOR + ", slaveIp=" + slaveIp + "]";
	}
	
	

	
}
package com.xuegao.PayServer.slaveServer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.xuegao.PayServer.handler.PayTradeProcessResultHandler;
import com.xuegao.PayServer.po.SlaveServerListPo;
import com.xuegao.PayServer.redisData.MemSlaveServerBean;
import com.xuegao.core.db.po.BasePo;
/**
 * 管理 客户端连接缓存
 * 
 * */
public class GlobalSlaveManage {
	public static Logger logger=Logger.getLogger(GlobalSlaveManage.class);
	//  serverId,slaveTcpConnect
//	private  static Map<Integer , SlaveServerVo> SlaveServerMap =  new ConcurrentHashMap<Integer , SlaveServerVo>();
	
	// <slaveIp , SlaveServerVo(包含scConnect和slaveIP)>
	private  static Map<String ,SlaveServerVo> ScMap=new  ConcurrentHashMap<String ,SlaveServerVo>();
	
	/**
	 * 服务器启动时候加载
	 * */
	public  static synchronized  void  reloadData(){
		// 初始化 salve 连接:
		List<SlaveServerListPo> list=BasePo.findAllEntitiesFromDB(SlaveServerListPo.class);
		if(null==list){
			return;
		}
		for(SlaveServerListPo po:list){
			String slave_ip = po.getIp();
			GlobalSlaveManage.connectSlave(slave_ip,po.getPort());
		}
	}
	
	/** slaveServer刷新最新数据 进入redis
	 * 
	 * */
	public static void  refreshServerListRedisData(){
		
		logger.info("--重新加载各slave-server---server_list-redis缓存数据---begin-");
		//加载各个服务器列表  刷新最新数据到 reids中
		List<SlaveServerListPo> list=BasePo.findAllEntitiesFromDB(SlaveServerListPo.class);
		if(null==list){
			return;
		}
		for(SlaveServerListPo po:list){
			MemSlaveServerBean poRedisBeanLoadKey= new MemSlaveServerBean(Integer.toString(po.getServer_id()));
			poRedisBeanLoadKey.save(po);
		}
		logger.info("--重新加载各slave-server---server_list-redis缓存数据---end-");
	}
	
	public static synchronized SlaveServerListPo fecthSlaveServerByServerId(Integer serverId){
		long start=System.currentTimeMillis();
		// 从redis 拉取最新数据
		MemSlaveServerBean slveServBeanRedis =  new MemSlaveServerBean(Integer.toString(serverId));
		SlaveServerListPo salveServerpo= slveServBeanRedis.get();
		if(null==salveServerpo){
			// 读取数据
			salveServerpo = SlaveServerListPo.findEntityByServerIdFromDB(Integer.toString(serverId));
			logger.info("db:load:"+(null==salveServerpo?"null":salveServerpo.toString()));
		}
		logger.info("--fecthSlaveServerByServerId---cost:-"+(System.currentTimeMillis()-start));
		return salveServerpo;
	}

	public static Map<String, SlaveServerVo> getScMap() {
		return ScMap;
	}

	public static SlaveServerVo connectSlave(String slave_ip,int slaveGamePort){
		String key = slave_ip +":"+ slaveGamePort;
		SlaveServerVo slaveV0 = GlobalSlaveManage.getScMap().get(key);// 具体slave连接信息
		if(null==slaveV0){// 如果 scMap 没有那么主动创建一个连接
			synchronized (key) {
				// 创建新连接slave
				int payCbPort = slaveGamePort / 100 == 91 ? slaveGamePort + 200 : 8888;
				ScConnector sc_connector = new ScConnector(slave_ip,payCbPort);// 固定的端口号
				sc_connector.connect();
				sc_connector.msgHandler=new PayTradeProcessResultHandler();
				slaveV0= new SlaveServerVo();
				logger.info("waiting create tcp "+payCbPort+"...");
				try {
					Thread.sleep(1500);// 等待1.5秒
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				slaveV0.slaveIp = slave_ip;
				slaveV0.SC_CONNECTOR=sc_connector;
				GlobalSlaveManage.getScMap().put(key, slaveV0);
			}
		}
		return  slaveV0;
	}
	
}

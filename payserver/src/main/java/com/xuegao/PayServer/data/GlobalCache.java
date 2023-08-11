package com.xuegao.PayServer.data;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.xuegao.PayServer.data.Constants.PlatformOption;
import com.xuegao.PayServer.po.PfOptionsPo;
import com.xuegao.PayServer.po.SlaveServerListPo;
import com.xuegao.PayServer.redisData.MemPfOptionBean;
import com.xuegao.PayServer.redisData.MemSlaveServerBean;
import com.xuegao.core.db.po.BasePo;

/**
 * 不再缓存 ,直接从redis 获取最新数据
 *
 */
public class GlobalCache {
	static Logger logger=Logger.getLogger(GlobalCache.class);

	/**
	 * 服务器启动时候调用
	 * */
	public static synchronized void reloadData(){

		logger.info("------reloadData 加载所有的配置文件 pf_options 和 slave_server-----");
		// 加载所有的配置文件 pf_options 和 slave_server
		List<PfOptionsPo> list=BasePo.findAllEntitiesFromDB(PfOptionsPo.class);
		if(null==list){
			return;
		}
		for(PfOptionsPo po:list){
			MemPfOptionBean poRedisBeanLoad= new MemPfOptionBean(po.getKey());
			poRedisBeanLoad.save(po);
		}
		//加载各个服务器列表  刷新最新数据到 reids中
		List<SlaveServerListPo> listSS=BasePo.findAllEntitiesFromDB(SlaveServerListPo.class);
		if(null==listSS){
			return;
		}
		for(SlaveServerListPo po:listSS){
			MemSlaveServerBean poRedisBeanLoadKey= new MemSlaveServerBean(Integer.toString(po.getServer_id()));
			poRedisBeanLoadKey.save(po);
		}
		logger.info("------reloadData 加载所有的配置文件 pf_options 和 slave_server---完成--");
	}

	/**
	 * 刷新最新数据 到redis中
	 * */
	public static synchronized void refreshPfOptionRedisData(){
		logger.info("---begin---刷新最新数据 到redis中----");
		//加载各渠道参数  刷新最新数据到 reids中
		List<PfOptionsPo> list=BasePo.findAllEntitiesFromDB(PfOptionsPo.class);
		if(null==list){
			return;
		}
		for(PfOptionsPo po:list){
			MemPfOptionBean poRedisBeanLoad= new MemPfOptionBean(po.getKey());
			poRedisBeanLoad.save(po);
		}

		logger.info("---end---刷新最新数据 到redis中----");
	}
	/**
	 * 获取平台 参数 根据 平台key
	 * 返回具体平台参数对象
	 * 例如  参数xgsdk
	 * 返回  XGSDK对象
	 * */
	public static  <T>T getPfConfig(String pfkeyName){
		Object config=null;
		try {
			MemPfOptionBean poRedisBean= new MemPfOptionBean(pfkeyName);
			PfOptionsPo po=poRedisBean.get();
			if(null==po){
				po = PfOptionsPo.findEntityByKeyFromDB(pfkeyName);
				logger.info("db:load:"+(null==po?"null":po.toString()));
			}
			String className=PlatformOption.class.getName()+"$"+pfkeyName;
			Class<?> clazz=Class.forName(className);
			if(po!=null&&clazz!=null){
				config=JSON.parseObject(po.getValue(), clazz);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return (T)config;
	}
}

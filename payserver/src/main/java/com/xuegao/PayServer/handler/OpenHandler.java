package com.xuegao.PayServer.handler;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.hutool.core.codec.Base64;
import com.xuegao.core.util.StringUtil;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.po.PfOptionsPo;
import com.xuegao.PayServer.po.SlaveServerListPo;
import com.xuegao.PayServer.redisData.MemPfOptionBean;
import com.xuegao.PayServer.redisData.MemSlaveServerBean;
import com.xuegao.PayServer.slaveServer.GlobalSlaveManage;
import com.xuegao.PayServer.slaveServer.ScConnector;
import com.xuegao.PayServer.slaveServer.SlaveServerVo;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.netty.Cmd;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpRequestJSONObject;

import io.netty.util.CharsetUtil;

public class OpenHandler {
	static Logger logger=Logger.getLogger(OpenHandler.class);

	/**由master 发来数据
	 * 同步数据 <server_list和pf_option> 到 网关本地数据库
	 * */
	@Cmd("/api/open/pay/sycnData")
	public  void notifySycnData(User sender, HttpRequestJSONObject params){
		String json=null;
		if(!params.isEmpty()){
			json=params.toJSONString();
		}else{
			json=params.getHttpBodyBuf().toString(CharsetUtil.UTF_8);
		}
		logger.info("sycnData:"+json);
		JSONObject  jsonObj= JSON.parseObject(json);
		String  tableName=  jsonObj.getString("table");
		String cmd= jsonObj.getString("operCmd");
		String id=jsonObj.getString("id");
		String record=jsonObj.getString("value");
		int  rsCnt=0;
		if("pf_options".equals(tableName)){
			PfOptionsPo pfnotifyData = JSONObject.toJavaObject(JSON.parseObject(record), PfOptionsPo.class);
			logger.info("pfnotifyData:"+pfnotifyData.toString());

            /**
             * 此处的value是master经过base64 加密的，为了解决json传输莫名其妙出现其他字符问题
             * 故对value进行base64解密
             * @author WuZhijian
             * @date 2019-08-06 10:56:43
             */
			pfnotifyData.setValue(Base64.decodeStr(pfnotifyData.getValue()));
			if("insert".equals(cmd)){
				rsCnt=pfnotifyData.insertToDB();
			}else if("update".equals(cmd)){
				rsCnt=pfnotifyData.updateToDB();
			}else if("delete".equals(cmd)){
				rsCnt=pfnotifyData.deleteFromDB();
				MemPfOptionBean pfRedis = new MemPfOptionBean(pfnotifyData.getKey());
				pfRedis.clearKey();

			}else{
				//返回
				JSONObject rs = MsgFactory.getDefaultErrorMsg("不支持操作");
				sender.sendAndDisconnect(rs);
				return;
			}
		}else if("server_list".equals(tableName)){
			SlaveServerListPo sslnotifyData = JSONObject.toJavaObject(JSON.parseObject(record), SlaveServerListPo.class);
			sslnotifyData.setIp(StringUtil.isEmpty(sslnotifyData.getNip())?sslnotifyData.getIp():sslnotifyData.getNip());
			logger.info("sslnotifyData:"+sslnotifyData.toString());
			boolean isRCheckConnect=false;// 是否需要检查 slave连接
			if("insert".equals(cmd)){
				rsCnt=sslnotifyData.insertToDB();
				if(rsCnt==1){
					isRCheckConnect=true;
				}
			}else if("update".equals(cmd)){
				rsCnt=sslnotifyData.updateServerListByServerId(sslnotifyData);
				if(rsCnt==1){
					isRCheckConnect=true;
				}
			}else if("delete".equals(cmd)){
				rsCnt=sslnotifyData.delServerListByServerId(sslnotifyData);
				MemSlaveServerBean serverListRedis =  new MemSlaveServerBean(Integer.toString(sslnotifyData.getServer_id()));
				serverListRedis.clearKey();
			}else{
				//返回
				JSONObject rs = MsgFactory.getDefaultErrorMsg("不支持操作");
				sender.sendAndDisconnect(rs);
				return;
			}
			if(isRCheckConnect){
				// 检查是否新的 slave_ip
				String slave_ip = StringUtil.isEmpty(sslnotifyData.getNip())?sslnotifyData.getIp():sslnotifyData.getNip();
				GlobalSlaveManage.connectSlave(slave_ip,sslnotifyData.getPort());
			}
		}else{
			//返回
			JSONObject rs = MsgFactory.getDefaultErrorMsg("表名不存在");
			sender.sendAndDisconnect(rs);
			return;
		}
		if(rsCnt>0){
			//返回
			JSONObject rs = MsgFactory.getDefaultSuccessMsg();
			rs.put("msg", "success");
			sender.sendAndDisconnect(rs);
			return;
		}else{
			//返回
			JSONObject rs = MsgFactory.getDefaultErrorMsg("执行结果个数为0条");
			sender.sendAndDisconnect(rs);
			return;
		}

	}


	/**通知回调  数据进行同步(刷新redis数据)
	 * 只只需要请求一次即可(因为在业务 中都是从reids取得 ,若不存在 从数据库中取得,加入redis中)
	 * serviceName 业务名称
	 * get  方式
	 * */
	@Cmd("/api/open/pay/reloadData")
	public void reloadData(User sender, JSONObject params){
		String  serviceName = params.getString("serviceName");
		logger.info("serviceName:"+serviceName);
		if("server_list".equals(serviceName)){
			GlobalSlaveManage.refreshServerListRedisData();// 刷新redis中 slaveServerList
		}else if("pf_options".equals(serviceName)){
			GlobalCache.refreshPfOptionRedisData();// 刷新redis中 pf_option数据
		}else{
			//返回
			JSONObject rs = MsgFactory.getDefaultErrorMsg("不支持操作");
			sender.sendAndDisconnect(rs);
			return;
		}
		//返回
		JSONObject rs = MsgFactory.getDefaultSuccessMsg();
		rs.put("msg", "success");
		sender.sendAndDisconnect(rs);
	}

	/**清除redis 缓存
	 * 刷新 server_list 和 pf_option 数据
	 * */
	@Cmd("/api/open/pay/refreshAllRedisData")
	public void refreshAllRedisData(User sender, JSONObject params){
		// 首先清除 所有缓存
		MemPfOptionBean pfRedis = new MemPfOptionBean("*");
		if(null!=pfRedis){
			Set<String> sets =  pfRedis.patten();
			if(null!=sets){
				Iterator<String> iter =sets.iterator();
				while(iter.hasNext()){
					String key = iter.next();
					pfRedis.getJedisUtil().KEYS.del(key);
				}
			}
		}
		MemSlaveServerBean serverListRedis =  new MemSlaveServerBean("*");
		if(null!=serverListRedis){
			Set<String> sets =  serverListRedis.patten();
			if(null!=sets){
				Iterator<String> iter =sets.iterator();
				while(iter.hasNext()){
					String key = iter.next();
					serverListRedis.getJedisUtil().KEYS.del(key);
				}
			}
		}

		logger.info("------refreshAllRedisData 加载所有的配置文件 pf_options 和 slave_server-----");
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
		logger.info("------refreshAllRedisData 加载所有的配置文件 pf_options 和 slave_server---完成--");

		//返回
		JSONObject rs = MsgFactory.getDefaultSuccessMsg();
		rs.put("msg", "success");
		sender.sendAndDisconnect(rs);
		return;
	}
}

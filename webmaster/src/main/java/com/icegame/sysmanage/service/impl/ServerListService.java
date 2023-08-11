package com.icegame.sysmanage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.util.StringUtil;
import com.icegame.framework.utils.*;
import com.icegame.sysmanage.entity.*;
import com.icegame.sysmanage.service.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.jsqlparser.schema.Server;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.mapper.ServerListMapper;

@Service
public class ServerListService implements IServerListService {

	private static Logger logger = Logger.getLogger(ServerListService.class);

	@Autowired
	private ServerListMapper serverListMapper;

	@Autowired
	private IServerNodesService serverNodesService;

	@Autowired
	private ISyncStatusService syncStatusService;

	@Autowired
	private IOpclServerStatusService opclServerStatusService;

	@Autowired
	private ISlaveNodesService slaveNodesService;


	@Autowired
	private LogService logService;

	Log log = new Log();

	/**
	 * 返回json
	 */
	public List<ServerList> download(ServerList server) {
		return serverListMapper.download(server);
	}

	/**
	 * 返回json
	 */
	public List<ServerList> getAllChannelServerList() {
		return serverListMapper.getAllChannelServerList();
	}

	public List<ServerList> checkExistServerList(ServerList serverList) {
		return serverListMapper.checkExistServerList(serverList);
	}



	/**
	 * 获取服务器列表 web 界面
	 * @param server
	 * @return
	 *
	 * ----------------------------------------
	 * @date 2019-06-15 10:26:40
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	public PageInfo<ServerList> getServerList(ServerList server,PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<ServerList> serverList = serverListMapper.getServerList(server);
		if(serverList.size() > 0){
			for(ServerList serverList1 : serverList){
				serverList1.setBeginTime(TimeUtils.stampToDateWithMill(serverList1.getBeginTime()));
				serverList1.setUpdateTime(TimeUtils.stampToDateWithMill(serverList1.getUpdateTime()));
				serverList1.setPassTime(TimeUtils.stampToDateWithMill(serverList1.getPassTime()));
			}
		}
		PageInfo<ServerList> pageInfo = new PageInfo<ServerList>(serverList);
		return pageInfo;
	}

	public List<ServerList> getServerIdsList() {
		return serverListMapper.getServerIdsList();
	}

	/**
	 *
	 * @param server
	 * @param pageParam
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 10:34:46
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	public PageInfo<ServerList> getServerListOpen(ServerList server,PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<ServerList> serverList = serverListMapper.getServerListOpen(server);
		if(serverList.size() > 0){
			for(ServerList serverList1 : serverList){
				serverList1.setBeginTime(TimeUtils.stampToDateWithMill(serverList1.getBeginTime()));
				serverList1.setUpdateTime(TimeUtils.stampToDateWithMill(serverList1.getUpdateTime()));
				serverList1.setPassTime(TimeUtils.stampToDateWithMill(serverList1.getPassTime()));
			}
		}
		PageInfo<ServerList> pageInfo = new PageInfo<ServerList>(serverList);
		return pageInfo;
	}

	public List<ServerList> getAllChannelList() {
		return serverListMapper.getAllChannelList();
	}

	public List<ServerList> getServerByIP(String ip){
		ServerList serverList = new ServerList();
		serverList.setIp(ip);
		return serverListMapper.getServerByIP(serverList);
	}

	public List<ServerList> getServerByChannel(String channel){
		ServerList serverList = new ServerList();
		serverList.setChannel(channel);
		return serverListMapper.getServerByChannel(serverList);
	}

	public ServerList getServerById(Long id) {
		return serverListMapper.getServerById(id);
	}

	public List<ServerList> getServerBySlaveId(Long slaveId){
		List<ServerList> serverList = serverListMapper.getServerBySlaveId(slaveId);
		return serverList;
	}

	@Override
	public List<ServerList> getServerByChannelAndCreateTime(ServerList server){
		List<ServerList> serverList = serverListMapper.getServerByChannelAndCreateTime(server);
		return serverList;
	}


	public boolean addServer(ServerList serverList) {
		log = UserUtils.recording("添加区服["+serverList.getName()+"]",Type.ADD.getName());
		logService.addLog(log);
		return serverListMapper.addServer(serverList);
	}

	public Long addServerReturnId(ServerList serverList) {
		log = UserUtils.recording("添加区服["+serverList.getName()+"]",Type.ADD.getName());
		logService.addLog(log);
		Long id = serverListMapper.addServerReturnId(serverList);
		return id;
	}

	public boolean delServer(Long id) {
		log = UserUtils.recording("删除区服["+id+"]",Type.DELETE.getName());
		logService.addLog(log);
		return serverListMapper.delServer(id);
	}

	public boolean updateServer(ServerList serverList) {
		log = UserUtils.recording("修改区服["+serverList.getName()+"]",Type.UPDATE.getName());
		logService.addLog(log);
		return serverListMapper.updateServer(serverList);
	}


	public boolean updateServerClose(ServerList serverList) {
		String operate = "";
		switch(serverList.getClose()){
			case 0:operate="开启";break;
			case 1:operate="关闭";break;
		}

		log = UserUtils.recording(""+operate+"["+serverList.getServerId()+"]",Type.UPDATE.getName());
		logService.addLog(log);
		return serverListMapper.updateServerClose(serverList);
	}

	@Override
	public boolean updateServerCloseBySlaveId(ServerList serverList) {
		String operate = "";
		switch(serverList.getClose()){
			case 0:operate="开启";break;
			case 1:operate="关闭";break;
		}

		log = UserUtils.recording(""+operate+"["+serverList.getServerId()+"]",Type.UPDATE.getName());
		logService.addLog(log);
		return serverListMapper.updateServerCloseBySlaveId(serverList);
	}

	public void saveSync(ServerList server) {

	}

	public void deleteSync(ServerList serverList) {

	}

	@Override
	public boolean updateSlaveIp(ServerList serverList) {
		return serverListMapper.updateSlaveIp(serverList);
	}

	@Override
	public boolean existsChannel(String channel) {
		List<ServerList> serverList = serverListMapper.existsChannel(channel);
		return serverList.size() > 0 ? true : false ;
	}

	@Override
	public boolean existsMainTainServer() {
		List<ServerList> serverList = serverListMapper.existsMainTainServer();
		return serverList.size() > 0 ? true : false ;
	}

	@Override
	public boolean syncSuccess(ServerList serverList) {
		return serverListMapper.syncSuccess(serverList);
	}

	@Override
	public boolean syncFailed(ServerList serverList) {
		return serverListMapper.syncFailed(serverList);
	}

	@Override
	public boolean suggest(ServerList serverList) {
		String isSuggest = serverList.getIsSuggest() == 1 ? "设置推荐" : "取消推荐";
		log = UserUtils.recording("操作推荐服[serverId:"+serverList.getServerId()+"，是否推荐：" + isSuggest + "]",Type.UPDATE.getName());
		logService.addLog(log);
		return serverListMapper.suggest(serverList);
	}

	@Override
	public Map<String,Object> syncServer(ServerList server) {

		/**
		 * 去掉字符串
		 */
		server.setName(server.getName().trim());
		server.setChannel(server.getChannel().trim());
		server.setGroup(server.getGroup().trim());
		if(!server.getPort().equals("")){
			server.setPort(server.getPort().trim());
		}

		/**
		 * 同步失败或者成功的提示改写
		 */
		boolean localAdd = false;

		boolean slaveAdd = false;

		boolean payAdd = false;

		StringBuffer sb = new StringBuffer();

		Map<String,Object> resultMap = new HashMap<String,Object>();

		// 查询已经打开的区服
		List<ServerList> openedList = this.getServerBySlaveId(server.getSlaveId());

		// 查询设定的区服数量
		SlaveNodes limitSize = slaveNodesService.getSlaveNodesById(server.getSlaveId());

		// 只对新增操作进行判断
		if(server.getServerId() == null){
			if(openedList.size() >= limitSize.getLimit() && limitSize.getLimit() != 0){
				resultMap.put("result","添加失败，当前服务器最多可添加" + limitSize.getLimit() + "个区服");
				return resultMap;
			}
		}


		String operate = "新增";
		String operCmd = "";
		String table = "server_list";
		server.setBeginTime(TimeUtils.dateToStampWithDetail(server.getBeginTime()));
		server.setUpdateTime(String.valueOf(System.currentTimeMillis()));
		server.setPassTime(TimeUtils.dateToStampWithDetail(server.getPassTime()));
		Long id = null;
		if(server.getPort().equals("")){
			server.setPort("8081");
		}
		try{
			/**----------------------------------------------------------------------------------------------------------------**/
			/**----------------------------------------------  先进行修改  在进行数据同步    ----------------------------------------------**/
			/**----------------------------------------------------------------------------------------------------------------**/
			if(server != null && server.getServerId() != null){
				operate = "修改";
				operCmd = "update";
				id = server.getServerId();
				this.updateServer(server);
				resultMap.put("result", "修改记录信息成功");
				logger.info("修改区服["+server.getName()+"]成功");
				ServerList getClose = this.getServerById(id);
				server.setClose(getClose.getClose());
			}else{

				// 添加的时候默认不是推荐服
				server.setIsSuggest(0);

				operCmd = "insert";
				id = this.addServerReturnId(server);
				resultMap.put("result", "增加记录信息成功");
				logger.info("增加区服["+server.getName()+"]成功");
				//默认开服
				server.setClose(0);
			}

			localAdd = true;
			sb.append("{Master:操作成功}").append(",");

			/**----------------------------------------------------------------------------------------------------------------**/
			/**----------------------------------------------保存之后           同步     开关服数据----------------------------------------------**/
			/**----------------------------------------------------------------------------------------------------------------**/
			Map<String,Object> opClServer = new HashMap<String,Object>();
			opClServer.put("id", server.getServerId());
			opClServer.put("group", server.getGroup());
			opClServer.put("wudaogroup", server.getWudaogroup());
			opClServer.put("name", server.getName());
			opClServer.put("beginTime", server.getBeginTime());
			opClServer.put("passTime", server.getPassTime());
			opClServer.put("close", server.getClose());
			JSONObject opClObj = JSONObject.fromObject(opClServer);
			JSONArray opClArr = new JSONArray();opClArr.add(opClObj);
			SlaveNodes slaveNodes = slaveNodesService.getSlaveNodesById(server.getSlaveId());
			//SlaveNodes slaveNodes = slaveNodesServicehost.getSlaveNodesByIP(server.getIp().trim());
			String hosts = "http://"+ ( StringUtils.isNull(slaveNodes.getNip())?slaveNodes.getIp():slaveNodes.getNip() )+":" + slaveNodes.getPort() + "/sync/addserver";
			logger.info("--- | host | ---:" + hosts );
			logger.info("向[" + hosts + "][添加服务器 ****发送****的数据--------------------->]"+opClArr.toString());
			String opClResult = HttpUtils.jsonPost(hosts,opClArr.toString());
			logger.info("["+hosts+"][添加服务器 ****接受****的数据--------------------->]"+opClResult.toString());
			OpclServerStatus opclServerStatus = new OpclServerStatus();
			JSONObject retObj = JSONObject.fromObject(opClResult);
			JSONObject reRetObj = new JSONObject();
			if(!retObj.get("ret").equals("0")){
				for( int i = 0 ; i < 5 ; i++ ){
					String reOpClResult = HttpUtils.jsonPost(hosts,opClArr.toString());
					reRetObj = JSONObject.fromObject(reOpClResult);
					if(reRetObj.get("ret").equals("0")){
						opclServerStatus = new OpclServerStatus(TimeUtils.getDateDetail(),hosts,"成功","开服",server.getServerId()+"."+server.getName(),opClArr.toString(),"");
						slaveAdd = true;
						sb.append("{Slave:同步成功}").append(",");
						break;
					}
					opclServerStatus = new OpclServerStatus(TimeUtils.getDateDetail(),hosts,"失败","开服",server.getServerId()+"."+server.getName(),opClArr.toString(),"0");
				}

				if(!slaveAdd){
					sb.append("{Slave:同步失败}").append(",");
				}

			}else{
				slaveAdd = true;
				sb.append("{Slave:同步成功}").append(",");
				opclServerStatus = new OpclServerStatus(TimeUtils.getDateDetail(),hosts,"成功","开服",server.getServerId()+"."+server.getName(),opClArr.toString(),"");
			}
			opclServerStatusService.addOpclServerStatus(opclServerStatus);


			/**----------------------------------------------------------------------------------------------------------------**/
			/**----------------------------------------------保存之后           向支付服同步数据 ----------------------------------------------**/
			/**----------------------------------------------------------------------------------------------------------------**/
			// 原本向第一个接口发送的数据
			JSONObject jsonObj = JSONObject.fromObject(server);
			jsonObj.remove("group");jsonObj.remove("ver");jsonObj.remove("close");
			jsonObj.put("server_id", jsonObj.get("serverId"));
			jsonObj.remove("serverId");
			String slaveNip = StringUtil.isEmpty(slaveNodes.getNip()) ? slaveNodes.getIp() : slaveNodes.getNip();
			jsonObj.put("nip", slaveNip);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("operCmd", operCmd);
			map.put("table", table);
			map.put("id", server.getServerId());
			map.put("value", jsonObj.toString());

			JSONObject syncDate = JSONObject.fromObject(map);
			//1.获取所有的节点列表
			List<ServerNodes> serverNodesList = serverNodesService.getServerNodesSync();

			/**
			 * 分别区分出登录服和支付服
			 */
			List<ServerNodes> loginNodesList = new ArrayList<ServerNodes>();
			List<ServerNodes> payNodesList = new ArrayList<ServerNodes>();
			for(int i = 0 ; i < serverNodesList.size() ; i++ ){
				if(serverNodesList.get(i).getDiff() == 1){
					loginNodesList.add(serverNodesList.get(i));
				}else if(serverNodesList.get(i).getDiff() == 2){
					payNodesList.add(serverNodesList.get(i));
				}
			}

			for(int i = 0 ; i < payNodesList.size() ; i++){
				ServerNodes sn = payNodesList.get(i);
				String syncHost = sn.getProtocol()+sn.getIp()+(StringUtils.isNull(sn.getPort())?"":":"+sn.getPort())+sn.getInterfaceName();
				logger.info("向第" + i + "个支付服[" + syncHost + "]同步数据,内容：" + syncDate.toString());
				String result = HttpUtils.jsonPost(syncHost,syncDate.toString());
				JSONObject jsonRet = JSONObject.fromObject(result);
				logger.info("["+ syncHost + "]接口返回：" + jsonRet.toString());
				SyncStatus syncStatus = new SyncStatus(TimeUtils.getDateDetail(),sn.getName(),sn.getIp(),"[添加/修改]["+sn.getName()+"]服务器列表数据----->[同步]",jsonRet.toString());
				syncStatusService.addSyncStatus(syncStatus);
				if(jsonRet.get("ret").equals(0) && jsonRet.get("msg").equals("success")){

					/**
					 * 修改此处，确定支付服同步成功之后才回调写入master成功，否则失败
					 */
					this.syncSuccess(new ServerList(1, server.getServerId()));
					payAdd = true;
					sb.append("{支付服:同步成功}");

					//说明支付服第一个节点同步成功，所以不再对其他节点进行同步，现在是需要对所有节点请求重载数据的接口
					logger.info("[=============================="+  server.getName()+ "配置数据向支付服"+ sn.getIp() +"同步成功======================================]");
					for(int j = 0 ; j < payNodesList.size() ; j++){
						ServerNodes snj = payNodesList.get(j);
						String reloadHost = snj.getProtocol()+snj.getIp()+(StringUtils.isNull(snj.getPort())?"":":"+snj.getPort())+snj.getReInterfaceName()+"server_list";
						logger.info("请求第" + j + "个支付服[" + reloadHost + "]重载数据");
						String resultj = HttpUtils.get(reloadHost);
						logger.info("["+reloadHost + "]接口返回：" + JSONObject.fromObject(resultj).toString());
						syncStatus = new SyncStatus(TimeUtils.getDateDetail(),snj.getName(),snj.getIp(),"[添加/修改]["+snj.getName()+"]服务器列表数据----->[重载]",JSONObject.fromObject(resultj).toString());
						syncStatusService.addSyncStatus(syncStatus);
					}
					break;
				}else{
					sb.append("{支付服:同步失败}");
				}
			}
		}catch(Exception e){
			resultMap.put("result","操作失败");
			logger.error("操作失败",e);
		}

		// 如果三个都成功，才返回成功
		if(localAdd && slaveAdd && payAdd){
			resultMap.put("result","操作成功");
		}else{
			resultMap.put("result","操作失败");
			resultMap.put("details",sb.toString());
		}

		return resultMap;

	}

	@Override
	public Map<String,Object> syncDelete(Long serverId) {

		boolean localDel = false;

		boolean slaveDel = false;

		boolean payDel = false;

		StringBuffer sb = new StringBuffer();

		String operCmd = "delete";
		String table = "server_list";
		String isSuccess = "";
		ServerList serverList = this.getServerById(serverId);
		ServerList server = new ServerList();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(this.delServer(serverId)){
				localDel = true;
				sb.append("{Master:删除成功}").append(",");

				logger.info("删除服务器"+serverId);
				Map<String,Object> syncData = new HashMap<String,Object>();
				syncData.put("id", serverList.getServerId());
				syncData.put("group", serverList.getGroup());
				syncData.put("wudaogroup", server.getWudaogroup());
				syncData.put("name", serverList.getName());
				syncData.put("beginTime", serverList.getBeginTime());
				syncData.put("passTime", serverList.getPassTime());
				syncData.put("close", serverList.getClose());
				JSONObject opClArr = JSONObject.fromObject(syncData);
				SlaveNodes slaveNodes = slaveNodesService.getSlaveNodesById(serverList.getSlaveId());
				String hosts = "http://"+( StringUtils.isNull(slaveNodes.getNip())?slaveNodes.getIp():slaveNodes.getNip() )+":" + slaveNodes.getPort() + "/sync/deleteserver";
				logger.info("向[" + hosts + "][删除服务器 ****发送****的数据--------------------->]"+opClArr.toString());
				String opClResult = HttpUtils.jsonPost(hosts,opClArr.toString());
				logger.info("[" + hosts + "][删除服务器 ****接受****的数据--------------------->]"+opClResult.toString());
				OpclServerStatus opclServerStatus = new OpclServerStatus();
				JSONObject retObj = JSONObject.fromObject(opClResult);
				JSONObject reRetObj = new JSONObject();
				if(!retObj.get("ret").equals("0")){
					for( int i = 0 ; i < 5 ; i++ ){
						String reOpClResult = HttpUtils.jsonPost(hosts,opClArr.toString());
						reRetObj = JSONObject.fromObject(reOpClResult);
						if(reRetObj.get("ret").equals("0")){
							slaveDel = true;
							sb.append("{Slave:同步成功}").append(",");
							opclServerStatus = new OpclServerStatus(TimeUtils.getDateDetail(),hosts,"成功","删除",serverList.getServerId()+"."+serverList.getName(),opClArr.toString(),"");
							break;
						}
						opclServerStatus = new OpclServerStatus(TimeUtils.getDateDetail(),hosts,"失败","删除",serverList.getServerId()+"."+serverList.getName(),opClArr.toString(),"2");
					}

					if(!slaveDel){
						sb.append("{Slave:同步失败}").append(",");
					}

				}else{
					slaveDel = true;
					sb.append("{Slave:同步成功}").append(",");
					opclServerStatus = new OpclServerStatus(TimeUtils.getDateDetail(),hosts,"成功","删除",serverList.getServerId()+"."+serverList.getName(),opClArr.toString(),"");
				}
				opclServerStatusService.addOpclServerStatus(opclServerStatus);




				/**----------------------------------------------------------------------------------------------------------------**/
				/**----------------------------------------------删除之后           向支付服同步数据----------------------------------------------**/
				/**----------------------------------------------------------------------------------------------------------------**/
				JSONObject jsonObj = JSONObject.fromObject(serverList);
				jsonObj.remove("group");jsonObj.remove("ver");jsonObj.remove("close");
				jsonObj.put("server_id", jsonObj.get("serverId"));
				jsonObj.remove("serverId");
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("operCmd", operCmd);
				map.put("table", table);
				map.put("id", serverId);
				map.put("value", jsonObj.toString());
				JSONObject syncDate = JSONObject.fromObject(map);
				JSONArray jsonArr = new JSONArray();jsonArr.add(syncDate);
				//1.获取所有的节点列表
				List<ServerNodes> serverNodesList = serverNodesService.getServerNodesSync();

				/**
				 * 分别区分出登录服和支付服
				 */
				List<ServerNodes> loginNodesList = new ArrayList<ServerNodes>();
				List<ServerNodes> payNodesList = new ArrayList<ServerNodes>();
				for(int i = 0 ; i < serverNodesList.size() ; i++ ){
					if(serverNodesList.get(i).getDiff() == 1){
						loginNodesList.add(serverNodesList.get(i));
					}else if(serverNodesList.get(i).getDiff() == 2){
						payNodesList.add(serverNodesList.get(i));
					}
				}

				/**
				 * 支付服同步
				 */
				for( int i = 0 ; i < payNodesList.size() ; i++){
					ServerNodes sn = payNodesList.get(i);
					String syncHost = sn.getProtocol()+sn.getIp()+(StringUtils.isNull(sn.getPort())?"":":"+sn.getPort())+sn.getInterfaceName();
					logger.info("向第" + i + "个支付服[" + syncHost + "]同步数据,内容：" + syncDate.toString());
					String result = HttpUtils.jsonPost(syncHost,syncDate.toString());
					JSONObject jsonRet = JSONObject.fromObject(result);
					SyncStatus syncStatus = new SyncStatus(TimeUtils.getDateDetail(),sn.getName(),sn.getIp(),"[删除]["+sn.getName()+"]服务器列表数据----->[同步]",jsonRet.toString());
					syncStatusService.addSyncStatus(syncStatus);
					if(jsonRet.get("ret").equals(0) && jsonRet.get("msg").equals("success")){
						//说明支付服第删除操作同步成功，所以不再对其他节点进行同步，现在是需要对所有节点请求重载数据的接口
						logger.info("[=============================="+  server.getName()+ "配置数据向支付服"+ sn.getIp() +"同步成功==============================]");

						payDel = true;
						sb.append("{支付服:同步成功}");

						for(int j = 0 ; j < payNodesList.size() ; j++){
							ServerNodes snj = payNodesList.get(j);
							String reloadHost = snj.getProtocol()+snj.getIp()+(StringUtils.isNull(snj.getPort())?"":":"+snj.getPort())+snj.getReInterfaceName()+"server_list";
							logger.info("请求第" + j + "个支付服[" + reloadHost + "]重载数据");
							String resultj = HttpUtils.get(reloadHost);
							logger.info("["+reloadHost + "]接口返回：" + JSONObject.fromObject(resultj).toString());
							syncStatus = new SyncStatus(TimeUtils.getDateDetail(),snj.getName(),snj.getIp(),"[删除]["+snj.getName()+"]服务器列表数据----->[重载]",JSONObject.fromObject(resultj).toString());
							syncStatusService.addSyncStatus(syncStatus);
						}
						break;
					}else{
						sb.append("{支付服:同步失败}");
					}
				}
			}
		}catch(Exception e){
			logger.error("删除记录失败",e);
		}

		// 如果三个都成功，才返回成功
		if(localDel && slaveDel && payDel){
			resultMap.put("result","删除成功");
		}else{
			resultMap.put("result","删除失败");
			resultMap.put("details",sb.toString());
		}

		return resultMap;
	}

	@Override
	public List<ServerList> getServerListByMultChannel(String multChannel, String format) {
		multChannel = StringUtils.multFormat(multChannel, format);
		return serverListMapper.getServerListByMultChannel(multChannel);
	}

	@Override
	public List<ServerList> getServerListByMultSlaveId(String multSlaveId) {
		multSlaveId = StringUtils.multFormatComma(multSlaveId);
		return serverListMapper.getServerListByMultSlaveId(multSlaveId);
	}

	@Override
	public List<ServerList> getAllChannelServerListNoPage(ServerList serverList) {
		return serverListMapper.getAllChannelServerListNoPage(serverList);
	}

    @Override
    public List<ServerList> getSuggestServerInChannel(String channel) {
        return serverListMapper.getSuggestServerInChannel(channel);
    }

}

package com.icegame.sysmanage.service.impl;

import cn.hutool.core.lang.Console;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.entity.Ret;
import com.icegame.framework.utils.*;
import com.icegame.sysmanage.entity.*;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.AllSrvMail;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.mapper.AllSrvMailMapper;
import com.icegame.sysmanage.service.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AllSrvMailService implements IAllSrvMailService {

	private static Logger logger = Logger.getLogger(AllSrvMailService.class);

	@Autowired
	private AllSrvMailMapper allSrvMailMapper;

	@Autowired
	private ISlaveNodesService slaveNodesService;

	@Autowired
	private ISyncMailService syncMailService;

	@Autowired
	private LogService logService;

	@Autowired
	private IServerListService serverListService;

	@Autowired
	private ISelSrvMailService selSrvMailService;

	Log log = new Log();

	/**
	 *
	 * @param allSrvMail
	 * @param pageParam
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 11:12:20
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	public PageInfo<AllSrvMail> getAllSrvMailList(AllSrvMail allSrvMail, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<AllSrvMail> allSrvMailList = allSrvMailMapper.getAllSrvMailList(allSrvMail);
		if(allSrvMailList.size() > 0){
			for(AllSrvMail asm : allSrvMailList){
				asm.setCreateTime(TimeUtils.stampToDateWithMill(asm.getCreateTime()));
			}
		}
		PageInfo<AllSrvMail> pageInfo = new PageInfo<AllSrvMail>(allSrvMailList);
		return pageInfo;
	}

	public AllSrvMail getAllSrvMailById(Long id) {
		return allSrvMailMapper.getAllSrvMailById(id);
	}

	public boolean addAllSrvMail(AllSrvMail allSrvMail) {
		log = UserUtils.recording("添加全服邮件",Type.ADD.getName());
		logService.addLog(log);
		return allSrvMailMapper.addAllSrvMail(allSrvMail);
	}

	public boolean delAllSrvMail(Long id) {
		log = UserUtils.recording("删除全服邮件",Type.DELETE.getName());
		logService.addLog(log);
		return allSrvMailMapper.delAllSrvMail(id);
	}

	public boolean updateAllSrvMail(AllSrvMail allSrvMail) {
		log = UserUtils.recording("修改全服邮件",Type.UPDATE.getName());
		logService.addLog(log);
		return allSrvMailMapper.updateAllSrvMail(allSrvMail);
	}

	public boolean refreshStatus(AllSrvMail allSrvMail){
		return allSrvMailMapper.refreshStatus(allSrvMail);
	}

	@Override
	public String syncAllSrvMail(Long id) {
		String retFlag = "";

		JSONObject syncJson = new JSONObject();
		AllSrvMail allSrvMail = getAllSrvMailById(id);
		Map<String, Object> syncData = new HashMap<String, Object>();
		syncData.put("id", allSrvMail.getId());
		syncData.put("subject", allSrvMail.getSubject());
		syncData.put("createTime", allSrvMail.getCreateTime());
		syncData.put("context", allSrvMail.getContext());
		syncData.put("awardStr", StringUtils.awardStrformart(allSrvMail.getAwardStr()));
		syncJson = JSONObject.fromObject(syncData);
		// 向所有的slave节点同步 获取所有slave的节点列表
		List<SlaveNodes> slaveNodesList = slaveNodesService.getSlaveNodesListNoPage();
		for (int i = 0; i < slaveNodesList.size(); i++) {
			String serverId = slaveNodesList.get(i).getId() + "." + slaveNodesList.get(i).getName();
			String hosts = "http://" + ( StringUtils.isNull(slaveNodesList.get(i).getNip())?slaveNodesList.get(i).getIp():slaveNodesList.get(i).getNip() ) + ":" + slaveNodesList.get(i).getPort() + "/sync/addallsrvmail";
			logger.info("[同步全服邮件****发送****的数据--------------------->]" + syncJson.toString());
			String result = HttpUtils.jsonPost(hosts, syncJson.toString());
			logger.info("[同步全服邮件****接收****的数据--------------------->]" + result.toString());
			SyncMail syncMail = new SyncMail();
			JSONObject retObj = JSONObject.fromObject(result);
			JSONObject reRetObj = new JSONObject();
			if (!retObj.get("ret").equals("0")) {
				for (int j = 0; j < 5; j++) {
					String relResult = HttpUtils.jsonPost(hosts, syncJson.toString());
					reRetObj = JSONObject.fromObject(relResult);
					if (reRetObj.get("ret").equals("0")) {
						retFlag = "发送成功";
						syncMail = new SyncMail(TimeUtils.getDateDetail(), hosts, "成功", "全服邮件", serverId,
								syncJson.toString(), "");
						break;
					}
					retFlag = "发送失败";
					syncMail = new SyncMail(TimeUtils.getDateDetail(), hosts, "失败", "全服邮件", serverId,
							syncJson.toString(), "0");
				}
			} else {
				retFlag = "发送成功";
				syncMail = new SyncMail(TimeUtils.getDateDetail(), hosts, "成功", "全服邮件", serverId, syncJson.toString(),
						"");
			}
			syncMailService.addSyncMail(syncMail);
		}

		//判断是否发送成功,根据结果修改数据库的状态
		if(retFlag.equals("发送成功")){
			refreshStatus(new AllSrvMail(allSrvMail.getId(), MailStatus.SUCCESS.getStatus()));
		}else if(retFlag.equals("发送失败")){
			refreshStatus(new AllSrvMail(allSrvMail.getId(),MailStatus.FAIL.getStatus()));
		}

		Ret ret = new Ret();
		ret.setRet(0);
		ret.setMsg(retFlag);
		return JSONObject.fromObject(ret).toString();
	}

	@Override
	public String syncAllSrvMailByChannel(AllSrvMail srvMail, String channel, String format) {
		List<ServerList> serverList = serverListService.getServerListByMultChannel(channel, format);
		String sid = "";
		String nextLine = "\r\n";
		for(ServerList server : serverList){
			sid += server.getServerId() + nextLine;
		}

		String retFlag = "";

		Map<String,Object> syncData = new HashMap<String,Object>();

		syncData.put("id",srvMail.getId());
		syncData.put("sid",sid);
		syncData.put("createTime",srvMail.getCreateTime());
		syncData.put("subject",srvMail.getSubject());
		syncData.put("context",srvMail.getContext());
		syncData.put("awardStr",StringUtils.awardStrformart(srvMail.getAwardStr()));
		JSONObject jsonObj = JSONObject.fromObject(syncData);
		Ret ret = new Ret();
		try{
			long[] targetIds = new long[serverList.size()];
			for( int i = 0 ; i < serverList.size() ; i++ ) {
				targetIds[i] = serverList.get(i).getServerId();
			}
		}catch(Exception e){
			logger.info("Send all_srv_mail failed {} --- 服务器ID 格式错误");
			ret.setRet(-1);
			ret.setMsg("服务器ID格式有误,请重新输入");
			return JSONObject.fromObject(ret).toString();
		}

		//向所有的slave节点同步  获取所有slave的节点列表
		List<SlaveNodes> slaveNodesList = slaveNodesService.getSlaveNodesListNoPage();
		for(int i = 0 ; i < slaveNodesList.size() ; i++ ) {
			String serverId = slaveNodesList.get(i).getId()+"."+slaveNodesList.get(i).getName();
			String hosts = "http://"+( StringUtils.isNull(slaveNodesList.get(i).getNip())?slaveNodesList.get(i).getIp():slaveNodesList.get(i).getNip() )+":" + slaveNodesList.get(i).getPort() + "/sync/addsrvmail";
			logger.info("[同步全服服邮件****发送****的数据--------------------->]"+jsonObj.toString());
			String result = HttpUtils.jsonPost(hosts, jsonObj.toString());
			logger.info("[同步全服邮件****接收****的数据--------------------->]"+result.toString());
			SyncMail syncMail = new SyncMail();
			JSONObject retObj = JSONObject.fromObject(result);
			JSONObject reRetObj = new JSONObject();
			if(!retObj.get("ret").equals("0")){
				for( int j = 0 ; j < 5 ; j++ ){
					String relResult = HttpUtils.jsonPost(hosts,jsonObj.toString());
					reRetObj = JSONObject.fromObject(relResult);
					if(reRetObj.get("ret").equals("0")){
						retFlag = "发送成功";
						syncMail = new SyncMail(TimeUtils.getDateDetail(),hosts,"成功","全服邮件",serverId,jsonObj.toString(),"");
						break;
					}
					retFlag = "发送失败";
					syncMail = new SyncMail(TimeUtils.getDateDetail(),hosts,"失败","全服邮件",serverId,jsonObj.toString(),"1");
				}
			}else{
				retFlag = "发送成功";
				syncMail = new SyncMail(TimeUtils.getDateDetail(),hosts,"成功","全服邮件",serverId,jsonObj.toString(),"");
			}
			syncMailService.addSyncMail(syncMail);
		}

		//判断是否发送成功,根据结果修改数据库的状态
		if(retFlag.equals("发送成功")){
			refreshStatus(new AllSrvMail(srvMail.getId(),MailStatus.SUCCESS.getStatus()));
		}else if(retFlag.equals("发送失败")){
			refreshStatus(new AllSrvMail(srvMail.getId(),MailStatus.FAIL.getStatus()));
		}

		ret.setRet(0);
		ret.setMsg(retFlag);
		return JSONObject.fromObject(ret).toString();
	}


	@Override
	public String syncAllSrvMailBySlave(AllSrvMail srvMail, String slave) {
		List<ServerList> serverList = serverListService.getServerListByMultSlaveId(slave);
		String sid = "";
		String nextLine = "\r\n";
		for(ServerList server : serverList){
			sid += server.getServerId() + nextLine;
		}

		String retFlag = "";

		Map<String,Object> syncData = new HashMap<String,Object>();

		syncData.put("id",srvMail.getId());
		syncData.put("sid",sid);
		syncData.put("createTime",srvMail.getCreateTime());
		syncData.put("subject",srvMail.getSubject());
		syncData.put("context",srvMail.getContext());
		syncData.put("awardStr",StringUtils.awardStrformart(srvMail.getAwardStr()));
		JSONObject jsonObj = JSONObject.fromObject(syncData);
		Ret ret = new Ret();
		try{
			long[] targetIds = new long[serverList.size()];
			for( int i = 0 ; i < serverList.size() ; i++ ) {
				targetIds[i] = serverList.get(i).getServerId();
			}
		}catch(Exception e){
			logger.info("Send all_srv_mail failed {} --- 服务器ID 格式错误");
			ret.setRet(-1);
			ret.setMsg("服务器ID格式有误,请重新输入");
			return JSONObject.fromObject(ret).toString();
		}

		//向所有的slave节点同步  获取所有slave的节点列表
		List<SlaveNodes> slaveNodesList = slaveNodesService.getSlaveNodesListNoPage();
		for(int i = 0 ; i < slaveNodesList.size() ; i++ ) {
			String serverId = slaveNodesList.get(i).getId()+"."+slaveNodesList.get(i).getName();
			String hosts = "http://"+( StringUtils.isNull(slaveNodesList.get(i).getNip())?slaveNodesList.get(i).getIp():slaveNodesList.get(i).getNip() )+":" + slaveNodesList.get(i).getPort() + "/sync/addsrvmail";
			logger.info("[同步全服服邮件****发送****的数据--------------------->]"+jsonObj.toString());
			String result = HttpUtils.jsonPost(hosts, jsonObj.toString());
			logger.info("[同步全服邮件****接收****的数据--------------------->]"+result.toString());
			SyncMail syncMail = new SyncMail();
			JSONObject retObj = JSONObject.fromObject(result);
			JSONObject reRetObj = new JSONObject();
			if(!retObj.get("ret").equals("0")){
				for( int j = 0 ; j < 5 ; j++ ){
					String relResult = HttpUtils.jsonPost(hosts,jsonObj.toString());
					reRetObj = JSONObject.fromObject(relResult);
					if(reRetObj.get("ret").equals("0")){
						retFlag = "发送成功";
						syncMail = new SyncMail(TimeUtils.getDateDetail(),hosts,"成功","全服邮件",serverId,jsonObj.toString(),"");
						break;
					}
					retFlag = "发送失败";
					syncMail = new SyncMail(TimeUtils.getDateDetail(),hosts,"失败","全服邮件",serverId,jsonObj.toString(),"1");
				}
			}else{
				retFlag = "发送成功";
				syncMail = new SyncMail(TimeUtils.getDateDetail(),hosts,"成功","全服邮件",serverId,jsonObj.toString(),"");
			}
			syncMailService.addSyncMail(syncMail);
		}

		//判断是否发送成功,根据结果修改数据库的状态
		if(retFlag.equals("发送成功")){
			refreshStatus(new AllSrvMail(srvMail.getId(),MailStatus.SUCCESS.getStatus()));
		}else if(retFlag.equals("发送失败")){
			refreshStatus(new AllSrvMail(srvMail.getId(),MailStatus.FAIL.getStatus()));
		}

		ret.setRet(0);
		ret.setMsg(retFlag);
		return JSONObject.fromObject(ret).toString();
	}


}

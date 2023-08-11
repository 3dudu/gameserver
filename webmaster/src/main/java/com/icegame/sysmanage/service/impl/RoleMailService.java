package com.icegame.sysmanage.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.icegame.framework.entity.Ret;
import com.icegame.framework.utils.*;
import com.icegame.sysmanage.controller.RoleMailController;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.entity.SyncMail;
import com.icegame.sysmanage.service.ISlaveNodesService;
import com.icegame.sysmanage.service.ISyncMailService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import com.icegame.framework.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.RoleMail;
import com.icegame.sysmanage.mapper.RoleMailMapper;
import com.icegame.sysmanage.service.IRoleMailService;

@Service
public class RoleMailService implements IRoleMailService {

	private static Logger logger = Logger.getLogger(RoleMailService.class);

	@Autowired
	private RoleMailMapper roleMailMapper;
	
	@Autowired
	private LogService logService;

	@Autowired
	private ISlaveNodesService slaveNodesService;

	@Autowired
	private ISyncMailService syncMailService;

	Log log = new Log();

	/**
	 *
	 * @param roleMail
	 * @param pageParam
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 11:03:37
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	public PageInfo<RoleMail> getRoleMailList(RoleMail roleMail, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<RoleMail> roleMailList = roleMailMapper.getRoleMailList(roleMail);
		if(roleMailList.size() > 0){
			for(RoleMail rm : roleMailList){
				rm.setCreateTime(TimeUtils.stampToDateWithMill(rm.getCreateTime()));
			}
		}
		PageInfo<RoleMail> pageInfo = new PageInfo<RoleMail>(roleMailList);
		return pageInfo;
	}

	public RoleMail getRoleMailById(Long id) {
		return roleMailMapper.getRoleMailById(id);
	}

	public boolean addRoleMail(RoleMail roleMail) {
		log = UserUtils.recording("添加个人邮件",Type.ADD.getName());
		logService.addLog(log);
		return roleMailMapper.addRoleMail(roleMail);
	}

	public boolean delRoleMail(Long id) {
		log = UserUtils.recording("删除个人邮件",Type.DELETE.getName());
		logService.addLog(log);
		return roleMailMapper.delRoleMail(id);
	}

	public boolean updateRoleMail(RoleMail roleMail) {
		log = UserUtils.recording("修改个人邮件",Type.UPDATE.getName());
		logService.addLog(log);
		return roleMailMapper.updateRoleMail(roleMail);
	}

	public boolean refreshStatus(RoleMail roleMail){
		return roleMailMapper.refreshStatus(roleMail);
	}

	@Override
	public String syncRoleMail(Long id) {
		String retFlag = "";
		RoleMail roleMail =  getRoleMailById(id);
		Map<String,Object> syncData = new HashMap<String,Object>();
		//JSONArray jsonArr = StringUtils.splitSidPid(roleMail.getSidPid());
		//JSONArray syncJson  = new JSONArray();
		//System.out.println(jsonArr.toString());
		/*for( int i = 0 ; i < jsonArr.size() ; i++) {
			JSONObject obj = jsonArr.getJSONObject(i);
			@SuppressWarnings("unchecked")
			Iterator<String> it = obj.keys();
			while(it.hasNext()) {
				String key = it.next();
				syncData.put("id",roleMail.getId());
				syncData.put("create_time",roleMail.getCreateTime());
				syncData.put("sid", key);
				syncData.put("pid", obj.getString(key));
				syncData.put("context", roleMail.getContext());
				syncData.put("awardStr", roleMail.getAwardStr());
			}
			syncJson.add(JSONObject.fromObject(syncData));
		}*/
		syncData.put("id", roleMail.getId());
		syncData.put("createTime", roleMail.getCreateTime());
		syncData.put("subject", roleMail.getSubject());
		syncData.put("sidPid", roleMail.getSidPid());
		syncData.put("context", roleMail.getContext());
		syncData.put("awardStr", StringUtils.awardStrformart(roleMail.getAwardStr()));
		JSONObject syncJson = JSONObject.fromObject(syncData);
		//向所有的slave节点同步  获取所有slave的节点列表
		List<SlaveNodes> slaveNodesList = slaveNodesService.getSlaveNodesListNoPage();
		for(int i = 0 ; i < slaveNodesList.size() ; i++ ) {
			String serverId = slaveNodesList.get(i).getId()+"."+slaveNodesList.get(i).getName();
			String hosts = "http://"+( StringUtils.isNull(slaveNodesList.get(i).getNip())?slaveNodesList.get(i).getIp():slaveNodesList.get(i).getNip() )+":" + slaveNodesList.get(i).getPort() + "/sync/addrolemail";
			logger.info("[同步个人/多人邮件****发送****的数据--------------------->]"+syncJson.toString());
			String result = HttpUtils.jsonPost(hosts, syncJson.toString());
			logger.info("[同步个人/多人邮件****接收****的数据--------------------->]"+result.toString());
			SyncMail syncMail = new SyncMail();
			JSONObject retObj = JSONObject.fromObject(result);
			JSONObject reRetObj = new JSONObject();
			if(!retObj.get("ret").equals("0")){
				for( int j = 0 ; j < 5 ; j++ ){
					String relResult = HttpUtils.jsonPost(hosts,syncJson.toString());
					reRetObj = JSONObject.fromObject(relResult);
					if(reRetObj.get("ret").equals("0")){
						syncMail = new SyncMail(TimeUtils.getDateDetail(),hosts,"成功","个人/多人邮件",serverId,syncJson.toString(),"");
						retFlag = "发送成功";
						break;
					}
					retFlag = "发送失败";
					syncMail = new SyncMail(TimeUtils.getDateDetail(),hosts,"失败","个人/多人邮件",serverId,syncJson.toString(),"2");
				}
			}else{
				retFlag = "发送成功";
				syncMail = new SyncMail(TimeUtils.getDateDetail(),hosts,"成功","个人/多人邮件",serverId,syncJson.toString(),"");
			}
			syncMailService.addSyncMail(syncMail);
		}

		//判断是否发送成功,根据结果修改数据库的状态
		if(retFlag.equals("发送成功")){
			refreshStatus(new RoleMail(roleMail.getId(), MailStatus.SUCCESS.getStatus()));
		}else if(retFlag.equals("发送失败")){
			refreshStatus(new RoleMail(roleMail.getId(),MailStatus.FAIL.getStatus()));
		}

		Ret ret = new Ret();
		ret.setRet(0);
		ret.setMsg(retFlag);
		return JSONObject.fromObject(ret).toString();
	}

}

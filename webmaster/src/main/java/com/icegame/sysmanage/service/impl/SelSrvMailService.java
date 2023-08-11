package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.entity.Ret;
import com.icegame.framework.utils.*;
import com.icegame.sysmanage.controller.SelSrvMailController;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.SelSrvMail;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.entity.SyncMail;
import com.icegame.sysmanage.mapper.SelSrvMailMapper;
import com.icegame.sysmanage.service.ISelSrvMailService;
import com.icegame.sysmanage.service.ISlaveNodesService;
import com.icegame.sysmanage.service.ISyncMailService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SelSrvMailService implements ISelSrvMailService {

	private static Logger logger = Logger.getLogger(SelSrvMailService.class);

	@Autowired
	private SelSrvMailMapper selSrvMailMapper;
	
	@Autowired
	private LogService logService;

	@Autowired
	private ISlaveNodesService slaveNodesService;

	@Autowired
	private ISyncMailService syncMailService;

	Log log = new Log();

	/**
	 * @param selSrvMail
	 * @param pageParam
	 * @return
	 *
	 * ----------------------------------------
	 * @date 2019-06-14 20:28:15
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	public PageInfo<SelSrvMail> getSelSrvMailList(SelSrvMail selSrvMail, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<SelSrvMail> selSrvMailList = selSrvMailMapper.getSelSrvMailList(selSrvMail);
		if(selSrvMailList.size() > 0){
			for(SelSrvMail srvMail : selSrvMailList){
				srvMail.setCreateTime(TimeUtils.stampToDateWithMill(srvMail.getCreateTime()));
			}
		}
		PageInfo<SelSrvMail> pageInfo = new PageInfo<SelSrvMail>(selSrvMailList);
		return pageInfo;
	}

	public SelSrvMail getSelSrvMailById(Long id) {
		return selSrvMailMapper.getSelSrvMailById(id);
	}

	public boolean addSelSrvMail(SelSrvMail selSrvMail) {
		log = UserUtils.recording("添加区服邮件",Type.ADD.getName());
		logService.addLog(log);
		return selSrvMailMapper.addSelSrvMail(selSrvMail);
	}

	public boolean delSelSrvMail(Long id) {
		log = UserUtils.recording("删除区服邮件",Type.DELETE.getName());
		logService.addLog(log);
		return selSrvMailMapper.delSelSrvMail(id);
	}

	public boolean updateSelSrvMail(SelSrvMail selSrvMail) {
		log = UserUtils.recording("修改区服邮件",Type.UPDATE.getName());
		logService.addLog(log);
		return selSrvMailMapper.updateSelSrvMail(selSrvMail);
	}

	public boolean refreshStatus(SelSrvMail selSrvMail){
		return selSrvMailMapper.refreshStatus(selSrvMail);
	}

	@Override
	public String syncSelSrvMail(Long id) {

		String retFlag = "";

		SelSrvMail selSrvMail =  getSelSrvMailById(id);
		Map<String,Object> syncData = new HashMap<String,Object>();
		//JSONArray syncJson = new JSONArray();
		//String[] sidStrArray = StringUtils.splitSid(selSrvMail.getSid());
		//for(String i : sidStrArray) {
		//	syncData.put("id", selSrvMail.getId());
		//	syncData.put("createTtime", selSrvMail.getCreateTime());
		//	syncData.put("sid", i);
		//	syncData.put("context", selSrvMail.getContext());
		//	syncData.put("awardStr", StringUtils.awardStrformart(selSrvMail.getAwardStr()));
		//	syncJson.add(JSONObject.fromObject(syncData));
		//}
		syncData.put("id",selSrvMail.getId());
		syncData.put("sid",selSrvMail.getSid());
		syncData.put("createTime",selSrvMail.getCreateTime());
		syncData.put("subject",selSrvMail.getSubject());
		syncData.put("context",selSrvMail.getContext());
		syncData.put("awardStr",StringUtils.awardStrformart(selSrvMail.getAwardStr()));
		JSONObject jsonObj = JSONObject.fromObject(syncData);
		Ret ret = new Ret();
		try{
			String[] tStrA = selSrvMail.getSid().split("\r\n");
			long[] targetIds = new long[tStrA.length];
			for( int i = 0 ; i < tStrA.length ; i++ ) {
				targetIds[i] = Long.parseLong(tStrA[i]);
			}
		}catch(Exception e){
			logger.info("Send sel_srv_mail failed {} --- 服务器ID 格式错误");
			ret.setRet(-1);
			ret.setMsg("服务器ID格式有误,请重新输入");
			return JSONObject.fromObject(ret).toString();
		}

		//向所有的slave节点同步  获取所有slave的节点列表
		List<SlaveNodes> slaveNodesList = slaveNodesService.getSlaveNodesListNoPage();
		for(int i = 0 ; i < slaveNodesList.size() ; i++ ) {
			String serverId = slaveNodesList.get(i).getId()+"."+slaveNodesList.get(i).getName();
			String hosts = "http://"+( StringUtils.isNull(slaveNodesList.get(i).getNip())?slaveNodesList.get(i).getIp():slaveNodesList.get(i).getNip() )+":" + slaveNodesList.get(i).getPort() + "/sync/addsrvmail";
			logger.info("[同步区服邮件****发送****的数据--------------------->]"+jsonObj.toString());
			String result = HttpUtils.jsonPost(hosts, jsonObj.toString());
			logger.info("[同步区服邮件****接收****的数据--------------------->]"+result.toString());
			SyncMail syncMail = new SyncMail();
			JSONObject retObj = JSONObject.fromObject(result);
			JSONObject reRetObj = new JSONObject();
			if(!retObj.get("ret").equals("0")){
				for( int j = 0 ; j < 5 ; j++ ){
					String relResult = HttpUtils.jsonPost(hosts,jsonObj.toString());
					reRetObj = JSONObject.fromObject(relResult);
					if(reRetObj.get("ret").equals("0")){
						retFlag = "发送成功";
						syncMail = new SyncMail(TimeUtils.getDateDetail(),hosts,"成功","区服邮件",serverId,jsonObj.toString(),"");
						break;
					}
					retFlag = "发送失败";
					syncMail = new SyncMail(TimeUtils.getDateDetail(),hosts,"失败","区服邮件",serverId,jsonObj.toString(),"1");
				}
			}else{
				retFlag = "发送成功";
				syncMail = new SyncMail(TimeUtils.getDateDetail(),hosts,"成功","区服邮件",serverId,jsonObj.toString(),"");
			}
			syncMailService.addSyncMail(syncMail);
		}

		//判断是否发送成功,根据结果修改数据库的状态
		if(retFlag.equals("发送成功")){
			refreshStatus(new SelSrvMail(selSrvMail.getId(),MailStatus.SUCCESS.getStatus()));
		}else if(retFlag.equals("发送失败")){
			refreshStatus(new SelSrvMail(selSrvMail.getId(),MailStatus.FAIL.getStatus()));
		}

		ret.setRet(0);
		ret.setMsg(retFlag);
		return JSONObject.fromObject(ret).toString();
	}


}

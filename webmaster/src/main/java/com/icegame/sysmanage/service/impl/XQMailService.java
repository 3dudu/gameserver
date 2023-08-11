package com.icegame.sysmanage.service.impl;

import cn.hutool.http.HttpUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.entity.Ret;
import com.icegame.framework.utils.*;
import com.icegame.sysmanage.entity.*;
import com.icegame.sysmanage.mapper.XQMailMapper;
import com.icegame.sysmanage.service.IServerListService;
import com.icegame.sysmanage.service.ISlaveNodesService;
import com.icegame.sysmanage.service.ISyncMailService;
import com.icegame.sysmanage.service.IXQMailService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class XQMailService implements IXQMailService {

	private static Logger logger = Logger.getLogger(XQMailService.class);

	@Autowired
	private XQMailMapper xqMailMapper;

	@Autowired
	private IServerListService serverListService;

	@Autowired
	private LogService logService;

	@Autowired
	private ISyncMailService syncMailService;

	@Autowired
	private ISlaveNodesService slaveNodesService;

	Log log = new Log();

	@Override
	public PageInfo<XQMail> findAll(XQMail mail, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<XQMail> roleMailList = xqMailMapper.findAll(mail);
		return new PageInfo<>(roleMailList);
	}

	@Override
	public XQMail findById(Long id) {
		return xqMailMapper.findById(id);
	}

	@Override
	public boolean addXQMail(XQMail mail) {
		log = UserUtils.recording("添加小7邮件",Type.ADD.getName());
		log.setUserId(0L);
		logService.addLog(log);
		return xqMailMapper.addXQMail(mail);
	}

	@Override
	public boolean refreshStatus(XQMail mail){
		return xqMailMapper.refreshStatus(mail);
	}

	@Override
	public String syncXQMail(Long id) {

		XQMail xqMail = findById(id);
		Ret validateResult = new Ret();
		boolean hasSameXqMail = hasSameIssueOrderId(xqMail.getIssueOrderId(), 1);
		if (hasSameXqMail) {
			validateResult.setRet(-1);
			validateResult.setMsg("存在与当前发送邮件时间相同的记录，请稍后重新发送。");
			logger.error("存在与当前发送邮件时间相同的记录，请稍后重新发送。");
			return JSONObject.fromObject(validateResult).toString();
		}
		String sidPid = "";
		try {
			sidPid = xqMail.getServerId() + ":" + xqMail.getRoleId().split("@")[1];
		} catch (Exception e) {
			logger.error("syncXQMail Exception:" + e.getMessage());
			validateResult.setRet(-2);
			validateResult.setMsg("区服ID或角色id格式错误");
			return JSONObject.fromObject(validateResult).toString();
		}

		JSONObject syncData = new JSONObject();
		syncData.put("id", xqMail.getId());
		syncData.put("createTime", xqMail.formatCreateTime());
		syncData.put("subject", xqMail.getMailTitle());
		syncData.put("sidPid", sidPid);
		syncData.put("context", xqMail.getMailContent());
		syncData.put("awardStr", StringUtils.awardStrFormat(xqMail.formatAwardStr()));

		List<ServerList> serverList = new ArrayList<>();
		String[] split = sidPid.split("\r\n");
		for(String s :split) {
			ServerList server = serverListService.getServerById(Long.parseLong(s.split(":")[0]));
			if (server == null) {
				continue;
			}
			serverList.add(server);
		}
		if (serverList.isEmpty()) {
			throw new RuntimeException("区服ID错误，没有找到对应的区服。");
		}

		List<Long> slaveIds = getSlaveIds(serverList);
		List<SlaveNodes> slaveNodesList = slaveNodesService.getSlaveNodesListNoPage();
		String logPrefix = "小七邮件(邮件id=" + id + ")";
		List<Long> success = new ArrayList<>();
		List<Long> failed = new ArrayList<>();
		Ret ret = new Ret();

		for (SlaveNodes slaveNodes : slaveNodesList) {
			// serverLists 不为 null ,说明是只给serverLists中的这些区服发送
			if(slaveIds.size() > 0 && !slaveIds.contains(slaveNodes.getId())) {
				continue;
			}
			String message = "[slaveId=" + slaveNodes.getId() + "]";
			String serverId = slaveNodes.getId() + "." + slaveNodes.getName();
			String hosts = "http://" + (StringUtils.isNull(slaveNodes.getNip()) ? slaveNodes.getIp() : slaveNodes.getNip()) + ":" + slaveNodes.getPort() + "/sync/addrolemail";
			logger.info("{" + logPrefix + "/Request}:向 " + message + " 发送请求,请求地址 " + hosts + ",发送数据:" + syncData);
			String result;
			try {
				result = HttpUtil.post(hosts, syncData.toString(), 3000);
				logger.info("{" + logPrefix + "/Response}:接收到 " + message + " 响应内容:" + result);
			} catch (Exception e) {
				result = "{}";
				logger.error("{" + logPrefix + "/Exception}: 接口请求异常 " + message + " 异常内容:" + e.getMessage());
			}
			SyncMail syncMail;
			JSONObject retObj = JSONObject.fromObject(result);
			if (retObj != null && !retObj.isEmpty() && "0".equals(retObj.get("ret"))) {
				success.add(slaveNodes.getId());
				syncMail = new SyncMail(TimeUtils.getDateDetail(), hosts, "成功", XQMail.MAIL_TYPE, serverId, syncData.toString(),
						"");
			} else {
				failed.add(slaveNodes.getId());
				syncMail = new SyncMail(TimeUtils.getDateDetail(), hosts, "失败", XQMail.MAIL_TYPE, serverId,
						syncData.toString(), "0");
			}
			syncMailService.addSyncMail(syncMail);
		}
		if(failed.isEmpty()){
			ret.setRet(0);
			ret.setMsg("发送成功");
		} else {
			ret.setRet(-1);
			ret.setMsg("如下slave发送失败，slaveId分别是：" + Arrays.toString(failed.toArray()));
		}
		if(ret.success()){
			refreshStatus(new XQMail(xqMail.getId(), MailStatus.SUCCESS.getStatus()));
		} else {
			refreshStatus(new XQMail(xqMail.getId(),MailStatus.FAIL.getStatus()));
		}
		return JSONObject.fromObject(ret).toString();
	}

	private List<Long> getSlaveIds(List<ServerList> serverLists) {
		List<Long> ids = new ArrayList<>();
		if(serverLists == null || serverLists.size() == 0){
			return ids;
		}
		for (ServerList server :serverLists ){
			if (!ids.contains(server.getSlaveId())) {
				ids.add(server.getSlaveId());
			}
		}
		return ids;
	}

	@Override
	public boolean hasSameIssueOrderId(String issueOrderId, Integer maxValue) {
		List<XQMail> xqMails = xqMailMapper.hasSameIssueOrderId(issueOrderId);
		return xqMails.size() > maxValue;
	}

}

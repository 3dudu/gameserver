package com.icegame.sysmanage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.icegame.framework.data.Constant;
import com.icegame.framework.utils.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.controller.ServerListController;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.MainTain;
import com.icegame.sysmanage.entity.OpclServerStatus;
import com.icegame.sysmanage.entity.ServerList;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.mapper.MainTainMapper;
import com.icegame.sysmanage.service.IMainTainService;
import com.icegame.sysmanage.service.IOpclServerStatusService;
import com.icegame.sysmanage.service.IServerListService;
import com.icegame.sysmanage.service.ISlaveNodesService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Service
public class MainTainService implements IMainTainService {

	private static Logger logger = Logger.getLogger(ServerListController.class);

	private static final String ALL_SERVER_MAINTAIN = "全服维护";

	@Autowired
	private MainTainMapper mainTainMapper;

	@Autowired
	private LogService logService;

	@Autowired
	private IServerListService serverListService;

	@Autowired
	private IOpclServerStatusService opclServerStatusService;

	@Autowired
	private ISlaveNodesService slaveNodesService;

	Log log = new Log();

	@Override
	public PageInfo<MainTain> getMainTainList(MainTain mainTain, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<MainTain> mainTainList = mainTainMapper.getMainTainList(mainTain);
		PageInfo<MainTain> pageInfo = new PageInfo<MainTain>(mainTainList);
		return pageInfo;
	}


	@Override
	public MainTain getMainTainById(Long id) {
		return mainTainMapper.getMainTainById(id);
	}

	@Override
	public boolean addMainTain(MainTain mainTain) {
		log = UserUtils.recording("添加维护记录["+mainTain.getReason()+"]",Type.ADD.getName());
		logService.addLog(log);
		return mainTainMapper.addMainTain(mainTain);
	}

	@Override
	public boolean delMainTain(Long id) {
		log = UserUtils.recording("删除维护记录["+id+"]",Type.DELETE.getName());
		logService.addLog(log);
		return mainTainMapper.delMainTain(id);
	}

	@Override
	public boolean updateMainTain(MainTain mainTain) {
		log = UserUtils.recording("修改维护记录["+mainTain.getId()+"]",Type.UPDATE.getName());
		logService.addLog(log);
		return mainTainMapper.updateMainTain(mainTain);
	}


	@Override
	public List<OpclServerStatus> saveSync(MainTain mainTain) {
		List<SlaveNodes> slaveNodesList;
		if(ALL_SERVER_MAINTAIN.equals(mainTain.getReason()) && ALL_SERVER_MAINTAIN.equals(mainTain.getTitle())){
			slaveNodesList = slaveNodesService.getSlaveNodesListNoPage();
		} else {
			slaveNodesList = new ArrayList<>();
			String slaveStr = mainTain.getSelectedSlave();
			String[] slaveArray = slaveStr.split(",");
			for(String s : slaveArray){
				SlaveNodes slaveNodes = slaveNodesService.getSlaveNodesById(Long.parseLong(s));
				slaveNodesList.add(slaveNodes);
			}

		}
		if(slaveNodesList != null && slaveNodesList.size() > 0){
			return sync(slaveNodesList, mainTain.getClosed());
		}
		return null;
	}

	/**
	 * 数据同步
	 * @param slaveNodesList slave节点list
	 * @param close 开启/关闭 状态
	 */
	private List<OpclServerStatus> sync(List<SlaveNodes> slaveNodesList,int close){
		List<OpclServerStatus> statusList = new ArrayList<>();
		Map<String,Object> data = new HashMap<>();
		String[] serverIds = new String[]{"all"};
		data.put("serverIds",serverIds);
        data.put("opr",close == 1 ? "close" : "open");
        String dataStr = JSON.toJSONString(data);
		for(SlaveNodes s : slaveNodesList){

			// 向slave同步，这边砍掉了原先的同步失败后自动同步五次
			String message = "[slaveId=" + s.getId() + "]";
			String hosts = "http://" + (StringUtils.isNotNull(s.getNip()) ? s.getNip() : s.getIp()) +":" +
					s.getPort() + "/sync/maintainserver";
			String hostsMessage = message + "#" + hosts;
			logger.info("{Slave维护/Request}:向 " + message + " 发送请求,请求地址 " + hosts + ",发送数据:" + dataStr);
			String result = new JSONObject().toString();
			try {
				result = HttpUtil.post(hosts,dataStr,3000);
			} catch (Exception e){
				logger.error("MainTain sync exceprion",e);
			}
			logger.info("{Slave维护/Response}:接收到 " + message + " 响应内容:" + result);
			OpclServerStatus opclServerStatus = null;
			JSONObject retObj = JSONObject.fromObject(result);
			if(Constant.MAINTAIN_SYNC_SUCCESS_STATUS.equals(retObj.get(Constant.MAINTAIN_SYNC_SUCCESS_KEY))){
				// 同步成功的情况下才能修改本地数据库中的区服状态
				ServerList serverList = new ServerList();
				serverList.setSlaveId(s.getId());
				serverList.setClose(close);
				serverListService.updateServerCloseBySlaveId(serverList);

				opclServerStatus = new OpclServerStatus(TimeUtils.getDateDetail(),hostsMessage,"成功","维护",s.getName(),dataStr,"");
			}else{
				opclServerStatus = new OpclServerStatus(TimeUtils.getDateDetail(),hostsMessage,"失败","维护",s.getName(),dataStr,"1");
				statusList.add(opclServerStatus);
			}
			opclServerStatusService.addOpclServerStatus(opclServerStatus);
		}
		return statusList;
	}
}

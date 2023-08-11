package com.icegame.sysmanage.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.SyncStatus;
import com.icegame.sysmanage.service.ISyncStatusService;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/sysmgr/sync")
public class SyncStatusController {
		
	@Autowired
	private ISyncStatusService syncStatusService;

	
	@RequestMapping("gotoSyncStatus")
	public String gotoServerList(){
		return "sysmanage/server/syncStatus";
	}
	
	@RequestMapping("getSyncStatusList")
	@ResponseBody
	public String getSyncStatusList(String status,String type,String serverNodeIp,String startDate,String endDate,int pageNo,int pageSize){
		if(startDate.equals("") && endDate.equals("")){
			startDate = TimeUtils.getStartTime();
			endDate = TimeUtils.getEndTime();	
		}else{
			startDate = TimeUtils.checkDate(startDate, endDate).get("startDate");
			endDate = TimeUtils.checkDate(startDate, endDate).get("endDate");
		}
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<SyncStatus> syncStatusList = new ArrayList<SyncStatus>();
		SyncStatus syncStatus = new SyncStatus();syncStatus.setStatus(status);syncStatus.setServerNodeIp(serverNodeIp.trim());
		syncStatus.setStartDate(startDate);syncStatus.setEndDate(endDate);syncStatus.setType(type);
		PageInfo<SyncStatus> pageInfo = this.syncStatusService.getSyncStatusList(syncStatus, pageParam);
		syncStatusList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(syncStatusList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("getTypeListSync")
	@ResponseBody
	public String getTypeList(){
		List<SyncStatus> typeList = syncStatusService.getTypeList();
		JSONArray array = JSONArray.fromObject(typeList);
		return array.toString();
	}
	
	@RequestMapping("getStatusListSync")
	@ResponseBody
	public String getStatusList(){
		List<SyncStatus> statusList = syncStatusService.getStatusList();
		JSONArray array = JSONArray.fromObject(statusList);
		return array.toString();
	}
	

}

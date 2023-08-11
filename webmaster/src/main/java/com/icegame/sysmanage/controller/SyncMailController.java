package com.icegame.sysmanage.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.HttpUtils;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.sysmanage.entity.OpclServerStatus;
import com.icegame.sysmanage.entity.SyncMail;
import com.icegame.sysmanage.service.ISyncMailService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/sysmgr/syncmail")
public class SyncMailController {
		
	@Autowired
	private ISyncMailService syncMailService;
		
	@RequestMapping("gotoSyncMail")
	public String gotoSyncMail(){
		return "sysmanage/mail/syncmail";
	}
	
	@RequestMapping("getSyncMailList")
	@ResponseBody
	public String getSyncMailList(String status,String type,String startDate,String endDate,int pageNo,int pageSize){
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
		List<SyncMail> syncMailList = new ArrayList<SyncMail>();
		SyncMail syncMail = new SyncMail();syncMail.setStatus(status.trim());
		syncMail.setStartDate(startDate);syncMail.setEndDate(endDate);syncMail.setType(type);
		PageInfo<SyncMail> pageInfo = this.syncMailService.getSyncMailList(syncMail, pageParam);
		syncMailList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(syncMailList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("getStatusListSyncMail")
	@ResponseBody
	public String getTypeList(){
		List<SyncMail> syncMailList = syncMailService.getStatusList();
		JSONArray array = JSONArray.fromObject(syncMailList);
		return array.toString();
	}
	
	@RequestMapping("reSyncMail")
	@ResponseBody
	public String reSyncMail(Long id){
		Map<String,String> retMap = new HashMap<String,String>();
		SyncMail syncMail = syncMailService.getSyncMailById(id);
		String hosts = syncMail.getHosts();
		String data = syncMail.getFailed();
		String opClResult = HttpUtils.jsonPost(hosts,data);
		if(!opClResult.equals("timeout")){
			JSONObject retObj = JSONObject.fromObject(opClResult);
			if(retObj.get("ret").equals("0")){
				opClResult = "成功";
				SyncMail sm = new SyncMail();
				sm.setCreateTime(TimeUtils.getDateDetail());
				sm.setStatus(opClResult);
				sm.setId(id);
				syncMailService.updateSyncMail(sm);
			}
		}
		retMap.put("result", opClResult);
		return JSONObject.fromObject(retMap).toString();
	}
	

}

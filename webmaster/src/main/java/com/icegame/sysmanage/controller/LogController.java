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
import com.icegame.sysmanage.service.ILogService;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/sysmgr/log")
public class LogController {
	
	@Autowired
	private ILogService logService;

	@RequestMapping("gotoLogList")
	public String gotoServerList(){
		return "sysmanage/log/logList";
	}
	
	@RequestMapping("getLogList")
	@ResponseBody
	public String getServerList(String userName,String startDate,String type,String endDate,int pageNo,int pageSize){
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
		List<Log> serverList = new ArrayList<Log>();
		Log log = new Log();
		log.setStartDate(startDate);
		log.setEndDate(endDate);
		log.setUserName(userName);
		log.setType(type);
		PageInfo<Log> pageInfo = this.logService.getLogList(log, pageParam);
		serverList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(serverList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("getTypeList")
	@ResponseBody
	public String getTypeList(){
		List<Log> typeList = logService.getTypeList();
		JSONArray array = JSONArray.fromObject(typeList);
		return array.toString();
	}

}

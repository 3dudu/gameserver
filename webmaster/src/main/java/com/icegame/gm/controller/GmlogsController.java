package com.icegame.gm.controller;


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
import com.icegame.gm.entity.Gmlogs;
import com.icegame.gm.service.IGmlogsService;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/sysmgr/gmlogs")
public class GmlogsController {
	
	@Autowired
	private IGmlogsService gmlogsService;

	@RequestMapping("gotoGmlogsList")
	public String gotoGmlogsList(){
		return "sysmanage/gm/gmlogs";
	}
	
	@RequestMapping("getGmlogsList")
	@ResponseBody
	public String getGmlogsList(String loginName,String type,String startDate,String endDate,int pageNo,int pageSize){
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
		List<Gmlogs> gmlogsList = new ArrayList<Gmlogs>();
		Gmlogs gmlogs = new Gmlogs();
		gmlogs.setStartDate(startDate);
		gmlogs.setEndDate(endDate);
		gmlogs.setLoginName(loginName);
		gmlogs.setType(type);
		PageInfo<Gmlogs> pageInfo = this.gmlogsService.getGmlogsList(gmlogs, pageParam);
		gmlogsList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(gmlogsList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("getTypeList")
	@ResponseBody
	public String getTypeList(){
		List<Gmlogs> typeList = gmlogsService.getTypeList();
		JSONArray array = JSONArray.fromObject(typeList);
		return array.toString();
	}

}

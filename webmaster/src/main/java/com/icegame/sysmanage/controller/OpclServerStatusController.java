package com.icegame.sysmanage.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.http.HttpUtil;
import com.icegame.framework.data.Constant;
import com.icegame.framework.utils.StringUtils;
import org.apache.log4j.Logger;
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
import com.icegame.sysmanage.service.IOpclServerStatusService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Controller
@RequestMapping("/sysmgr/opcl")
public class OpclServerStatusController {

	private static Logger logger = Logger.getLogger(OpclServerStatusController.class);

	@Autowired
	private IOpclServerStatusService opclServerStatusService;
		
	@RequestMapping("gotoOpclServerStatus")
	public String gotoOpclServerStatus(){
		return "sysmanage/server/opclserver";
	}
	
	@RequestMapping("getOpclServerList")
	@ResponseBody
	public String getOpclServerList(String status,String type,String startDate,String endDate,int pageNo,int pageSize){
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
		List<OpclServerStatus> opclServerStatusList = new ArrayList<OpclServerStatus>();
		OpclServerStatus opclServerStatus = new OpclServerStatus();opclServerStatus.setStatus(status.trim());
		opclServerStatus.setStartDate(startDate);opclServerStatus.setEndDate(endDate);opclServerStatus.setType(type);
		PageInfo<OpclServerStatus> pageInfo = this.opclServerStatusService.getOpclServerStatusList(opclServerStatus, pageParam);
		opclServerStatusList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(opclServerStatusList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("getStatusListOpcl")
	@ResponseBody
	public String getTypeList(){
		List<OpclServerStatus> opclServerStatusList = opclServerStatusService.getStatusList();
		JSONArray array = JSONArray.fromObject(opclServerStatusList);
		return array.toString();
	}
	
	@RequestMapping("reSyncOpcl")
	@ResponseBody
	public String reSyncOpcl(Long id){
		Map<String,String> retMap = new HashMap<String,String>();
		OpclServerStatus opclServerStatus = opclServerStatusService.getOpclServerStatusById(id);
		String hostsMessage = opclServerStatus.getHosts();
		String hosts = StringUtils.substringAfter(hostsMessage,"#");
		String data = opclServerStatus.getFailed();
		String message = StringUtils.substringBefore(hostsMessage,"#");
		logger.info("[重新同步]{Slave维护/Request}:向 " + message + " 发送请求,请求地址 " + hosts + ",发送数据:" + data);
		String opClResult =  HttpUtil.post(hosts,data,3000);
		logger.info("[重新同步]{Slave维护/Response}:接收到 " + message + " 响应内容:" + opClResult);
		JSONObject retObj = JSONObject.fromObject(opClResult);
		if(Constant.MAINTAIN_SYNC_SUCCESS_STATUS.equals(retObj.get(Constant.MAINTAIN_SYNC_SUCCESS_KEY))){
			opClResult = "成功";
			OpclServerStatus ost = new OpclServerStatus();
			ost.setCreateTime(TimeUtils.getDateDetail());
			ost.setStatus(opClResult);
			ost.setId(id);
			opclServerStatusService.updateOpclServerStatus(ost);
		}
		retMap.put("result", opClResult);
		return JSONObject.fromObject(retMap).toString();
	}
	

}

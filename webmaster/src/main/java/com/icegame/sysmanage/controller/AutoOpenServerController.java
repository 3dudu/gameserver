package com.icegame.sysmanage.controller;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.*;
import com.icegame.sysmanage.service.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sysmgr/autoopenserver")
public class AutoOpenServerController {
	
	private static Logger logger = Logger.getLogger(AutoOpenServerController.class);
	
	@Autowired
	private IAutoOpenServerService autoOpenServerService;

	@Autowired
	private IServerListService serverListService;
	
	@RequestMapping("gotoAutoOpenServer")
	public String gotoAutoOpenServer(){
		return "sysmanage/autoopenserver/openserver";
	}
	
	@RequestMapping("/getOpenServer")
	@ResponseBody
	public String getOpenServer(String key, int pageNo,int pageSize){

		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<AutoOpenServer> autoOpenServerList = new ArrayList<AutoOpenServer>();

		// 1 表示是 自动开服的数据
		AutoOpenServer autoOpenServer = new AutoOpenServer();
		autoOpenServer.setKey(key);
		PageInfo<AutoOpenServer> pageInfo = this.autoOpenServerService.getAutoOpenServerList(autoOpenServer, pageParam);
		autoOpenServerList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(autoOpenServerList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("/gotoOpenServerEdit")
	@ResponseBody
	public String gotoOpenServerEdit(@ModelAttribute("editFlag") int editFlag,
			Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		AutoOpenServer autoOpenServer = new AutoOpenServer();
		if(editFlag == 2){
			autoOpenServer = autoOpenServerService.getAutoOpenServerById(id);
			jsonObj = JSONObject.fromObject(autoOpenServer);
		}
		return jsonObj.toString();   
	}
	
	@RequestMapping("/saveOpenServer")
	public @ResponseBody Map<String,Object> saveOpenServer(@RequestBody AutoOpenServer autoOpenServer){

		Map<String,Object> resultMap = new HashMap<String,Object>();

		// 此处设置为1 代表是自动开服
		autoOpenServer.setType(1);
		try{
			if(autoOpenServer != null && autoOpenServer.getId() != null){
				if(autoOpenServerService.updateAutoOpenServer(autoOpenServer)){
					resultMap.put("result", "修改成功");
					logger.info("修改自动开服成功");
				}
			}else{

				// 如果是增加操作，需要判断是否已经存在
				if(autoOpenServerService.existsKey(autoOpenServer.getKey())){
					resultMap.put("result", "渠道已经存在");
					return resultMap;
				}

				if(autoOpenServerService.addAutoOpenServer(autoOpenServer)){
					resultMap.put("result", "增加记录信息成功");
					logger.info("增加自动开服信息");
				}
			}	
		}catch(Exception e){
			resultMap.put("result", "操作自动开服失败");
			logger.error("操作自动开服失败",e);
		}
		return resultMap;
	}

	@RequestMapping("/checkExistAutoOpenServer")
	public @ResponseBody String checkExistAutoOpenServer(String key){
		AutoOpenServer autoOpenServer = new AutoOpenServer();
		autoOpenServer.setKey(key);
		List<AutoOpenServer> autoOpenServerList = autoOpenServerService.checkExistAutoOpenServer(autoOpenServer);
		if(autoOpenServerList.size() > 0) {
			return "1";
		}else {
			return "0";
		}
	}

	@RequestMapping("/getSwitchStatus")
	public @ResponseBody Map<String,Object> getSwitchStatus(String key){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		AutoOpenServer status = autoOpenServerService.getAutoOpenServerTipsStatus(new AutoOpenServer());
		AutoOpenServer slimit = autoOpenServerService.getAutoOpenServerTipsSlimit(new AutoOpenServer());
		resultMap.put("status",status.getValue());
		resultMap.put("slimit",slimit.getValue());
		return resultMap;
	}

	@RequestMapping("/saveSwitchStatus")
	public @ResponseBody Map<String,Object> saveSwitchStatus(@RequestBody AutoOpenServer autoOpenServer){
		Map<String,Object> resultMap = new HashMap<String,Object>();

		boolean flag = false;

		flag = autoOpenServerService.changeAutoOpenServerTipsStatus(autoOpenServer);

		// 此处用key暂时存这页面传过来的value，上一步操作已经用过了，所以这个地方可以还原回去
		autoOpenServer.setValue(autoOpenServer.getKey());
		flag = autoOpenServerService.changeAutoOpenServerTipsSlimit(autoOpenServer);

		if(flag){
			resultMap.put("result","修改成功");
		}else{
			resultMap.put("result","修改失败");
		}
		return resultMap;
	}

	@RequestMapping("/getAllChannel")
	public @ResponseBody String getAllChannel(){
		List<ServerList> serverLists = serverListService.getAllChannelList();
		return JSON.toJSONString(serverLists);
	}

	
	@RequestMapping("/delOpenServer")
	public @ResponseBody Map<String,Object> delOpenServer(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(autoOpenServerService.delAutoOpenServer(id)){
				resultMap.put("result", "删除成功");
				logger.info("删除成功");
			}	
		}catch(Exception e){
			logger.error("删除失败",e);
			resultMap.put("result", "删除失败");
		}
		return resultMap;
	}
	
}

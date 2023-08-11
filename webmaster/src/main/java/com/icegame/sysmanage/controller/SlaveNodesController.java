package com.icegame.sysmanage.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.sysmanage.entity.ServerList;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.service.IServerListService;
import com.icegame.sysmanage.service.ISlaveNodesService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/sysmgr/slavenodes")
public class SlaveNodesController {

	private static Logger logger = Logger.getLogger(SlaveNodesController.class);

	@Autowired
	private ISlaveNodesService slaveNodesService;

	@Autowired
	private IServerListService serverListService;

	@RequestMapping("gotoSlaveNodes")
	public String gotoSlaveNodes(){
		return "sysmanage/slave/slavenodes";
	}

	@RequestMapping("getSlaveNodesList")
	@ResponseBody
	public String getSlaveNodesList(String ip, String name,int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<SlaveNodes> slaveNodesList = new ArrayList<SlaveNodes>();
		SlaveNodes slaveNodes = new SlaveNodes();slaveNodes.setIp(ip);slaveNodes.setName(name);
		PageInfo<SlaveNodes> pageInfo = this.slaveNodesService.getSlaveNodesList(slaveNodes, pageParam);
		slaveNodesList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(slaveNodesList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("getSlaveNodesListNoPage")
	@ResponseBody
	public String getSlaveNodesListNoPage(){
		List<SlaveNodes> slaveNodesList = slaveNodesService.getSlaveNodesListNoPage();
		JSONArray jsonArr = JSONArray.fromObject(slaveNodesList);
		return jsonArr.toString();
	}

	@RequestMapping("/gotoSlaveNodesEdit")
	@ResponseBody
	public String gotoSlaveNodesEdit(@ModelAttribute("editFlag") int editFlag,
			Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		SlaveNodes slaveNodes = new SlaveNodes();
		if(editFlag == 2){
			slaveNodes = slaveNodesService.getSlaveNodesById(id);
			jsonObj = JSONObject.fromObject(slaveNodes);
		}
		return jsonObj.toString();
	}

	@RequestMapping("/checkExistSlaveNodeWithName")
	public @ResponseBody String checkExistServerList(String name){
		SlaveNodes slaveNodes = new SlaveNodes();
		slaveNodes.setName(name);
		List<SlaveNodes> slaveNodesList = slaveNodesService.checkExistSlaveNodeWithName(slaveNodes);
		if(slaveNodesList.size() > 0) {
			return "1";
		}else {
			return "0";
		}
	}

	@RequestMapping("/checkExistSlaveNodeWithIp")
	public @ResponseBody String checkExistSlaveNodeWithIp(String ip){
		SlaveNodes slaveNodes = new SlaveNodes();
		slaveNodes.setIp(ip);
		List<SlaveNodes> slaveNodesList = slaveNodesService.checkExistSlaveNodeWithIp(slaveNodes);
		if(slaveNodesList.size() > 0) {
			return "1";
		}else {
			return "0";
		}
	}

	@RequestMapping("/saveSlaveNodes")
	public @ResponseBody Map<String,Object> saveSlaveNodes(@RequestBody SlaveNodes slaveNodes){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(slaveNodes != null && slaveNodes.getId() != null){
				if(StringUtil.isEmpty(slaveNodes.getNip()) || StringUtil.isEmpty(slaveNodes.getIp())){
					resultMap.put("result", "外部通讯地址和内部通讯地址不允许为空");
				} else {
					slaveNodesService.updateSlaveNodes(slaveNodes);
					ServerList serverList = new ServerList();
					serverList.setSlaveId(slaveNodes.getId());
					serverList.setIp(slaveNodes.getIp());
					serverListService.updateSlaveIp(serverList);
					resultMap.put("result", "修改记录信息成功");
					logger.info("修改salve节点");
				}
			}else{//增加
				slaveNodesService.addSlaveNodes(slaveNodes);
				resultMap.put("result", "增加记录信息成功");
				logger.info("增加salve节点");
			}
		}catch(Exception e){
			resultMap.put("result", "操作记录失败");
			logger.error("操作邮件失败",e);
		}
		return resultMap;
	}

	@RequestMapping("/delSlaveNodes")
	public @ResponseBody Map<String,Object> delSlaveNodes(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(slaveNodesService.delSlaveNodes(id)){
				resultMap.put("result", "删除slave节点成功");
				logger.info("删除slave节点"+id);
			}
		}catch(Exception e){
			logger.error("删除slave节点失败",e);
		}
		return resultMap;
	}

}

package com.icegame.sysmanage.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.icegame.framework.utils.HttpUtils;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.sysmanage.entity.ServerNodes;
import com.icegame.sysmanage.entity.SyncStatus;
import com.icegame.sysmanage.service.IServerNodesService;
import com.icegame.sysmanage.service.ISyncStatusService;
import com.icegame.sysmanage.vo.ServerNodesVo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/sysmgr/server")
public class ServerNodesController {
	
	private static Logger logger = Logger.getLogger(ServerNodesController.class);
	
	@Autowired
	private IServerNodesService serverNodesService;
	
	@Autowired
	private ISyncStatusService syncStatusService;

	
	@RequestMapping("gotoServerNodes")
	public String gotoServerList(){
		return "sysmanage/server/serverNodes";
	}
	
	@RequestMapping("getServerNodesList")
	@ResponseBody
	public String getServerNodesList(String nodeNames,int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<ServerNodes> serverNodesList = new ArrayList<ServerNodes>();
		ServerNodes serverNodes = new ServerNodes();serverNodes.setName(nodeNames.trim());
		PageInfo<ServerNodes> pageInfo = this.serverNodesService.getServerNodes(serverNodes, pageParam);
		serverNodesList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(serverNodesList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("/gotoServerNodesEdit")
	@ResponseBody
	public String gotoServerNodesEdit(@ModelAttribute("editFlag") int editFlag,
			Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		ServerNodes serverNodes = new ServerNodes();
		if(editFlag == 2){
			serverNodes = serverNodesService.getServerNodesById(id);
			jsonObj = JSONObject.fromObject(serverNodes);
		}
		return jsonObj.toString();   
	}
	
	@RequestMapping("/saveServerNodes")
	public @ResponseBody Map<String,Object> saveServerNodes(@RequestBody ServerNodes serverNodes){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(serverNodes != null && serverNodes.getId() != null){
				serverNodesService.updateServerNodes(serverNodes);		
				resultMap.put("result", "修改记录信息成功");
				logger.info("修改["+serverNodes.getName()+"]配置成功");
			}else{//增加
				serverNodesService.addServerNodes(serverNodes);
				resultMap.put("result", "增加记录信息成功");
				logger.info("增加["+serverNodes.getName()+"]配置成功");
			}	
		}catch(Exception e){
			resultMap.put("result", "操作记录失败");
			logger.error("操作["+serverNodes.getName()+"]失败",e);
		}finally{
			//refreshAll(server);
		}
		return resultMap;
	}
	
	@RequestMapping("/delServerNodes")
	public @ResponseBody Map<String,Object> delServerNodes(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(serverNodesService.delServerNodes(id)){
				resultMap.put("result", "删除服务器节点成功");
				logger.info("删除服务器"+id);
			}	
		}catch(Exception e){
			logger.error("删除服务器节点失败",e);
		}
		return resultMap;
	}
	
	@RequestMapping("/syncServer")
	public @ResponseBody String syncServer(@RequestBody ServerNodesVo serverNodesVo){
		Map<Long,Long> nodesIds = serverNodesVo.getNodesIds();
		try{
			if(nodesIds != null){
				for(Long id : nodesIds.values()) {
					//1.获取到所有的记录id
					System.out.println(id);
					//2.根据ID查询记录的详细信息
					ServerNodes serverNodes = serverNodesService.getServerNodesById(id);
					//3.对记录发送请求
					String response = HttpUtils.get(serverNodes);
					System.out.println(response);
					//4.把请求结果写入到同步情况表中
					SyncStatus syncStatus = new SyncStatus();
					syncStatus.setCreateTime(TimeUtils.getDateDetail());
					syncStatus.setServerName(serverNodes.getName());
					syncStatus.setStatus(response.substring(0, 10));
					syncStatus.setServerNodeIp(serverNodes.getIp());
					syncStatusService.addSyncStatus(syncStatus);
				}
			}	
		}catch(Exception e){
			logger.error("删除服务器节点失败",e);
		}
		return "";
	}

	
}

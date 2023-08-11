package com.icegame.sysmanage.controller;


import java.util.*;

import com.alibaba.fastjson.JSON;
import com.icegame.framework.data.Constant;
import com.icegame.framework.utils.ArrayUtils;
import com.icegame.framework.utils.StringUtils;
import com.icegame.sysmanage.entity.OpclServerStatus;
import com.icegame.sysmanage.entity.ServerList;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.service.IServerListService;
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
import com.icegame.sysmanage.entity.MainTain;
import com.icegame.sysmanage.service.IMainTainService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/sysmgr/maintain")
public class MainTainController {

	private static Logger logger = Logger.getLogger(MainTainController.class);

	@Autowired
	private IMainTainService mainTainService;

	@Autowired
	private IServerListService serverListService;

	@RequestMapping("gotoMainTain")
	public String gotoMainTain(){
		return "sysmanage/maintain/maintain";
	}

	@RequestMapping("getMainTainList")
	@ResponseBody
	public String getMainTainList(String reason,int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<MainTain> mainTainList = new ArrayList<MainTain>();
		MainTain mainTain = new MainTain();mainTain.setReason(reason);
		PageInfo<MainTain> pageInfo = this.mainTainService.getMainTainList(mainTain, pageParam);
		mainTainList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(mainTainList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}


	@RequestMapping("/gotoMainTainEdit")
	@ResponseBody
	public String gotoMainTainEdit(@ModelAttribute("editFlag") int editFlag,
			Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		MainTain mainTain = new MainTain();
		if(editFlag == 2){
			mainTain = mainTainService.getMainTainById(id);
			jsonObj = JSONObject.fromObject(mainTain);
		}
		return jsonObj.toString();
	}

	@RequestMapping("/saveMainTain")
	public @ResponseBody Map<String,Object> saveMainTain(@RequestBody MainTain mainTain){

		Map<String,Object> resultMap = new HashMap<String,Object>();

		if(mainTain.getChannel() != null){
			mainTain.setSelectedChannel(ArrayUtils.toStr(mainTain.getChannel()));
		}

		if(mainTain.getSlaveNodes() != null){
			mainTain.setSelectedSlave(ArrayUtils.toStr(mainTain.getSlaveNodes()));
		}

		List<ServerList> serverListList = null;

		try{
			if(mainTain != null && mainTain.getId() != null){
				mainTainService.updateMainTain(mainTain);
				resultMap.put("result", "修改维护列表成功");
				logger.info("修改维护列表");
			}else{//增加
				mainTainService.addMainTain(mainTain);
				resultMap.put("result", "增加维护列表成功");
				logger.info("增加修改维护列表");
			}

			/**
			 * 根据类型维护
			 * 0：默认以前维护逻辑 全服维护
			 * 1：根据slave节点维护
			 * 2：根据channel维护
			 */
			switch (mainTain.getMainType()){
				case 0 : break;
				case 1 :
					serverListList = serverListService.getServerListByMultSlaveId(mainTain.getSelectedSlave());
					mainTain = selectedTargetListAndTargetList(serverListList,mainTain);
					break;
				//case 2 : serverListList = serverListService.getServerListByMultChannel(mainTain.getSelectedChannel());
				//mainTain = selectedTargetListAndTargetList(serverListList,mainTain);
				//break;
				default:
					break;
			}
			List<OpclServerStatus> statusList = mainTainService.saveSync(mainTain);
			if(statusList == null){
				resultMap.put("result", "没有找到任何Slave节点");
				return resultMap;
			}
			if(statusList.size() == 0){
				resultMap.put("result", "同步成功");
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("以下节点同步失败:").append(Constant.NL);
				for(OpclServerStatus s : statusList){
					String hostsMessage = s.getHosts();
					String message = StringUtils.substringBefore(hostsMessage,"#");
					sb.append(message).append(Constant.NL);
				}
				resultMap.put("result", sb.toString());
			}
		}catch(Exception e){
			resultMap.put("result", "操作记录失败");
			logger.error("操作维护列表失败",e);
		}
		return resultMap;
	}

	public MainTain selectedTargetListAndTargetList (List<ServerList> serverList, MainTain mainTain){
		if(serverList.size() > 0){
			StringBuffer selectedTargetList = new StringBuffer();
			JSONObject targetList = new JSONObject();
			for(ServerList server : serverList){
				selectedTargetList.append(server.getServerId()).append(".").append(server.getName()).append(",");
				targetList.put(server.getServerId().toString(),server.getServerId().toString());
			}
			selectedTargetList = selectedTargetList.deleteCharAt(selectedTargetList.length() - 1);
			mainTain.setSelectedTargetsList(selectedTargetList.toString());
			mainTain.setTargetList(targetList.toString());
		}
		return mainTain;
	}

	@RequestMapping("/delMainTain")
	public @ResponseBody Map<String,Object> delMainTain(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(mainTainService.delMainTain(id)){
				resultMap.put("result", "删除维护记录成功");
				logger.info("删除维护记录"+id);
			}
		}catch(Exception e){
			logger.error("删除维护记录失败",e);
		}
		return resultMap;
	}

}

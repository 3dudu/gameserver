package com.icegame.sysmanage.controller;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.icegame.framework.utils.*;
import com.icegame.sysmanage.components.GroupUtils;
import com.icegame.sysmanage.entity.*;
import com.icegame.sysmanage.service.*;
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.validation.constraints.NotNull;

@Controller
public class ServerListController {

	private static Logger logger = Logger.getLogger(ServerListController.class);

	@Autowired
	private IServerListService serverListService;

	@Autowired
	private GroupUtils groupUtils;

	@Autowired
	private IPropertiesService propertiesService;

	@Autowired
	private IAutoOpenServerService autoOpenServerService;

	public String jsonPath;

	public String jsonSuffix;

	@RequestMapping("gotoServerList")
	public String gotoServerList(){
		return "sysmanage/server/serverList";
	}

	@RequestMapping("getServerList")
	@ResponseBody
	public String getServerList(String channel, String name, Long slaveId, int pageNo, int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<ServerList> serverList = new ArrayList<ServerList>();
		ServerList server = new ServerList();server.setChannel(channel);server.setName(name);server.setSlaveId(slaveId);

		/**
		 * 此处添加联运权限验证
		 */
		String hasChannel = groupUtils.getGroupHasChannel();
		if(StringUtils.isNotNull(hasChannel)){
			hasChannel = StringUtils.multFormat(hasChannel);
		}
		server.setHasChannel(hasChannel);

		PageInfo<ServerList> pageInfo = this.serverListService.getServerList(server, pageParam);
		serverList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(serverList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("getServerListOpen")
	@ResponseBody
	public String getServerListOpen(String channel,String name,Long slaveId,int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<ServerList> serverList = new ArrayList<ServerList>();
		ServerList server = new ServerList();server.setChannel(channel);server.setName(name);server.setSlaveId(slaveId);

		/**
		 * 此处添加联运权限验证
		 */
		String hasChannel = groupUtils.getGroupHasChannel();
		if(StringUtils.isNotNull(hasChannel)){
			hasChannel = StringUtils.multFormat(hasChannel);
		}
		server.setHasChannel(hasChannel);

		PageInfo<ServerList> pageInfo = this.serverListService.getServerListOpen(server,pageParam);
		serverList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(serverList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getTargetsList");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("/getServerListByIP")
	@ResponseBody
	public String getServerListByIP(String ip){
		List<ServerList> serverList = serverListService.getServerByIP(ip);
		JSONArray jsonArr = JSONArray.fromObject(serverList);
		return jsonArr.toString();
	}


	/**
	 *
	 * @param editFlag
	 * @param serverId
	 * @param model
	 * @return
	 *
	 * ----------------------------------------
	 * @date 2019-06-15 10:29:51
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@RequestMapping("/gotoServerListEdit")
	@ResponseBody
	public String gotoServerListEdit(@ModelAttribute("editFlag") int editFlag,
			Long serverId,Model model){
		JSONObject jsonObj = new JSONObject();
		ServerList server = new ServerList();
		if(editFlag == 2){
			server = serverListService.getServerById(serverId);
			if(server != null){
				server.setBeginTime(TimeUtils.stampToDateWithMill(server.getBeginTime()));
				server.setUpdateTime(TimeUtils.stampToDateWithMill(server.getUpdateTime()));
				server.setPassTime(TimeUtils.stampToDateWithMill(server.getPassTime()));
			}
			jsonObj = JSONObject.fromObject(server);
		}
		return jsonObj.toString();
	}

	@RequestMapping("/checkExistServerList")
	public @ResponseBody String checkExistServerList(String name){
		ServerList server = new ServerList();
		server.setName(name);
		List<ServerList> serverList = serverListService.checkExistServerList(server);
		if(serverList.size() > 0) {
			return "1";
		}else {
			return "0";
		}
	}

	@RequestMapping("/saveServerList")
	public @ResponseBody Map<String,Object> saveServerList(@RequestBody ServerList server){

		Map<String,Object> resultMap = new HashMap<String,Object>();

		String beginTime = TimeUtils.dateToStampWithDetail(server.getBeginTime());
		String passTime = TimeUtils.dateToStampWithDetail(server.getPassTime());
		Long pbValue = Long.valueOf(passTime) - Long.valueOf(beginTime);

		if(pbValue <= 0){
			resultMap.put("result","添加失败。开始时间不能大于结束时间；还没开始就已经结束，这是我们不能接受的！");
			return resultMap;
		}

		resultMap = serverListService.syncServer(server);

		return resultMap;

	}
	/**
	 * 2019-05-20 15:22:26
	 * 去掉区服列表的删除功能
	 */
//	@RequestMapping("/delServerList")
//	public @ResponseBody Map<String,Object> delServerList(Long serverId){
//
//		Map<String,Object> resultMap = new HashMap<String,Object>();
//
//		resultMap = serverListService.syncDelete(serverId);
//
//		return resultMap;
//	}

	/**
	 * 配置文件全局刷新
	 * @param server
	 * @throws FileNotFoundException
	 */
	protected void refreshAll(ServerList server) throws FileNotFoundException{

		jsonPath = propertiesService.getJsonPath();

		jsonSuffix = propertiesService.getJsonSuffix();

		List<ServerList> channelList = serverListService.getAllChannelList();
		for(ServerList channelServer: channelList){
			//全局刷新
			File file = new File(jsonPath+channelServer.getChannel()+jsonSuffix);
			if (!file.exists()) { //检查是否存在当前channel的json文件 : ?创建之
				try {
					file.createNewFile();
				} catch (IOException e) {
					logger.error("创建文件异常"+e);
					e.printStackTrace();
				}
			}
			JSONArray array = new JSONArray();
			JSONObject object = new JSONObject();
			//然后查询当前channel的服务器列表

			List<ServerList> serverList = serverListService.download(channelServer);
			if(serverList.size() <= 0){
				server = new ServerList();
			}else{
				for(int i = 0 ; i < serverList.size();i++){
					if(serverList.get(i).getPort().equals("")){
						serverList.get(i).setPort("8080");
					}
					object = JSONObject.fromObject(serverList.get(i));
					object.remove("channel");object.remove("ver");object.remove("group");
					array.add(object);
				}
			}
			@SuppressWarnings("resource")
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			//查询出来之后把它写入文件中
			ps.println(JsonUtils.formatJson(array.toString()));
			logger.info("全局配置刷新");
		}
	}

	@RequestMapping("/suggest")
	public @ResponseBody Map<String,Object> suggest(Long serverId, int suggest){

		Map<String,Object> resultMap = new HashMap<String,Object>();

		if(suggest != 0 && suggest !=1){
			resultMap.put("ret",-1);
			resultMap.put("msg","参数不合法");
			return resultMap;
		}

		if(serverId == null){
			resultMap.put("ret",-1);
			resultMap.put("msg","serverId不可以为null");
			return resultMap;
		}

		/**
		 * 设置推荐服操作，判断当前channel是否有 开启了自动开服，如果有，直接return
		 */
		if(suggest == 1){
			ServerList server = serverListService.getServerById(serverId);
			List<AutoOpenServer> autoOpenServerList = autoOpenServerService.existsEnableAutoOpenServer(server.getChannel());
			if(autoOpenServerList.size() > 0){
				resultMap.put("ret",-4);
				resultMap.put("msg","操作失败！\r\n当前channel[" + server.getChannel() + "]开启了自动开服，要设置推荐服，必须先关闭此channel的自动开服功能");
				return resultMap;
			}
		}


		try{
			ServerList serverList = new ServerList();
			serverList.setServerId(serverId);
			serverList.setIsSuggest(suggest);
			if(serverListService.suggest(serverList)){
				resultMap.put("ret",1);
				resultMap.put("msg","操作成功");
			}else{
				resultMap.put("ret",-2);
				resultMap.put("msg","操作失败");
			}

		}catch (Exception e){
			logger.error("设置推荐服异常，serverId ：" + serverId + "，suggest ：" + suggest);
			logger.error(e.getMessage());
			resultMap.put("ret",-3);
			resultMap.put("msg","操作异常");
		}

		return resultMap;

	}

}

package com.icegame.gm.controller;



import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.AuthorizeUtil;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.StringUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.gm.entity.AgentRecharge;
import com.icegame.gm.service.IAgentRechargeService;
import com.icegame.gm.service.IGmlogsService;
import com.icegame.sysmanage.components.GroupUtils;
import com.icegame.sysmanage.entity.ServerList;
import com.icegame.sysmanage.service.IServerListService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/sysmgr/gm")
public class AgentRechargeController {

	private static Logger logger = Logger.getLogger(AgentRechargeController.class);

	@Autowired
	private IAgentRechargeService agentRechargeService;

	@Autowired
	private GroupUtils groupUtils;

	@Autowired
	private IServerListService serverListService;

	@Autowired
	private AuthorizeUtil authorizeUtil;



	@RequestMapping("gotoAgentRecharge")
	public String gotoServerList(){
		return "sysmanage/gm/agentrecharge";
	}

	@RequestMapping("getAgentRechargeList")
	@ResponseBody
	public String getAgentRecharge(String loginName, String moneyType,
								Long serverId, Long playerId, String playerName, Integer proIdx,
							    Double money, String platform, Integer orderType,
								String channel, String channelCode, Integer status,
								String orderId, String startTime,String passTime,
								Integer pageNo, Integer pageSize){
		JSONObject resultJson = new JSONObject();
		boolean hasAuthorize = authorizeUtil.hasAuthorize("GM工具","代理充值");
		if(!hasAuthorize){
			resultJson.put("ret","201");
			resultJson.put("msg","没有权限操作");
			return resultJson.toString();
		}
		//前端传过来（"yyyy-MM-dd HH:mm:ss"），需要转化为毫秒数
		startTime = TimeUtils.checkDateDetail(startTime,passTime).get("startDate");
		passTime = TimeUtils.checkDateDetail(startTime,passTime).get("endDate");
		startTime = TimeUtils.dateToStampWithDetail(startTime);
		passTime = TimeUtils.dateToStampWithDetail(passTime);

		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);

		PageInfo<AgentRecharge> pageInfo;
		// 获取分页数据总和
		List<AgentRecharge> lists;

		AgentRecharge agentRecharge = new AgentRecharge(loginName,serverId,playerId,playerName,proIdx,money,moneyType,platform,orderType,channel,
				channelCode, status, orderId);

		pageInfo = agentRechargeService.getAgentRechargeList(agentRecharge,Long.valueOf(startTime),
				Long.valueOf(passTime),pageParam);
		lists = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(lists);
		// 获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

//	@RequestMapping("getAgentRechargeById")
//	@ResponseBody
//	public String getAgentRechargeById(@RequestParam("editFlag") int editFlag, Integer id){
//
//		JSONObject jsonObj = new JSONObject();
//		AgentRecharge agentRecharge;
//		if (editFlag == 2 && id != null){
//			agentRecharge = agentRechargeService.getAgentRechargeById(id);
//			jsonObj = JSONObject.fromObject(agentRecharge);
//		}
//		return jsonObj.toString();
//	}

	@RequestMapping("saveAgentRecharge")
	@ResponseBody
	public Map<String,Object> saveAgentRecharge(@RequestBody AgentRecharge agentRecharge){
		Map<String,Object> resultJson = new HashMap<>();
		boolean hasAuthorize = authorizeUtil.hasAuthorize("GM工具","代理充值");
		if(!hasAuthorize){
			resultJson.put("ret","201");
			resultJson.put("msg","没有权限操作");
			return resultJson;
		}
		return agentRechargeService.saveAgentRecharge(agentRecharge);

	}

	@RequestMapping("syncAgentRechargeToPay")
	@ResponseBody
	public Map<String,Object> syncAgentRechargeToPay(Long id){
		Map<String,Object> resultJson = new HashMap<>();
		boolean hasAuthorize = authorizeUtil.hasAuthorize("GM工具","代理充值");
		if(!hasAuthorize){
			resultJson.put("ret","201");
			resultJson.put("msg","没有权限操作");
			return resultJson;
		}
		return agentRechargeService.syncAgentRechargeToPay(id);

	}


	/**
	 *
	 * @param request
	 * @param response
	 * @param serverId
	 * @param playerId
	 * @param userId
	 * @param proIdx
	 * @param money
	 * @param platform
	 * @param orderType
	 * @param channel
	 * @param channelCode
	 * @param status
	 * @param orderId
	 * @param startTime
	 * @param passTime
	 * @throws Exception
	 */
	@RequestMapping("exportTable")
	public void exportTableOneMonth(HttpServletRequest request, HttpServletResponse response,
									String loginName, String moneyType,
									Long serverId, Long playerId, String playerName, Integer proIdx,
									Double money, String platform, Integer orderType,
									String channel, String channelCode, Integer status,
									String orderId, String startTime, String passTime) throws Exception {
		boolean hasAuthorize = authorizeUtil.hasAuthorize("GM工具","代理充值");
		if(!hasAuthorize){
			return ;
		}
		startTime = TimeUtils.dateToStampWithDetail(startTime);
		passTime = TimeUtils.dateToStampWithDetail(passTime);

		AgentRecharge agentRecharge = new AgentRecharge(loginName,serverId,playerId,playerName,proIdx,money,moneyType,platform,orderType,channel,
				channelCode, status, orderId);
		Long curStartTime = Long.valueOf(startTime);
		Long curPassTime = Long.valueOf(passTime);

		Long montyMills = 7L * 86400 * 1000;

		// 这边对时间做二次验证，如果时间大于7天，就不做导出操作
		if(curPassTime - curStartTime >  montyMills){
			logger.info("startTime:" + startTime + ",passTime:" + passTime + ", 差为" + (curPassTime - curStartTime ) + ",时间差不符合规范，操作无效...");
			return;
		}

		String hasChannel = groupUtils.getGroupHasChannel();

		List<ServerList> allServerList = null;

		if(StringUtils.isNotNull(hasChannel)){

			StringBuffer sb = new StringBuffer();

			hasChannel = StringUtils.multFormat(hasChannel);

			ServerList serverList = new ServerList();

			serverList.setHasChannel(hasChannel);

			allServerList = serverListService.getAllChannelServerListNoPage(serverList);

			sb.append("(");
			for(int i = 0 ; i < allServerList.size() ; i++){
				sb.append("'").append(allServerList.get(i).getServerId()).append("'").append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
			agentRecharge.setMultServerId(sb.toString());

		}


		InputStream is=agentRechargeService.export(agentRecharge, Long.valueOf(startTime), Long.valueOf(passTime));
		response.setContentType("application/octet-stream;charset=UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename=AgentRecharge.xls");
		ServletOutputStream output = response.getOutputStream();
		IOUtils.copy(is,output);
	}

}

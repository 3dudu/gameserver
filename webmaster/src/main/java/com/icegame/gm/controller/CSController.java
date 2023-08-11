package com.icegame.gm.controller;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.*;
import com.icegame.gm.entity.*;
import com.icegame.gm.service.*;
import com.icegame.gm.util.Misc;
import com.icegame.sysmanage.dto.UserDto;
import com.icegame.sysmanage.entity.Group;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.entity.User;
import com.icegame.sysmanage.service.IGroupService;
import com.icegame.sysmanage.service.IServerListService;
import com.icegame.sysmanage.service.ISlaveNodesService;
import com.icegame.sysmanage.service.IUserService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sysmgr/cs")
public class CSController {
	
	private static final Map<String,Object> retMap = new HashMap<String,Object>();
			
	@Autowired
	private IJMailCrossServerService jmcsService;
	
	@Autowired
	private IJPreinstallMessageService jpmServerice;

	@Autowired
	private IJAutoreplyMessageService jamServerice;

	@Autowired
	private IServerListService serverListservice;
	
	@Autowired
	private IJWorldMessageService jMessageService;
	
	@Autowired
	private IJCsMessageService jcmService;
	
	@Autowired
	private IGroupService groupService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IJReduceLogService reduceLogService;

	@Autowired
	private IJEvaluateService jEvaluateService;

	@Autowired
	private IJQuestionnaireService jQuestionnaireService;

	@Autowired
	private ISlaveNodesService slaveNodesService;
		
	private static Logger logger = Logger.getLogger(CSController.class);
	
	private static String host = "";
	
	private static String IP = "";

	private static int PORT = 0;
	
	@RequestMapping("/gotoWordMessage")
	public String gotoGM(){	
		return "sysmanage/customservice/jworldmessage";   
	}
	
	@RequestMapping("/gotoReduceLog")
	public String gotoReduceLog(){
		return "sysmanage/customservice/reducelog";   
	}

	@RequestMapping("/gotoEvaluate")
	public String gotoEvaluate(){
		return "sysmanage/customservice/evaluate";
	}

	@RequestMapping("/gotoQuestionnaire")
	public String gotoQuestionnaire(){
		return "sysmanage/customservice/questionnaire";
	}
	
	@RequestMapping("/gotoChatting")
	public String chatting(){	
		return "sysmanage/customservice/chatting";   
	}
	
	@RequestMapping("/gotoPreinstallMessage")
	public String gotoPreinstallMessage(){	
		return "sysmanage/customservice/preinstallmessage";   
	}

	@RequestMapping("/gotoAutoreplyMessage")
	public String gotoAutoreplyMessage(){
		return "sysmanage/customservice/autoreplymessage";
	}
	
	@RequestMapping("/gotoMailMessage")
	public String gotoMailCrossService(){	
		return "sysmanage/customservice/jmailmessage";   
	}
	
	@RequestMapping("/gotoQuestion")
	public String gotoQuestion(){	
		return "sysmanage/customservice/question";   
	}

	
	@RequestMapping("/getWorldMessageList")
	public @ResponseBody String getWorldMessageList(Long playerId,Long serverId,String startDate,String endDate,String playerName,String content,int pageNo,int pageSize){
		if (startDate.equals("") && endDate.equals("")) {
			startDate = TimeUtils.getStartTime();
			endDate = TimeUtils.getEndTime();
		}
		Long startTime = null;
		Long passTime = null;
		try{
			startTime = Long.valueOf(TimeUtils.dateToStampWithDetail(startDate));
			passTime = Long.valueOf(TimeUtils.dateToStampWithDetail(endDate));
		}catch (Exception e){
			logger.info(e.toString());
		}

		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		List<JWorldMessage> jmessageList = new ArrayList<JWorldMessage>();
		PageInfo<JWorldMessage> pageInfo = jMessageService.getJMessageList(new JWorldMessage(serverId,playerId,playerName,content,startTime,passTime), pageParam);
		jmessageList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(jmessageList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.searchMessage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("/getEvaluateList")
	public @ResponseBody String getEvaluateList(Long npcId,String npcName,int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		List<JEvaluate> msgList = new ArrayList<JEvaluate>();
		JEvaluate jEvaluate = new JEvaluate();jEvaluate.setNpcId(npcId);jEvaluate.setNpcName(npcName);
		PageInfo<JEvaluate> pageInfo = jEvaluateService.getJEvaluateList(jEvaluate,pageParam);
		msgList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(msgList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.searchMessage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("/editEvaluate")
	public @ResponseBody String editEvaluate(String key, String value, Long npcId){
		Map<String,Object> retMap = new HashMap<String,Object>();
	    if(StringUtils.isNotNull(key) && StringUtils.isNotNull(value) && null != npcId){
            JEvaluate jEvaluate = new JEvaluate();
            jEvaluate.setKey(key);
            jEvaluate.setValue(value);
            jEvaluate.setNpcId(npcId);
            jEvaluateService.updateAvalue(jEvaluate);

            int question = StringUtils.getId(key,"question");
            int option = StringUtils.getId(key,"option");
            int val = Integer.valueOf(value);
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("question",question);
            map.put("option",option);
			map.put("npcId",npcId);
            map.put("val",val);
			map.put("sign",Misc.md5("editEvaluate"+question+option+val));
			String data = JSONObject.fromObject(map).toString();
			List<SlaveNodes> slaveNodesList = slaveNodesService.getSlaveNodesListNoPage();
			logger.info("--- - | editEvaluate | - --->" + JSONObject.fromObject(map).toString());
			String result = "";
			for(SlaveNodes sn : slaveNodesList){
				host = "http://" + (StringUtils.isNull(sn.getNip())?sn.getIp():sn.getIp()) + ":" + sn.getPort() + "/gm/editEvaluate";
				result = HttpUtils.jsonPost(host, data);
			}
			logger.info("--- - | editEvaluate | - --->" + result);
            retMap.put("ret","1");
        }else{
            retMap.put("ret","-1");
        }
		return JSONObject.fromObject(retMap).toString();
	}

    @RequestMapping("/getEvaluateNpcId")
    public @ResponseBody String getEvaluateNpcId(){
        List<JEvaluate> npcIds = jEvaluateService.getEvaluateNpcId(new JEvaluate());
        return JSONArray.fromObject(npcIds).toString();
    }

    @RequestMapping("/getEvaluateNpcName")
    public @ResponseBody String getEvaluateNpcName(){
        List<JEvaluate> npcNames = jEvaluateService.getEvaluateNpcName(new JEvaluate());
        return JSONArray.fromObject(npcNames).toString();
    }

	@RequestMapping("/getReduceLogList")
	public @ResponseBody String getReduceLogList(Long playerId,Long serverId,String startDate,String endDate,String playerName,int pageNo,int pageSize){
		if (startDate.equals("") && endDate.equals("")) {
			startDate = TimeUtils.getStartTime();
			endDate = TimeUtils.getEndTime();
		}
		Long startTime = null;
		Long passTime = null;
		try{
			startTime = Long.valueOf(TimeUtils.dateToStampWithDetail(startDate))-7*24*3600000;
			passTime = Long.valueOf(TimeUtils.dateToStampWithDetail(endDate));
		}catch (Exception e){
			logger.info(e.toString());
		}
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		List<JReduceLog> jmessageList = new ArrayList<JReduceLog>();
		PageInfo<JReduceLog> pageInfo = reduceLogService.getJReduceLogList(new JReduceLog(serverId,playerId,playerName,startTime,passTime), pageParam);
		jmessageList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(jmessageList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.searchMessage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("/getJQuestionnairePlayerId")
	public @ResponseBody String getJQuestionnairePlayerId(){
		List<JQuestionnaire> playerIds = jQuestionnaireService.getJQuestionnairePlayerId(new JQuestionnaire());
		return JSONArray.fromObject(playerIds).toString();
	}

	@RequestMapping("/getQuestionnaireList")
	public @ResponseBody String getQuestionnaireList(Long playerId,String playerName, int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		JQuestionnaire jqn = new JQuestionnaire();
		jqn.setPlayerId(playerId);
		jqn.setPlayerName(playerName);
		List<JQuestionnaire> jQuestionnairesList = new ArrayList<JQuestionnaire>();
		PageInfo<JQuestionnaire> pageInfo = jQuestionnaireService.getJQuestionnaireList(jqn,pageParam);
		jQuestionnairesList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(jQuestionnairesList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.searchMessage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

    @RequestMapping("/exportTable")
    public void exportTable(HttpServletRequest request, HttpServletResponse response,int flag, String playerIds) throws Exception {
	    ExportTable et = new ExportTable();
	    et.setIds(playerIds);et.setFlag(flag);
	    InputStream is=jQuestionnaireService.getInputStreamRetention(et);
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=questionAnswer.xls");
        ServletOutputStream output = response.getOutputStream();
        IOUtils.copy(is,output);
    }
	
	@RequestMapping("/getMailMessageList")
	public @ResponseBody String getMailMessageList(Long senderId,String senderName,Long senderServerId,
			  Long receiverId, String receiverName, Long receiverServerId,
			 String content,String startDate,String endDate,int pageNo,int pageSize){
		if (startDate.equals("") && endDate.equals("")) {
			startDate = TimeUtils.getStartTime();
			endDate = TimeUtils.getEndTime();
		}
		Long startTime = null;
		Long passTime = null;
		try{
			startTime = Long.valueOf(TimeUtils.dateToStampWithDetail(startDate));
			passTime = Long.valueOf(TimeUtils.dateToStampWithDetail(endDate));
		}catch (Exception e){
			logger.info(e.toString());
		}
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		List<JMailMessage> jmessageList = new ArrayList<JMailMessage>();
		PageInfo<JMailMessage> pageInfo = jmcsService.getJMCSList(new JMailMessage(senderId,senderName,senderServerId,"",receiverId,receiverName,receiverServerId,"",content,startTime,passTime), pageParam);
		jmessageList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(jmessageList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.searchMessage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("/getChattingMessageList")
	public @ResponseBody String getChattingMessageList(Long playerId,Long serverId,Long csId){
		String startDate = "";
		String endDate = "";
		if (startDate.equals("") && endDate.equals("")) {
			startDate = TimeUtils.getStartTime();
			endDate = TimeUtils.getEndTime();
		}
		//startDate = TimeUtils.checkDateDetail(startDate, endDate).get("startDate");
		//endDate = TimeUtils.checkDateDetail(startDate, endDate).get("endDate");
		Long startTime = Long.valueOf(TimeUtils.dateToStampWithDetail(startDate))-7*24*3600000;
		Long passTime = Long.valueOf(TimeUtils.dateToStampWithDetail(endDate));

		/**
		 * 如果客服点击的是自己认领的问题，那么就去掉新消息标识
		 */
		JCsMessage jcm = new JCsMessage();jcm.setServerId(serverId);jcm.setPlayerId(playerId);
		if(csId == jcmService.getCsIdBySPId(jcm)){
			jcmService.updateJCsMessage(new JCsMessage(csId,playerId));
		}

		List<JCsMessage> jmessageList = jcmService.getJCsMessageListChatting(new JCsMessage(csId,playerId,serverId,startTime,passTime));
		if(jmessageList.size() > 0){
			for(JCsMessage jCsMessage : jmessageList){
				jCsMessage.setStrCreateTime(TimeUtils.stampToDateWithMill(jCsMessage.getCreateTime()));
			}
		}
		JSONArray jsonArr = JSONArray.fromObject(jmessageList);
		return jsonArr.toString();
	}
	
	@RequestMapping("/getNewMessage")
	public @ResponseBody String getNewMessage(Long csId){
		String startDate = "";
		String endDate = "";
		if (startDate.equals("") && endDate.equals("")) {
			startDate = TimeUtils.getStartTime();
			endDate = TimeUtils.getEndTime();
		}

		Long startTime = Long.valueOf(TimeUtils.dateToStampWithDetail(startDate))-7*24*3600000;
		Long passTime = Long.valueOf(TimeUtils.dateToStampWithDetail(endDate));

		JCsMessage jcm = new JCsMessage();
		jcm.setCsId(csId);
		jcm.setStartTime(startTime);
		jcm.setPassTime(passTime);

		List<JCsMessage> jmessageList = jcmService.getNewMessage(jcm);
		JSONArray jsonArr = JSONArray.fromObject(jmessageList);
		return jsonArr.toString();
	}
	
	@RequestMapping("/getOldMessage")
	public @ResponseBody String getOldMessage(Long csId){
		String startDate = "";
		String endDate = "";
		if (startDate.equals("") && endDate.equals("")) {
			startDate = TimeUtils.getStartTime();
			endDate = TimeUtils.getEndTime();
		}

		Long startTime = Long.valueOf(TimeUtils.dateToStampWithDetail(startDate))-7*24*3600000;
		Long passTime = Long.valueOf(TimeUtils.dateToStampWithDetail(endDate));

		JCsMessage jcm = new JCsMessage();
		jcm.setCsId(csId);
		jcm.setStartTime(startTime);
		jcm.setPassTime(passTime);

		List<JCsMessage> jmessageList = jcmService.getOldMessage(jcm);
		JSONArray jsonArr = JSONArray.fromObject(jmessageList);
		return jsonArr.toString();
	}
	
	@RequestMapping("/send")
	public @ResponseBody String send(Long csId,Long playerId,Long serverId,String csName,String playerName,String serverName,String content){
		JCsMessage jcm = new JCsMessage(csId,playerId,serverId,csName,playerName,serverName,System.currentTimeMillis(),System.currentTimeMillis(),content,1,0l,0l);
		jcmService.addJCsMessage(jcm);
		jcmService.updateProcess(jcm);
		boolean flag = jcmService.sendMail(jcm);
		/*本地数据库更新之后向salve发送数据，然后通通过slave转发到客户端*/
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("csId", csId);
		map.put("playerId", playerId);
		map.put("serverId", serverId);
		map.put("csName", csName);
		map.put("playerName", playerName);
		map.put("serverName", serverName);
		map.put("content", content);
		map.put("sendMail", flag);
		String sign = Misc.md5("reply"+csId+playerId);
		map.put("sign", sign);
		IP = StringUtils.isNull(serverListservice.getServerById(serverId).getNip())?serverListservice.getServerById(serverId).getIp():serverListservice.getServerById(serverId).getNip();
		PORT = serverListservice.getServerById(serverId).getSlavePort();
		host = "http://" + IP + ":" + PORT + "/cs/reply";
		String data = JSONObject.fromObject(map).toString();
		logger.info("--- - | reply-send | - --->" + data);
		String result = HttpUtils.jsonPost(host, data);
		logger.info("--- - | reply-return | - --->" + result);
		
		/** 这边向客服回复完消息之后，把sendMail 设置为 1 也就是说不会在向客户端发送邮件提醒了  ，因为这边是一个订单的第一次回复才会给玩家进行邮件提醒 */
		if(flag){
			jcmService.closeSendMail(jcm);
		}

		return result;
	}

	@RequestMapping("/isClaimed")
	public @ResponseBody String isClaimed(Long playerId,Long serverId){
		JCsMessage jcm = new JCsMessage();
		jcm.setServerId(serverId);
		jcm.setPlayerId(playerId);
		boolean flag = jcmService.isClaimed(jcm);
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("result",flag);
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping("/getJCsMessageList")
	public @ResponseBody String getJCsMessageList(int status,String playerName,Long playerId,Long serverId, Long csId, String startDate,String endDate,int pageNo,int pageSize){
		if (startDate.equals("") && endDate.equals("")) {
			startDate = TimeUtils.getStartTime();
			endDate = TimeUtils.getEndTime();
		}
		startDate = TimeUtils.checkDate(startDate, endDate).get("startDate");
		endDate = TimeUtils.checkDate(startDate, endDate).get("endDate");
		Long startTime = Long.valueOf(TimeUtils.dateToStampWithDetail(startDate))-7*24*3600000;
		Long passTime = Long.valueOf(TimeUtils.dateToStampWithDetail(endDate));
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		List<JCsMessage> jql = new ArrayList<JCsMessage>();
		PageInfo<JCsMessage> pageInfo = jcmService.getJCsMessageList(new JCsMessage(csId,playerId,serverId,"",playerName,"",System.currentTimeMillis(),"",1,status,startTime,passTime), pageParam);
		jql = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(jql);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("/claim")
	public @ResponseBody String claim(Long msgId, Long csId, String csName ,int flag, Long statusId){
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(flag == 1){
		//说明是 消息 完成之后执行的指派，所以先需要打开问题
			JCsMessage jcm = new JCsMessage();jcm.setId(statusId);
			jcmService.open(jcm);
		}
		boolean operateFlag = jcmService.claimQuestion(new JCsMessage(msgId,csId,csName));
		retMap.put("flag", operateFlag);
		return JSONObject.fromObject(retMap).toString();
	}

	@RequestMapping("/quickClaim")
	public @ResponseBody String quickClaim(){
		//首先找出所有未被认领的问题
		Map<String,Object> retMap = new HashMap<String,Object>();
		/*boolean isOnWork = TimeUtils.isOnWork(System.currentTimeMillis());
		if(!isOnWork){
			retMap.put("ret", false);
			retMap.put("msg", "客服小姐姐处于休息中，请在上班时间执行此操作");
			return JSONObject.fromObject(retMap).toString();
		}*/
		User user;
		List<JCsMessage> jcmList = jcmService.getQuestionListNoclaim(new JCsMessage());
		if(jcmList.size() > 0){
			for(JCsMessage jcm :jcmList){
				user = jcmService.getEnableCs(new JCsMessage());
				if(null != user){
					jcm.setCsId(user.getUserId());
					jcm.setCsName(user.getUserName());
					jcmService.quickClaim(jcm);
				}else{
					retMap.put("ret", false);
					retMap.put("msg", "当前无客服在线");
					return JSONObject.fromObject(retMap).toString();
				}
			}
			retMap.put("ret", true);
			retMap.put("msg", "分配成功");
		}else{
			retMap.put("ret", false);
			retMap.put("msg", "当前不存在未指派的问题");
		}
		return JSONObject.fromObject(retMap).toString();
	}
	
	@RequestMapping("/finish")
	public @ResponseBody String finish(Long id){
		Map<String,Object> retMap = new HashMap<String,Object>();
		JCsMessage jcm = new JCsMessage();jcm.setId(id);
		jcm.setCreateTime(System.currentTimeMillis());
		boolean flag = jcmService.finish(jcm);
		
		jcm = jcmService.getJCsMessageByMsgId(jcm);
		jcmService.closeSendMail(jcm);
		retMap.put("flag", flag);
		return JSONObject.fromObject(retMap).toString();
	}
	
	@RequestMapping("/open")
	public @ResponseBody String open(Long id){
		Map<String,Object> retMap = new HashMap<String,Object>();
		JCsMessage jcm = new JCsMessage();jcm.setId(id);
		boolean flag = jcmService.open(jcm);
		
		jcm = jcmService.getJCsMessageByMsgId(jcm);
		jcmService.openSendMail(jcm);
		retMap.put("flag", flag);
		return JSONObject.fromObject(retMap).toString();
	}

	/**
	 *
	 * @param id
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 12:10:12
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@RequestMapping("/getJCsMessageById")
	public @ResponseBody String getJCsMessageById(Long id){
		List<JCsMessage> jcmList= jcmService.getJCsMessageById(id);
		if (jcmList.size() > 0){
			for(JCsMessage jCsMessage : jcmList){
				jCsMessage.setStrCreateTime(TimeUtils.stampToDateWithMill(jCsMessage.getCreateTime()));
				jCsMessage.setStrUpdateTime(TimeUtils.stampToDateWithMill(jCsMessage.getUpdateTime()));
			}
		}
		return JSONArray.fromObject(jcmList).toString();
	}
	
	@RequestMapping("/getCsInfoForSearch")
	public @ResponseBody String getQuestionListNoPage(){
		List<JCsMessage> jcmList = jcmService.getCsInfoForSearch(new JCsMessage());
		JSONArray jsonArr = JSONArray.fromObject(jcmList);
		return jsonArr.toString();
	}
	
	@RequestMapping("/getServerInfoForSearch")
	public @ResponseBody String getQuestionServerList(){
		List<JCsMessage> jcmList = jcmService.getServerInfoForSearch(new JCsMessage());
		JSONArray jsonArr = JSONArray.fromObject(jcmList);
		return jsonArr.toString();
	}
	
	@RequestMapping("/getCsList")
	public @ResponseBody String getCsList(){
		Long userId = UserUtils.getCurrrentUserId();
		Group group = groupService.getGroupIdByUserId(userId);
		UserDto userDto = new UserDto();
		userDto.setGroupId(group.getGroupId());
		List<UserDto> userList = userService.getCsList(userDto);
		return JSONArray.fromObject(userList).toString();
	}

	@RequestMapping("/getCsMgrList")
	public @ResponseBody String getCsMgrList(){
		Long userId = UserUtils.getCurrrentUserId();
		Group group = groupService.getGroupIdByUserId(userId);
		UserDto userDto = new UserDto();
		userDto.setGroupId(group.getGroupId());
		List<UserDto> userList = userService.getCsMgrList(userDto);
		return JSONArray.fromObject(userList).toString();
	}

	/*********************************************************
	 *                预设消息
	 *
	 ********************************************************/

	/*预设消息*/
	@RequestMapping("/getPreinstallMessage")
	public @ResponseBody String getJPreinstallMessage(int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		List<JPreinstallMessage> jpmList = new ArrayList<JPreinstallMessage>();
		PageInfo<JPreinstallMessage> pageInfo = jpmServerice.getJPreinstallMessageList(new JPreinstallMessage(), pageParam);
		jpmList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(jpmList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("/gotoPreinstallMessageEdit")
	@ResponseBody
	public String gotoServerListEdit(@ModelAttribute("editFlag") int editFlag,
			Long id){
		JSONObject jsonObj = new JSONObject();
		JPreinstallMessage jpm = new JPreinstallMessage();
		if(editFlag == 2){
			jpm = jpmServerice.getJPreinstallMessageById(id);
			jsonObj = JSONObject.fromObject(jpm);
		}
		return jsonObj.toString();   
	}
	
	@RequestMapping("/delPreinstallMessage")
	@ResponseBody
	public String delPreinstallMessage(Long id){
		Map<String,Object> retMap = new HashMap<String,Object>();
		try{
			if(jpmServerice.delJPreinstallMessage(id))
				return JsonUtils.success("删除用户成功");
				logger.info("删除预设消息成功");
		}catch(Exception e){
			logger.error("删除预设消息失败",e);
		}
		retMap.put("flag", "删除成功");
		return JSONObject.fromObject(retMap).toString();   
	}
	
	@RequestMapping("/saveJPreinstallMessage")
	public @ResponseBody Map<String,Object> saveJPreinstallMessage(@RequestBody JPreinstallMessage jpm){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(jpm.getId()!=null){
				jpmServerice.updateJPreinstallMessage(jpm);
				logger.info("修改预设消息");
				resultMap.put("result", "修改预设消息成功");
			}else{//增加
				jpmServerice.addJPreinstallMessage(jpm);
				logger.info("添加预设消息");
				resultMap.put("result", "增加预设消息成功");
			}	
		}catch(Exception e){
			resultMap.put("result", "编辑预设消息失败");
			logger.error("编辑预设消息失败",e);
		}		
		return resultMap;
	}

	/*********************************************************
	 *                离线消息
	 *
	 ********************************************************/
	@RequestMapping("/getAutoreplyMessage")
	public @ResponseBody String getJAutoreplyMessage(int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		List<JAutoreplyMessage> jamList = new ArrayList<JAutoreplyMessage>();
		PageInfo<JAutoreplyMessage> pageInfo = jamServerice.getJAutoreplyMessageList(new JAutoreplyMessage(),pageParam);
		jamList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(jamList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("/gotoAutoreplyMessageEdit")
	@ResponseBody
	public String gotoAutoreplyMessageEdit(@ModelAttribute("editFlag") int editFlag,
									 Long id){
		JSONObject jsonObj = new JSONObject();
		JAutoreplyMessage jam = new JAutoreplyMessage();
		if(editFlag == 2){
			jam = jamServerice.getJAutoreplyMessageById(id);
			jsonObj = JSONObject.fromObject(jam);
		}
		return jsonObj.toString();
	}

	@RequestMapping("/delAutoreplyMessage")
	@ResponseBody
	public String delAutoreplyMessage(Long id){
		Map<String,Object> retMap = new HashMap<String,Object>();
		try{
			if(jamServerice.delJAutoreplyMessage(id))
				return JsonUtils.success("删除自动回复消息成功");
			logger.info("删除自动回复消息成功");
		}catch(Exception e){
			logger.error("删除自动回复消息失败",e);
		}
		retMap.put("flag", "删除成功");
		return JSONObject.fromObject(retMap).toString();
	}

	@RequestMapping("/saveJAutoreplyMessage")
	public @ResponseBody Map<String,Object> saveJAutoreplyMessage(@RequestBody JAutoreplyMessage jam){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(jam.getId()!=null){
				jamServerice.updateJAutoreplyMessage(jam);
				logger.info("修改自动回复消息消息");
				resultMap.put("result", "修改自动回复消息成功");
			}else{//增加
				jamServerice.addJAutoreplyMessage(jam);
				logger.info("添加自动回复消息消息");
				resultMap.put("result", "增加自动回复消息成功");
			}
		}catch(Exception e){
			resultMap.put("result", "编辑自动回复消息失败");
			logger.error("编辑自动回复消息失败",e);
		}
		return resultMap;
	}

	@RequestMapping("/mergePlayerBanDay")
	@ResponseBody
	public Object mergePlayerBanDay(Long serverId,Long playerId,String key,int value,int flag){
		JCsMessage jcm = new JCsMessage();
		jcm.setServerId(serverId);
		jcm.setPlayerId(playerId);
		Long time = 0L;
		/**先修改master数据库的记录*/
		switch(key){
			case "banTime" : jcm.setBanTime(time); break;
			case "chattingBanTime" : 
				if(flag == 0){	//禁言
					time = System.currentTimeMillis() + TimeUtils.MINUTE_TIME * value;
					jcm.setChattingBanTime(time) ;
				}else if(flag == 1){ //解禁
					jcm.setChattingBanTime(time) ;
				}
				break;
			case "mailBanTime" :
				if(flag == 0){
					time = System.currentTimeMillis() + TimeUtils.MINUTE_TIME * value;
					jcm.setMailBanTime(time) ;
				}else{
					jcm.setMailBanTime(time) ;
				}
				break;
		}
		jcmService.updateInfo(jcm);
		
		Map<String,Object> map = new HashMap<String,Object>();
		String sign = Misc.md5("mergePlayerBanDay"+serverId+playerId+key+time);
		map.put("serverId", serverId);
		map.put("playerId", playerId);
		map.put("key", key);
		map.put("value", time);
		map.put("sign", sign);
		//先根据serverId查询slave的主机地址，然后发送请求
		IP = StringUtils.isNull(serverListservice.getServerById(serverId).getNip())?serverListservice.getServerById(serverId).getIp():serverListservice.getServerById(serverId).getNip();
		PORT = serverListservice.getServerById(serverId).getSlavePort();
		host = "http://" + IP + ":" + PORT + "/gm/mergePlayerBanDay";
		logger.info("--- - | mergePlayerBanDay-send | - --->"+JSONObject.fromObject(map).toString());
		String result = HttpUtils.jsonPost(host, JSONObject.fromObject(map).toString());
		logger.info("--- - | mergePlayerBanDay-return | - --->"+JSONObject.fromObject(result).toString());
		return JSONObject.fromObject(result).toString();
	}
	
	@RequestMapping("/refreshBanData")
	@ResponseBody
	public String refreshBanData(Long serverId,Long playerId){
		JCsMessage jcm = new JCsMessage();
		jcm.setServerId(serverId);jcm.setPlayerId(playerId);
		jcm = jcmService.refreshBanData(jcm);
		return JSONObject.fromObject(jcm).toString();
	}


    @RequestMapping("/getEvaluate")
    public @ResponseBody String getEvaluate(HttpServletRequest request){
        String filePath = request.getSession().getServletContext().getRealPath("");
        JSONArray result = XlsUtils.getEvaluate(filePath);
        return result.toString();
    }
	
	
}

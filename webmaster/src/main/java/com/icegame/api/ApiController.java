package com.icegame.api;


import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.util.StringUtil;
import com.icegame.framework.utils.*;
import com.icegame.gm.entity.*;
import com.icegame.gm.service.*;
import com.icegame.gm.util.Misc;
import com.icegame.init.InitData;
import com.icegame.sysmanage.components.GroupUtils;
import com.icegame.sysmanage.entity.*;
import com.icegame.sysmanage.service.*;
import com.icegame.third_party.entity.XQRequest;
import com.icegame.third_party.entity.XQResponse;
import com.icegame.third_party.entity.XQShop;
import com.icegame.third_party.service.XQService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ApiController {

	private static Logger logger = Logger.getLogger(ApiController.class);

	private static final Map<String,Object> retMap = new HashMap<String,Object>();


	@Autowired
	private INoticeService noticeService;

	@Autowired
	private IServerListService serverListService;

	@Autowired
	private IPfOptionsService pfOptionsService;

	@Autowired
	private IJWorldMessageService jMessageService;

	@Autowired
	private IJMailCrossServerService jmcsService;

	@Autowired
	private IJCsMessageService jcmService;

	@Autowired
	private IJPreinstallMessageService jpmServerice;

    @Autowired
    private IJAutoreplyMessageService jamServerice;

	@Autowired
	private IJReduceLogService reduceLogService;

    @Autowired
    private IJEvaluateService jEvaluateService;

    @Autowired
    private IJQuestionnaireService jQuestionnaireService;

    @Autowired
	private IJActivityGiftService jActivityGiftService;

	@Autowired
	private IJActivityGiftKeyService jActivityGiftKeyService;

	@Autowired
	private IJKeyUseService ijKeyUseService;

    @Autowired
    private IUserService userService;

	@Autowired
	private IHelpSysService helpSysService;

	@Autowired
	private InitData initData;

	@Autowired
	private IMainTainDescTimeService mainTainDescTimeService;

	@Autowired
	private IExclusiveCsQQService exclusiveCsQQService;

	@Autowired
    private IWechatPubService wechatPubService;

	@Autowired
	private IPropertiesService propertiesService;

	@Autowired
	private GroupUtils groupUtils;

	@Autowired
	private IJVipqqWxConfigService jVipqqWxConfigService;

	@Autowired
	private IJFacebookService jFacebookService;

	@Autowired
	private ILocalPushService localPushService;

	@Autowired
	private XQService xqService;

	/**
	 * 客户端请求服务器列表
	 * @param m
	 * @param x
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/serverlist")
	@ResponseBody
	public String serverlist(String m,String x) throws Exception{
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtils.isNull(m) || StringUtils.isNull(x)){
			retMap.put("ret", 201);
			retMap.put("msg", "parameter empty error");
			return JSONObject.fromObject(retMap).toString();
		}
		MessageDigest inMd5 = MessageDigest.getInstance("MD5");
		String md5str = "";
		try{
			x = AesEncryptUtil.aesDecrypt(x);
			md5str = HexConver.conver16HexStr(inMd5.digest(x.getBytes("utf-8")));
		}catch(Exception e){
			retMap.put("ret", 202);
			retMap.put("msg", "md5 format error");
			return JSONObject.fromObject(retMap).toString();
		}

		if(!md5str.toLowerCase().equals(m.toLowerCase())){
			retMap.put("ret", 203);
			retMap.put("msg", "md5 validate failed");
			return JSONObject.fromObject(retMap).toString();
		}
		logger.info("- --- | serverlist-accept | --- --> [md5] = " + m + ",[channel] = " + x);
		JSONArray array = new JSONArray();
		if(x.length() <= 20) {
			List<ServerList> serverList = new ArrayList<ServerList>();
			JSONObject object = new JSONObject();
			ServerList server = new ServerList();
			server.setChannel(x);
			serverList = serverListService.download(server);
			if(serverList.size() <= 0){
				server = new ServerList();
			}else{
				Long currentTime = 0l;
				long sTime = 0l;
				long pTime = 0l;
				LinkedHashMap<String,Object> map = null;
				for(int i = 0 ; i < serverList.size();i++){
					sTime = Long.valueOf(serverList.get(i).getBeginTime());
					pTime = Long.valueOf(serverList.get(i).getPassTime());

					// 添加判断，标记是否同步成功  如果同步成功才返回客户端
					boolean is_success = serverList.get(i).getIsSuccess() == 1 ? true : false;
					//如果开服时间 小于 当前时间 并且 关服时间大于当前时间  就返回次服务器列表
					if(sTime < System.currentTimeMillis() && pTime > System.currentTimeMillis() && is_success ){
						currentTime = System.currentTimeMillis()/1000;
						Long beginTime = Long.valueOf(serverList.get(i).getBeginTime())/1000;
						map =  new LinkedHashMap<String,Object>();
						map.put("server_id", ""+serverList.get(i).getServerId()+"");
						map.put("server_ip", ""+serverList.get(i).getIp()+"");
						if(serverList.get(i).getPort().equals(""))serverList.get(i).setPort("8080");
						map.put("server_port", ""+serverList.get(i).getPort()+"");
						map.put("name", ""+serverList.get(i).getName()+"");
						switch(serverList.get(i).getClose()){
							case 0: map.put("status", "1");break;
							case 1: map.put("status", "2");break;
							case 2: map.put("status", "3");break;
						}
						if((currentTime-beginTime)/86400 > 7){
							map.put("recommend", 0);
						}else{
							map.put("recommend", 1);
						}
						object = JSONObject.fromObject(map);
						array.add(object);
					}

				}
			}
		}
		String retData = array.toString();
		String retStr = AesEncryptUtil.aesEncrypt(retData);
		String outmd5 = HexConver.conver16HexStr(inMd5.digest(retData.getBytes("UTF-8")));
		retMap.put("m", outmd5);
		retMap.put("x", retStr);
		//logger.info("- --- | serverlist-return | --- --> [md5] = " + outmd5 + ",[serverlist] = " + retStr);
		return JSONObject.fromObject(retMap).toString();
	}



	/**
	 * 客户端请求服务器列表,推荐服方式
	 * @param m
	 * @param x
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/serverlistSuggested")
	@ResponseBody
	public String serverlistSuggested(String m,String x) throws Exception{
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtils.isNull(m) || StringUtils.isNull(x)){
			retMap.put("ret", 201);
			retMap.put("msg", "parameter empty error");
			return JSONObject.fromObject(retMap).toString();
		}
		MessageDigest inMd5 = MessageDigest.getInstance("MD5");
		String md5str = "";
		try{
			x = AesEncryptUtil.aesDecrypt(x);
			md5str = HexConver.conver16HexStr(inMd5.digest(x.getBytes("utf-8")));
		}catch(Exception e){
			retMap.put("ret", 202);
			retMap.put("msg", "md5 format error");
			return JSONObject.fromObject(retMap).toString();
		}

		if(!md5str.toLowerCase().equals(m.toLowerCase())){
			retMap.put("ret", 203);
			retMap.put("msg", "md5 validate failed");
			return JSONObject.fromObject(retMap).toString();
		}
		logger.info("- --- | serverlist-accept | --- --> [md5] = " + m + ",[channel] = " + x);
		JSONArray array = new JSONArray();
		List<ServerList> serverList = new ArrayList<ServerList>();
		if(x.length() <= 20) {
			JSONObject object = new JSONObject();
			ServerList server = new ServerList();
			server.setChannel(x);
			serverList = serverListService.download(server);
			if(serverList.size() <= 0){
				server = new ServerList();
			}else{
				Long currentTime = 0L;
				long sTime = 0L;
				long pTime = 0L;
				LinkedHashMap<String,Object> map = null;
				for(int i = 0 ; i < serverList.size();i++){
					sTime = Long.valueOf(serverList.get(i).getBeginTime());
					pTime = Long.valueOf(serverList.get(i).getPassTime());

					// 添加判断，标记是否同步成功  如果同步成功才返回客户端
					boolean is_success = serverList.get(i).getIsSuccess() == 1 ? true : false;
					//如果开服时间 小于 当前时间 并且 关服时间大于当前时间  就返回次服务器列表
					if(sTime < System.currentTimeMillis() && pTime > System.currentTimeMillis() && is_success ){
						currentTime = System.currentTimeMillis()/1000;
						Long beginTime = Long.valueOf(serverList.get(i).getBeginTime())/1000;
						map =  new LinkedHashMap<String,Object>();
						map.put("server_id", ""+serverList.get(i).getServerId()+"");
						map.put("server_ip", ""+serverList.get(i).getIp()+"");
						if(serverList.get(i).getPort().equals(""))serverList.get(i).setPort("8080");
						map.put("server_port", ""+serverList.get(i).getPort()+"");
						map.put("name", ""+serverList.get(i).getName()+"");
						switch(serverList.get(i).getClose()){
							case 0: map.put("status", "1");break;
							case 1: map.put("status", "2");break;
							case 2: map.put("status", "3");break;
						}
						if((currentTime-beginTime)/86400 > 7){
							map.put("recommend", 0);
						}else{
							map.put("recommend", 1);
						}
						object = JSONObject.fromObject(map);
						array.add(object);
					}

				}
			}
		}
		String retData = array.toString();

		/**
		 * 此处做个修改，之前的接口
		 * @see com.icegame.api.ApiController.serverlist(String,String)
		 * 返回的该数据格式为[{},{},{},{}...]
		 *
		 * 现在为了完成推荐服的需求以及后续的扩展
		 * 采用如下的方式返回
		 * {
		 *     "serverlist":[{},{},{},{}...],
		 *     "suggest":[serverId1,serverId2,serverId3...]
		 * }
		 */

		Map<String,Object> suggestedRetData = new HashMap<String, Object>();

		Long[] suggest = getSuggestServerId(serverList);

		suggestedRetData.put("serverlist",array);
		suggestedRetData.put("suggest",suggest);

        String sourceData = JSON.toJSONString(suggestedRetData);
		String retStr = AesEncryptUtil.aesEncrypt(sourceData);
		String outmd5 = HexConver.conver16HexStr(inMd5.digest(sourceData.getBytes("UTF-8")));
		retMap.put("m", outmd5);
		retMap.put("x", retStr);
		//logger.info("- --- | serverlist-return | --- --> [md5] = " + outmd5 + ",[serverlist] = " + retStr);
		return JSONObject.fromObject(retMap).toString();
	}

	public Long[] getSuggestServerId(List<ServerList> serverLists){
		if(serverLists.size() <= 0){
			return new Long[0];
		}
		List<Long> list = new ArrayList<>();
		for(ServerList server : serverLists){
			if(server.getIsSuggest() == 1){
				// 说明是推荐服，那就取其精华
				list.add(server.getServerId());
			}
		}
		return Convert.toLongArray(list);
	}



	/**
	 * 获取所有渠道服务器列表(就我们内部人使用)
	 * @return
	 */
	/*@CrossOrigin(origins = "*")*/
	@RequestMapping(value="/getAllChannelServerList",produces = "text/plain;charset=utf-8")
	@ResponseBody
	public String getAllChannelServerList(){
		List<ServerList> allServerList = serverListService.getAllChannelServerList();
		JSONArray jsonArr = new JSONArray();
		if(StringUtils.isNotNull(allServerList.toString())){
			jsonArr = JSONArray.fromObject(allServerList);
		}
		return jsonArr.toString();
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value="/getAllChannelServerListNoPage",produces = "text/plain;charset=utf-8")
	@ResponseBody
	public String getAllChannelServerListNoPage(){

		/**
		 * 此处添加联运权限验证
		 */

		List<ServerList> allServerList = new ArrayList<>();

		String hasChannel = groupUtils.getGroupHasChannel();
		if(StringUtils.isNotNull(hasChannel)){

			hasChannel = StringUtils.multFormat(hasChannel);

			ServerList serverList = new ServerList();

			serverList.setHasChannel(hasChannel);

			allServerList = serverListService.getAllChannelServerListNoPage(serverList);
		} else {
			allServerList = serverListService.getAllChannelServerList();
		}

		JSONArray jsonArr = new JSONArray();
		if(StringUtils.isNotNull(allServerList.toString())){
			jsonArr = JSONArray.fromObject(allServerList);
		}
		return jsonArr.toString();
	}


	/**
	 * 获取公告
	 * @param m
	 * @return
	 */
	@RequestMapping(value="/announcements",produces="text/html;charset=UTF-8;")
	public @ResponseBody String announcements(String m,String x) throws Exception{
		Map<String,Object> retMap = new HashMap<String,Object>();
		boolean isReview = false;
		JSONArray retArr = new JSONArray();
		if(StringUtils.isNull(m) || StringUtils.isNull(x)){
			retMap.put("ret", 201);
			retMap.put("msg", "parameter empty error");
			return JSONObject.fromObject(retMap).toString();
		}
		MessageDigest inMd5 = MessageDigest.getInstance("MD5");
		String md5str = "";
		try{
			x = AesEncryptUtil.aesDecrypt(x);
			md5str = HexConver.conver16HexStr(inMd5.digest(x.getBytes("utf-8")));
		}catch(Exception e){
			retMap.put("ret", 202);
			retMap.put("msg", "md5 format error");
			return JSONObject.fromObject(retMap).toString();
		}

		if(!md5str.toLowerCase().equals(m.toLowerCase())){
			retMap.put("ret", 203);
			retMap.put("msg", "md5 validate failed");
			return JSONObject.fromObject(retMap).toString();
		}
		logger.info("- --- | announcements-accept | --- --> [md5] = " + m + ",[channel] = " + x);
		String retStr = "";
		String outmd5 = "";
		if(x.length() <= 20) {
			if(x.contains("_review")){
				isReview = true;
				x = x.substring(0,x.indexOf("_review"));
			}
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String curTime = formatter.format(new Date(System.currentTimeMillis()));
			List<Notice> noticeList = noticeService.getNoticeByChannel(x,curTime);
			if(noticeList.size() > 0){
				JSONObject obj = new JSONObject();
				for(Notice notice : noticeList){
					obj.put("n_id",notice.getId());
					obj.put("n_type",notice.getType());
					obj.put("n_isNew",notice.getIsNew());
					obj.put("n_isUnfold",notice.getIsUnfold());
					obj.put("n_title", notice.getTitle());
					obj.put("n_titleColor", notice.getTitleColor());
					obj.put("n_sort",notice.getSort());
					obj.put("n_content",isReview ? notice.getContextReview() : notice.getContext());
					obj.put("n_contentColor",isReview ? notice.getContextReviewColor() : notice.getContextColor());
					retArr.add(obj);
				}
			}else{
				retMap.put("ret", 204);
				retMap.put("msg", "channel not found");
			}
		}
		String retData = retArr.toString();
		retStr = AesEncryptUtil.aesEncrypt(retData);
		outmd5 = HexConver.conver16HexStr(inMd5.digest(retData.getBytes(StandardCharsets.UTF_8)));
		retMap.put("m", outmd5);
		retMap.put("x", retStr);
		// logger.info("- --- | announcements-return | --- --> [md5] = " + outmd5 + ",[serverlist] = " + retStr);
		return JSONObject.fromObject(retMap).toString();
	}

	/**
	 * 获取平台配置
	 * @return
	 */
	@RequestMapping(value="/getpfOptions",produces = "text/plain;charset=utf-8")
	@ResponseBody
	public String getPfOptionsList(){
		List<PfOptions> pfOptionsList = pfOptionsService.getPfOptionsAll();
		JSONArray jsonArr = new JSONArray();
		JSONObject jsonObj = new JSONObject();
		for(PfOptions pf : pfOptionsList){
			jsonObj = JSONObject.fromObject(pf);
			jsonObj.remove("id");jsonObj.remove("close");
			jsonArr.add(jsonObj);
		}
		return jsonArr.toString();
	}


	public static final String HELP_PATH = "sysmanage" + File.separator + "help" + File.separator ;
	public static final String HELP_SUFFIX = "helpsysclient";

	/**
	 * 客户端请求跳转到帮助页面
	 * @return
	 */
	@RequestMapping("/gotoHelpInfo")
	public String gotoHelpInfo(){
		String project = propertiesService.getCurrentProject();
		String language = propertiesService.getCurrentLanguage();
		return HELP_PATH + project + File.separator + language + File.separator + HELP_SUFFIX;
	}


	/**
	 * 客户端请求帮助信息
	 * @return
	 */
	@RequestMapping(value="/api/getHelpInfo",produces = "text/plain;charset=utf-8")
	@ResponseBody
	public String getHelpInfo(){

		List<HelpSys> officalList = new ArrayList<>();
		List<HelpSys> strategyList = new ArrayList<>();
		HelpSys helpSys = new HelpSys();

		// 先去内存中拿
		Map<String, List<?>> map = (Map<String, List<?>>)initData.getMap();


		// 如果拿不到，去数据库拿
		if(map == null){
			logger.info("内存获取失败，从数据库去获取数据");
			officalList = helpSysService.getAllOfficalList(helpSys);
			strategyList = helpSysService.getAllStrategyList(helpSys);

			if(officalList != null){
				map.put("officalList",officalList);
			}
			if(strategyList != null){
				map.put("strategyList",strategyList);
			}
		}

		officalList = (List<HelpSys>) map.get("officalList");
		strategyList = (List<HelpSys>) map.get("strategyList");

		// 对数据进行排序
		officalList = HelpUtils.sort(officalList);
		strategyList = HelpUtils.sort(strategyList);

		map.put("officalList",officalList);
		map.put("strategyList",strategyList);

		return JSONArray.fromObject(map).toString();
	}

	/**
	 * 客户端帮助系统点赞
	 * @return
	 */
//	@RequestMapping(value="/api/clickZan",produces = "text/plain;charset=utf-8")
//	@ResponseBody
//	public String clickZan(Long id){
//
//		Map<String,Object> retMap = new HashMap<String,Object>();
//
//		if(helpSysService.clickZan(id)) {
//			retMap.put("ret","0");
//			retMap.put("msg","点赞成功");
//		}else{
//			retMap.put("ret","-1");
//			retMap.put("msg","点赞失败，超出上限999999");
//		}
//
//		return JSONArray.fromObject(retMap).toString();
//	}



	/**
	 * 增加世界聊天消息
	 * @return
	 */
	@RequestMapping(value="/api/cs/addWorldMessage",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object addMessage(@RequestBody JWorldMessage message){
		Map<String,Object> retMap = new HashMap<String,Object>();
		JSONObject obj = JSONObject.fromObject(message);
		Long playerId = obj.getLong("playerId");
		Long serverId = obj.getLong("serverId");
		String playerName = obj.getString("playerName");
		String serverName = obj.getString("serverName");
		String content = obj.getString("content");
		Long createTime = obj.getLong("createTime");

		Long banTime = obj.getLong("banTime");
		Long chattingBanTime = obj.getLong("chattingBanTime");
		Long mailBanTime = obj.getLong("mailBanTime");

		int vip = obj.getInt("vip");

		String sign = obj.getString("sign");
		String rSign = Misc.md5("addWorldMessage"+playerId+serverId);
		if(StringUtils.isNotNull(rSign) && rSign.equals(sign)){
			jMessageService.addJMessage(new JWorldMessage(serverId,playerId,serverName,playerName,createTime,content));

			JCsMessage jcm = new JCsMessage(playerId,serverId,banTime,chattingBanTime,mailBanTime,vip);
			if(jcmService.isExistsInfo(jcm)){
				jcmService.updateInfo(jcm);
			}else{
				jcmService.addJCsMessageInfo(jcm);
			}

			logger.info("- ---| addWorldMessage |--- -"+obj.toString());
			retMap.put("ret", 1);
			retMap.put("msg", "success");
		}else{
			retMap.put("ret", -1);
			retMap.put("msg", "md5校检失败");
		}

		return JSONObject.fromObject(retMap).toString();
	}

	/**
	 * 增加玩家留言邮件消息
	 * @return
	 */
	@RequestMapping(value="/api/cs/addMailMessage",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object addMailCrossServer(@RequestBody JMailMessage jmcs){
		Map<String,Object> retMap = new HashMap<String,Object>();
		JSONObject obj = JSONObject.fromObject(jmcs);
		Long senderId = obj.getLong("senderId");
		String senderName = obj.getString("senderName");
		Long senderServerId = obj.getLong("senderServerId");
		String senderServerName = obj.getString("senderServerName");
		Long receiverId = obj.getLong("receiverId");
		String receiverName = obj.getString("receiverName");
		Long receiverServerId = obj.getLong("receiverServerId");
		String receiverServerName = obj.getString("receiverServerName");
		String content = obj.getString("content");
		Long createTime = obj.getLong("createTime");

		Long banTime = obj.getLong("banTime");
		Long chattingBanTime = obj.getLong("chattingBanTime");
		Long mailBanTime = obj.getLong("mailBanTime");
		int vip = obj.getInt("vip");

		String sign = obj.getString("sign");
		String rSign = Misc.md5("addMailMessage"+senderId);
		if(StringUtils.isNotNull(rSign) && rSign.equals(sign)){
			jmcsService.addJMCS(new JMailMessage(createTime,senderId,senderName,senderServerId,senderServerName,
					receiverId,receiverName,receiverServerId,receiverServerName,content));

			JCsMessage jcm = new JCsMessage(senderId,senderServerId,banTime,chattingBanTime,mailBanTime,vip);
			if(jcmService.isExistsInfo(jcm)){
				jcmService.updateInfo(jcm);
			}else{
				jcmService.addJCsMessageInfo(jcm);
			}

			logger.info("- ---| addMailMessage |--- -"+obj.toString());
			retMap.put("ret", 1);
			retMap.put("msg", "success");
		}else{
			retMap.put("ret", -1);
			retMap.put("msg", "md5校检失败");
		}

		return JSONObject.fromObject(retMap).toString();
	}

	@RequestMapping("/api/sys/getCurrentInfo")
	@ResponseBody
	public Object getCurrentInfo(){
		Map<String,Object> retMap = new HashMap<String,Object>();
		retMap.put("csId", UserUtils.getCurrrentUserId());
		retMap.put("csName",UserUtils.getCurrrentUserName());
		retMap.put("isAdmin",UserUtils.getCurrrentPerm());
		User user = UserUtils.getCurrrentUser();
		retMap.put("loginName",user == null ? "Unknown_User" : user.getLoginName());
		return JSONObject.fromObject(retMap).toString();
	}

	/**
	 * 增加玩家反馈消息
	 * @return
	 */
	@RequestMapping(value="/api/cs/addCsMessage",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE+";charset=utf-8")
	@ResponseBody
	public Object addCsMessage(@RequestBody JCsMessage jcm){
		Map<String,Object> retMap = new HashMap<String,Object>();

		boolean _isFinished = false;

        User user = new User();

        JSONObject obj = JSONObject.fromObject(jcm);
		Long playerId = obj.getLong("playerId");
		Long serverId = obj.getLong("serverId");
		String playerName = obj.getString("playerName");
		String serverName = obj.getString("serverName");
		String content = obj.getString("content");
		Long createTime = obj.getLong("createTime");

		Long banTime = obj.getLong("banTime");
		Long chattingBanTime = obj.getLong("chattingBanTime");
		Long mailBanTime = obj.getLong("mailBanTime");
		int vip = obj.getInt("vip");
		int usedTitle = obj.getInt("usedTitle");
		int skinId = obj.getInt("skinId");
		Long speakerTypeId = obj.getLong("speakerTypeId");
		String sign = obj.getString("sign");

		String rSign = Misc.md5("addCsMessage"+playerId+serverId);

		boolean isOnWork = TimeUtils.isOnWork(createTime);

		/**
		 * 当验证通过之后 需要对在线客服分配 问题
		 */
		if(isOnWork){   //判断客服是否在上班时间
		    user = jcmService.getEnableCs(new JCsMessage());
		    if(null == user){
				isOnWork = false;
			}
        }


		if(StringUtils.isNotNull(sign) && rSign.equals(sign)){
			jcm = new JCsMessage(playerId, serverId, playerName, serverName, createTime,System.currentTimeMillis(),
					content, 0,1,banTime,chattingBanTime,mailBanTime,vip,usedTitle, skinId,speakerTypeId);
			jcmService.addJCsMessage(jcm);
			/**
			 * 在向jcsmessage_status插入消息之前，先判断是否存在该玩家的反馈记录，如果存在并且是已完成的状态，修改status为  "未完成"，如果不存在，那就插入一条 "未完成" 的新纪录
			 */
			if(jcmService.isExistsStatus(jcm)){



				/**首先在这边判断 status的值，如果值为 0，说明上次问题是完成了的{true}，那么就需要打开发送邮件的开关*/
				if(jcmService.isStatusFinished(jcm)){
					_isFinished = true;
					jcmService.openSendMail(jcm);
				}

				jcmService.updateStatus(jcm);
			}else{
				jcmService.addJCsMessageStatus(new JCsMessage(jcm.getId(),playerId, serverId,1,createTime));
			}

			if(jcmService.isExistsProcess(jcm)){

				if(_isFinished){
                    if(isOnWork){
						jcm.setCsId(user.getUserId());
						jcm.setCsName(user.getUserName());
					}
                }
				jcmService.updateProcess(jcm);
			}else{
			    if(isOnWork){
                    jcmService.addJCsMessageProcess(new JCsMessage(jcm.getId(),playerId, serverId, user.getUserId(), user.getUserName()));
                }else{
                    jcmService.addJCsMessageProcess(new JCsMessage(jcm.getId(),playerId, serverId));
                }
			}

			if(jcmService.isExistsInfo(jcm)){
				jcmService.updateInfo(jcm);
			}else{
				jcmService.addJCsMessageInfo(new JCsMessage(playerId, serverId, vip, 0, banTime, chattingBanTime, mailBanTime));
			}

			logger.info("- ---| addCsMessage |--- -"+obj.toString());
			retMap.put("ret", 1);
			retMap.put("msg", "success");
			if(!isOnWork){ //如果客服不在线，返回留言消息
                retMap.put("ret", 2);
                retMap.put("msg", jamServerice.getJAutoreplyMessageList(new JAutoreplyMessage()).get(0).getcontent());
				logger.info("---- |客服不在线,返回:| ----"+JSONObject.fromObject(retMap).toString());
            }
		}else{
			retMap.put("ret", -1);
			retMap.put("msg", "md5校检失败");
		}

		return JSONObject.fromObject(retMap).toString();
	}

	/**
	 * 预设消息
	 * @param m
	 * @param x
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	@RequestMapping(value="/api/cs/getPreinstallMsg",produces="text/html;charset=UTF-8;")
	@ResponseBody
	public Object getPreinstallMsg(String m, String x) throws NoSuchAlgorithmException{
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtils.isNull(m) || StringUtils.isNull(x)){
			retMap.put("ret", 201);
			retMap.put("msg", "parameter empty error");
			return JSONObject.fromObject(retMap).toString();
		}
		MessageDigest inMd5 = MessageDigest.getInstance("MD5");
		String md5str = "";
		try{
			x = AesEncryptUtil.aesDecrypt(x);
			md5str = HexConver.conver16HexStr(inMd5.digest(x.getBytes("utf-8")));
		}catch(Exception e){
			retMap.put("ret", 202);
			retMap.put("msg", "md5 format error");
			return JSONObject.fromObject(retMap).toString();
		}

		if(!md5str.toLowerCase().equals(m.toLowerCase())){
			retMap.put("ret", 203);
			retMap.put("msg", "md5 validate failed");
			return JSONObject.fromObject(retMap).toString();
		}
		/**
		 * 这边先要根据pid和sid查询是否有遗留的反馈订单，如果没有就返回预设消息，如果有，就返回没有完成的订单的聊天内容
		 * {playerId|serverId}
		 */
		String[] ids = x.split("\\|");
		JCsMessage jcm = new JCsMessage();
		jcm.setPlayerId(Long.valueOf(ids[0]));
		jcm.setServerId(Long.valueOf(ids[1]));

		boolean flag = jcmService.isStatusFinished(jcm);
		if(flag){	 //已经完成 反馈问题 --向着客户端发送 预设消息
			List<JPreinstallMessage> jpmList = jpmServerice.getJPreinstallMessageList(new JPreinstallMessage());
			JPreinstallMessage jpm = jpmList.get(0);
			retMap.put("msg", jpm.getcontent());
			retMap.put("flag", "0");
		}else{	//存在未完成的反馈问题 -- 向客户端发送历史聊天消息
			List<JCsMessage> latastMessage = jcmService.getLatestMessage(jcm);
			retMap.put("msg", JSONArray.fromObject(latastMessage).toString());
			retMap.put("flag", "1");
		}
		//logger.info("--- -|getPreinstallMsg|- --->"+JSONObject.fromObject(retMap).toString());
		return JSONObject.fromObject(retMap).toString();
	}

	/**
	 * 获取剩余维护时间
	 * @return
	 */
	@RequestMapping(value="/api/maintain/getResidueTime",produces="text/html;charset=UTF-8;")
	@ResponseBody
	public Object getResidueTime(String m, String x) throws NoSuchAlgorithmException{
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtils.isNull(m) || StringUtils.isNull(x)){
			retMap.put("ret", 201);
			retMap.put("msg", "parameter empty error");
			return JSONObject.fromObject(retMap).toString();
		}
		MessageDigest inMd5 = MessageDigest.getInstance("MD5");
		String md5str = "";
		try{
			x = AesEncryptUtil.aesDecrypt(x);
			md5str = HexConver.conver16HexStr(inMd5.digest(x.getBytes("utf-8")));
		}catch(Exception e){
			retMap.put("ret", 202);
			retMap.put("msg", "md5 format error");
			return JSONObject.fromObject(retMap).toString();
		}

		if(!md5str.toLowerCase().equals(m.toLowerCase())){
			retMap.put("ret", 203);
			retMap.put("msg", "md5 validate failed");
			return JSONObject.fromObject(retMap).toString();
		}

		List<MainTainDescTime> mainTainDescTimeList = new ArrayList<MainTainDescTime>();

		// logger.info("- --- | getResidueTime-accept | --- --> parameter : " + x);

		if(x.equals("client")){
			mainTainDescTimeList = mainTainDescTimeService.getMainTainDescTimeList(new MainTainDescTime());
			MainTainDescTime mainTainDescTime = mainTainDescTimeList.get(0);
			retMap.put("msg", "success");
			// 预计结束时间 时间戳格式
			retMap.put("expectEndTime", mainTainDescTime.getExpectEndTime());
			// 剩余毫秒数
			retMap.put("residueTime", mainTainDescTime.getResidueTime());
		}else{
			retMap.put("ret", 204);
			retMap.put("msg", "parameter error");
		}
		// logger.info("--- -|getResidueTime|- --->"+JSONObject.fromObject(retMap).toString());
		return JSONObject.fromObject(retMap).toString();
	}


	@RequestMapping(value="/api/cs/shieldMessage",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object shieldMessage(@RequestBody JCsMessage jcm){
		Map<String,Object> retMap = new HashMap<String,Object>();
		JSONObject obj = JSONObject.fromObject(jcm);
		Long playerId = obj.getLong("playerId");
		Long serverId = obj.getLong("serverId");
		int operate = obj.getInt("operate");
		String rSign = obj.getString("sign");
		String sign = Misc.md5("shieldMessage" + playerId + serverId + operate);
		if(StringUtils.isNotNull(rSign) && rSign.equals(sign)){
			jcm = new JCsMessage(playerId, serverId, operate);
			if(jcmService.isExistsInfo(jcm)){
				if(operate == 0){
					jcmService.addShield(jcm);
				}else if(operate == 1){
					jcmService.subShield(jcm);
				}
			}
            retMap.put("ret", 1);
            retMap.put("msg", "success");
		}else{
			retMap.put("ret", -1);
			retMap.put("msg", "md5校检失败");
		}
		return JSONObject.fromObject(retMap).toString();

	}

	@RequestMapping(value="/api/cs/addReduceLog",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object addReduceLog(@RequestBody JReduceLog jrl){
		Map<String,Object> retMap = new HashMap<String,Object>();
		JSONObject obj = JSONObject.fromObject(jrl);
		Long playerId = obj.getLong("playerId");
		Long serverId = obj.getLong("serverId");
		Long createTime = obj.getLong("createTime");
		Long npcId = obj.getLong("npcId");
		int potential = obj.getInt("potential");
		int evolveLevel = obj.getInt("evolveLevel");
		int level = obj.getInt("level");
		int exp = obj.getInt("exp");
		String items = obj.getString("items");
		String playerName = obj.getString("playerName");
		String serverName = obj.getString("serverName");
		String npcName = obj.getString("npcName");
		String rSign = obj.getString("sign");
		String sign = Misc.md5("addReduceLog"+playerId+npcId);
		if(StringUtils.isNotNull(rSign) && rSign.equals(sign)){
			jrl = new JReduceLog(playerId,  playerName,  serverId,  serverName,  npcId,  npcName,
					 createTime,  items,  potential,  evolveLevel,  level,  exp);
			reduceLogService.addJReduceLog(jrl);
			retMap.put("ret", 1);
			retMap.put("msg", "success");
		}else{
			retMap.put("ret", -1);
			retMap.put("msg", "md5校检失败");
		}
		return JSONObject.fromObject(retMap).toString();

	}

	@RequestMapping(value="/api/cs/evaluate",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object evaluate(@RequestBody JEvaluate jEvaluate){
		Map<String,Object> retMap = new HashMap<String,Object>();
		JSONObject obj = JSONObject.fromObject(jEvaluate);
		int npcId = obj.getInt("npcId");
		String npcName = obj.getString("npcName");
		String options = obj.getString("options");
		String rSign = obj.getString("sign");

		String sign = Misc.md5("evaluate" + npcId + options);
		if(StringUtils.isNotNull(rSign) && rSign.equals(sign)){
            JEvaluate jeu = new JEvaluate();
            jeu.setNpcId(Long.valueOf(npcId));
            jeu.setNpcName(npcName);
            jeu.setQuestion1("1.该武将值不值得培养");
            jeu.setQuestion2("2.该武将如何定位");
            jeu.setQuestion3("3.该将领是否符合您心目中的形象");

            String[] opt = options.split(",");
            int opt1 = Integer.valueOf(opt[0]);
            int opt2 = Integer.valueOf(opt[1]);
            int opt3 = Integer.valueOf(opt[2]);

            Long lopt1 = Long.valueOf(opt[0]);
            Long lopt2 = Long.valueOf(opt[1]);
            Long lopt3 = Long.valueOf(opt[2]);

            switch(opt1){
                case 1 : jeu.setAnswer11(1L);break;
                case 2 : jeu.setAnswer12(1L);break;
                case 3 : jeu.setAnswer13(1L);break;
                case 4 : jeu.setAnswer14(1L);break;
            }

            switch(opt2){
                case 1 : jeu.setAnswer21(1L);break;
                case 2 : jeu.setAnswer22(1L);break;
                case 3 : jeu.setAnswer23(1L);break;
                case 4 : jeu.setAnswer24(1L);break;
            }

            switch(opt3){
                case 1 : jeu.setAnswer31(1L);break;
                case 2 : jeu.setAnswer32(1L);break;
                case 3 : jeu.setAnswer33(1L);break;
                case 4 : jeu.setAnswer34(1L);break;
            }
            /**
             * 首先判断是否存在此武将的信息，如果存在，就修改，不存在添加一条新的记录
             */
            if(jEvaluateService.exists(jEvaluate)){
                jEvaluateService.updateJEvaluate(jeu);
            }else{
                jEvaluateService.addJEvaluate(jeu);
            }
            retMap.put("ret", 1);
            retMap.put("msg", "success");
		}else{
			retMap.put("ret", -1);
			retMap.put("msg", "md5校检失败");
		}
		return JSONObject.fromObject(retMap).toString();

	}

	@RequestMapping(value="/api/cs/question",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object question(@RequestBody JQuestionnaire jQuestionnaire){
		Map<String,Object> retMap = new HashMap<String,Object>();
		JSONObject obj = JSONObject.fromObject(jQuestionnaire);
		Long playerId = obj.getLong("playerId");
		String playerName = obj.getString("playerName");
		String answer = obj.getString("answer");
		int questionId = obj.getInt("questionId");

		String rSign = obj.getString("sign");

		String sign = Misc.md5("question" + playerId + questionId);
		if(StringUtils.isNotNull(rSign) && rSign.equals(sign)){
            JQuestionnaire jqn = new JQuestionnaire();
            jqn.setPlayerId(playerId);
            jqn.setPlayerName(playerName);
            if(questionId == 9){
            	//先判断是否 question9 是否有值，如果有那就设置为10,
				questionId = jQuestionnaireService.exists9(jqn) ? 10 : 9 ;
			}
            switch (questionId){
                case 1 : jqn.setQuestion1(answer); break;
                case 2 : jqn.setQuestion2(answer); break;
                case 3 : jqn.setQuestion3(answer); break;
                case 4 : jqn.setQuestion4(answer); break;
                case 5 : jqn.setQuestion5(answer); break;
                case 6 : jqn.setQuestion6(answer); break;
                case 7 : jqn.setQuestion7(answer); break;
                case 8 : jqn.setQuestion8(answer); break;
                case 9 : jqn.setQuestion9(answer); break;
                case 10 : jqn.setQuestion10(answer); break;
            }
            if(jQuestionnaireService.exists(jQuestionnaire)){
                jqn.setKey("question"+questionId);
                jqn.setValue(answer);
                jQuestionnaireService.updateJQuestionnaire(jqn);
            }else{
                jQuestionnaireService.addJQuestionnaire(jqn);
            }
			retMap.put("ret", 1);
			retMap.put("msg", "success");
		}else{
			retMap.put("ret", -1);
			retMap.put("msg", "md5校检失败");
		}
		return JSONObject.fromObject(retMap).toString();

	}

	@RequestMapping(value="/api/cdKey/activityGift",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object activityGift(@RequestBody JActivityGift jag){
		Map<String,Object> retMap = new HashMap<String,Object>();
		JSONObject obj = JSONObject.fromObject(jag);
		Long playerId = obj.getLong("playerId");
		Long serverId = obj.getLong("serverId");
		String cdKey = obj.getString("cdKey");
		String platform = obj.getString("channel");
		String rSign = obj.getString("sign");

		String sign = Misc.md5("activity" + playerId + serverId);
		if(StringUtils.isNotNull(rSign) && rSign.equals(sign)){

			if(cdKey.length() >= 32){
				retMap.put("ret", 1);
				return JSONObject.fromObject(retMap).toString();
			}

            JActivityGift jActivityGift = jActivityGiftService.getGiftByCDKey(cdKey);

            JActivityGiftKey jActivityGiftKey = jActivityGiftKeyService.getGiftKeyByCDKey(cdKey);

			if(null != jActivityGift){

				logger.info("--- - | activityGift | - --->进入礼包兑换... |serverId:" + serverId + "|playerId:" + playerId + "|platform:" + platform + "|cdKey:" + cdKey + "|");

				//判断此玩家是否领取过该礼包
				JKeyUse jKeyUse = new JKeyUse(serverId, playerId, cdKey, jActivityGift.getUseType(),jActivityGift.getDiffType());


				if(jActivityGift.isUseLimit()){
					retMap.put("ret", 6);
					return JSONObject.fromObject(retMap).toString();
				}

				if(Integer.valueOf(jActivityGift.getDiffType()) == 0){

					if(ijKeyUseService.isUsed(cdKey)){
						logger.info("--- - | activityGift | - --->被使用:3");
						retMap.put("ret", 3);
						return JSONObject.fromObject(retMap).toString();
					}

					//判断玩家是否领取过相同使用类型的礼包码，如果使用过的话，返回 4 相同类型的已经被使用
					if(ijKeyUseService.isUsedSameType(jKeyUse)){
						logger.info("--- - | activityGift | - --->相同类型被使用:4");
						retMap.put("ret", 4);
						return JSONObject.fromObject(retMap).toString();
					}

					retMap.put("ret",0);
					retMap.put("coin",jActivityGift.getCoin());
					retMap.put("dollar",jActivityGift.getDollar());
					retMap.put("merit",jActivityGift.getMerit());
					retMap.put("awardStr",jActivityGift.getAwardStr());
					retMap.put("type",-1);
					logger.info("--- - | activityGift | - --->无限使用 兑换成功");
					ijKeyUseService.addKeyUse(jKeyUse);

				}else if(Integer.valueOf(jActivityGift.getDiffType()) == 1){

					String targets = jActivityGift.getTargets();
					//判断区服是否匹配
					if(targets.indexOf(String.valueOf(serverId)) == -1){
						logger.info("--- - | activityGift | - --->区服不匹配:2");
						retMap.put("ret", 2);
						return JSONObject.fromObject(retMap).toString();
					}

					//判断礼包码是否被使用
					if(ijKeyUseService.isUsed(cdKey)){
						logger.info("--- - | activityGift | - --->被使用:3");
						retMap.put("ret", 3);
						return JSONObject.fromObject(retMap).toString();
					}

					//判断玩家是否领取过相同使用类型的礼包码，如果使用过的话，返回 4 相同类型的已经被使用
					if(ijKeyUseService.isUsedSameType(jKeyUse)){
						logger.info("--- - | activityGift | - --->相同类型被使用:4");
						retMap.put("ret", 4);
						return JSONObject.fromObject(retMap).toString();
					}

					retMap.put("ret",0);
					retMap.put("coin",jActivityGift.getCoin());
					retMap.put("dollar",jActivityGift.getDollar());
					retMap.put("merit",jActivityGift.getMerit());
					retMap.put("awardStr",jActivityGift.getAwardStr());
					retMap.put("type",-1);
					logger.info("--- - | activityGift | - --->指定区服 兑换成功");
					ijKeyUseService.addKeyUse(new JKeyUse(serverId,playerId,cdKey,jActivityGift.getUseType(),jActivityGift.getDiffType()));

				}else if(Integer.valueOf(jActivityGift.getDiffType()) == 2){

					ServerList server = serverListService.getServerById(serverId);
					//判断渠道是否匹配
					if(!platform.equals(jActivityGift.getChannel()) && !platform.equals(server.getChannel())){
						logger.info("--- - | activityGift | - --->渠道不匹配:2");
						retMap.put("ret", 2);
						return JSONObject.fromObject(retMap).toString();
					}

					//判断礼包码是否被使用
					if(ijKeyUseService.isUsed(cdKey)){
						logger.info("--- - | activityGift | - --->被使用:3");
						retMap.put("ret", 3);
						return JSONObject.fromObject(retMap).toString();
					}

					//判断玩家是否领取过相同使用类型的礼包码，如果使用过的话，返回 4 相同类型的已经被使用
					if(ijKeyUseService.isUsedSameType(jKeyUse)){
						logger.info("--- - | activityGift | - --->相同类型被使用:4");
						retMap.put("ret", 4);
						return JSONObject.fromObject(retMap).toString();
					}

					retMap.put("ret",0);
					retMap.put("coin",jActivityGift.getCoin());
					retMap.put("dollar",jActivityGift.getDollar());
					retMap.put("merit",jActivityGift.getMerit());
					retMap.put("awardStr",jActivityGift.getAwardStr());
					retMap.put("type",-1);
					logger.info("--- - | activityGift | - --->特定渠道 兑换成功");
					ijKeyUseService.addKeyUse(new JKeyUse(serverId,playerId,cdKey,jActivityGift.getUseType(),jActivityGift.getDiffType()));

				}
				else if(Integer.valueOf(jActivityGift.getDiffType()) == 3){

					String targets = jActivityGift.getTargets();
					//判断区服是否匹配
					if(targets.indexOf(String.valueOf(serverId)) == -1){
						logger.info("--- - | activityGift | - --->区服不匹配:2");
						retMap.put("ret", 2);
						return JSONObject.fromObject(retMap).toString();
					}

					ServerList server = serverListService.getServerById(serverId);
					//判断渠道是否匹配
					if(!platform.equals(jActivityGift.getChannel()) && !platform.equals(server.getChannel())){
						logger.info("--- - | activityGift | - --->渠道不匹配:2");
						retMap.put("ret", 2);
						return JSONObject.fromObject(retMap).toString();
					}

					//判断礼包码是否被使用
					if(ijKeyUseService.isUsed(cdKey)){
						logger.info("--- - | activityGift | - --->被使用:3");
						retMap.put("ret", 3);
						return JSONObject.fromObject(retMap).toString();
					}

					//判断玩家是否领取过相同使用类型的礼包码，如果使用过的话，返回 4 相同类型的已经被使用
					if(ijKeyUseService.isUsedSameType(jKeyUse)){
						logger.info("--- - | activityGift | - --->相同类型被使用:4");
						retMap.put("ret", 4);
						return JSONObject.fromObject(retMap).toString();
					}

					retMap.put("ret",0);
					retMap.put("coin",jActivityGift.getCoin());
					retMap.put("dollar",jActivityGift.getDollar());
					retMap.put("merit",jActivityGift.getMerit());
					retMap.put("awardStr",jActivityGift.getAwardStr());
					retMap.put("type",-1);
					logger.info("--- - | activityGift | - --->指定区服渠道 兑换成功");
					ijKeyUseService.addKeyUse(new JKeyUse(serverId,playerId,cdKey,jActivityGift.getUseType(),jActivityGift.getDiffType()));

				}

				logger.info("--- - | activityGift | - --->礼包兑换操作完成");

			} else if (null != jActivityGiftKey){

				/**
				 * 礼包码的没有useType diffType 所以向JKeyUser添加记录的时候 写值为-1
				 */

				logger.info("--- - | activityGift | - --->进入口令兑换...|serverId:" + serverId + "|playerId:" + playerId + "|platform:" + platform + "|cdKey:" + cdKey + "|");

				if(ijKeyUseService.isThisPlayerUsed(new JKeyUse(serverId, playerId, cdKey, -1,"-1"))){
					logger.info("--- - | activityGift | - --->被使用:3");
					retMap.put("ret", 3);
					return JSONObject.fromObject(retMap).toString();
				}

				if(jActivityGiftKey.isDisabled()){
					logger.info("--- - | activityGift | - --->被禁用:6");
					retMap.put("ret", 6);
					return JSONObject.fromObject(retMap).toString();
				}

				ServerList server = serverListService.getServerById(serverId);
				if(!jActivityGiftKey.getChannel().equals("")){
                	if(!platform.equals(jActivityGiftKey.getChannel())  && !platform.equals(server.getChannel())){
						logger.info("--- - | activityGift | - --->渠道不匹配:2");
						retMap.put("ret", 2);
						return JSONObject.fromObject(retMap).toString();
					}
				}

                //判断当前key是否是指定区服的，如果是，并且玩家的区服id不在指定区服里面，返回错误码
                if(!jActivityGiftKey.getTargets().equals("")){
                    String targets = jActivityGiftKey.getTargets();
                    if(targets.indexOf(String.valueOf(serverId)) == -1){
						logger.info("--- - | activityGift | - --->区服不匹配:2");
                        retMap.put("ret", 2);
                        return JSONObject.fromObject(retMap).toString();
                    }
                }

				if(Long.valueOf(jActivityGiftKey.getTimesLimit()) > 0){
					if(ijKeyUseService.usedTimes(cdKey) >= jActivityGiftKey.getTimesLimit()){
						logger.info("--- - | activityGift | - --->超出次数限制:3");
						retMap.put("ret", 3);	//这个地方原本应该是返回7，但是因为客户端的case判断没有7，所以改成了3
						return JSONObject.fromObject(retMap).toString();
					}
				}

				if(Long.valueOf(jActivityGiftKey.getStartTime()) > System.currentTimeMillis() || Long.valueOf(jActivityGiftKey.getPassTime()) < System.currentTimeMillis()){
					logger.info("--- - | activityGift | - --->超出时间限制:5");
					retMap.put("ret", 5);
					return JSONObject.fromObject(retMap).toString();
				}

				retMap.put("ret",0);
				retMap.put("coin",jActivityGiftKey.getCoin());
				retMap.put("dollar",jActivityGiftKey.getDollar());
				retMap.put("merit",jActivityGiftKey.getMerit());
				retMap.put("awardStr",jActivityGiftKey.getAwardStr());
				retMap.put("type",jActivityGiftKey.getTalkType());

				ijKeyUseService.addKeyUse(new JKeyUse(serverId,playerId,cdKey,-1,"-1"));

				logger.info("--- - | activityGift | - --->口令兑换操作完成");

			}else{
                retMap.put("ret", 2);
                return JSONObject.fromObject(retMap).toString();
            }

		}else{
			retMap.put("ret", -1);
			retMap.put("msg", "md5校检失败");
		}

		return JSONObject.fromObject(retMap).toString();

	}


	/**
	 * 专属vipqq
	 * @param m
	 * @param x
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	@RequestMapping(value="/api/vip/getExclusiveCsQQ",produces="text/html;charset=UTF-8;")
	@ResponseBody
	public Object getExclusiveCsQQ(String m, String x) throws NoSuchAlgorithmException{
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtils.isNull(m) || StringUtils.isNull(x)){
			retMap.put("ret", 201);
			retMap.put("msg", "parameter empty error");
			return JSONObject.fromObject(retMap).toString();
		}
		MessageDigest inMd5 = MessageDigest.getInstance("MD5");
		String md5str = "";
		try{
			x = AesEncryptUtil.aesDecrypt(x);
			md5str = HexConver.conver16HexStr(inMd5.digest(x.getBytes("utf-8")));
		}catch(Exception e){
			retMap.put("ret", 202);
			retMap.put("msg", "md5 format error");
			return JSONObject.fromObject(retMap).toString();
		}

		if(!md5str.toLowerCase().equals(m.toLowerCase())){
			retMap.put("ret", 203);
			retMap.put("msg", "md5 validate failed");
			return JSONObject.fromObject(retMap).toString();
		}

		// pid|sid|money|channel

		JVipqqWxConfig jVipqqWxConfig = new JVipqqWxConfig();
		String[] ids = x.split("\\|");

		Long serverId = Long.valueOf(ids[0]);
		Long playerId = Long.valueOf(ids[1]);
		int cmoney = Integer.valueOf(ids[2]);
		String channel = ids[3];

		jVipqqWxConfig.setPlayerId(serverId);
		jVipqqWxConfig.setServerId(playerId);
		jVipqqWxConfig.setQqMoney(cmoney);
		jVipqqWxConfig.setChannel(channel);

		logger.info("--- -|getExclusiveCsQQ --- accept |- --->" + ids.toString());

		// 获取开关是否打开
		/**
		 * 唐文锐 变态需求
		 * 之前是从mconfigure 表获取开关，现改为 从 jvipqq_wx_config 表获取
		 */
		JVipqqWxConfig config = jVipqqWxConfigService.getConfigByChannelRetList(jVipqqWxConfig);

		boolean isOpen = config.getQqStatus() == 1 ? true : false ;

		int money = config.getQqMoney();

		ExclusiveCsQQ exclusiveCsQQ = new ExclusiveCsQQ(serverId, playerId, channel);

		// 判断是否已经存在此玩家
		List<ExclusiveCsQQ> playerList = exclusiveCsQQService.existCurrentPlayer(exclusiveCsQQ);

		retMap.put("isOpen", isOpen);
		retMap.put("money", money);

		if(isOpen){
			if(playerList.size() > 0){
				retMap.put("qq", playerList.get(0).getQq());
			}else{

				// 找出分配次数最少的客服qq
				List<ExclusiveCsQQ> exclusiveCsQQList = exclusiveCsQQService.getMinTimesQQ(exclusiveCsQQ);

				String qq = "";

				if(exclusiveCsQQList.size() > 0){
					Long minQQId = exclusiveCsQQList.get(0).getId();
					qq =  exclusiveCsQQList.get(0).getQq();

					exclusiveCsQQ.setQqId(minQQId);

					// 然后添加到记录表中
					exclusiveCsQQService.addPlayerRecode(exclusiveCsQQ);
					// 分配过了之后 此客服qq 分配次数自增1
					exclusiveCsQQService.timesPlus(exclusiveCsQQ);

					retMap.put("qq", qq);
				}else{

					/**
					 * 走到这边，说明没有配置任何一个qq号，那就给客户堵返回开关关闭
					 * 这步操作主要是为了防止客户端报错，因为客户端 在 true的时候
					 * 会读取 qq号
 					 */
					logger.info("--- -|getExclusiveCsQQ |- --- 没有配置qq号，将开关置为false返回");
					retMap.put("isOpen", false);
				}
			}
		}

		logger.info("--- -|getExclusiveCsQQ --- return|- --->"+JSONObject.fromObject(retMap).toString());
		return JSONObject.fromObject(retMap).toString();
	}

	/**
	 * 获取FB点赞配置
	 * @param m
	 * @param x
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @author wzy
	 */
	@RequestMapping(value="/api/facebook/getJFacebooklike",produces="text/html;charset=UTF-8;")
	@ResponseBody
	public Object getJFacebooklike(String m, String x) throws NoSuchAlgorithmException{
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtils.isNull(m) || StringUtils.isNull(x)){
			retMap.put("ret", 201);
			retMap.put("msg", "parameter empty error");
			return JSONObject.fromObject(retMap).toString();
		}
		MessageDigest inMd5 = MessageDigest.getInstance("MD5");
		String md5str = "";
		try{
			x = AesEncryptUtil.aesDecrypt(x);
			md5str = HexConver.conver16HexStr(inMd5.digest(x.getBytes("utf-8")));
		}catch(Exception e){
			retMap.put("ret", 202);
			retMap.put("msg", "md5 format error");
			return JSONObject.fromObject(retMap).toString();
		}

		if(!md5str.toLowerCase().equals(m.toLowerCase())){
			retMap.put("ret", 203);
			retMap.put("msg", "md5 validate failed");
			return JSONObject.fromObject(retMap).toString();
		}

		// 传个channel过来就行

		JFacebook jFacebook = new JFacebook();
		String[] ids = x.split("\\|");

		String channel = ids[0];


		jFacebook.setChannel(channel);

		logger.info("--- -|getJFacebooklike --- accept |- --->" + Arrays.toString(ids));


		// 判断这个渠道是否存在配置
		List<JFacebook> facebooklikeList = jFacebookService.getFacebooklikeByChannel(jFacebook);


			if(facebooklikeList.size() > 0){
				retMap.put("result", "成功");
				retMap.put("yuanbao", facebooklikeList.get(0).getYuanbao());
				retMap.put("likenum", facebooklikeList.get(0).getLikenum());
			}else{
				retMap.put("yuanbao", 1000);
				retMap.put("likenum", 1000);
				retMap.put("result", "失败，无此渠道的配置，返回默认值");
			}


		logger.info("--- -|getJFacebooklike --- return|- --->"+JSONObject.fromObject(retMap).toString());
		return JSONObject.fromObject(retMap).toString();
	}


    /**
     * 获取微信公众号
     * @return
     */
    @RequestMapping(value="/api/getWechatPubNumber",produces="text/html;charset=UTF-8;")
    @ResponseBody
    public Object getWechatPubNumber(String m, String x) throws NoSuchAlgorithmException{
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtils.isNull(m) || StringUtils.isNull(x)){
			retMap.put("ret", 201);
			retMap.put("msg", "parameter empty error");
			return JSONObject.fromObject(retMap).toString();
		}
		MessageDigest inMd5 = MessageDigest.getInstance("MD5");
		String md5str = "";
		try{
			x = AesEncryptUtil.aesDecrypt(x);
			md5str = HexConver.conver16HexStr(inMd5.digest(x.getBytes("utf-8")));
		}catch(Exception e){
			retMap.put("ret", 202);
			retMap.put("msg", "md5 format error");
			return JSONObject.fromObject(retMap).toString();
		}

		if(!md5str.toLowerCase().equals(m.toLowerCase())){
			retMap.put("ret", 203);
			retMap.put("msg", "md5 validate failed");
			return JSONObject.fromObject(retMap).toString();
		}

		JVipqqWxConfig jVipqqWxConfig = new JVipqqWxConfig();
		String[] ids = x.split("\\|");
		String channel = ids[2];
		jVipqqWxConfig.setChannel(channel);

		JVipqqWxConfig config = jVipqqWxConfigService.getConfigByChannelRetList(jVipqqWxConfig);

		boolean isOpen = config.getWxStatus() == 1 ? true : false ;

        String wxPnum = config.getWxPnum();

        retMap.put("isOpen", isOpen);

        if(isOpen){
            if(wxPnum == null || "".equals(wxPnum) || wxPnum.length() == 0){
                logger.info("--- -|getWechatPubNumber --- 开关目前已打开，但是没有配置微信公众号，所以关闭开关|- --->");
                retMap.put("isOpen", false);
            } else {
                retMap.put("wxName", wxPnum);
            }
        }

        logger.info("--- -|getWechatPubNumber --- return|- --->" + JSONObject.fromObject(retMap).toString());
        return JSONObject.fromObject(retMap).toString();
    }

	/**
	 * 本地推送，厉文辉需求
	 * @return
	 */
	@RequestMapping(value="/api/getLocalPush",produces="text/html;charset=UTF-8;")
	@ResponseBody
	public Object getLocalPush(String m, String x) throws NoSuchAlgorithmException{
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(StringUtils.isNull(m) || StringUtils.isNull(x)){
			retMap.put("ret", 201);
			retMap.put("msg", "parameter empty error");
			return JSONObject.fromObject(retMap).toString();
		}
		MessageDigest inMd5 = MessageDigest.getInstance("MD5");
		String md5str = "";
		try{
			x = AesEncryptUtil.aesDecrypt(x);
			md5str = HexConver.conver16HexStr(inMd5.digest(x.getBytes("utf-8")));
		}catch(Exception e){
			retMap.put("ret", 202);
			retMap.put("msg", "md5 format error");
			return JSONObject.fromObject(retMap).toString();
		}

		if(!md5str.toLowerCase().equals(m.toLowerCase())){
			retMap.put("ret", 203);
			retMap.put("msg", "md5 validate failed");
			return JSONObject.fromObject(retMap).toString();
		}

		if(StringUtil.isEmpty(x) || !x.contains("|")){
			retMap.put("ret", 204);
			retMap.put("msg", "parameter error");
			return JSONObject.fromObject(retMap).toString();
		}

		String[] array = x.split("\\|");
		String channel = array[0].trim();
		String channelCode = array[1].trim();

		logger.info("--- -|getLocalPush --- accept|- --->" + Arrays.toString(array));

		List<LocalPush> localPushByChannel = localPushService.getLocalPushByChannel(channel);
		JSONArray array1 = new JSONArray();
		for(LocalPush l : localPushByChannel){
			if(l != null){
				String[] temp = l.getChannelCode().split(",");
				List<String> list = Arrays.asList(temp);
				if(list.contains(channelCode) || (list.size() == 1 && list.get(0).trim().equals(""))){
					JSONObject object = new JSONObject();
					object.put("context",l.getContext());
					object.put("pushTime",TimeUtils.dateToStampWithDetail(l.getPushTime()));
					array1.add(object);
				}
			}
		}
		retMap.put("ret",0);
		retMap.put("result",array1);
		retMap.put("msg","success");
		logger.info("--- -|getLocalPush --- return|- --->" + JSONObject.fromObject(retMap).toString());
		return JSONObject.fromObject(retMap).toString();
	}


	@ResponseBody
	@RequestMapping(value = "/api/xqMall",
			method =RequestMethod.POST,
			produces = "application/json;charset=utf-8")
	public XQResponse XQServerV1(@Validated XQRequest request) {
		return xqService.dealArgs(request);
	}

	@ResponseBody
	@RequestMapping(value = "/api/xqShop",
			method =RequestMethod.POST,
			produces = "application/json;charset=utf-8")
	public com.alibaba.fastjson.JSONObject gameShop(@RequestBody @Validated XQShop shop) {
		return xqService.xqShop(shop);
	}
}

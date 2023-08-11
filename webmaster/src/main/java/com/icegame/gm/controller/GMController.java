package com.icegame.gm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.icegame.framework.utils.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.icegame.gm.entity.BatchDelete;
import com.icegame.gm.entity.Gmlogs;
import com.icegame.gm.entity.JCsMessage;
import com.icegame.gm.service.IGmlogsService;
import com.icegame.gm.service.IJCsMessageService;
import com.icegame.gm.util.MapUtils;
import com.icegame.gm.util.Misc;
import com.icegame.gm.value.EBehaviorType;
import com.icegame.sysmanage.entity.ServerList;
import com.icegame.sysmanage.service.IServerListService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/sysmgr/gm")
public class GMController {

	private static Logger logger = Logger.getLogger(GMController.class);

	private static String host = "";

	private static String IP = "";

	private static int PORT = 0;

	public static Map<String,Object> retMap = new HashMap<String,Object>();

	private final static String _LEVELNAME = "_levelName";

	private final static String HANGLEVEL = "hangLevel";

	private final static String LEVELID = "levelId";

	private final static String GODID ="godId";

	private final static String PETLEVELID ="petLevelId";

	private final static String BOOKLEVELID ="bookLevelId";

	private final static String CAMPAIGNLEVELID ="campaignLevelId";

	private final static String DESTINYLEVELID="destinyLevelId";

	@Autowired
	private IGmlogsService gmlogsService;

	@Autowired
	private IServerListService serverListService;

	@RequestMapping("/gotoGM")
	public String gotoGM(){
		return "sysmanage/gm/gm";
	}

	@RequestMapping("/gotoGMRecharge")
	public String gotoGmTopUp(){
		return "sysmanage/gm/recharge";
	}

	@Autowired
	private IJCsMessageService jcmService;

	@RequestMapping("/gotoGMRechargeHistory")
	public String gotoGMRechargeHistory(){
		return "sysmanage/gm/rechargehistory";
	}

	/**
	 * @param playerId
	 * @param serverId
	 * @param playerName
	 * @param username
	 * @param serverInfo
	 * @return
	 *
	 * ----------------------------------------
	 * @date 2019-06-14 20:20:21
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * @date 2019-12-12
	 * @author wzy
	 * 支持使用修仙名查询玩家信息,改用CryptUtils进行MD5加密
	 * ----------------------------------------
	 */
	@RequestMapping("/searchPlayerInfo")
	public @ResponseBody String searchPlayerInfo(Long playerId,Long serverId,String playerName,String playerGodName,String username,String serverInfo){
		Map<String,Object> map = new HashMap<String,Object>();
		String sign = "";
		try {
			if (playerGodName == null){
				sign = CryptUtils.GetMD5Code("searchPlayerInfo"+serverId+playerId+playerName+username);
			}else {
				sign = CryptUtils.GetMD5Code("searchPlayerInfo"+serverId+playerId+playerName+playerGodName+username);
			}
		} catch (Exception e){
			logger.error("searchPlayerInfo 加密操作失败");
		}
		ServerList server = serverListService.getServerById(serverId);
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		map.put("sign", sign);
		map.put("serverId", serverId);
		map.put("playerId", playerId);
		map.put("playerName", playerName);
		map.put("playerGodName", playerGodName);
		map.put("username", username);
		JSONObject jo = JSONObject.fromObject(map);
		host = "http://" + IP + ":" + PORT + "/gm/search";
		logger.info("- ---| searchPlayerInfo |--- -"+jo.toString());
		String result = HttpUtils.jsonPost(host, jo.toString());
		JSONObject retObj = JSONObject.fromObject(result);
		// 此处把 时间戳修改成 日期格式在返回 客户端
		if(retObj.containsKey("lastOffline")){
			String createTime = TimeUtils.stampToDateWithMill(retObj.getString("lastOffline"));
			retObj.put("lastOffline", createTime);
		}
		try{
			if(retObj.get("ret").equals("-2")){
				result = "-2";
			}
		}catch(Exception e){

		}
		/*Gmlogs gmlogs = UserUtils.addGmlogs("查询", UserUtils.getCurrrentUserName()+"查找["+serverInfo+"]区"+playerId+"玩家");
		gmlogsService.addGmlogs(gmlogs);*/
		//logger.info(result);
		return retObj.toString();
	}

	@RequestMapping("/addGoods")
	public @ResponseBody String addGoods(Long playerId,Long serverId,String goods,String playerName,String serverInfo){
		Map<String,Object> map = new HashMap<String,Object>();
		String sign = Misc.md5("addObject"+goods+playerId);
		ServerList server = serverListService.getServerById(serverId);
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		map.put("sign", sign);
		map.put("playerId", playerId);
		map.put("goods", goods);
		JSONObject jo = JSONObject.fromObject(map);
		logger.info("- ---| addGoods |--- -"+jo.toString());
		host = "http://" + IP + ":" + PORT + "/gm/addObject";
		String result = HttpUtils.jsonPost(host, jo.toString());
		Gmlogs gmlogs = UserUtils.addGmlogs("添加物品", UserUtils.getCurrrentUserName()+" 向-- ["+serverInfo+"] -- ["+playerName+"] 添加["+goods+"]");
		gmlogsService.addGmlogs(gmlogs);
		logger.info(result);
		return result;
	}

	@RequestMapping("/merge")
	public @ResponseBody String merge(Long playerId,Long serverId,String name,String value,String playerName,String serverInfo){
		Map<String,Object> map = new HashMap<String,Object>();

		/**此处如果修改的是chattingBanTime 或者 mailBanTime，还需要修改master数据库中的值*/
		if(name.equals("chattingBanTime") || name.equals("mailBanTime")){
			JCsMessage jcm = new JCsMessage();
			jcm.setServerId(serverId);
			jcm.setPlayerId(playerId);
			Long time = Integer.valueOf(value) == 0 ? 0L : System.currentTimeMillis() + TimeUtils.MINUTE_TIME * Integer.valueOf(value) ;
			switch(name){
				case "chattingBanTime" : jcm.setChattingBanTime(time) ; break;
				case "mailBanTime" : jcm.setMailBanTime(time) ; break;
			}
			jcmService.updateInfo(jcm);
		}

		ServerList server = serverListService.getServerById(serverId);
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		String sign = Misc.md5("merge"+playerId+name+value);
		map.put("sign", sign);
		map.put("playerId", playerId);
		map.put("name", name);
		map.put("value", value);
		JSONObject jo = JSONObject.fromObject(map);
		logger.info("- ---| merge |--- -"+jo.toString());
		host = "http://" + IP + ":" + PORT + "/gm/merge";
		String result = HttpUtils.jsonPost(host, jo.toString());
		Gmlogs gmlogs = UserUtils.addGmlogs("修改", UserUtils.getCurrrentUserName()+" 修改-- ["+serverInfo+"] -- ["+playerName+"] 的 ["+name+"]，数量=["+value+"]");
		gmlogsService.addGmlogs(gmlogs);
		logger.info(result);
		return result;
	}

	@RequestMapping("/mergeItem")
	public @ResponseBody String mergeItem(Long playerId,Long serverId,Long id,String name,String value,String playerName,String serverInfo){
		Map<String,Object> map = new HashMap<String,Object>();
		ServerList server = serverListService.getServerById(serverId);
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		String sign = Misc.md5("merge"+id+name+value);
		map.put("sign", sign);
		map.put("id", id);
		map.put("playerId", playerId);
		map.put("name", name);
		map.put("value", value);
		JSONObject jo = JSONObject.fromObject(map);
		logger.info("- ---| mergeItem |--- -"+jo.toString());
		host = "http://" + IP + ":" + PORT + "/gm/mergeItem";
		String result = HttpUtils.jsonPost(host, jo.toString());
		Gmlogs gmlogs = UserUtils.addGmlogs("修改", UserUtils.getCurrrentUserName()+" 修改-- ["+serverInfo+"] -- ["+playerName+"] 的 ["+name+"]，数量=["+value+"]");
		gmlogsService.addGmlogs(gmlogs);
		logger.info(result);
		return result;
	}

	@RequestMapping("/mergeLevel")
	public @ResponseBody String mergeLevel(Long playerId,Long serverId,Long levelId,String playerName,String serverInfo){
		Map<String,Object> map = new HashMap<String,Object>();
		ServerList server = serverListService.getServerById(serverId);
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		String sign = "";
		try {
			sign = CryptUtils.GetMD5Code("mergeLevel"+playerId+(levelId-1));
		}catch (Exception e){
			logger.error("mergeLevel 数据加密失败！", e);
		}
		map.put("sign", sign);
		map.put("levelId", levelId-1);
		map.put("playerId", playerId);
		map.put("serverId", serverId);
		JSONObject jo = JSONObject.fromObject(map);
		logger.info("- ---| mergeLevel |--- -"+jo.toString());
		host = "http://" + IP + ":" + PORT + "/gm/mergeLevel";
		String result = HttpUtils.jsonPost(host, jo.toString());
		Gmlogs gmlogs = UserUtils.addGmlogs("修改", UserUtils.getCurrrentUserName()+" 修改-- ["+serverInfo+"] -- ["+playerName+"] 的 [挑战关卡]，关卡ID=["+levelId+"]");
		gmlogsService.addGmlogs(gmlogs);
		logger.info(result);
		return result;
	}

	@RequestMapping("/delGoods")
	public @ResponseBody String delGoods(@RequestBody BatchDelete bd){
		Map<String,Object> map = new HashMap<String,Object>();
		ServerList server = serverListService.getServerById(bd.getServerId());
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		String ids = MapUtils.getGoodsId(bd);
		String sign = Misc.md5("delObject"+bd.getPlayerId()+ids+bd.getType());
		map.put("sign", sign);
		map.put("playerId", Long.valueOf(bd.getPlayerId()));
		map.put("goodsId", ids);
		map.put("type", Integer.valueOf(bd.getType()));
		JSONObject jo = JSONObject.fromObject(map);
		logger.info("- ---| delGoods |--- -"+jo.toString());
		host = "http://" + IP + ":" + PORT + "/gm/delObject";
		String result = HttpUtils.jsonPost(host, jo.toString());
		Gmlogs gmlogs = UserUtils.addGmlogs("删除", UserUtils.getCurrrentUserName()+" 删除-- ["+bd.getServerInfo()+"] -- ["+bd.getPlayerName()+"] 的物品，物品ID=["+ids+"]");
		gmlogsService.addGmlogs(gmlogs);
		logger.info(result);
		return result;
	}

	@RequestMapping("/recharge")
	public @ResponseBody String recharge(Long playerId,Long serverId,String index,float dollar,String playerName,String serverInfo){
		int realIndex = -1;
		if(StringUtils.isNotNull(index))
			realIndex = Integer.parseInt(index);

		Map<String,Object> map = new HashMap<String,Object>();
		ServerList server = serverListService.getServerById(serverId);
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		String sign = Misc.md5("topup"+playerId+dollar+realIndex);
		map.put("sign", sign);
		map.put("playerId", playerId);
		map.put("dollar", dollar);
		map.put("index", realIndex);
		JSONObject jo = JSONObject.fromObject(map);
		logger.info("- ---| recharge |--- -"+jo.toString());
		host = "http://" + IP + ":" + PORT + "/gm/topup";
		String result = HttpUtils.jsonPost(host, jo.toString());

		Gmlogs gmlogs = new Gmlogs();
		if(realIndex == -1){
			gmlogs = UserUtils.addGmlogs("充值", UserUtils.getCurrrentUserName()+" 向-- ["+serverInfo+"] -- ["+playerName+"] 充值 ["+(int)dollar+"]元宝");
		}else {
			gmlogs = UserUtils.addGmlogs("充值", UserUtils.getCurrrentUserName()+" 向-- ["+serverInfo+"] -- ["+playerName+"] 充值 ，充值档位是"+realIndex);
		}
		gmlogsService.addGmlogs(gmlogs);

		JSONObject retObj = JSONObject.fromObject(result);
		if(retObj.get("ret").equals("1")){
			retMap.put("ret", 1);
		}else{
			retMap.put("ret", -1);
		}
		return JSONObject.fromObject(retMap).toString();
	}

	@RequestMapping("/giftRecharge")
	public @ResponseBody String giftRecharge(Long playerId,Long serverId,String tplId,String playerName,String serverInfo){

		Map<String,Object> map = new HashMap<String,Object>();
		ServerList server = serverListService.getServerById(serverId);
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		tplId = tplId.trim();
		String[] tplIdStr = tplId.split("_");
        try {
            if(!tplIdStr[0].equals("appBuyItem") || tplIdStr[1].length() > 10 || !StringUtils.isNumeric(tplIdStr[1])){
                throw new Exception("");
            }
        } catch (Exception e) {
            retMap.put("ret", "礼包编号不合法，添加失败");
            logger.error("playerId:"+playerId+",tplId:"+tplId,e);
            return JSONObject.fromObject(retMap).toString();
        }
        String sign = Misc.md5("topup2"+playerId+tplId);
		map.put("sign", sign);
		map.put("playerId", playerId);
		map.put("tplId", tplId);
		JSONObject jo = JSONObject.fromObject(map);
		logger.info("- ---| topup2 |--- -"+jo.toString());
		host = "http://" + IP + ":" + PORT + "/gm/topup2";
		String result = HttpUtils.jsonPost(host, jo.toString());

		Gmlogs gmlogs = UserUtils.addGmlogs("礼包充值", UserUtils.getCurrrentUserName()+" 向-- ["+serverInfo+"] -- ["+playerName+"] 充值礼包，编号： ["+tplId+"]");

		gmlogsService.addGmlogs(gmlogs);

		JSONObject retObj = JSONObject.fromObject(result);
		String ret = retObj.get("ret").toString();
		switch (ret){
			case "1":
				retMap.put("ret", "添加成功");
				break;

			case "-1":
				retMap.put("ret", "玩家不存在或参数不合法");
				break;

			case "-2":
				retMap.put("ret", "参数为空");
				break;

			case "-3":
				retMap.put("ret", "md5验证失败");
				break;

			case "-4":
				retMap.put("ret", "没有此玩家");
				break;

			case "-5":
				retMap.put("ret", "没有此档位");
				break;

			default:
				retMap.put("ret", "添加失败");
				break;
		}

		return JSONObject.fromObject(retMap).toString();
	}

	@RequestMapping("/kickedOffPlayer")
	public @ResponseBody String kickedOffPlayer(Long playerId,Long serverId,String playerName,String serverInfo,int type){
		Map<String,Object> map = new HashMap<String,Object>();
		ServerList server = serverListService.getServerById(serverId);
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		String sign = Misc.md5("kickedOffOnePlayer"+playerId);
		map.put("sign", sign);
		map.put("type", type);
		map.put("playerId", playerId);
		map.put("serverId", serverId);
		JSONObject jo = JSONObject.fromObject(map);
		logger.info("- ---| kickedOffPlayer |--- -"+jo.toString());
		host = "http://" + IP + ":" + PORT + "/gm/kickedOffPlayer";
		String result = HttpUtils.jsonPost(host, jo.toString());
		JSONObject retObj = JSONObject.fromObject(result);
		Gmlogs gmlogs = new Gmlogs();
		if(type == 1){
			gmlogs = UserUtils.addGmlogs("踢掉所有在线玩家", UserUtils.getCurrrentUserName()+" 踢掉-- ["+serverInfo+"] -- 的 所有在线玩家");
		}else{
			gmlogs = UserUtils.addGmlogs("踢掉在线玩家", UserUtils.getCurrrentUserName()+" 踢掉-- ["+serverInfo+"] -- ["+playerName+"] 玩家");
		}
		gmlogsService.addGmlogs(gmlogs);
		logger.info(result);
		retMap.clear();
		if(retObj.get("ret").equals("1")){
			retMap.put("ret", 1);
		}else{
			retMap.put("ret", -1);
		}
		return JSONObject.fromObject(retMap).toString();
	}

	/**
	 * @param playerId
	 * @param serverId
	 * @param startDate
	 * @param endDate
	 * @param checkedValue
	 * @param playerName
	 * @param username
	 * @return
	 *
	 * ----------------------------------------
	 * @date 2019-06-14 20:20:21
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@RequestMapping("/searchRechargehistory")
	public @ResponseBody String searchRechargehistory(Long playerId,Long serverId,String startDate,String endDate,String checkedValue,String playerName,String username){
		if(StringUtils.isNotNull(startDate))
			startDate = TimeUtils.dateToStampWithDetail(TimeUtils.checkDate(startDate, endDate).get("startDate"));

		if(StringUtils.isNotNull(endDate))
			endDate = TimeUtils.dateToStampWithDetail(TimeUtils.checkDate(startDate, endDate).get("endDate"));

		String sign = Misc.md5("searchRechargehistory"+serverId);
		ServerList server = serverListService.getServerById(serverId);
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		List<EBehaviorType> types = new ArrayList<EBehaviorType>();
		if(StringUtils.isNotNull(checkedValue)){
			String[] arr = checkedValue.split(",");

			types = new ArrayList<EBehaviorType>();
			for(String str : arr){
				switch(Integer.valueOf(str)){
					case 1:
						types.add(EBehaviorType.TOP_UP);
						break;
					case 2:
						types.add(EBehaviorType.BUY);
						break;
					case 3:
						types.add(EBehaviorType.LOT);
						break;
					case 4:
						for (EBehaviorType type : EBehaviorType.values()) {
							if (type != EBehaviorType.TOP_UP || type != EBehaviorType.BUY || type != EBehaviorType.LOT) {
								types.add(type);
							}
						}
						break;
				}
			}
		}

		Map<String,Object>  map =  new HashMap<String,Object>();
		map.put("serverId", serverId);
		map.put("playerId", playerId);
		map.put("playerName", playerName);
		map.put("username", username);
		map.put("startTime", startDate);
		map.put("passTime", endDate);
		map.put("types", types);
		map.put("sign", sign);
		JSONObject jo = JSONObject.fromObject(map);
		logger.info("- ---| searchRechargehistory |--- -"+jo.toString());
		host = "http://" + IP + ":" + PORT + "/gm/behaviors";
		String result = HttpUtils.jsonPost(host, jo.toString());
		JSONArray retArr = JSONArray.fromObject(result);
		JSONArray formatRetArr = new JSONArray();
		if(retArr.size() > 0){
            for(int i = 0 ; i < retArr.size() ; i++){
                JSONObject jObj = JSONObject.fromObject(retArr.get(i));
                if(jObj.containsKey("createTime")){
                	String createTime = TimeUtils.stampToDateWithMill(jObj.getString("createTime"));
                    jObj.put("createTime", createTime);
					formatRetArr.add(jObj);
                }
            }
        }
		logger.info(formatRetArr);
		return formatRetArr.toString();
	}

	@RequestMapping("/nextDay")
	public @ResponseBody String nextDay(Long playerId,Long serverId,String playerName,String serverInfo){

		Map<String,Object> map = new HashMap<String,Object>();
		ServerList server = serverListService.getServerById(serverId);
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		String sign = Misc.md5("nextDay"+playerId);
		map.put("sign", sign);
		map.put("playerId", playerId);
		JSONObject jo = JSONObject.fromObject(map);
		logger.info("- ---| nextDay |--- -"+jo.toString());
		host = "http://" + IP + ":" + PORT + "/gm/nextDay";
		String result = HttpUtils.jsonPost(host, jo.toString());
		logger.info(result);

		Gmlogs gmlogs = UserUtils.addGmlogs("修改", UserUtils.getCurrrentUserName()+" 修改-- ["+serverInfo+"] -- ["+playerName+"],刷新下一天");
		gmlogsService.addGmlogs(gmlogs);

		JSONObject retObj = JSONObject.fromObject(result);
		retMap.clear();
		if(retObj.get("ret").equals("1")){
			retMap.put("ret", 1);
		}else{
			retMap.put("ret", -1);
		}
		return JSONObject.fromObject(retMap).toString();
	}

	@RequestMapping("/mergeSixDao")
	public @ResponseBody String mergeSixDao(Long playerId,Long serverId,String playerName,String serverInfo,int sixDaoLevel, int sixDaoCen,int type){

		Map<String,Object> map = new HashMap<String,Object>();
		ServerList server = serverListService.getServerById(serverId);
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		String sign = Misc.md5("mergeSixDao"+playerId+sixDaoLevel+sixDaoCen+type);
		map.put("sign", sign);
		map.put("playerId", playerId);
		map.put("sixDaoLevel", sixDaoLevel);
		map.put("sixDaoCen", sixDaoCen);
		map.put("type", type);
		JSONObject jo = JSONObject.fromObject(map);
		logger.info("- ---| mergeSixDao |--- -"+jo.toString());
		host = "http://" + IP + ":" + PORT + "/gm/mergeSixDao";
		String result = HttpUtils.jsonPost(host, jo.toString());
		logger.info(result);
		Gmlogs gmlogs = new Gmlogs();
		if(type == 1){
			gmlogs = UserUtils.addGmlogs("修改", UserUtils.getCurrrentUserName()+" 修改-- ["+serverInfo+"] -- ["+playerName+"]的六道关卡到["+sixDaoLevel+"关"+sixDaoCen+"层]");
		}else if(type == 2){
			gmlogs = UserUtils.addGmlogs("修改", UserUtils.getCurrrentUserName()+" 修改-- ["+serverInfo+"] -- ["+playerName+"]的六道关卡到最后一层");
		}
		gmlogsService.addGmlogs(gmlogs);

		JSONObject retObj = JSONObject.fromObject(result);
		retMap.clear();
		if(retObj.get("ret").equals("1")){
			retMap.put("ret", 1);
		}else{
			retMap.put("ret", -1);
		}
		return JSONObject.fromObject(retMap).toString();
	}

	@RequestMapping("/batchUnEquipNpc")
	public @ResponseBody String batchUnEquipNpc(Long playerId,Long serverId,String playerName,String serverInfo){

		Map<String,Object> map = new HashMap<String,Object>();
		ServerList server = serverListService.getServerById(serverId);
		IP = StringUtils.isNull(server.getNip())?server.getIp():server.getNip();
		PORT = server.getSlavePort();
		String sign = Misc.md5("batchUnEquipNpc"+playerId);
		map.put("sign", sign);
		map.put("playerId", playerId);
		JSONObject jo = JSONObject.fromObject(map);
		logger.info("- ---| batchUnEquipNpc |--- -"+jo.toString());
		host = "http://" + IP + ":" + PORT + "/gm/batchUnEquipNpc";
		String result = HttpUtils.jsonPost(host, jo.toString());
		logger.info(result);
		Gmlogs gmlogs = UserUtils.addGmlogs("修改", UserUtils.getCurrrentUserName()+" 下阵-- ["+serverInfo+"] -- ["+playerName+"]的所有上阵将领");;
		gmlogsService.addGmlogs(gmlogs);

		JSONObject retObj = JSONObject.fromObject(result);
		retMap.clear();
		if(retObj.get("ret").equals("1")){
			retMap.put("ret", 1);
		}else{
			retMap.put("ret", -1);
		}
		return JSONObject.fromObject(retMap).toString();
	}

	@RequestMapping("/getGoods")
	public @ResponseBody String getGoods(HttpServletRequest request){
		String filePath = request.getSession().getServletContext().getRealPath("");
		//logger.info("[GM工具]添加物品加载 csv文件路径----->"+filePath);
		JSONArray result = XlsUtils.getGoods(filePath);
		return result.toString();
	}

	@RequestMapping("/loadCSV")
	public @ResponseBody String loadCSV(@RequestParam(value = "thisId") String editTitle, HttpServletRequest request){
		String filePath = request.getSession().getServletContext().getRealPath("");
		JSONArray result = new JSONArray();
		if (org.apache.commons.lang3.StringUtils.isNotBlank(editTitle)){
			switch (editTitle){
				case _LEVELNAME:
					result = XlsUtils.getLevel(filePath);
					break;
				case LEVELID:
					result = XlsUtils.getLevelId(filePath);
					break;
				case HANGLEVEL:
					result = XlsUtils.getHanglevel(filePath);
					break;
				case GODID:
					result = XlsUtils.getGodId(filePath);
					break;
				case PETLEVELID:
					result = XlsUtils.getPetLevelId(filePath);
					break;
				case BOOKLEVELID:
					result = XlsUtils.getBookLevelId(filePath);
					break;
				case CAMPAIGNLEVELID:
					result = XlsUtils.getCampaignLevelId(filePath);
					break;
				case DESTINYLEVELID:
					result = XlsUtils.getDestinyLevelId(filePath);
					break;
				default:
					break;
			}

		}
		return result.toString();
	}

}

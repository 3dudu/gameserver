package com.icegame.sysmanage.controller;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.sysmanage.entity.JActivityGift;
import com.icegame.sysmanage.entity.JActivityGiftKey;
import com.icegame.sysmanage.service.IJActivityGiftKeyService;
import com.icegame.sysmanage.service.IJActivityGiftService;
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sysmgr/giftkey")
public class ActivityGiftKeyController {

	private static Logger logger = Logger.getLogger(ActivityGiftKeyController.class);

	@Autowired
	private IJActivityGiftKeyService ijActivityGiftKeyService;

	@RequestMapping("gotoActivityGiftKey")
	public String gotoActivityGiftKey() {
		return "sysmanage/giftkey/activitygiftkey";
	}

	@RequestMapping("getActivityGiftKeyList")
	@ResponseBody
	public String getActivityGiftKeyList(int pageNo, int pageSize) {

		//logger.info("------------------------------------------->" + startDate);
		//logger.info("------------------------------------------->" + endDate);
		// 获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		// 获取分页数据总和
		List<JActivityGiftKey> jActivityGiftList = new ArrayList<JActivityGiftKey>();
		JActivityGiftKey jActivityGiftKey = new JActivityGiftKey();
		PageInfo<JActivityGiftKey> pageInfo = this.ijActivityGiftKeyService.getGiftKeyList(jActivityGiftKey,pageParam);
		jActivityGiftList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(jActivityGiftList);
		// 获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	/**
	 *
	 * @param editFlag
	 * @param id
	 * @param model
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 11:20:53
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@RequestMapping("/gotoActivityGiftKeyEdit")
	@ResponseBody
	public String gotoActivityGiftKeyEdit(@ModelAttribute("editFlag") int editFlag, Long id, Model model) {
		JSONObject jsonObj = new JSONObject();
		JActivityGiftKey jActivityGiftKey = new JActivityGiftKey();
		if (editFlag == 2) {
			jActivityGiftKey = ijActivityGiftKeyService.getGiftKeyById(id);
			if(jActivityGiftKey != null){
				jActivityGiftKey.setStartTime(TimeUtils.stampToDateWithMill(jActivityGiftKey.getStartTime()));
				jActivityGiftKey.setPassTime(TimeUtils.stampToDateWithMill(jActivityGiftKey.getPassTime()));
			}
			jsonObj = JSONObject.fromObject(jActivityGiftKey);
		}
		return jsonObj.toString();
	}

	/**
	 * 发送邮件
	 * 
	 * @author chsterccw
	 * @date 2018年8月20日
	 */
	/*@RequestMapping("/sendAllSrvMail")
	@ResponseBody
	public String sendAllSrvMail(Long id) {
		String retFlag = "";
		
		JSONObject syncJson = new JSONObject();
		AllSrvMail allSrvMail = allSrvMailService.getAllSrvMailById(id);
		Map<String, Object> syncData = new HashMap<String, Object>();
 		syncData.put("id", allSrvMail.getId());
		syncData.put("subject", allSrvMail.getSubject());
		syncData.put("createTime", allSrvMail.getCreateTime());
		syncData.put("context", allSrvMail.getContext());
		syncData.put("awardStr", StringUtils.awardStrformart(allSrvMail.getAwardStr()));
		syncJson = JSONObject.fromObject(syncData);
		// 向所有的slave节点同步 获取所有slave的节点列表
		List<SlaveNodes> slaveNodesList = slaveNodesService.getSlaveNodesListNoPage();
		for (int i = 0; i < slaveNodesList.size(); i++) {
			String serverId = slaveNodesList.get(i).getId() + "." + slaveNodesList.get(i).getName();
			String hosts = "http://" + ( StringUtils.isNull(slaveNodesList.get(i).getNip())?slaveNodesList.get(i).getIp():slaveNodesList.get(i).getNip() ) + ":" + slaveNodesList.get(i).getPort() + "/sync/addallsrvmail";
			logger.info("[同步全服邮件****发送****的数据--------------------->]" + syncJson.toString());
			String result = HttpUtils.jsonPost(hosts, syncJson.toString());
			logger.info("[同步全服邮件****接收****的数据--------------------->]" + result.toString());
			SyncMail syncMail = new SyncMail();
			JSONObject retObj = JSONObject.fromObject(result);
			JSONObject reRetObj = new JSONObject();
			if (!retObj.get("ret").equals("0")) {
				for (int j = 0; j < 5; j++) {
					String relResult = HttpUtils.jsonPost(hosts, syncJson.toString());
					reRetObj = JSONObject.fromObject(relResult);
					if (reRetObj.get("ret").equals("0")) {
						retFlag = "发送成功";
						syncMail = new SyncMail(TimeUtils.getDateDetail(), hosts, "成功", "全服邮件", serverId,
								syncJson.toString(), "");
						break;
					}
					retFlag = "发送失败";
					syncMail = new SyncMail(TimeUtils.getDateDetail(), hosts, "失败", "全服邮件", serverId,
							syncJson.toString(), "0");
				}
			} else {
				retFlag = "发送成功";
				syncMail = new SyncMail(TimeUtils.getDateDetail(), hosts, "成功", "全服邮件", serverId, syncJson.toString(),
						"");
			}
			syncMailService.addSyncMail(syncMail);
		}
		Ret ret = new Ret();
		ret.setRet(0);
		ret.setMsg(retFlag);
		return JSONObject.fromObject(ret).toString();
	}*/

	@RequestMapping("/saveActivityGiftKey")
	public @ResponseBody Map<String, Object> saveActivityGiftKey(@RequestBody JActivityGiftKey jActivityGiftKey) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		jActivityGiftKey.setStartTime(TimeUtils.dateToStampWithDetail(jActivityGiftKey.getStartTime()));
		jActivityGiftKey.setPassTime(TimeUtils.dateToStampWithDetail(jActivityGiftKey.getPassTime()));
		try {
			if (jActivityGiftKey != null && jActivityGiftKey.getId() != null) {
				ijActivityGiftKeyService.updateGiftKey(jActivityGiftKey);
				resultMap.put("result", "修改礼包码信息成功");
				logger.info("修改礼包码");
			} else {// 增加
				ijActivityGiftKeyService.addGiftKey(jActivityGiftKey);
				resultMap.put("result", "增加礼包码信息成功");
				logger.info("增加礼包码");
			}
		} catch (Exception e) {
			resultMap.put("result", "增加礼包码信息成功");
			logger.error("增加礼包码信息成功", e);
		}
		return resultMap;
	}

	@RequestMapping("/delActivityGiftKey")
	public @ResponseBody Map<String, Object> delActivityGiftKey(Long id) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (ijActivityGiftKeyService.delGiftKey(id)) {
				resultMap.put("result", "删除礼包码成功");
				logger.info("删除礼包码" + id);
			}
		} catch (Exception e) {
			logger.error("删除礼包码成功", e);
		}
		return resultMap;
	}

	@RequestMapping("/checkExistCdKey")
	public @ResponseBody String checkExistUser(String cdKey){
		JActivityGiftKey jActivityGiftKey = new JActivityGiftKey();
		jActivityGiftKey.setCdKey(cdKey);
		List<JActivityGiftKey> keyList = ijActivityGiftKeyService.checkExistCdKey(jActivityGiftKey);
		if(keyList.size() > 0) {
			return "1";
		}else {
			return "0";
		}
	}

}

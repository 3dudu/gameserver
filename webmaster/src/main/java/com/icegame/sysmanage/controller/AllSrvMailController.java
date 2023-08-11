package com.icegame.sysmanage.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.entity.Ret;
import com.icegame.framework.utils.*;
import com.icegame.sysmanage.components.GroupUtils;
import com.icegame.sysmanage.entity.AllSrvMail;
import com.icegame.sysmanage.entity.ServerList;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.entity.SyncMail;
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
@RequestMapping("/sysmgr/allsrvmail")
public class AllSrvMailController {

	private static Logger logger = Logger.getLogger(AllSrvMailController.class);

	@Autowired
	private IAllSrvMailService allSrvMailService;

	@Autowired
	private ISyncMailService syncMailService;

	@Autowired
	private ISlaveNodesService slaveNodesService;
	
	@Autowired
	private IMailMaxIdService mailMaxIdService;

	@Autowired
	private IServerListService serverListService;

    @Autowired
    private GroupUtils groupUtils;

	@RequestMapping("gotoAllSrvMail")
	public String gotoAllSrvMail() {
		return "sysmanage/mail/allsrvmail";
	}

	@RequestMapping("getAllSrvMailList")
	@ResponseBody
	public String getAllSrvMailList(String startDate, String endDate, int pageNo, int pageSize) {
		if (startDate.equals("") && endDate.equals("")) {
			startDate = TimeUtils.getStartTime();
			endDate = TimeUtils.getEndTime();
		} else {
			startDate = TimeUtils.checkDate(startDate, endDate).get("startDate");
			endDate = TimeUtils.checkDate(startDate, endDate).get("endDate");
		}
		//logger.info("------------------------------------------->" + startDate);
		//logger.info("------------------------------------------->" + endDate);
		// 获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		// 获取分页数据总和
		List<AllSrvMail> allSrvMailList = new ArrayList<AllSrvMail>();
		AllSrvMail allSrvMail = new AllSrvMail();
		allSrvMail.setStartDate(startDate);
		allSrvMail.setEndDate(endDate);
		PageInfo<AllSrvMail> pageInfo = this.allSrvMailService.getAllSrvMailList(allSrvMail, pageParam);
		allSrvMailList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(allSrvMailList);
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
	 * @date 2019-06-15 11:11:59
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@RequestMapping("/gotoAllSrvMailEdit")
	@ResponseBody
	public String gotoAllSrvMailEdit(@ModelAttribute("editFlag") int editFlag, Long id, Model model) {
		JSONObject jsonObj = new JSONObject();
		AllSrvMail allSrvMail = new AllSrvMail();
		if (editFlag == 2) {
			allSrvMail = allSrvMailService.getAllSrvMailById(id);
			if(allSrvMail != null){
				allSrvMail.setCreateTime(TimeUtils.stampToDateWithMill(allSrvMail.getCreateTime()));
			}
			jsonObj = JSONObject.fromObject(allSrvMail);
		}
		return jsonObj.toString();
	}

	/**
	 * 发送邮件
	 * 
	 * @author chsterccw
	 * @date 2018年8月20日
	 */
	@RequestMapping("/sendAllSrvMail")
	@ResponseBody
	public String sendAllSrvMail(Long id) {

        AllSrvMail allSrvMail = allSrvMailService.getAllSrvMailById(id);
        String result = "";

        /**
         * 验证逻辑：
         * 1.如果channel为空，判断当前用户所在用户组是否有指定channel
         *      如果有，说明是某个组的全服邮件，需要拿到此组的所有channel，给拥有此channel的区服发邮件
         *      如果没有，说明是真正的全服邮件
         */

        /**
         * 0 : 根据channel发送
         * 1 : 根据slave发送
         */
        if(allSrvMail.getMailType() == 0){
            /**
             * 如果channel为空，判断此用户用户组的channel 是否有指定channel
             *      如果有，说明是某个组的全服邮件，需要拿到此组的所有channel，给拥有此channel的区服发邮件
             *      如果没有，说明是真正的全服邮件
             * 如果不为空，直接发送即可
             */
            if(allSrvMail.getChannel() == null || allSrvMail.getChannel().equals("")){
                String hasChannel = groupUtils.getGroupHasChannel();
                if(StringUtils.isNotNull(hasChannel)){

                    // 此处的从用户组表拿到的channel, 需要用 换行分割
                    result = allSrvMailService.syncAllSrvMailByChannel(allSrvMail, hasChannel, "\r\n");
                } else {
                    result = allSrvMailService.syncAllSrvMail(id);
                }
            } else {
                // 此处的从邮件表拿到的channel, 需要用 , 分割
                result = allSrvMailService.syncAllSrvMailByChannel(allSrvMail, allSrvMail.getChannel(),",");
            }

        } else if(allSrvMail.getMailType() == 1) {
            result = allSrvMailService.syncAllSrvMailBySlave(allSrvMail, allSrvMail.getSlave());
        }

		return result;
	}

	@RequestMapping("/saveAllSrvMail")
	public @ResponseBody Map<String, Object> saveAllSrvMail(@RequestBody AllSrvMail allSrvMail) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		if(allSrvMail.getSelectedChannel() != null){
			allSrvMail.setChannel(ArrayUtils.toStr(allSrvMail.getSelectedChannel()));
		}

		if(allSrvMail.getSelectedSlave() != null){
			allSrvMail.setSlave(ArrayUtils.toStr(allSrvMail.getSelectedSlave()));
		}

		allSrvMail.setCreateTime(String.valueOf(System.currentTimeMillis()));

		try {
			if (allSrvMail != null && allSrvMail.getId() != null) {
				allSrvMailService.updateAllSrvMail(allSrvMail);
				resultMap.put("result", "修改记录信息成功");
				logger.info("修改邮件");
			} else {// 增加
				allSrvMail.setId(mailMaxIdService.getMailMaxId());
				allSrvMail.setStatus(MailStatus.NEW.getStatus());
				allSrvMailService.addAllSrvMail(allSrvMail);
				resultMap.put("result", "增加记录信息成功");
				logger.info("增加邮件");
			}
		} catch (Exception e) {
			resultMap.put("result", "操作记录失败");
			logger.error("操作邮件失败", e);
		} finally {
			// refreshAll(server);
		}
		return resultMap;
	}

	@RequestMapping("/delAllSrvMail")
	public @ResponseBody Map<String, Object> delAllSrvMail(Long id) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (allSrvMailService.delAllSrvMail(id)) {
				resultMap.put("result", "删除邮件成功");
				logger.info("删除邮件" + id);
			}
		} catch (Exception e) {
			logger.error("删除邮件失败", e);
		}
		return resultMap;
	}


	@RequestMapping("/getAllChannel")
	public @ResponseBody String getAllChannel(){
		List<ServerList> serverLists = serverListService.getAllChannelList();
		return JSON.toJSONString(serverLists);
	}

}

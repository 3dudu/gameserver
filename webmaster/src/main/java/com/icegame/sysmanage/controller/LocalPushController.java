package com.icegame.sysmanage.controller;


import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.StringUtils;
import com.icegame.sysmanage.components.GroupUtils;
import com.icegame.sysmanage.entity.LocalPush;
import com.icegame.sysmanage.entity.Notice;
import com.icegame.sysmanage.service.ILocalPushService;
import com.icegame.sysmanage.service.INoticeService;
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
@RequestMapping("/sysmgr/localpush")
public class LocalPushController {

	private static Logger logger = Logger.getLogger(LocalPushController.class);

	@Autowired
	private ILocalPushService localPushService;

	@Autowired
	private GroupUtils groupUtils;

	@RequestMapping("gotoLocalPush")
	public String gotoLocalPush(){
		return "sysmanage/localpush/localpush";
	}

	@RequestMapping("getLocalPushList")
	@ResponseBody
	public String getLocalPushList(String channel,int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<LocalPush> localPushList = new ArrayList<LocalPush>();
		LocalPush localPush = new LocalPush();localPush.setChannel(channel);
		/**
		 * 此处添加联运权限验证
		 */
		String hasChannel = groupUtils.getGroupHasChannel();
		if(StringUtils.isNotNull(hasChannel)){
			hasChannel = StringUtils.multFormat(hasChannel);
		}
		localPush.setHasChannel(hasChannel);
		PageInfo<LocalPush> pageInfo = this.localPushService.getLocalPushList(localPush, pageParam);
		localPushList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(localPushList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("/gotoLocalPushEdit")
	@ResponseBody
	public String gotoLocalPushEdit(@ModelAttribute("editFlag") int editFlag,
			Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		LocalPush localPush = new LocalPush();
		if(editFlag == 2){
			localPush = localPushService.getLocalPushById(id);
			jsonObj = JSONObject.fromObject(localPush);
		}
		return jsonObj.toString();
	}

	@RequestMapping("/getChannelList")
	@ResponseBody
	public String getChannelList(){
		List<LocalPush> channelList = localPushService.getChannelList();
		JSONArray array = JSONArray.fromObject(channelList);
		return array.toString();
	}

	@RequestMapping("/checkExistChannel")
	public @ResponseBody String checkExistChannel(String channel){
		LocalPush localPush = new LocalPush();
		localPush.setChannel(channel);
		List<LocalPush> noticeList = localPushService.checkExistChannel(localPush);
		if(noticeList.size() > 0) {
			return "1";
		}else {
			return "0";
		}
	}

	@RequestMapping("/saveLocalPush")
	public @ResponseBody Map<String,Object> saveLocalPush(@RequestBody LocalPush localPush){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(localPush != null){
				if(localPush.getChannelCode().contains("，")){
					resultMap.put("result", "保存失败,ChannelCode存在中文逗号！");
					return resultMap;
				}
			}
			if(localPush != null && localPush.getId() != null){
				localPushService.updateLocalPush(localPush);
				resultMap.put("result", "修改推送记录成功");
			}else{//增加
				localPushService.addLocalPush(localPush);
				resultMap.put("result", "增加推送记录成功");
			}
		}catch(Exception e){
			resultMap.put("result", "编辑推送记录失败");
			logger.error("编辑推送记录失败",e);
		}
		return resultMap;
	}

	@RequestMapping("/delLocalPush")
	public @ResponseBody Map<String,Object> delLocalPush(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(localPushService.delLocalPush(id)){
				resultMap.put("result", "删除本地推送成功");
			}
		}catch(Exception e){
			logger.error("删除本地推送失败",e);
		}
		return resultMap;
	}
}

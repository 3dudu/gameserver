package com.icegame.sysmanage.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.icegame.framework.utils.StringUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.sysmanage.components.GroupUtils;
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
import com.icegame.sysmanage.entity.Notice;
import com.icegame.sysmanage.service.INoticeService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/sysmgr/notice")
public class NoticeController {

	private static Logger logger = Logger.getLogger(NoticeController.class);

	@Autowired
	private INoticeService noticeService;

	@Autowired
	private GroupUtils groupUtils;

	@RequestMapping("gotoNotice")
	public String gotoPfOptions(){
		return "sysmanage/notice/notice";
	}

	@RequestMapping("getNoticeList")
	@ResponseBody
	public String getNoticeList(String channel,int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<Notice> noticeList = new ArrayList<Notice>();
		Notice notice = new Notice();
		notice.setChannel(channel);
		/**
		 * 此处添加联运权限验证
		 */
		String hasChannel = groupUtils.getGroupHasChannel();
		if(StringUtils.isNotNull(hasChannel)){
			hasChannel = StringUtils.multFormat(hasChannel);
		}
		notice.setHasChannel(hasChannel);
		PageInfo<Notice> pageInfo = this.noticeService.getNoticeList(notice, pageParam);
		noticeList = pageInfo.getList();
		for (Notice notice1 : noticeList) {
			String startTime = notice1.getStartTime();
			String endTime = notice1.getEndTime();
			if(StringUtils.isNotNull(startTime))
				startTime = TimeUtils.dateToStampWithDetail(TimeUtils.checkDate(startTime, endTime).get("startDate"));

			if(StringUtils.isNotNull(endTime))
				endTime = TimeUtils.dateToStampWithDetail(TimeUtils.checkDate(startTime, endTime).get("endDate"));
			long curTime = System.currentTimeMillis();
			if(curTime >= Long.valueOf(startTime) && curTime <= Long.valueOf(endTime)){
				notice1.setOpenFlag(1);
			}
		}
		JSONArray jsonArr = JSONArray.fromObject(noticeList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("/gotoNoticeEdit")
	@ResponseBody
	public String gotoNoticeEdit(String startTime,String endTime,@ModelAttribute("editFlag") int editFlag,
			Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		Notice notice = new Notice();
		if(editFlag == 2){
			notice = noticeService.getNoticeById(id);
			if(notice != null){
				notice.setStartTime(TimeUtils.stampToDateWithMill(notice.getStartTime()));
				notice.setEndTime(TimeUtils.stampToDateWithMill(notice.getEndTime()));
			}
			jsonObj = JSONObject.fromObject(notice);
		}
		return jsonObj.toString();
	}

	@RequestMapping("/getChannelList")
	@ResponseBody
	public String getChannelList(){
		List<Notice> channelList = noticeService.getChannelList();
		JSONArray array = JSONArray.fromObject(channelList);
		return array.toString();
	}

	@RequestMapping("/checkExistNotice")
	public @ResponseBody String checkExistNotice(String channel){
		Notice notice = new Notice();
		notice.setChannel(channel);
		List<Notice> noticeList = noticeService.checkExistNotice(notice);
		if(noticeList.size() > 0) {
			return "1";
		}else {
			return "0";
		}
	}

	@RequestMapping("/saveNotice")
	public @ResponseBody Map<String,Object> saveNotice(@RequestBody Notice notice){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		notice.setStartTime(TimeUtils.dateToStampWithDetail(notice.getStartTime()));
		notice.setEndTime(TimeUtils.dateToStampWithDetail(notice.getEndTime()));
		try{
			if(notice != null && notice.getId() != null){
				noticeService.updateNotice(notice);
				resultMap.put("result", "修改公告成功");
			}else{//增加
				noticeService.addNotice(notice);
				resultMap.put("result", "增加公告成功");
			}
		}catch(Exception e){
			resultMap.put("result", "编辑公告失败");
			logger.error("编辑公告失败",e);
		}
		return resultMap;
	}

	@RequestMapping("/delNotice")
	public @ResponseBody Map<String,Object> delNotice(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(noticeService.delNotice(id)){
				resultMap.put("result", "删除公告成功");
			}
		}catch(Exception e){
			logger.error("删除公告失败",e);
		}
		return resultMap;
	}
}

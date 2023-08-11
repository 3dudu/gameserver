package com.icegame.sysmanage.controller;


import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.entity.Ret;
import com.icegame.framework.utils.*;
import com.icegame.sysmanage.entity.SelSrvMail;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.entity.SyncMail;
import com.icegame.sysmanage.service.IMailMaxIdService;
import com.icegame.sysmanage.service.ISelSrvMailService;
import com.icegame.sysmanage.service.ISlaveNodesService;
import com.icegame.sysmanage.service.ISyncMailService;
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
@RequestMapping("/sysmgr/selsrvmail")
public class SelSrvMailController {
	
	private static Logger logger = Logger.getLogger(SelSrvMailController.class);
	
	@Autowired
	private ISelSrvMailService selSrvMailService;
	
	@Autowired
	private ISyncMailService syncMailService;
	
	@Autowired
	private ISlaveNodesService slaveNodesService;
	
	@Autowired
	private IMailMaxIdService mailMaxIdService;

	
	@RequestMapping("gotoSelSrvMail")
	public String gotoSelSrvMail(){
		return "sysmanage/mail/selsrvmail";
	}
	
	@RequestMapping("getSelSrvMailList")
	@ResponseBody
	public String getSelSrvMailList(String startDate,String endDate,int pageNo,int pageSize){
		if(startDate.equals("") && endDate.equals("")){
			startDate = TimeUtils.getStartTime();
			endDate = TimeUtils.getEndTime();	
		}else{
			startDate = TimeUtils.checkDate(startDate, endDate).get("startDate");
			endDate = TimeUtils.checkDate(startDate, endDate).get("endDate");
		}
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<SelSrvMail> roleMailList = new ArrayList<SelSrvMail>();
		SelSrvMail selSrvMail = new SelSrvMail();selSrvMail.setStartDate(startDate);selSrvMail.setEndDate(endDate);
		PageInfo<SelSrvMail> pageInfo = this.selSrvMailService.getSelSrvMailList(selSrvMail, pageParam);
		roleMailList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(roleMailList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("/gotoSelSrvMailEdit")
	@ResponseBody
	public String gotoSelSrvMailEdit(@ModelAttribute("editFlag") int editFlag,
			Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		SelSrvMail selSrvMail = new SelSrvMail();
		if(editFlag == 2){
			selSrvMail = selSrvMailService.getSelSrvMailById(id);
			if(selSrvMail != null){
				selSrvMail.setCreateTime(TimeUtils.stampToDateWithMill(selSrvMail.getCreateTime()));
			}
			jsonObj = JSONObject.fromObject(selSrvMail);
		}
		return jsonObj.toString();   
	}
	
	@RequestMapping("/saveSelSrvMail")
	public @ResponseBody Map<String,Object> saveSelSrvMail(@RequestBody SelSrvMail selSrvMail){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		selSrvMail.setCreateTime(String.valueOf(System.currentTimeMillis()));
		try{
			if(selSrvMail != null && selSrvMail.getId() != null){
				selSrvMailService.updateSelSrvMail(selSrvMail);
				resultMap.put("result", "修改记录信息成功");
				logger.info("修改邮件");
			}else{//增加
				selSrvMail.setId(mailMaxIdService.getMailMaxId());
				selSrvMail.setStatus(MailStatus.NEW.getStatus());
				selSrvMailService.addSelSrvMail(selSrvMail);
				resultMap.put("result", "增加记录信息成功");
				logger.info("增加邮件");
			}	
		}catch(Exception e){
			resultMap.put("result", "操作记录失败");
			logger.error("操作邮件失败",e);
		}finally{
			//refreshAll(server);
		}
		return resultMap;
	}
	
	/**
	 * 发送邮件
	 * @author chsterccw  
	 * @date 2018年8月20日
	 */
	@RequestMapping("/sendSelSrvMail")
	@ResponseBody
	public String sendSelSrvMail(Long id){
		String result = selSrvMailService.syncSelSrvMail(id);
		return result;
	}
	
	@RequestMapping("/delSelSrvMail")
	public @ResponseBody Map<String,Object> delSelSrvMail(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(selSrvMailService.delSelSrvMail(id)){
				resultMap.put("result", "删除邮件成功");
				logger.info("删除邮件"+id);
			}	
		}catch(Exception e){
			logger.error("删除邮件失败",e);
		}
		return resultMap;
	}

}

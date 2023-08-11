package com.icegame.sysmanage.controller;


import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.entity.Ret;
import com.icegame.framework.utils.*;
import com.icegame.sysmanage.entity.RoleMail;
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.entity.SyncMail;
import com.icegame.sysmanage.service.IMailMaxIdService;
import com.icegame.sysmanage.service.IRoleMailService;
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
@RequestMapping("/sysmgr/rolemail")
public class RoleMailController {
	
	private static Logger logger = Logger.getLogger(RoleMailController.class);
	
	@Autowired
	private IRoleMailService roleMailService;
	
	@Autowired
	private ISlaveNodesService slaveNodesService;
	
	@Autowired
	private ISyncMailService syncMailService;
	
	@Autowired
	private IMailMaxIdService mailMaxIdService;

	
	@RequestMapping("gotoRoleMail")
	public String gotoRoleMail(){
		return "sysmanage/mail/rolemail";
	}
	
	@RequestMapping("getRoleMailList")
	@ResponseBody
	public String getRoleMailList(String startDate,String endDate,int pageNo,int pageSize){
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
		List<RoleMail> roleMailList = new ArrayList<RoleMail>();
		RoleMail roleMail = new RoleMail();roleMail.setStartDate(startDate);roleMail.setEndDate(endDate);
		PageInfo<RoleMail> pageInfo = this.roleMailService.getRoleMailList(roleMail, pageParam);
		roleMailList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(roleMailList);
		//获取分页条
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
	 * @date 2019-06-15 11:05:54
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@RequestMapping("/gotoRoleMailEdit")
	@ResponseBody
	public String gotoRoleMailEdit(@ModelAttribute("editFlag") int editFlag,
			Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		RoleMail roleMail = new RoleMail();
		if(editFlag == 2){
			roleMail = roleMailService.getRoleMailById(id);
			if(roleMail != null){
				roleMail.setCreateTime(TimeUtils.stampToDateWithMill(roleMail.getCreateTime()));
			}
			jsonObj = JSONObject.fromObject(roleMail);
		}
		return jsonObj.toString();   
	}
	
	@RequestMapping("/roleMailPreview")
	public @ResponseBody Map<String,Object> roleMailPreview(@RequestBody RoleMail roleMail){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		return resultMap;
	}
	
	@RequestMapping("/saveRoleMail")
	public @ResponseBody Map<String,Object> saveRoleMail(@RequestBody RoleMail roleMail){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		roleMail.setCreateTime(String.valueOf(System.currentTimeMillis()));
		try{
			if(roleMail != null && roleMail.getId() != null){
				roleMailService.updateRoleMail(roleMail);	
				resultMap.put("result", "修改记录信息成功");
				logger.info("修改邮件");
			}else{//增加
				roleMail.setId(mailMaxIdService.getMailMaxId());
				roleMail.setStatus(MailStatus.NEW.getStatus());
				roleMailService.addRoleMail(roleMail);
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
	@RequestMapping("/sendRoleMail")
	@ResponseBody
	public String sendRoleMail(Long id){
		String result = roleMailService.syncRoleMail(id);
		return result;
	}
	
	@RequestMapping("/delRoleMail")
	public @ResponseBody Map<String,Object> delRoleMail(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(roleMailService.delRoleMail(id)){
				resultMap.put("result", "删除邮件成功");
				logger.info("删除邮件"+id);
			}	
		}catch(Exception e){
			logger.error("删除邮件失败",e);
		}
		return resultMap;
	}
	
}

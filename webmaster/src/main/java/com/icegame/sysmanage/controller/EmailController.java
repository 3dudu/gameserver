package com.icegame.sysmanage.controller;


import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.sysmanage.entity.Email;
import com.icegame.sysmanage.entity.ExclusiveCsQQ;
import com.icegame.sysmanage.service.IEmailService;
import com.icegame.sysmanage.service.IExclusiveCsQQService;
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
@RequestMapping("/sysmgr/email")
public class EmailController {
	
	private static Logger logger = Logger.getLogger(EmailController.class);

	@Autowired
	private IEmailService emailService;
	
	@RequestMapping("gotoEmail")
	public String gotoEmail(){
		return "sysmanage/email/email";
	}
	
	@RequestMapping("/getEmailList")
	@ResponseBody
	public String getEmailList(String emailAddress, int pageNo,int pageSize){

		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<Email> emailList = new ArrayList<Email>();

		Email email = new Email(emailAddress);
		PageInfo<Email> pageInfo = emailService.getEmailList(email, pageParam);
		emailList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(emailList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	
	@RequestMapping("/gotoEmailEdit")
	@ResponseBody
	public String gotoEmailEdit(@ModelAttribute("editFlag") int editFlag,
			Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		Email email = new Email();
		if(editFlag == 2){
			email = emailService.getEmailById(id);
			jsonObj = JSONObject.fromObject(email);
		}
		return jsonObj.toString();   
	}
	
	@RequestMapping("/saveEmail")
	public @ResponseBody Map<String,Object> saveEmail(@RequestBody Email email){

		Map<String,Object> resultMap = new HashMap<String,Object>();
		email.setType(1);
		try{
			if(email != null && email.getId() != null){
				if(emailService.updateEmail(email)){
					resultMap.put("result", "修改成功");
					logger.info("修改成功");
				}
			}else{

				// 如果是增加操作，需要判断是否已经存在
				if(emailService.existEmail(email)){
					resultMap.put("result", "邮箱与存在");
					return resultMap;
				}

				if(emailService.addEmail(email)){
					resultMap.put("result", "增加成功");
					logger.info("增加邮箱");
				}
			}	
		}catch(Exception e){
			resultMap.put("result", "操作失败");
			logger.error("操作失败",e);
		}
		return resultMap;
	}

	@RequestMapping("/checkExistEmail")
	public @ResponseBody String checkExistEmail(String email){
		boolean flag = emailService.existEmail(new Email(email));
		if(flag) {
			return "1";
		}else {
			return "0";
		}
	}

	
	@RequestMapping("/delEmail")
	public @ResponseBody Map<String,Object> delEmail(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(emailService.delEmail(id)){
				resultMap.put("result", "删除成功");
				logger.info("删除成功");
			}	
		}catch(Exception e){
			logger.error("删除失败",e);
			resultMap.put("result", "删除失败");
		}
		return resultMap;
	}
	
}

package com.icegame.sysmanage.controller;


import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.HelpSys;
import com.icegame.sysmanage.entity.HelpSysSign;
import com.icegame.sysmanage.entity.HelpSysType;
import com.icegame.sysmanage.service.IHelpSysService;
import com.icegame.sysmanage.service.IHelpSysSignService;
import com.icegame.sysmanage.service.IHelpSysTypeService;
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
@RequestMapping("/sysmgr/helpsys")
public class HelpSysController {
	
	private static Logger logger = Logger.getLogger(HelpSysController.class);
	
	@Autowired
	private IHelpSysService helpSysService;

	@Autowired
	private IHelpSysTypeService helpSysTypeService;

	@Autowired
	private IHelpSysSignService helpSysSignService;
	
	@RequestMapping("gotoHelpSys")
	public String gotoHelpSys(){
		return "sysmanage/help/helpsys";
	}
	
	@RequestMapping("getHelpSysList")
	@ResponseBody
	public String getHelpSysList(String startDate,String endDate,String userName,String diffType,String signType,int pageNo,int pageSize){

		//初始化日期
		startDate =TimeUtils.checkDateDetail(startDate, endDate).get("startDate");
		endDate = TimeUtils.checkDateDetail(startDate, endDate).get("endDate");

		startDate = TimeUtils.dateToStampWithDetail(startDate);
		endDate = TimeUtils.dateToStampWithDetail(endDate);

		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<HelpSys> helpSysList = new ArrayList<HelpSys>();

		int diffTypeNum = (diffType == null || diffType.equals("")) ? null :Integer.valueOf(diffType);
		int signTypeNum = (signType == null || signType.equals("")) ? null :Integer.valueOf(signType);

		HelpSys helpSys = new HelpSys(startDate,endDate,diffTypeNum,signTypeNum);
		PageInfo<HelpSys> pageInfo = this.helpSysService.getHelpSysList(helpSys, pageParam);
		helpSysList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(helpSysList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("/gotoHelpSysEdit")
	@ResponseBody
	public String gotoHelpSysEdit(@ModelAttribute("editFlag") int editFlag,
			Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		HelpSys helpSys = new HelpSys();
		if(editFlag == 2){
			helpSys = helpSysService.getHelpSysById(id);
			jsonObj = JSONObject.fromObject(helpSys);
		}
		return jsonObj.toString();   
	}

	@RequestMapping("/getDiffTypeList")
	@ResponseBody
	public String getDiffTypeList(){
		List<HelpSysType> helpSysTypeList = helpSysTypeService.getDiffTypeList(new HelpSysType());
		JSONArray jsonArr = JSONArray.fromObject(helpSysTypeList);
		return jsonArr.toString();
	}

	@RequestMapping("/getSignTypeList")
	@ResponseBody
	public String getSignTypeList(){
		List<HelpSysSign> helpSysSignList = helpSysSignService.getSignTypeList(new HelpSysSign());
		JSONArray jsonArr = JSONArray.fromObject(helpSysSignList);
		return jsonArr.toString();
	}
	
	@RequestMapping("/saveHelpSys")
	public @ResponseBody Map<String,Object> saveHelpSys(@RequestBody HelpSys helpSys){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(helpSys != null && helpSys.getId() != null){
				if(helpSysService.updateHelpSys(helpSys)){
					resultMap.put("result", "修改记录信息成功");
					logger.info("修改帮助信息");
				}
			}else{//增加
				helpSys.setUserName(UserUtils.getCurrrentUserName());
				helpSys.setCreateTime(String.valueOf(System.currentTimeMillis()));
				if(helpSysService.addHelpSys(helpSys)){
					resultMap.put("result", "增加记录信息成功");
					logger.info("增加帮助信息");
				}
			}	
		}catch(Exception e){
			resultMap.put("result", "操作记录失败");
			logger.error("操作邮件失败",e);
		}
		return resultMap;
	}

	
	@RequestMapping("/delHelpSys")
	public @ResponseBody Map<String,Object> delHelpSys(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(helpSysService.delHelpSys(id)){
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

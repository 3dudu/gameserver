package com.icegame.sysmanage.controller;


import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.sysmanage.entity.HelpSysSign;
import com.icegame.sysmanage.service.IHelpSysSignService;
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
@RequestMapping("/sysmgr/helpsyssign")
public class HelpSysSignController {

	private static Logger logger = Logger.getLogger(HelpSysSignController.class);

	@Autowired
	private IHelpSysSignService helpSysSignService;


	@RequestMapping("gotoHelpSysSign")
	public String gotoHelpSysSign(){
		return "sysmanage/help/helpsyssign";
	}

	@RequestMapping("/getHelpSysSignList")
	@ResponseBody
	public String getHelpSysSignList(String signName,int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<HelpSysSign> helpSysSignList = new ArrayList<HelpSysSign>();
		HelpSysSign helpSysSign = new HelpSysSign(signName);
		PageInfo<HelpSysSign> pageInfo = this.helpSysSignService.getHelpSysSignList(helpSysSign, pageParam);
		helpSysSignList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(helpSysSignList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("/gotoHelpSysSignEdit")
	@ResponseBody
	public String gotoHelpSysSignEdit(@ModelAttribute("editFlag") int editFlag,
								  Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		HelpSysSign helpSysSign = new HelpSysSign();
		if(editFlag == 2){
			helpSysSign = helpSysSignService.getHelpSysSignById(id);
			jsonObj = JSONObject.fromObject(helpSysSign);
		}
		return jsonObj.toString();
	}

	@RequestMapping("/checkExistSignType")
	public @ResponseBody String checkExistSignType(String signType){
		HelpSysSign helpSysSign = new HelpSysSign();
		helpSysSign.setSignType(Integer.valueOf(signType));
		List<HelpSysSign> helpSysSignList = helpSysSignService.checkExistSignType(helpSysSign);
		if(helpSysSignList.size() > 0) {
			return "1";
		}else {
			return "0";
		}
	}


	@RequestMapping("/saveHelpSysSign")
	public @ResponseBody Map<String,Object> saveHelpSysSign(@RequestBody HelpSysSign helpSysSign){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(helpSysSign != null && helpSysSign.getId() != null){
				if(helpSysSignService.updateHelpSysSign(helpSysSign)){
					resultMap.put("result", "修改记录信息成功");
					logger.info("修改帮助系统标记类型信息");
				}
			}else{//增加
				if(helpSysSignService.addHelpSysSign(helpSysSign)){
					resultMap.put("result", "增加记录信息成功");
					logger.info("增加帮助系统标记类型信息");
				}
			}
		}catch(Exception e){
			resultMap.put("result", "操作记录失败");
			logger.error("操作记录失败",e);
		}
		return resultMap;
	}


	@RequestMapping("/delHelpSysSign")
	public @ResponseBody Map<String,Object> delHelpSysSign(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(helpSysSignService.delHelpSysSign(id)){
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

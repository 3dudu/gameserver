package com.icegame.sysmanage.controller;


import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.sysmanage.entity.HelpSysType;
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
@RequestMapping("/sysmgr/helpsystype")
public class HelpSysTypeController {

	private static Logger logger = Logger.getLogger(HelpSysTypeController.class);

	@Autowired
	private IHelpSysTypeService helpSysTypeService;


	@RequestMapping("gotoHelpSysType")
	public String gotoHelpSysType(){
		return "sysmanage/help/helpsystype";
	}

	@RequestMapping("/getHelpSysTypeList")
	@ResponseBody
	public String getHelpSysTypeList(String diffName,int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<HelpSysType> helpSysTypeList = new ArrayList<HelpSysType>();
		HelpSysType helpSysType = new HelpSysType(diffName);
		PageInfo<HelpSysType> pageInfo = this.helpSysTypeService.getHelpSysTypeList(helpSysType, pageParam);
		helpSysTypeList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(helpSysTypeList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("/gotoHelpSysTypeEdit")
	@ResponseBody
	public String gotoHelpSysTypeEdit(@ModelAttribute("editFlag") int editFlag,
								  Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		HelpSysType helpSysType = new HelpSysType();
		if(editFlag == 2){
			helpSysType = helpSysTypeService.getHelpSysTypeById(id);
			jsonObj = JSONObject.fromObject(helpSysType);
		}
		return jsonObj.toString();
	}

	@RequestMapping("/checkExistDiffType")
	public @ResponseBody String checkExistDiffType(String diffType){
		HelpSysType helpSysType = new HelpSysType();
		helpSysType.setDiffType(Integer.valueOf(diffType));
		List<HelpSysType> helpSysTypeList = helpSysTypeService.checkExistDiffType(helpSysType);
		if(helpSysTypeList.size() > 0) {
			return "1";
		}else {
			return "0";
		}
	}


	@RequestMapping("/saveHelpSysType")
	public @ResponseBody Map<String,Object> saveHelpSysType(@RequestBody HelpSysType helpSysType){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(helpSysType != null && helpSysType.getId() != null){
				if(helpSysTypeService.updateHelpSysType(helpSysType)){
					resultMap.put("result", "修改记录信息成功");
					logger.info("修改帮助类型信息");
				}
			}else{//增加
				if(helpSysTypeService.addHelpSysType(helpSysType)){
					resultMap.put("result", "增加记录信息成功");
					logger.info("增加帮助类型信息");
				}
			}
		}catch(Exception e){
			resultMap.put("result", "操作记录失败");
			logger.error("操作记录失败",e);
		}
		return resultMap;
	}


	@RequestMapping("/delHelpSysType")
	public @ResponseBody Map<String,Object> delHelpSysType(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(helpSysTypeService.delHelpSysType(id)){
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

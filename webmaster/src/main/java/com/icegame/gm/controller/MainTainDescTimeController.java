package com.icegame.gm.controller;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.gm.entity.MainTainDescTime;
import com.icegame.gm.service.IMainTainDescTimeService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sysmgr/maintaintime")
public class MainTainDescTimeController {
	
	private static final Map<String,Object> retMap = new HashMap<String,Object>();

	
	@Autowired
	private IMainTainDescTimeService mainTainDescTimeService;

		
	private static Logger logger = Logger.getLogger(MainTainDescTimeController.class);

	@RequestMapping("/gotoMainTainDescTime")
	public String gotoMainTainDescTime(){
		return "sysmanage/maintain/maintaindesctime";
	}


	/*维护倒计时*/
	@RequestMapping("/getMainTainDescTime")
	public @ResponseBody String MainTainDescTime(int pageNo,int pageSize){
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		List<MainTainDescTime> jpmList = new ArrayList<MainTainDescTime>();
		PageInfo<MainTainDescTime> pageInfo = mainTainDescTimeService.getMainTainDescTimeList(new MainTainDescTime(),pageParam);
		jpmList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(jpmList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	/**
	 *
	 * @param editFlag
	 * @param id
	 * @return
	 * ----------------------------------------
	 * @date 2019-06-15 11:52:48
	 * @author wuzhijian
	 * 修改返回master后台的时间戳转换为服务器进行转换
	 * ----------------------------------------
	 */
	@RequestMapping("/gotoMainTainDescTimeEdit")
	@ResponseBody
	public String gotoMainTainDescTimeEdit(@ModelAttribute("editFlag") int editFlag,
			Long id){
		JSONObject jsonObj = new JSONObject();
		MainTainDescTime mtdt = new MainTainDescTime();
		if(editFlag == 2){
			mtdt = mainTainDescTimeService.getMainTainDescTimeById(id);
			if(mtdt != null){
				mtdt.setExpectStartTime(TimeUtils.stampToDateWithMill(mtdt.getExpectStartTime()));
				mtdt.setExpectEndTime(TimeUtils.stampToDateWithMill(mtdt.getExpectEndTime()));
			}
			jsonObj = JSONObject.fromObject(mtdt);
		}
		return jsonObj.toString();   
	}
	
	@RequestMapping("/delMainTainDescTime")
	@ResponseBody
	public String delMainTainDescTime(Long id){
		Map<String,Object> retMap = new HashMap<String,Object>();
		try{
			if(mainTainDescTimeService.delMainTainDescTime(id)){
				retMap.put("result", "删除成功");
				logger.info("删除维护时间记录成功");
			}
		}catch(Exception e){
			retMap.put("result", "删除失败");
			logger.error("删除失败",e);
		}
		return JSONObject.fromObject(retMap).toString();
	}
	
	@RequestMapping("/saveMainTainDescTime")
	public @ResponseBody Map<String,Object> saveMainTainDescTime(@RequestBody MainTainDescTime mtdt){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		mtdt.setExpectStartTime(TimeUtils.dateToStampWithDetail(mtdt.getExpectStartTime()));
		mtdt.setExpectEndTime(TimeUtils.dateToStampWithDetail(mtdt.getExpectEndTime()));
		try{
			if(mtdt.getId()!=null){
				mainTainDescTimeService.updateMainTainDescTime(mtdt);
				logger.info("修改维护时间成功");
				resultMap.put("result", "修改维护时间成功");
			}else{//增加
				mainTainDescTimeService.addMainTainDescTime(mtdt);
				logger.info("添加维护时间成功");
				resultMap.put("result", "增加维护时间成功");
			}	
		}catch(Exception e){
			resultMap.put("result", "编辑维护时间失败");
			logger.error("编辑维护时间失败",e);
		}		
		return resultMap;
	}

}

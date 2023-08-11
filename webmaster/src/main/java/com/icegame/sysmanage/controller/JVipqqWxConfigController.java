package com.icegame.sysmanage.controller;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.StringUtils;
import com.icegame.sysmanage.components.GroupUtils;
import com.icegame.sysmanage.entity.AutoOpenServer;
import com.icegame.sysmanage.entity.JVipqqWxConfig;
import com.icegame.sysmanage.entity.ServerList;
import com.icegame.sysmanage.service.IAutoOpenServerService;
import com.icegame.sysmanage.service.IJVipqqWxConfigService;
import com.icegame.sysmanage.service.IServerListService;
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
@RequestMapping("/sysmgr/vipqq")
public class JVipqqWxConfigController {

	private static Logger logger = Logger.getLogger(JVipqqWxConfigController.class);

	@Autowired
	private IJVipqqWxConfigService jVipqqWxConfigService;

	@Autowired
	private GroupUtils groupUtils;

	@RequestMapping("/gotoJVipqqWxConfig")
	public String gotoJVipqqWxConfig(){
		return "sysmanage/vip/vipqq";
	}

	@RequestMapping("/getJVipqqWxConfig")
	@ResponseBody
	public String getJVipqqWxConfig(int pageNo,int pageSize){

		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<JVipqqWxConfig> configList = new ArrayList<JVipqqWxConfig>();

		/**
		 * 此处添加联运权限验证
		 */
		String hasChannel = groupUtils.getGroupHasChannel();
		if(StringUtils.isNotNull(hasChannel)){
			hasChannel = StringUtils.multFormat(hasChannel);
		}

		PageInfo<JVipqqWxConfig> pageInfo = this.jVipqqWxConfigService.getListPage(new JVipqqWxConfig(hasChannel), pageParam);
		configList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(configList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("/gotoJVipqqWxConfigEdit")
	@ResponseBody
	public String gotoJVipqqWxConfigEdit(@ModelAttribute("editFlag") int editFlag,
			Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		JVipqqWxConfig jVipqqWxConfig = new JVipqqWxConfig();
		if(editFlag == 2){
			jVipqqWxConfig = jVipqqWxConfigService.getConfigById(id);
			jsonObj = JSONObject.fromObject(jVipqqWxConfig);
		}
		return jsonObj.toString();
	}

	@ResponseBody
	@RequestMapping("/existsConfig")
	public String existsCurrentChannelConfig(String channel){
		boolean flag = jVipqqWxConfigService.getConfigByChannel(new JVipqqWxConfig(channel));
		if(flag) {
			return "1";
		}else {
			return "0";
		}
	}

	@RequestMapping("/saveJVipqqWxConfig")
	public @ResponseBody Map<String,Object> saveJVipqqWxConfig(@RequestBody JVipqqWxConfig jVipqqWxConfig){

		Map<String,Object> resultMap = new HashMap<String,Object>();

		try{
			if(jVipqqWxConfig != null && jVipqqWxConfig.getId() != null){
				if(jVipqqWxConfigService.updateConfig(jVipqqWxConfig)){
					resultMap.put("result", "修改成功");
					logger.info("修改QQ公众号配置成功");
				}
			}else{

				// 如果是增加操作，需要判断是否已经存在
				if(jVipqqWxConfigService.getConfigByChannel(jVipqqWxConfig)){
					resultMap.put("result", "渠道配置已经存在");
					return resultMap;
				}

				if(jVipqqWxConfigService.addConfig(jVipqqWxConfig)){
					resultMap.put("result", "增加记录信息成功");
					logger.info("增加QQ公众号配置成功");
				}
			}
		}catch(Exception e){
			resultMap.put("result", "操作QQ公众号配置失败");
			logger.error("操作QQ公众号配置失败",e);
		}
		return resultMap;
	}


	@RequestMapping("/delJVipqqWxConfig")
	public @ResponseBody Map<String,Object> delOpenServer(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(jVipqqWxConfigService.delConfig(id)){
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

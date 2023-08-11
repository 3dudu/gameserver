package com.icegame.sysmanage.controller;


import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.sysmanage.entity.ExclusiveCsQQ;
import com.icegame.sysmanage.entity.WechatPub;
import com.icegame.sysmanage.service.IExclusiveCsQQService;
import com.icegame.sysmanage.service.IWechatPubService;
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
@RequestMapping("/sysmgr/wechat")
public class WechatPubController {
	
	private static Logger logger = Logger.getLogger(WechatPubController.class);

	@Autowired
	private IWechatPubService wechatPubService;
	
	@RequestMapping("gotoWechat")
	public String gotoVipqq(){
		return "sysmanage/vip/wechat";
	}


	@RequestMapping("/getNameStatus")
	@ResponseBody
	public Map<String,Object> getMoneyStatus(){

		Map<String,Object> resultMap = new HashMap<String,Object>();

		WechatPub name = wechatPubService.getPubNumber(new WechatPub());

		if(name == null){
			name = new WechatPub();
		}

		WechatPub status = wechatPubService.getWeixinStatusObject(new WechatPub());

		if(status == null){
			status = new WechatPub();
		}

		resultMap.put("isOpen",status.getIsOpen());

		resultMap.put("name",name.getName());

		return resultMap;
	}



	@RequestMapping("/saveNameStatus")
	public @ResponseBody Map<String,Object> saveNameStatus(@RequestBody WechatPub wechatPub){

		Map<String,Object> resultMap = new HashMap<String,Object>();

		boolean flag = false;

		flag = wechatPubService.updatePubNumber(wechatPub.getName());

		flag = wechatPubService.updateWeixinStatus(wechatPub.getIsOpen());

		if(flag){
			resultMap.put("result", "保存成功");
		} else {
			resultMap.put("result", "保存失败");
		}

		return resultMap;
	}

}

package com.icegame.sysmanage.controller;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.CdKeyUtils;
import com.icegame.framework.utils.PageUtils;
import com.icegame.sysmanage.dto.PayListDto;
import com.icegame.sysmanage.entity.PayList;
import com.icegame.sysmanage.service.IPayListService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sysmgr/juser")
public class JUserController {

	private static Logger logger = Logger.getLogger(JUserController.class);

	@Autowired
	private IPayListService payListService;

	@RequestMapping("gotoResetJUserPwd")
	public String gotoResetJUserPwd() {
		return "sysmanage/juser/resetJUserPwd";
	}

	/**
	 *
	 * @param flag	{0:根据订单查询  1:根据第三方订单查询  2:根据角色ID查询  3:根据用户名查询}
	 * @param orderId
	 * @param thirdTradeNo
	 * @param username
	 * @param pid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("getPayList")
	@ResponseBody
	public String getActivityGiftKeyList(/*int flag ,*/String orderId,String thirdTradeNo
			,String username,Integer pid,Integer sid,String playerName,int pageNo, int pageSize) {

		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		// 获取分页数据总和
		List<PayListDto> payLists = new ArrayList<PayListDto>();
		PayList payList = new PayList(0,orderId,thirdTradeNo,pid,sid,username,playerName);
		PageInfo<PayListDto> pageInfo = this.payListService.getPayListResetPwd(payList,pageParam);
		payLists = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(payLists);
		// 获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("resetJUserPwd")
	@ResponseBody
	public String resetJUserPwd(Long userId,String username,String passwd) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(payListService.resetJUserPwd(userId,username,passwd)){
			map.put("ret",0);
		}else{
			map.put("ret",-1);
		}
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("getRandomPwd")
	@ResponseBody
	public String getRandomPwd() {
		Map<String,Object> map = new HashMap<String,Object>();
		String randomPwd = CdKeyUtils.randomPwd(8,10);
		map.put("ret",1);
		map.put("msg",randomPwd);
		return JSONObject.fromObject(map).toString();
	}

}

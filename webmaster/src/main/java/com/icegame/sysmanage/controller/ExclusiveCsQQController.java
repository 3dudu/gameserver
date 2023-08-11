package com.icegame.sysmanage.controller;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.StringUtils;
import com.icegame.sysmanage.components.GroupUtils;
import com.icegame.sysmanage.entity.AutoOpenServer;
import com.icegame.sysmanage.entity.ExclusiveCsQQ;
import com.icegame.sysmanage.entity.ServerList;
import com.icegame.sysmanage.service.IAutoOpenServerService;
import com.icegame.sysmanage.service.IExclusiveCsQQService;
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
import java.util.regex.Pattern;

@Controller
@RequestMapping("/sysmgr/vipqq")
public class ExclusiveCsQQController {

	private static Logger logger = Logger.getLogger(ExclusiveCsQQController.class);

	@Autowired
	private IExclusiveCsQQService exclusiveCsQQService;

	@Autowired
	private GroupUtils groupUtils;

	@RequestMapping("gotoVipqq")
	public String gotoVipqq(){
		return "sysmanage/vip/qq";
	}

	@RequestMapping("/getQQList")
	@ResponseBody
	public String getQQList(String qq, int pageNo,int pageSize){

		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<ExclusiveCsQQ> qqList = new ArrayList<ExclusiveCsQQ>();

		/**
		 * 此处添加联运权限验证
		 */
		String hasChannel = groupUtils.getGroupHasChannel();
		if(StringUtils.isNotNull(hasChannel)){
			hasChannel = StringUtils.multFormat(hasChannel);
		}

		// 1 表示是 自动开服的数据
		ExclusiveCsQQ exclusiveCsQQ = new ExclusiveCsQQ();
		exclusiveCsQQ.setQq(qq);
		exclusiveCsQQ.setChannel(hasChannel);
		PageInfo<ExclusiveCsQQ> pageInfo = this.exclusiveCsQQService.getQQList(exclusiveCsQQ, pageParam);
		qqList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(qqList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("/getMoneyStatus")
	@ResponseBody
	public Map<String,Object> getMoneyStatus(){

		Map<String,Object> resultMap = new HashMap<String,Object>();

		ExclusiveCsQQ status = exclusiveCsQQService.getVipQQStatusObject(new ExclusiveCsQQ());

		if(status == null){
            status = new ExclusiveCsQQ();
        }

		ExclusiveCsQQ money = exclusiveCsQQService.getMoney(new ExclusiveCsQQ());

		if(money == null){
		    money = new ExclusiveCsQQ();
        }

		resultMap.put("isOpen",status.getIsOpen());

		resultMap.put("money",money.getMoney());

		return resultMap;
	}

	@RequestMapping("/gotoQQEdit")
	@ResponseBody
	public String gotoOpenServerEdit(@ModelAttribute("editFlag") int editFlag,
			Long id,Model model){
		JSONObject jsonObj = new JSONObject();
		ExclusiveCsQQ exclusiveCsQQ = new ExclusiveCsQQ();
		if(editFlag == 2){
			exclusiveCsQQ = exclusiveCsQQService.getQQById(id);
			jsonObj = JSONObject.fromObject(exclusiveCsQQ);
		}
		return jsonObj.toString();
	}

	@RequestMapping("/saveQQ")
	public @ResponseBody Map<String,Object> saveOpenServer(@RequestBody ExclusiveCsQQ exclusiveCsQQ){

		Map<String,Object> resultMap = new HashMap<String,Object>();
		exclusiveCsQQ.setQq(exclusiveCsQQ.getQq().replaceAll(" +",""));

		try{
			if(exclusiveCsQQ != null && exclusiveCsQQ.getId() != null){
				if(exclusiveCsQQService.updateQQ(exclusiveCsQQ)){
					resultMap.put("result", "修改成功");
					logger.info("修改QQ信息成功");
				}
			}else{

				exclusiveCsQQ.setQq(StringUtils.getNumInStr(exclusiveCsQQ.getQq().replaceAll(" +","")).toString());
				// 如果是增加操作，需要判断是否已经存在
				if(exclusiveCsQQService.existsQQ(exclusiveCsQQ)){
					resultMap.put("result", "此channel已配置相同QQ");
					return resultMap;
				}

				if(exclusiveCsQQService.addQQ(exclusiveCsQQ)){
					resultMap.put("result", "增加QQ成功");
					logger.info("增加qq信息");
				}
			}
		}catch(Exception e){
			resultMap.put("result", "操作QQ失败");
			logger.error("操作QQ失败",e);
		}
		return resultMap;
	}

	@RequestMapping("/saveMoneyStatus")
	public @ResponseBody Map<String,Object> saveMoneyStatus(@RequestBody ExclusiveCsQQ exclusiveCsQQ){

		Map<String,Object> resultMap = new HashMap<String,Object>();

		boolean flag = false;

		flag = exclusiveCsQQService.updateMoney(exclusiveCsQQ.getMoney());

		flag = exclusiveCsQQService.updateQQStatus(exclusiveCsQQ.getIsOpen());

		if(flag){
			resultMap.put("result", "保存成功");
		} else {
			resultMap.put("result", "保存失败");
		}

		return resultMap;
	}

//	@RequestMapping("/checkQQ")
//	public @ResponseBody String checkQQ(String qq){
//		//去掉空格
//		qq = qq.replace(" ", "");
//		//数字验证正则
//		Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
//		String[] qqs = qq.split("");
//		String strNum = "";
//		//找出qq号
//		for (int i = 0; i < qqs.length; i++) {
//			if(pattern.matcher(qqs[i]).matches()){
//				strNum += qqs[i];
//			}
//			else {
//				break;
//			}
//		}
//		//判断是否超出位数
//		if(strNum.length() < 20 && strNum.length() > 0){
//			boolean flag = exclusiveCsQQService.existsQQ(StringUtils.getNumInStr(qq));
//			if(flag) {
//				return "1";
//			}else {
//				return "0";
//			}
//		}
//		else {
//			return "1";
//		}
//	}

//	@RequestMapping("/checkExistQQ")
//	public @ResponseBody String checkExistQQ(String qq){
//		qq = qq.replaceAll(" +","");
//		boolean flag = exclusiveCsQQService.existsQQ(StringUtils.getNumInStr(qq));
//		if(flag) {
//			return "1";
//		}else {
//			return "0";
//		}
//	}


	@RequestMapping("/delQQ")
	public @ResponseBody Map<String,Object> delQQ(Long id){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(exclusiveCsQQService.delQQ(id)){

				//删除成功之后，同时删除关联的玩家
				exclusiveCsQQService.unbindQQ(id);

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

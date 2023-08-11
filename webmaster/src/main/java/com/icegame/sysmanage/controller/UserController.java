package com.icegame.sysmanage.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.JsonUtils;
import com.icegame.framework.utils.PageUtils;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.dto.UserDto;
import com.icegame.sysmanage.entity.Group;
import com.icegame.sysmanage.entity.User;
import com.icegame.sysmanage.service.IGroupService;
import com.icegame.sysmanage.service.IUserService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/sysmgr/user")
public class UserController {
	
	private static Logger logger = Logger.getLogger(UserController.class);
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IGroupService groupService;
	
	
	@RequestMapping("/gotoUserList")
	public String gotoUserList(Model model){
		List<Group> groupList = groupService.getAllGroupName();
		model.addAttribute("groupList", groupList);
		return "sysmanage/user/userList";   
	}
	
	@RequestMapping("/getGroupName")
	@ResponseBody
	public String getGroupName(){
		List<Group> groupList = groupService.getAllGroupName();
		JSONArray jsonArr = JSONArray.fromObject(groupList);
		return jsonArr.toString();   
	}
	
	@RequestMapping("/gotoUserInfo")
	public String gotoUserInfo() {
		return "sysmanage/user/userInfo";
	}
	
	@RequestMapping("/gotoChangePwd")
	public String gotoChangePwd() {
		return "sysmanage/user/changePwd";
	}
	
	
	@RequestMapping("/getUserList")
	public @ResponseBody String getUserList(String groupName,String userName,String isAdmin,String isOnline,int pageNo,int pageSize){
		UserDto userDto = new UserDto();
		if(userName != null)userDto.setUserName(userName.trim());userDto.setGroupName(groupName.trim());
		if(isAdmin == null);isAdmin = "";
		if(isOnline == null)isOnline = "";
		userDto.setIsAdmin(isAdmin);
		userDto.setIsOnline(isOnline);
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<UserDto> userList = new ArrayList<UserDto>();
		PageInfo<UserDto> pageInfo = this.userService.getUserDtoList(userDto,pageParam);
		userList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(userList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}
	
	@RequestMapping("/gotoUserEdit")
	@ResponseBody
	public String gotoUserEdit(@ModelAttribute("editFlag") int editFlag,
			Long userId,Model model){
		List<Group> groupList = groupService.getAllGroupName();
		JSONArray jsonArr = new JSONArray();
		jsonArr = JSONArray.fromObject(groupList);
		if(editFlag==2){
			UserDto userDto = userService.getUserInfoById(userId);
			JSONObject userDtoObj = JSONObject.fromObject(userDto);
			jsonArr.add(0, userDtoObj);
		}
		return jsonArr.toString();   
	}
	
	
	@RequestMapping("/getUserInfoById")
	public @ResponseBody User getUserInfoById() {
		// 1:可以通过session获取id ,同样也可以封装一个统一的方法来获取id
		Long userId = UserUtils.getCurrrentUserId();
		UserDto userDto = userService.getUserInfoById(userId);
		return userDto;
	}

	
	@RequestMapping("saveSelfUserInfo")
	public @ResponseBody Map<String,Object> saveSelfUserInfo(@RequestBody User user){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(userService.saveSelfUserInfo(user)){
				logger.info("修改个人信息成功");
				resultMap.put("result", "修改用户信息成功");
			}else{
				resultMap.put("result", "修改用户信息失败");
				logger.info("修改个人信息失败");
			}
		}catch(Exception e){
			resultMap.put("result", "修改用户信息失败");
			logger.error("修改用户信息失败", e);
		}
		return resultMap;
	}
	
	@RequestMapping("/checkExistUser")
	public @ResponseBody String checkExistUser(String loginName){
		UserDto userDto = new UserDto();
		userDto.setLoginName(loginName);
		List<UserDto> userDtoList = userService.checkExistUser(userDto);
		if(userDtoList.size() > 0) {
			return "1";
		}else {
			return "0";
		}
	}
	
	@RequestMapping("/saveUser")
	public @ResponseBody Map<String,Object> saveUser(@RequestBody UserDto userDto){
		userDto.setIsAdmin(null == userDto.getIsAdmin() ? "off" : "on");
		String groupName = groupService.getGroupInfoById(userDto.getGroupId()).getGroupName();
        if(groupName.indexOf("客服") > -1){
            userDto.setTypeName("cs");
        }else{
			userDto.setTypeName("user");
		}
		userDto.setIsOnline("off");
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(userDto.getUserId()!=null){
				userService.updateUser(userDto);	
				logger.info("修改用户"+userDto.getUserName());
				resultMap.put("result", "修改用户信息成功");
			}else{//增加
				userService.addUser(userDto);
				logger.info("添加用户"+userDto.getUserName());
				resultMap.put("result", "增加用户信息成功");
			}	
		}catch(Exception e){
			resultMap.put("result", "操作用户失败");
			logger.error("操作用户失败",e);
		}		
		return resultMap;
	}
	
	
	@RequestMapping("/saveChangePwd")
	public @ResponseBody Map<String, Object> saveChangePwd(String oldPassword, String newPassword) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 1:可以通过session获取id ,同样也可以封装一个统一的方法来获取id
		Long userId = UserUtils.getCurrrentUserId();
		// 第一步:确保我们的旧密码是正确的 getUserById()
		// 第二步:如果旧密码 则进行更新密码的操作
		UserDto userDto = userService.getUserInfoById(userId);
		// 2:校验原密码是否有效
		//logger.info(userDto.getPassword());
		boolean validOldPass = userService.vaildatePassword(oldPassword, userDto.getPassword());
		if (!validOldPass) {
			resultMap.put("result", "修改密码失败,原密码错误");
		} else {
			if (StringUtil.isNotEmpty(newPassword)) {
				try {
					if (userService.updateUserPassword(userId, newPassword)) {
						resultMap.put("result", "修改密码成功");
						logger.info("修改个人密码成功");
					} else {
						resultMap.put("result", "修改密码失败");
						logger.info("修改个人密码失败");
					}
				} catch (Exception e) {
					resultMap.put("result", "修改密码失败");
					logger.error("修改密码失败", e);
				}
			} else {
				resultMap.put("result", "新密码不能为空");
			}
		}
		return resultMap;
	}
	
	
	@RequestMapping("delUser")
	public @ResponseBody String delUser(Long userId,Long groupId){
		try{
			if(userService.delUser(userId,groupId))
				return JsonUtils.success("删除用户成功");
				logger.info("删除用户"+userId);
		}catch(Exception e){
			logger.error("删除用户失败",e);
		}
		return JsonUtils.success();
	}

	@RequestMapping("online")
	public @ResponseBody String online(Long id){
		boolean flag = userService.online(new User(id));
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("ret",flag);
		return JSONObject.fromObject(map).toString();
	}

	@RequestMapping("offline")
	public @ResponseBody String offline(Long id){
		boolean flag = userService.offline(new User(id));
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("ret",flag);
		return JSONObject.fromObject(map).toString();
	}

}

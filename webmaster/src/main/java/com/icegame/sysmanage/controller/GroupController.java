package com.icegame.sysmanage.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.icegame.framework.utils.ArrayUtils;
import com.icegame.framework.utils.StringUtils;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.dto.GroupDto;
import com.icegame.sysmanage.entity.*;
import com.icegame.sysmanage.service.IServerListService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.PageUtils;
import com.icegame.sysmanage.dto.UserDto;
import com.icegame.sysmanage.service.IGroupService;
import com.icegame.sysmanage.service.IMenuService;
import com.icegame.sysmanage.vo.GroupVo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/sysmgr/group")
public class GroupController {

	private static Logger logger = Logger.getLogger(GroupController.class);

	@Autowired
	private IGroupService groupService;

	@Autowired
	private IMenuService menuService;

	@Autowired
	private IServerListService serverListService;

	@RequestMapping("/gotoGroupList")
	public String gotoGroupList(){
		return "sysmanage/group/groupList";
	}


	@RequestMapping("/gotoGroupEdit")
	@ResponseBody
	public String gotoGroupEdit(@ModelAttribute("editFlag") int editFlag,
			Long groupId,Model model){
		List<Menu> menuList = menuService.getAllMenuList();
		JSONArray jsonArr = new JSONArray();
		jsonArr = JSONArray.fromObject(menuList);
		if(editFlag == 2){
			List<GroupToMenu> groupMenuList = groupService.getMenuListByGroupId(groupId);
			JSONArray groupMenuArr = JSONArray.fromObject(groupMenuList);
			jsonArr.add(0,groupMenuArr);
			Group group = groupService.getGroupInfoById(groupId);
			JSONObject groupObj = JSONObject.fromObject(group);
			jsonArr.add(1,groupObj);
		}
		return jsonArr.toString();
	}


	@RequestMapping("/gotoGroupDetail")
	@ResponseBody
	public String gotoGroupDetail(@ModelAttribute("groupId") Long groupId,String groupName,Model model){
		Group group = new Group();
		group.setGroupId(groupId);
		group.setGroupName(groupName);
		JSONObject groupObj = JSONObject.fromObject(group);
		return groupObj.toString();
	}


	@RequestMapping("/gotoAddUserToGroup")
	public String gotoAddUserToGroup(@ModelAttribute("groupId") Long groupId,Model model){
		model.addAttribute("groupId",groupId);
		return "sysmanage/group/addUserToGroup";
	}


	@RequestMapping("/getUserInGroup")
	public @ResponseBody String getUserInGroup(Long groupId,String userName,int pageNo,int pageSize){
		UserDto userDto = new UserDto();
		userDto.setGroupId(groupId);
		userDto.setUserName(userName);
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		List<UserDto> userList = new ArrayList<UserDto>();
		PageInfo<UserDto> pageInfo = groupService.getUserInGroup(userDto,pageParam);
		userList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(userList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.groupStatus");
		jsonArr.add(0, pageStr.toString());
		return jsonArr.toString();
	}


	@RequestMapping("/getUserNotInGroup")
	public @ResponseBody String getUserNotInGroup(Long groupId,String userName,int pageNo,int pageSize){
		User user = new User();
		user.setUserName(userName);
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		List<User> userList = new ArrayList<User>();
		PageInfo<User> pageInfo = groupService.getUserNotInGroup(user, pageParam);
		userList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(userList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "userMgr.getUserListPage");
		jsonArr.add(0,pageStr);
		return jsonArr.toString();
	}


	@RequestMapping("/getGroupList")
	public @ResponseBody String getGroupList(String groupName,int pageNo,int pageSize){
		Group group = new Group();
		if(groupName != null)group.setGroupName(groupName);
		//获取分页条件
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(pageSize);
		//获取分页数据总和
		List<Group> groupList = new ArrayList<Group>();
		PageInfo<Group> pageInfo = this.groupService.getGroupList(group,pageParam);
		groupList = pageInfo.getList();
		JSONArray jsonArr = JSONArray.fromObject(groupList);
		//获取分页条
		String pageStr = PageUtils.pageStr(pageInfo, "mgr.getListPage");
		jsonArr.add(0, pageStr);
		return jsonArr.toString();
	}

	@RequestMapping("/checkExistGroup")
	public @ResponseBody String checkExistGroup(String groupName){
		Group group = new Group();
		group.setGroupName(groupName);
		List<Group> groupList = groupService.checkExistGroup(group);
		if(groupList.size() > 0) {
			return "1";
		}else {
			return "0";
		}
	}

	@RequestMapping("/saveGroup")
	public @ResponseBody Map<String,Object> saveGroup(@RequestBody GroupVo groupVo){
		Map<String,Object> resultMap = new HashMap<String,Object>();

		if(groupVo.getChannel() != null){
			groupVo.getGroup().setHasChannel(ArrayUtils.toStr(groupVo.getChannel()));
		}

		try{
			if(groupVo != null && groupVo.getGroup() != null && groupVo.getGroup().getGroupId() != null){
				groupService.updateGroup(groupVo);
				resultMap.put("result", "修改用户组信息成功");
			}else{//增加
				groupService.addGroup(groupVo);
				resultMap.put("result", "增加用户组信息成功");
			}
		}catch(Exception e){
			resultMap.put("result", "操作用户失败");
			logger.error("操作用户失败",e);
		}
		return resultMap;
	}


	@RequestMapping("/addUserToGroup")
	public @ResponseBody Map<String,Object> addUserToGroup(@RequestBody GroupVo groupVo){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(groupVo.getUserIds() != null)
				groupService.addUserToGroup(groupVo);
				resultMap.put("result", "添加成功");
		}catch(Exception e){
			logger.info("添加失败",e);
		}
		return resultMap;
	}


	@RequestMapping("/delUserFromGroup")
	public @ResponseBody Map<String,Object> delUserFromGroup(@RequestBody GroupVo groupVo){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(groupVo.getUserIds() != null)
				groupService.delUserFromGroup(groupVo);
				resultMap.put("result", "删除成功");
		}catch(Exception e){
			logger.info("删除失败",e);
		}
		return resultMap;
	}



	@RequestMapping("/delGroup")
	public @ResponseBody Map<String,Object> delGroup(Long groupId){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(groupService.delGroup(groupId))
				resultMap.put("result", "删除用户组成功");
		}catch(Exception e){
			logger.info("删除用户组失败",e);
		}
		return resultMap;
	}


	@RequestMapping("/getAuthorizeChannelList")
	public @ResponseBody Object getAuthorizeChannelList(){
		Long userId = UserUtils.getCurrrentUserId();

		/**
		 * 因为在 springmvc.xml 配置文件中放开了 getAuthorizeChannelList 请求，所以是不用登陆就可以访问的， 所以在这个地方加个判断，
		 * 如果id为空说明 不是从后台请求的 ，直接return new Group()
		 */
		if(userId == null){
			return new Group();
		}

		Group groupId = null;
		Group group = new Group();

		// 拿到groupId
		if(userId != null){
			groupId = groupService.getGroupIdByUserId(userId);
		}

		// 根据groupId 拿到group 对象
		if(groupId != null && groupId.getGroupId() != null){
			group = groupService.getGroupInfoById(groupId.getGroupId());
		}

		/**
		 * 如果getHasChannel 为空，说明此用户组没有限制channel 需要返回所有的
		 * 此处所有的是拿区服列表中去重之后的所有channel返回
		 */
		if(StringUtils.isNull(group.getHasChannel())){
			group.setHasChannel(getChannel());
		}
		return group;
	}

	public String getChannel(){
		StringBuffer sb = new StringBuffer();
		List<ServerList> serverList = serverListService.getAllChannelList();
		for(int i = 0 ; i < serverList.size() ; i++){
			// 此处 list 的最后一个 元素 不加 \r\n
			if(i == serverList.size() - 1){
				sb.append(serverList.get(i).getChannel());
			}else{
				sb.append(serverList.get(i).getChannel()).append("\r\n");
			}
		}
		return sb.toString();
	}

}

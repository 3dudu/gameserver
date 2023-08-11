package com.icegame.sysmanage.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.dto.UserDto;
import com.icegame.sysmanage.entity.Group;
import com.icegame.sysmanage.entity.GroupToMenu;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.User;
import com.icegame.sysmanage.entity.UserToGroup;
import com.icegame.sysmanage.mapper.GroupMapper;
import com.icegame.sysmanage.service.IGroupService;
import com.icegame.sysmanage.vo.GroupVo;

@Service
public class GroupService implements IGroupService {
	
	@Autowired
	private GroupMapper groupMapper;
	
	@Autowired
	private LogService logService;

	Log log = new Log();
	
	public PageInfo<Group> getGroupList(Group group, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<Group> groupList = this.groupMapper.getGroupList(group);
		PageInfo<Group> pageInfo = new PageInfo<Group>(groupList);
		return pageInfo;
	}
	
	
	public Group getGroupInfoById(Long groupId) {
		return groupMapper.getGroupInfoById(groupId);
	}
	
	public List<Group> checkExistGroup(Group group) {
		return groupMapper.checkExistGroup(group);
	}
	
	public PageInfo<UserDto> getUserInGroup(UserDto userDto, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<UserDto> userList = groupMapper.getUserInGroup(userDto);
		PageInfo<UserDto> pageInfo = new PageInfo<UserDto>(userList);
		return pageInfo;
	}

	public PageInfo<User> getUserNotInGroup(User user, PageParam pageParam){
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<User> userList = groupMapper.getUserNotInGroup(user);
		PageInfo<User> pageInfo = new PageInfo<User>(userList);
		return pageInfo;
	}
	
	public boolean addGroup(GroupVo groupVo) {
		boolean flag = false;
		Group group = groupVo.getGroup();
		flag= groupMapper.addGroup(group);
		
		Long groupId = group.getGroupId();
		List<GroupToMenu> groupMenuList = new ArrayList<GroupToMenu>();
		GroupToMenu groupMenu;
		for(Long menuId:groupVo.getMenuIds().values()){
			groupMenu = new GroupToMenu();
			groupMenu.setGroupId(groupId);
			groupMenu.setMenuId(menuId);
			groupMenuList.add(groupMenu);
		}
		//批量插入菜单角色对应表
		if(groupMenuList.size()>0)
		flag = groupMapper.addGroupMenuBatch(groupMenuList);
		log = UserUtils.recording("添加用户组         ["+groupVo.getGroup().getGroupName()+"]   ",Type.ADD.getName());
		logService.addLog(log);
		return flag;
	}
	
	
	//批量添加用户到用户组
	public boolean addUserToGroup(GroupVo groupVo) {
		boolean flag = false;
		List<UserToGroup> userGroupList = new ArrayList<UserToGroup>();
		Long groupId = groupVo.getGroupId();
		UserToGroup userGroup;
		for (Long userId : groupVo.getUserIds().values()) {  		  
			userGroup = new UserToGroup();
			userGroup.setUserId(userId);
			userGroup.setGroupId(groupId);
			userGroupList.add(userGroup);
         }
		flag = groupMapper.addUserToGroup(userGroupList);
		log = UserUtils.recording("批量添加用户到用户组         ["+groupVo.getGroupId()+"]   ",Type.ADD.getName());
		logService.addLog(log);
		return flag;
	}

	public boolean delGroup(Long groupId) {
		boolean flag = false;
		//先删除 用户组记录表
		flag = groupMapper.delGroup(groupId);
		//然后删除用户组菜单对应表
		flag = groupMapper.delGroupMenuByGroupId(groupId);
		log = UserUtils.recording("删除用户组         ["+groupId+"]   ",Type.DELETE.getName());
		logService.addLog(log);
		return flag;
	}
	
	//从用户组中删除用户
	public boolean delUserFromGroup(GroupVo groupVo){
		boolean flag = false;
		List<UserToGroup> userGroupList = new ArrayList<UserToGroup>();
		Long groupId = groupVo.getGroupId();
		UserToGroup userGroup;
		for (Long userId : groupVo.getUserIds().values()) {  		  
			userGroup = new UserToGroup();
			userGroup.setUserId(userId);
			userGroup.setGroupId(groupId);
			userGroupList.add(userGroup);
         }
		flag = groupMapper.delUserFromGroup(userGroupList);
		log = UserUtils.recording("从用户组         ["+groupVo.getGroupId()+"]   中批量删除用户",Type.DELETE.getName());
		logService.addLog(log);
		return flag;
	}
	

	public boolean updateGroup(GroupVo groupVo) {
		boolean flag = false;
		//修改角色对象
		Group group = groupVo.getGroup();
		flag= groupMapper.updateGroup(group);
		
		Long groupId = group.getGroupId();
		//根据角色id删除角色部门对应关系,角色菜单对应关系,角色区域对应关系
		flag = groupMapper.delGroupMenuByGroupId(groupId);
				
		
		//根据新增对象的id (角色id) 菜单id组合角色菜单对应列表,进行批量插入
		List<GroupToMenu> groupMenuList = new ArrayList<GroupToMenu>();
		GroupToMenu groupMenu;
		for(Long menuId:groupVo.getMenuIds().values()){
			groupMenu = new GroupToMenu();
			groupMenu.setGroupId(groupId);
			groupMenu.setMenuId(menuId);
			groupMenuList.add(groupMenu);
		}
		//批量插入菜单角色对应表
		if(groupMenuList.size()>0)
		flag = groupMapper.addGroupMenuBatch(groupMenuList);
		log = UserUtils.recording("修改用户组         ["+groupVo.getGroupName()+"]   ",Type.UPDATE.getName());
		logService.addLog(log);
		return flag;
	}
	
	public List<GroupToMenu> getMenuListByGroupId(Long groupId) {
		return groupMapper.getMenuListByGroupId(groupId);
	}

	
	public List<Group> getAllGroupName() {
		return groupMapper.getAllGroupName();
	}


	@Override
	public Group getGroupIdByUserId(Long userId) {
		return groupMapper.getGroupIdByUserId(userId);
	}

}

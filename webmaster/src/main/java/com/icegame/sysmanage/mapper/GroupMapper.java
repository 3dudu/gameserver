package com.icegame.sysmanage.mapper;

import java.util.List;

import com.icegame.sysmanage.dto.UserDto;
import com.icegame.sysmanage.entity.Group;
import com.icegame.sysmanage.entity.GroupToMenu;
import com.icegame.sysmanage.entity.User;
import com.icegame.sysmanage.entity.UserToGroup;

public interface GroupMapper {
	
	public List<Group> getGroupList(Group group);
	
	public List<Group> checkExistGroup(Group group);
	
	public Group getGroupInfoById(Long groupId);
	
	public Group getGroupIdByUserId(Long userId);
	
	public boolean addGroup(Group group);
	
	public boolean delGroup(Long groupId);

	public boolean updateGroup(Group group);
	
	public List<Group> getAllGroupName();
	
	/**
	 * 根据菜单id删除所有菜单用户组对应关系记录  
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月18日
	 */
	public boolean delGroupMenuByMenuId(Long menuId);
	
	/**
	 * 增加用户组菜单对应记录
	 * @Description: TODO  
	 * @author chesterccw
	 * @date 2018年1月18日
	 */
	public boolean addGroupToMenu(GroupToMenu groupMenu);
	
	/**
	 * 根据角色id查询角色菜单对应关系
	 * @param roleId
	 * @return
	 */
	public List<GroupToMenu> getMenuListByGroupId(Long groupId);
	
	/**
	 * 根据用户组ID查询组内成员
	 * @Description: TODO  
	 * @author chesterccw
	 * @date 2018年1月20日
	 */
	public List<UserDto> getUserInGroup(UserDto userDto);
	
	/**
	 * 查询所有不属于任何用户组的用户
	 * @Description: TODO  
	 * @author chesterccw
	 * @date 2018年1月20日
	 */
	public List<User> getUserNotInGroup(User user);
	
	/**
	 * 批量插入角色菜单对应信息
	 * @param roleMenuList
	 * @return
	 */
	public boolean addGroupMenuBatch(List<GroupToMenu> groupMenuList);
	
	/**
	 * 添加用户到用户组
	 * @Description: TODO  
	 * @author chesterccw
	 * @date 2018年1月20日
	 */
	public boolean addUserToGroup(List<UserToGroup> userGroupList);
	
	/**
	 * 根据角色ID删除角色菜单对应关系
	 * @param roleId
	 * @return
	 */
	public boolean delGroupMenuByGroupId(Long roleId);
	
	/**
	 * 从用户组删除用户
	 * @Description: TODO  
	 * @author chesterccw
	 * @date 2018年1月20日
	 */
	public boolean delUserFromGroup(List<UserToGroup> userGroupList);
	
}

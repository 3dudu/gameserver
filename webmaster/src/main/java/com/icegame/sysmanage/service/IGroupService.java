package com.icegame.sysmanage.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.dto.UserDto;
import com.icegame.sysmanage.entity.Group;
import com.icegame.sysmanage.entity.GroupToMenu;
import com.icegame.sysmanage.entity.User;
import com.icegame.sysmanage.vo.GroupVo;

/**
 * 用户组业务处理接口
 * 
 * @Description: TODO
 * @Package com.icegame.sysmanage.service
 * @author chesterccw
 * @date 2018年1月22日
 */
public interface IGroupService {

	/**
	 * 查询用户组列表
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public PageInfo<Group> getGroupList(Group group, PageParam pageParam);

	/**
	 * 获取所有的用户组名称
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public List<Group> getAllGroupName();
	
	/**
	 * 检查是否存在该用户组
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public List<Group> checkExistGroup(Group group);

	/**
	 * 根据ID查询 用户组详细信息
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public Group getGroupInfoById(Long groupId);
	
	/**
	 * 根据用户id查询用户组id
	 * @param userId
	 * @return
	 */
	public Group getGroupIdByUserId(Long userId);

	/**
	 * 根据用户组ID查询组内成员
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public PageInfo<UserDto> getUserInGroup(UserDto userDto, PageParam pageParam);

	/**
	 * 查询所有不属于任何用户组的用户
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public PageInfo<User> getUserNotInGroup(User user, PageParam pageParam);

	/**
	 * 增加用户到用户组
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean addUserToGroup(GroupVo groupVo);

	/**
	 * 新增用户组
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean addGroup(GroupVo groupVo);

	/**
	 * 删除用户组
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean delGroup(Long groupId);

	/**
	 * 从用户组删除用户
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean delUserFromGroup(GroupVo groupVo);

	/**
	 * 修改用户组
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean updateGroup(GroupVo groupVo);

	/**
	 * 根据角色id查询角色菜单对应关系
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public List<GroupToMenu> getMenuListByGroupId(Long groupId);

}

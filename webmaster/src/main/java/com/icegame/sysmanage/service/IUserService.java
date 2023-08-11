package com.icegame.sysmanage.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.dto.UserDto;
import com.icegame.sysmanage.entity.User;

/**
 * 用户业务处理接口
 * 
 * @Description: TODO
 * @Package com.icegame.sysmanage.service
 * @author chesterccw
 * @date 2018年1月22日
 */
public interface IUserService {

	/**
	 * 检查登录
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public User checkLogin(String loginName, String password);

	/**
	 * 查询用户列表
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public List<User> getUserList(User user);

	/**
	 * 分页查询用户列表
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public PageInfo<UserDto> getUserDtoList(UserDto userDto, PageParam pageParam);
	
	/**
	 * 检查是否该用户
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public List<UserDto> checkExistUser(UserDto userDto);
	
	/**
	 * 选择所有的客服用户
	 * @param userDto
	 * @return
	 */
	public List<UserDto> getCsList(UserDto userDto);

	/**
	 * 选择所有的客服管理员用户
	 * @param userDto
	 * @return
	 */
	public List<UserDto> getCsMgrList(UserDto userDto);

	/**
	 * 根据用户ID查询用户详细信息
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public UserDto getUserInfoById(Long userId);

	/**
	 * 修改用户密码
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean updateUserPassword(Long userId, String newPassword);

	/**
	 * 校检密码
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean vaildatePassword(String plainPsd, String encryptPsd);

	/**
	 * 新增用户
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean addUser(UserDto userDto);

	/**
	 * 删除用户
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean delUser(Long userId, Long groupId);
	
	/**
	 * 修改用户个人信息
	 * @param userDto
	 * @return
	 */
	public boolean saveSelfUserInfo(User user);

	/**
	 * 修改用户
	 * 
	 * @Description: TODO
	 * @author chesterccw
	 * @date 2018年1月22日
	 */
	public boolean updateUser(UserDto userDto);


	public boolean online(User user);


	public boolean offline(User user);

	public List<User> getOnlineCs(User user);

	public User isOnline(UserDto userDto);

	public boolean isAdmin(Long id);

}

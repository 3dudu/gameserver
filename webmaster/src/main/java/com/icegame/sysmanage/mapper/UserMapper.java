package com.icegame.sysmanage.mapper;

import java.util.List;

import com.icegame.sysmanage.dto.UserDto;
import com.icegame.sysmanage.entity.User;

public interface UserMapper {
	
	public List<User> getUserList(User user);
	
	public List<UserDto> getUserDtoList(UserDto userDto);
	
	public List<UserDto> checkExistUser(UserDto userDto);
	
	public List<UserDto> getUserDtoListByGroupName(UserDto userDto);
	
	public List<UserDto> getCsList(UserDto userDto);

	public List<UserDto> getCsMgrList(UserDto userDto);
	
	public UserDto getUserInfoById(Long userId);
	
	public boolean addUser(User user);
	
	public boolean delUser(Long userId);

	public boolean updateUser(User user);

	public boolean online(User user);

	public boolean offline(User user);
	
	public boolean updateUserPassword(Long userId, String newPassword);

	public List<User> getOnlineCs(User user);

	public UserDto isOnline(UserDto userDto);
	
}

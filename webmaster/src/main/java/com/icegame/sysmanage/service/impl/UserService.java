package com.icegame.sysmanage.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.EncryptUtil;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.dto.UserDto;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.User;
import com.icegame.sysmanage.entity.UserToGroup;
import com.icegame.sysmanage.mapper.GroupMapper;
import com.icegame.sysmanage.mapper.UserMapper;
import com.icegame.sysmanage.service.IUserService;

@Service
public class UserService implements IUserService {
	
	public static final int SALT_SIZE = 8;
	public static final int HASH_ITERATIONS = 1024;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private LogService logService;
	
	@Autowired
	private GroupMapper groupMapper;
	
	Log log = new Log();
	
	public User checkLogin(String loginName, String password) {
		User user = new User();
		user.setLoginName(loginName);
		List<User> userList = userMapper.getUserList(user);
		if(userList.isEmpty()){
			return null;
		}else{
			String ecnryptPassword = userList.get(0).getPassword();
			/**
			 * 这边验证密码的逻辑是：首先拿到用户输入的密码。其次还需要从数据库中获取的密码，
			 * 然后把明文密码加密，加密之后入如果和 数据库密码相同，返回true 否则返回false
			 */
			if(vaildatePassword(password,ecnryptPassword))
				return userList.get(0);	
			else
				return null;	
		}
	}
	
	/**
	 * 检验密码是否有效
	 * @Description: TODO  
	 * @author chesterccw
	 * @date 2018年1月15日
	 */
	public boolean vaildatePassword(String plainPsd,String encryptPsd){
		//将密文逆转,截取salt
		byte[] salt = EncryptUtil.decodeHex(encryptPsd.substring(0,SALT_SIZE*2));
		//重新平凑 盐+密码  进行sha1的加密
		byte[] hashPassword = EncryptUtil.sha1(plainPsd.getBytes(), salt, HASH_ITERATIONS);
		String newEncrypPsd = EncryptUtil.encodeHex(salt) + EncryptUtil.encodeHex(hashPassword);
		boolean flag = false; 
		flag = newEncrypPsd.equals(encryptPsd);
		return flag;
		 
	}

	public List<User> getUserList(User user) {
		return userMapper.getUserList(user);
	}
	
	public PageInfo<UserDto> getUserDtoList(UserDto userDto, PageParam pageParam){
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<UserDto> userList = null;
		//如果用户组名为空，那么就查询所有用户
		if(userDto.getGroupName().equals("选择用户组"))userDto.setGroupName("");
		userList = this.userMapper.getUserDtoList(userDto);
		/*if(userDto.getGroupName().equals("选择用户组") || userDto.getGroupName().equals("")){
			userList = this.userMapper.getUserDtoList(userDto);
		}
		else{ //不为空就根据用户组名查询用户
			userList = this.userMapper.getUserDtoListByGroupName(userDto);
		}*/
		PageInfo<UserDto> pageInfo = new PageInfo<UserDto>(userList);
		return pageInfo;
	}
	
	public List<UserDto> checkExistUser(UserDto userDto) {
		return userMapper.checkExistUser(userDto);
	}
	
	public boolean updateUserPassword(Long userId, String password) {
		 String encryptPsd = this.encyptPassword(password);
		 log = UserUtils.recording("修改个人登录密码",Type.UPDATE.getName());
		 logService.addLog(log);
		 return userMapper.updateUserPassword(userId, encryptPsd);
	}
	
	public String encyptPassword (String plainPassword) {
   	//生成一个随机数 ，所谓的salt 盐
       byte[] salt = EncryptUtil.generateSalt(SALT_SIZE);
       //盐+密码   进行sha1的加密
       byte[] hashPass = EncryptUtil.sha1(plainPassword.getBytes(), salt, HASH_ITERATIONS);
       //盐可逆加密+(盐+密码 sha1加密后)可逆加密
       return EncryptUtil.encodeHex(salt) + EncryptUtil.encodeHex(hashPass);
	}
	
	
	public UserDto getUserInfoById(Long userId) {
		return userMapper.getUserInfoById(userId);
	}

	
	public boolean addUser(UserDto userDto) {
		boolean flag = false;
		String password = userDto.getLoginName();
		String encryptPassword = this.encyptPassword(password);
		userDto.setPassword(encryptPassword);
		flag = this.userMapper.addUser(userDto);
		
		if(userDto.getGroupId()==null){
			userDto.setGroupId(userDto.getInitGroupId());
		}
		//在增加用户的同时，把用户添加到用户组
		List<UserToGroup> userGroupList = new ArrayList<UserToGroup>();
		UserToGroup userGroup = new UserToGroup();
		userGroup.setUserId(userDto.getUserId());
		userGroup.setGroupId(userDto.getGroupId());
		userGroupList.add(userGroup);
		flag = this.groupMapper.addUserToGroup(userGroupList);
		log = UserUtils.recording("添加用户["+userDto.getUserName()+"]",Type.ADD.getName());
		logService.addLog(log);
		return flag;
	}
	
	
	public boolean delUser(Long userId,Long groupId) {
		boolean flag = false;
		flag = userMapper.delUser(userId);
		//删除用户的时候同样删除用户和用户组的关系映射
		List<UserToGroup> userGroupList = new ArrayList<UserToGroup>();
		UserToGroup userGroup = new UserToGroup();
		userGroup.setUserId(userId);
		userGroup.setGroupId(groupId);
		userGroupList.add(userGroup);
		flag = this.groupMapper.delUserFromGroup(userGroupList);
		log = UserUtils.recording("删除用户["+userId+"]",Type.DELETE.getName());
		logService.addLog(log);
		return flag;
	}
	
	public boolean saveSelfUserInfo(User user) {
		boolean flag = false;
		flag = this.userMapper.updateUser(user);
		log = UserUtils.recording("修改个人["+user.getUserName()+"]用户信息",Type.UPDATE.getName());
		logService.addLog(log);
		return flag;
	}
	
	
	public boolean updateUser(UserDto userDto) {
		boolean flag = false;
		if(userDto.getGroupId()==null){
			userDto.setGroupId(userDto.getInitGroupId());
		}
		//1.先修改M_USER表中的用户信息，然后修改用户用户组关系映射
		flag = this.userMapper.updateUser(userDto);
		//2.删除原有的关系映射
		List<UserToGroup> userGroupList = new ArrayList<UserToGroup>();
		UserToGroup userGroup = new UserToGroup();
		userGroup.setUserId(userDto.getUserId());
		userGroup.setGroupId(userDto.getInitGroupId());
		userGroupList.add(userGroup);
		flag = this.groupMapper.delUserFromGroup(userGroupList);
		//3.添加新的关系映射
		userGroup.setUserId(userDto.getUserId());
		userGroup.setGroupId(userDto.getGroupId());
		flag = this.groupMapper.addUserToGroup(userGroupList);
		log = UserUtils.recording("修改["+userDto.getUserName()+"]用户信息",Type.UPDATE.getName());
		logService.addLog(log);
		return flag;
	}

	@Override
	public boolean online(User user) {
		return userMapper.online(user);
	}

	@Override
	public boolean offline(User user) {
		return userMapper.offline(user);
	}

	@Override
	public List<User> getOnlineCs(User user) {
		return userMapper.getOnlineCs(user);
	}

	@Override
	public List<UserDto> getCsList(UserDto userDto) {
		return userMapper.getCsList(userDto);
	}

	@Override
	public List<UserDto> getCsMgrList(UserDto userDto) {
		return userMapper.getCsMgrList(userDto);
	}

	@Override
	public User isOnline(UserDto userDto) {
		return userMapper.isOnline(userDto);
	}

	@Override
	public boolean isAdmin(Long id){
		User user = userMapper.getUserInfoById(id);
		if(user.getIsAdmin().equals("on")){
			return true;
		}else{
			return false;
		}
	}

}

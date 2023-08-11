package com.icegame.sysmanage.controller;

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

@Controller
@RequestMapping("/sysmgr/userpwd")
public class UserPwdController {
	
	private static Logger logger = Logger.getLogger(UserPwdController.class);
	
	@Autowired
	private IUserService userService;

	@RequestMapping("/gotoChangeUserPwd")
	public String gotoChangeUserPwd() {
		return "sysmanage/user/changeUserPwd";
	}


	@RequestMapping("/saveChangeUserPwd")
	public @ResponseBody Map<String, Object> saveChangeUserPwd(Long userId, String newPassword) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 1:可以通过session获取id ,同样也可以封装一个统一的方法来获取id
		// 第一步:确保我们的旧密码是正确的 getUserById()
		// 第二步:如果旧密码 则进行更新密码的操作
		UserDto userDto = userService.getUserInfoById(userId);
		// 2:校验原密码是否有效
		//logger.info(userDto.getPassword());
		boolean havePerm = userService.isAdmin(UserUtils.getCurrrentUserId());
		if (!havePerm) {
			resultMap.put("result", "没有权限进行此操作");
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

}

package com.icegame.framework.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.icegame.gm.entity.Gmlogs;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.User;

/**
 * 获取当前登录用户信息
 * @Description: TODO
 * @Package com.icegame.framework.utils
 * @author chesterccw
 * @date 2018年1月22日
 */
public class UserUtils {

	
	public static Long getCurrrentUserId(){
		if(RequestContextHolder.getRequestAttributes()==null) {
			return 0L;
		}
		HttpServletRequest request = ((ServletRequestAttributes)
							RequestContextHolder.getRequestAttributes()).getRequest();
		if(request.getSession().getAttribute("user")!=null){
			User user = (User) request.getSession().getAttribute("user");
			return user.getUserId();
		}else{
			return null;
		}
	}
	
	
	public static String getCurrrentUserName(){
		if(RequestContextHolder.getRequestAttributes()==null) {
			return "System";
		}
		HttpServletRequest request = ((ServletRequestAttributes)
							RequestContextHolder.getRequestAttributes()).getRequest();
		if(request.getSession().getAttribute("user")!=null){
			User user = (User) request.getSession().getAttribute("user");
			return user.getUserName();
		}else{
			return null;
		}
	}

	public static void setCurrrentUserName(String username){
		HttpServletRequest request = ((ServletRequestAttributes)
				RequestContextHolder.getRequestAttributes()).getRequest();
		if(request.getSession().getAttribute("user")!=null){
			User user = (User) request.getSession().getAttribute("user");
			if(user == null){
				user = new User();
			}
			user.setUserName(username);
		}
	}

	public static User getCurrrentUser(){

		if(RequestContextHolder.getRequestAttributes()==null) {
			User user = new User();
			user.setUserName("System");
			user.setLoginName("System");
			return user;
		}

		HttpServletRequest request = ((ServletRequestAttributes)
				RequestContextHolder.getRequestAttributes()).getRequest();
		if(request.getSession().getAttribute("user")!=null){
			User user = (User) request.getSession().getAttribute("user");
			return user;
		}else{
			return null;
		}
	}
	
	public static String getCurrrentPerm(){
		HttpServletRequest request = ((ServletRequestAttributes)
							RequestContextHolder.getRequestAttributes()).getRequest();
		if(request.getSession().getAttribute("user")!=null){
			User user = (User) request.getSession().getAttribute("user");
			return user.getIsAdmin();
		}else{
			return null;
		}
	}
	
	public static Log recording(String operation,String type){
		Log log = new Log();
		log.setUserId(getCurrrentUserId());
		log.setUserName(getCurrrentUserName());
		log.setCreateTime(TimeUtils.getDateDetail());
		log.setOperation(operation);
		log.setType(type);
		return log;
	}
	
	public static Gmlogs addGmlogs(String type,String description){
		Gmlogs gmlogs = new Gmlogs();
		gmlogs.setCreateTime(TimeUtils.getDateDetail());
		gmlogs.setType(type);
		gmlogs.setLoginName(getCurrrentUserName());
		gmlogs.setDescription(description);
		return gmlogs;
	}
}

package com.icegame.sysmanage.controller;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.icegame.sysmanage.dto.UserDto;
import com.icegame.sysmanage.service.IPropertiesService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.icegame.framework.utils.HelperRandom;
import com.icegame.framework.utils.JsonUtils;
import com.icegame.framework.utils.StringUtils;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.dto.MenuDto;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.Menu;
import com.icegame.sysmanage.entity.User;
import com.icegame.sysmanage.service.ILogService;
import com.icegame.sysmanage.service.IMenuService;
import com.icegame.sysmanage.service.IUserService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @Description: 用户的登录验证
 * @Package com.icegame.sysmanage.controller
 * @author chesterccw
 * @date 2018年1月15日
 */
@Controller
public class LoginController {
	
	private static Logger logger = Logger.getLogger(LoginController.class);
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ILogService logService;
	
	@Autowired
	private IMenuService menuService;

	@Autowired
	private IPropertiesService propertiesService;

	
	/** 图片验证码  **/
	
	private static float fontSize = 18.0f;

	private static String fontName = "Times New Roman";

	private static int minHeight = 16;

	private static float heightWidth = 1.28f;

	private static float heightDefault = 28.0f;

	@RequestMapping("/checkImg")
	public static void verifyCode(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		int width = 165;
		int height = 50;
		int type = 0;
		if (height < minHeight) {
			height = minHeight;
		}

		int fontCount = (int) (width * heightWidth / height);
		if (fontCount < 1) {
			fontCount = 1;
			width = (int) (height / heightWidth);

		} else if (fontCount > 12) {
			fontCount = 12;
		}

		float scale = height / heightDefault;
		int fontSize = (int) (scale * LoginController.fontSize);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics graphics = image.getGraphics();
		graphics.setColor(HelperRandom.randColor(200, 250, -1));
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(Color.black);
		System.setProperty("java.awt.headless", "true");
		graphics.setFont(new Font(fontName, Font.PLAIN, fontSize));
		graphics.setColor(HelperRandom.randColor(160, 200, -1));
		int line = (int) (scale * 12);
		for (int i = 0; i < 128; i++) {
			int x0 = HelperRandom.nextInt(width);
			int y0 = HelperRandom.nextInt(height);
			int x1 = HelperRandom.nextInt(line);
			int y1 = HelperRandom.nextInt(line);
			graphics.drawLine(x0, y0, x0 + x1, y0 + y1);
		}

		line = (int) (height * 0.5f + scale * 5.56f);
		float left = width * 0.05f;
		float step = (width - (left * 2.0f)) / (fontCount + 1.0f);
		left -= scale * 4.56f;
		String verifycode = HelperRandom.randChars(fontCount, type);
		for (char chr : verifycode.toCharArray()) {
			left += step;
			graphics.setColor(HelperRandom.randColor(20, 130, -1));
			graphics.drawString(String.valueOf(chr), (int) left, line);
		}

		request.getSession().setAttribute("verifycode", verifycode.toLowerCase());
		session.setAttribute("verifyCode", verifycode.toLowerCase());
		response.setContentType("image/jpeg");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 256);
		ServletOutputStream out = response.getOutputStream();

		ImageIO.write(image, "JPEG", out);
		out.flush();
		out.close();
	}
	
	protected void addCookie(User user,HttpServletResponse response,HttpServletRequest request){
	    if(!user.getLoginName().equals("") && !user.getPassword().equals("")){
	        //创建  Cookie
	        Cookie loginName = new Cookie("loginName",user.getLoginName());
	        Cookie password = new Cookie("password",user.getPassword());
	        //设置Cookie的父路经
	        loginName.setPath(request.getContextPath()+"/");
	        password.setPath(request.getContextPath()+"/");
	        //获取是否保存Cookie（例如：复选框的值）
	        if( null == user.getOnline() || user.getOnline().equals("false") ){
	            //不保存Cookie
	        	loginName.setMaxAge(0);
	        	password.setMaxAge(0);
	        }else{
	            //保存Cookie的时间长度，单位为秒
	        	loginName.setMaxAge(/*7**/24*60*60);
	        	password.setMaxAge(/*7**/24*60*60);
	        }
	        //加入Cookie到响应头
	        response.addCookie(loginName);
	        response.addCookie(password);
	    }
	}
	
	@RequestMapping("/getCookie")
	@ResponseBody
	public String getCookie(HttpServletRequest request){
		User user = new User();
		Cookie[] cookies = request.getCookies();
		if(cookies!=null&&cookies.length>0){
			for(int i=0;i<cookies.length;i++){
				Cookie cookie = cookies[i];
				if("loginName".equals(cookie.getName()))user.setLoginName(cookie.getValue());
				if("password".equals(cookie.getName()))user.setPassword(cookie.getValue());
			}
		}
		JSONObject obj = JSONObject.fromObject(user);
		return obj.toString();
	}
	
	/**
	 * 用户登录
	 * @param user
	 * @param model
	 * @param session
	 * @param request
	 * @return 0：验证码有误  1：输入为空  2：用户名或者密码有误   3：验证成功
	 */
	@RequestMapping("/login")
	@ResponseBody
	public String checkLogin(@RequestBody User user,Model model,HttpSession session,HttpServletRequest request,HttpServletResponse response){
		String initLoginName = user.getLoginName();
		String initPassword = user.getPassword();
		String initOnline = user.getOnline(); 
		//logger.info("用户输入验证码---------------->"+user.getVerifycode().toLowerCase());
		//logger.info("系统生成验证码---------------->"+session.getAttribute("verifycode"));
		if(user.getVerifycode().toLowerCase().equals(session.getAttribute("verifycode"))) {
			if(StringUtils.isNotNull(user.getLoginName())&& StringUtils.isNotNull(user.getPassword())){
				user = userService.checkLogin(user.getLoginName(), user.getPassword());
				if(user!=null) {
					logger.info("登录用户名:"+user.getLoginName());
					session.setAttribute("user", user);
					session.setMaxInactiveInterval(3600 * 60);
					Log log = UserUtils.recording("用户登录",Type.LOGIN.getName());
					logService.addLog(log);
					user.setLoginName(initLoginName);
					user.setPassword(initPassword);
					user.setOnline(initOnline);
					this.addCookie(user, response, request);
					//userService.online(user);
					return JsonUtils.success() ;
				}else {
					return JsonUtils.autherror();
				}
			}else {
				return JsonUtils.emptyerror();
			}
		}else{
			return JsonUtils.verifyerror();
		}
	}
	
	
	@RequestMapping("/main")
	public String main(Model model){
		Long userId = UserUtils.getCurrrentUserId();
		String userName = UserUtils.getCurrrentUserName();
		List<Menu> menuList = this.menuService.getMenuListByUserId(userId);
		model.addAttribute("menuList",menuList);
		model.addAttribute("userName",userName);
		return "main/main";
	}
	
	@RequestMapping("/getMenuListByUserId")
	@ResponseBody
	public String getMenuListByUserId(HttpSession session){
		JSONArray jsonArr = null;
		Long userId = UserUtils.getCurrrentUserId();
		String userName = UserUtils.getCurrrentUserName();
		List<Menu> menuList = this.menuService.getMenuListByUserId(userId);
		String url  = "";
		for(Menu menu : menuList){
			if(menu.getHref().indexOf("sysmgr") > -1){
				url += StringUtils.str(menu.getHref());
				//url += menu.getHref();
			}
		}
		session.setAttribute("url", url);
		jsonArr = JSONArray.fromObject(menuList);
		jsonArr.add(0, userName);
		return jsonArr.toString();
	}
	
	@RequestMapping("/getButtonListByUserId")
	@ResponseBody
	public String getButtonListByUserId(Long parentId){
		JSONArray jsonArr = null;
		Long userId = UserUtils.getCurrrentUserId();
		MenuDto menuDto = new MenuDto();
		menuDto.setParentId(parentId);
		menuDto.setUserId(userId);
		List<Menu> menuList = this.menuService.getButtonListByUserId(menuDto);
		jsonArr = JSONArray.fromObject(menuList);
		//logger.info(jsonArr.toString());
		return jsonArr.toString();
	}
	
	@RequestMapping("/gotoLogin")
	public String gotoLogin(){		 
		return "login";   
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session,HttpServletResponse response){
//		User user = UserUtils.getCurrrentUser();
//		userService.offline(user);
		session.removeAttribute("user");
		session.invalidate();
		return "login" ;   
	}

	@RequestMapping("/getProjectName")
	@ResponseBody
	public String getProjectName(){
		Map<String,Object> map = new HashMap<String, Object>();
		String projectName = propertiesService.getProjectName() + "(GMT" + DateFormatUtils.format(new Date(), "ZZ") + ")";
		map.put("name",projectName);
		return JSONObject.fromObject(map).toString() ;
	}

	@RequestMapping("/getVersion")
	@ResponseBody
	public String getVersion(){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("version",propertiesService.getVersion());
		return JSONObject.fromObject(map).toString() ;
	}

	@RequestMapping("/isOnline")
	@ResponseBody
	public String isOnline(Long id){
		if(null == id)
			id = UserUtils.getCurrrentUserId();
		User user =  userService.getUserInfoById(id);
		boolean flag = false;
		if(user.getIsOnline().equals("on")){
			flag = true;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("ret",flag);
		return JSONObject.fromObject(map).toString();
	}
	
}

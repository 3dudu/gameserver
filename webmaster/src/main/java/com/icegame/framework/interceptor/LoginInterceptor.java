package com.icegame.framework.interceptor;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.icegame.framework.utils.StringUtils;
import com.icegame.sysmanage.entity.User;

/**
 * 用户登录认证拦截器
 * @Description: TODO
 * @Package com.icegame.framework.interceptor
 * @author chesterccw
 * @date 2018年1月22日
 */
public class LoginInterceptor implements HandlerInterceptor{

	//在执行handler之前 运行这个方法里面的代码
	//用户登录验证
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
							 Object handler) throws Exception {
		//得到请求的url
		HttpSession session = request.getSession();
		String url = request.getRequestURI();
		String referer = request.getHeader("referer");
		//System.out.println(referer);
		User user = (User) session.getAttribute("user");
		if(user!=null){
			String permList = (String)session.getAttribute("url");
			if(url.indexOf("sysmgr") > -1 ){
				if(permList.indexOf(StringUtils.str(url)) > -1){
					return true;
				}else{
					request.getRequestDispatcher("/WEB-INF/pages/login.html").forward(request, response);
				}
			}
			return true;
		}else{
			if(null != referer){
				toAlert(request,response);
			}
			else{
				request.getRequestDispatcher("/WEB-INF/pages/login.html").forward(request, response);
			}
		}
		return false;

	}

	//在执行handler ,返回modelAndView之前 运行这个方法里面的代码
	//向页面提供一些公用的数据或者视图信息,
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
						   Object handler, ModelAndView modelAndView) throws Exception {

	}
	//执行handler之后 运行这个方法来面的代码
	//系统统一的异常处理,方法执行时间 :afterCompletion-preHandler
	//系统日志记录
	@Override
	public void afterCompletion(HttpServletRequest request,
								HttpServletResponse response, Object handler, Exception exception)
			throws Exception {

	}

	//前台弹出alert框
	public void toAlert(HttpServletRequest request, HttpServletResponse response){

	    try {
	         response.setContentType("text/html;charset=UTF-8");
	         response.setCharacterEncoding("UTF-8");
	         OutputStreamWriter out=new OutputStreamWriter(response.getOutputStream());
	         String msg="登录超时";
	         msg=new String(msg.getBytes("UTF-8"));
	         out.write("<meta http-equiv='Content-Type' content='text/html';charset='UTF-8'>");
	         out.write("<script>");
	         out.write("alert('"+msg+"');");
	         out.write("top.location.href = '"+request.getContextPath()+"/WEB-INF/pages/login.html'; ");
	         out.write("</script>");
	         out.flush();
	         out.close();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}

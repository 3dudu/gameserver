package com.xuegao.core.netty.interceptor;

import java.io.File;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.netty.ICmdInterceptor;
import com.xuegao.core.netty.User;
import com.xuegao.core.netty.http.HttpResponseJSONObject;
import com.xuegao.core.util.ClassPathUtil;

public class StaticFilesInterceptor implements ICmdInterceptor{

	static File webdir=ClassPathUtil.findDirectory("web");
	
	@Override
	public int intercept(User sender, String cmd, JSONObject params) {
//		sender.send("/aaaa", params);
//		return 0;
//		if(MainCmdHandler.getInstance().hasCmd(cmd)){
//			//接口,返回
//			return 1;
//		}
		//文件
		File file=new File(webdir.getPath()+cmd);
		
		if(!file.exists()||file.isDirectory()){
//			sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("404,File not found!"));
			return 0;
		}
		//静态文件存在
		HttpResponseJSONObject rs=new HttpResponseJSONObject();
		rs.setFile(file);
		rs.setIsFile(true);
		sender.sendAndDisconnect(rs);
		return 0;
	}
	
}

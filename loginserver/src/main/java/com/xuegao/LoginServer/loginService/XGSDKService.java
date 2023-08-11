package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.Constants.PlatformOption.XGSDK;
import com.xuegao.LoginServer.po.UserPo;
import com.xuegao.core.util.RequestUtil;

public class XGSDKService  extends AbsLoginService{

	/** queryLoginUrl */
	private String queryLoginUrl = "http://xgsdk.xuegaogame.com:8080/ipp-web-sdk/user/checkLogin";

	@Override
	public UserPo loginUser(String[] parameters,JSONObject rs) {
		String channel=parameters[0];
		XGSDK config=getConfig();
		if (parameters.length == 5) {
			String appId=parameters[4];//gameId
			String uid=parameters[2];
			StringBuffer buff=new StringBuffer();
			buff.append(queryLoginUrl).append("?appId=").append(appId).append("&uid=").append(uid).append("&token=").append(parameters[3]);
			String request = RequestUtil.request(buff.toString());
			if(request!=null&&request.equals("success")){
				return openUserBase(uid, getPlatformName(),channel);
			}
	     }
		 return null;
	}
}

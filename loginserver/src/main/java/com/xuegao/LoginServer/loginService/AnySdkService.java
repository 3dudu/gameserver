package com.xuegao.LoginServer.loginService;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.po.UserPo;

public class AnySdkService extends AbsLoginService{
	
	@Override
	public UserPo loginUser(String[] parameters,JSONObject rs) {
		//AnySdk登录不走通用登录接口,此处不必实现
		return null;
	}
}

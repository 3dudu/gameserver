package com.xuegao.core.netty;

import com.alibaba.fastjson.JSONObject;

public interface ICmdInterceptor {
	/**
	 * 消息拦截处理
	 * @param sender
	 * @param cmd
	 * @param params
	 * @return 0:停止往下执行 1:继续往下执行
	 */
	public int intercept(User sender,String cmd,JSONObject params);
}

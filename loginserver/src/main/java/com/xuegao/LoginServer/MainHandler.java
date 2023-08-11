package com.xuegao.LoginServer;

import com.xuegao.LoginServer.handler.AppFlyerHandler;
import com.xuegao.LoginServer.loginService.DMMLoginService;
import com.xuegao.LoginServer.loginService.GOSULoginService;
import com.xuegao.LoginServer.loginService.TwitterService;
import com.xuegao.LoginServer.loginService.X7GameSDKLoginService;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.GlobalCache;
import com.xuegao.LoginServer.data.MsgFactory;
import com.xuegao.LoginServer.handler.OpenHandler;
import com.xuegao.LoginServer.handler.UserHandler;
import com.xuegao.core.netty.PooledCmdHandler;
import com.xuegao.core.netty.User;

import io.netty.channel.ChannelHandlerAdapter;

public class MainHandler extends PooledCmdHandler {

	private static Logger logger = Logger.getLogger(MainHandler.class);
	private static MainHandler instance = new MainHandler();

	public static MainHandler getInstance() {
		return instance;
	}

	private MainHandler() {
		super(8, 16, 20000);
	}

	@Override
	public void init() {
		GlobalCache.reload();
		// ----------------------------拦截器注册-------------------------

		// ----------------------------消息处理类注册--------------------------
		addRequestHandler("/user/register",UserHandler.class);
		addRequestHandler("/user/login",UserHandler.class);
		addRequestHandler("/user/validate",UserHandler.class);
		addRequestHandler("/user/loginIdentity",UserHandler.class);
		addRequestHandler("/user/AnySdk/login",UserHandler.class);
		addRequestHandler("/user/loginLog",UserHandler.class);
		addRequestHandler("/user/hasPlayer",UserHandler.class);
		addRequestHandler("/refresh",UserHandler.class);
		addRequestHandler("/user/bind",UserHandler.class);
		addRequestHandler("/user/fastInfo",UserHandler.class);
		addRequestHandler("/user/log",UserHandler.class);
		addRequestHandler("/user/Verified",UserHandler.class);
		addRequestHandler("/user/loginOff", UserHandler.class);
		addRequestHandler("/user/dmmUrlAnalysis", DMMLoginService.class);
		//推特
		addRequestHandler("/user/TwitterCallBack", TwitterService.class);
		addRequestHandler("/user/TwitterLogin", TwitterService.class);

		//DMM 上报假数据
		addRequestHandler("/dmm/addapp", DMMLoginService.class);
		addRequestHandler("/dmm/suspendapp", DMMLoginService.class);
		addRequestHandler("/dmm/resumeapp", DMMLoginService.class);
		addRequestHandler("/dmm/removeapp", DMMLoginService.class);
		/**提供外网接口更新配置数据*/
		addRequestHandler("/api/open/login/sycnData", OpenHandler.class);
		addRequestHandler("/api/open/login/reloadData", OpenHandler.class);

		/**
		 * af接口上报
		 */
		addRequestHandler("/api/af/report", AppFlyerHandler.class);
		/**小七提供账号相关数据*/
		addRequestHandler("/api/x7GameRoleCallback", X7GameSDKLoginService.class);
		addRequestHandler("/api/x7GameRoleCallbackV2", X7GameSDKLoginService.class);
		addRequestHandler("/user/logout", UserHandler.class);

		/**eyou*/
		addRequestHandler("/user/userlevel", UserHandler.class);
		addRequestHandler("/user/playerlist", UserHandler.class);

		/**gosu*/
		addRequestHandler("/api/gosuRoleList", GOSULoginService.class);


	}

	/**
	 * 消息入口
	 */
	@Override
	public void handleRequest(User sender, String cmd, JSONObject params) {
		logger.info("-------收到http消息:cmd=" + cmd + ",params=" + params + "-------");
		if ("/favicon.ico".equals(cmd)) {
			sender.sendAndDisconnect(null);
			return;
		}
		super.handleRequest(sender, cmd, params);
	}

	@Override
	public void handleOutOfMaxTaskSize(User sender, String cmd, JSONObject params) {
		logger.error("系统繁忙,params=" + params.toString());
		sender.sendAndDisconnect(MsgFactory.getDefaultErrorMsg("系统繁忙"));
	}

	@Override
	public void caughtLogicException(User sender, String cmd, JSONObject params, Throwable e) {
		logger.error(e.getMessage(), e);
		sender.send(MsgFactory.getDefaultErrorMsg(e.getMessage()));
	}

	@Override
	public void sessionCreated(User sender, ChannelHandlerAdapter channelHandlerAdapter) {
		// logger.info("-----session create:"+sender.getChannel().remoteAddress()+"------");
	}

	@Override
	public void sessionRemoved(User sender, ChannelHandlerAdapter channelHandlerAdapter) {
		// logger.info("-----session remove:"+sender.getChannel().remoteAddress()+"------");
	}
}

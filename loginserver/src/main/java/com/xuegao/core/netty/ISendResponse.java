package com.xuegao.core.netty;

import io.netty.channel.ChannelFuture;

public interface ISendResponse {
	/** 返回消息 */
	public  ChannelFuture sendMsg(User user,Object rs);
}

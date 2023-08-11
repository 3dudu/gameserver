package com.xuegao.PayServer.slaveServer;

/**
 * 消息处理接口
 * @author ccx
 * @date 2017-8-15 下午5:23:03
 */
public interface IScMsgHandler {
	public void handle(SC sc,ScJSONObject scReqMsg);
}

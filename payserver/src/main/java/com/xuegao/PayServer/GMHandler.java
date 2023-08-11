package com.xuegao.PayServer;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.MsgFactory;
import com.xuegao.PayServer.handler.*;
import com.xuegao.core.netty.PooledCmdHandler;
import com.xuegao.core.netty.User;
import io.netty.channel.ChannelHandlerAdapter;
import org.apache.log4j.Logger;

public class GMHandler extends PooledCmdHandler {

    private static Logger logger = Logger.getLogger(GMHandler.class);
    private static GMHandler instance = new GMHandler();

    public static GMHandler getInstance() {
        return instance;
    }

    private GMHandler() {
        super(8, 16, 20000);
    }

    @Override
    public void init() {


        // ----------------------------拦截器注册-------------------------

        // ----------------------------消息处理类注册--------------------------
        addRequestHandler("/p/GMPayNotify/cb", GmHandler.class);
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

package com.xuegao.PayServer;

import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ProxoolFacade;

import com.xuegao.PayServer.data.Constants;
import com.xuegao.PayServer.data.GlobalCache;
import com.xuegao.PayServer.schedule_task.PayTradeSynchJob;
import com.xuegao.PayServer.slaveServer.GlobalSlaveManage;
import com.xuegao.core.db.po.BasePoSyncPool;
import com.xuegao.core.netty.http.HttpServer;

public class ServerStartMain {
	public static Logger logger=Logger.getLogger(ServerStartMain.class);

	public static void main(String[] args){

		//缓存加载
		GlobalSlaveManage.reloadData();
		GlobalCache.reloadData();

		if("open".equals(Constants.open_scheduled)){
			// 启动 定时器  专门定时处理 未到账的订单
			new PayTradeSynchJob().start();
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new HttpServer(Constants.http_port,MainHandler.getInstance()).run();
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					System.exit(0);
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new HttpServer(Constants.http_gm_port,GMHandler.getInstance()).run();
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					System.exit(0);
				}
			}
		}).start();
		ProxoolFacade.disableShutdownHook();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("-------程序结束,开始缓存入库---");
				BasePoSyncPool.syncToDB();
				ProxoolFacade.shutdown();
			}
		}));
	}
}

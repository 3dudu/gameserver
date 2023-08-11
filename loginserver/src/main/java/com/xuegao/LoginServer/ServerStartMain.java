package com.xuegao.LoginServer;

import com.xuegao.LoginServer.schedule_task.AntiInduigedReportSynchJob;
import com.xuegao.LoginServer.vo.AntiIndulgedInfo;
import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ProxoolFacade;

import com.xuegao.LoginServer.data.Constants;
import com.xuegao.core.db.po.BasePoSyncPool;
import com.xuegao.core.netty.http.HttpServer;

public class ServerStartMain {
	public static Logger logger=Logger.getLogger(ServerStartMain.class);
	
	public static void main(String[] args){
		new AntiInduigedReportSynchJob().start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new HttpServer(Constants.port,MainHandler.getInstance()).run();
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
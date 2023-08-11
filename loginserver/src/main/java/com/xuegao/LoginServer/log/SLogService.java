/**
 * Copyright 2015 ABSir's Studio
 *
 * All right reserved
 *
 * Create on 2015年4月8日 下午4:57:44
 */
package com.xuegao.LoginServer.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.util.PropertiesUtil;

/**
 * @author lixiang.you
 *
 */
public class SLogService {
	/** LOGGER */

	public static final Logger ELK_LOGGER = Logger.getLogger("elk");

	public static String LANGUAGE = PropertiesUtil.get("language");
	public static String PROJRCT = PropertiesUtil.get("project");


	/**
	 * 加入到elk日志收集系统
	 * @param type	日志类型
	 * @param data	数据
	 */
	public static void log_elk(JSONObject data) {
		if(data==null)return;
		data.put("language",LANGUAGE);
		data.put("project",PROJRCT);
		ELK_LOGGER.info(data.toJSONString());
	}

	public static void new_log_elk(JSONObject data) {
		if(data==null)return;
		data.put("project",PROJRCT);
		ELK_LOGGER.info(data.toJSONString());
	}
}

package com.xuegao.core.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class PropertiesUtil {
	static Logger logger=Logger.getLogger(PropertiesUtil.class);
	public static String CONF_PATH=ClassPathUtil.findDirectory("config").getPath();
	private static Map<String, String> map=new HashMap<>();
	static{
		init();
	}
	public static void init(){
		File file=new File(CONF_PATH);
		logger.info("----PropertiesUti.init---CONF_PATH="+CONF_PATH);
		if(file.exists()&&file.isDirectory()){
			File[] files=file.listFiles();
			for(File f:files){
				if(f.isFile()&&f.getName().endsWith(".properties")){
					logger.info("----PropertiesUti.init---file="+f.getName());
					Map<String, String> m=FileUtil.readFileSafely(f.getPath(), new HashMap<String,String>());
					map.putAll(m);
				}
			}
		}
	}
	public static String get(String key){
		return map.get(key);
	}
}

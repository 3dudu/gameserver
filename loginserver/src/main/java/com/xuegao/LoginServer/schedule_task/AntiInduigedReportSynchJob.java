package com.xuegao.LoginServer.schedule_task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.redisData.MemAntilnduigedInfoList;
import com.xuegao.LoginServer.util.AESUtils;
import com.xuegao.LoginServer.vo.AntiIndulgedInfo;
import com.xuegao.core.util.PropertiesUtil;
import com.xuegao.core.util.RequestUtil;
import com.xuegao.core.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntiInduigedReportSynchJob extends  AbsScheduleTask{

	public static String CHECK_APPID = PropertiesUtil.get("check_appId");
	public static String BIZID = PropertiesUtil.get("bizId");
	public static String SECRETKEY = PropertiesUtil.get("secretkey");

	@Override
	public int fetchInitialDelaySec() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int fetchPeriodSec() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void run() {
		MemAntilnduigedInfoList memAntilnduigedInfoList = new MemAntilnduigedInfoList();
		AntiIndulgedInfo antiIndulgedInfo = memAntilnduigedInfoList.popFromTail();
		JSONObject jsonObject = new JSONObject();
		JSONArray collections = new JSONArray();
		if (antiIndulgedInfo==null){
			return;
		}
		long timestamps = System.currentTimeMillis();
		for (int i=1;i<=180;i++){
			if (antiIndulgedInfo==null){
				jsonObject.put("collections",collections);

				String data = AESUtils.encrypt(jsonObject.toString(),SECRETKEY);
				JSONObject postdata = new JSONObject();
				postdata.put("data",data);
				String sign = getSHA256(SECRETKEY+"appId"+CHECK_APPID+"bizId"+BIZID+"timestamps"+timestamps+postdata);
				Map<String, String> map = new HashMap<>();
				map.put("appId",CHECK_APPID);
				map.put("bizId",BIZID);
				map.put("timestamps",timestamps+"");
				map.put("sign",sign);
				try{
					logger.info("防沉迷数据上报:"+jsonObject.toString());
					String request = RequestUtil.requestWithPost("http://api2.wlc.nppa.gov.cn/behavior/collection/loginout", map,postdata.toString());
				} catch (URISyntaxException e) {
					logger.info(e.getMessage());
				}finally {
					return;
				}
			}
			JSONObject collection = new JSONObject();
			collection.put("no",i);
			collection.put("si", antiIndulgedInfo.si);
			collection.put("bt",antiIndulgedInfo.bt); //上线
			collection.put("ot",antiIndulgedInfo.ot);
			collection.put("ct",antiIndulgedInfo.ct);
			collection.put("pi",antiIndulgedInfo.pi);
			collection.put("di",antiIndulgedInfo.di);
			collections.add(collection);
			antiIndulgedInfo = memAntilnduigedInfoList.popFromTail();
		}
	}

	public static String getSHA256(String str) {
		MessageDigest messageDigest;
		String encodeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes("UTF-8"));
			encodeStr = byte2Hex(messageDigest.digest());
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodeStr;
	}

	/**
	 * 将byte转为16进制
	 *
	 * @param bytes
	 * @return
	 */
	private static String byte2Hex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		String temp = null;
		for (byte aByte : bytes) {
			temp = Integer.toHexString(aByte & 0xFF);
			if (temp.length() == 1) {
				// 1得到一位的进行补0操作
				sb.append("0");
			}
			sb.append(temp);
		}
		return sb.toString();
	}
}

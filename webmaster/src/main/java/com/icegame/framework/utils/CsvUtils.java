package com.icegame.framework.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CsvUtils {
	
	/**
	 * 全服邮件
	 * @author chsterccw  
	 * @date 2018年8月21日
	 */
	public static JSONArray getCsv(String filePath,String context) {
		JSONArray jsonArr = new JSONArray();
		//String filePath = "src/main/resources/xls/";
		try {
			JSONObject jsonObj = new JSONObject();
			ArrayList<String[]> csvList = new ArrayList<String[]>();
			Map<String, Object> xnpc = new HashMap<String,Object>();
			CsvReader reader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
			while (reader.readRecord()) {
				csvList.add(reader.getValues());
			}
			reader.close();
			for (int row = 1; row < csvList.size(); row++) {
				xnpc.put("id", ((String[]) csvList.get(row))[0]);
				xnpc.put("name", ((String[]) csvList.get(row))[1]);
				jsonObj = JSONObject.fromObject(xnpc);
				jsonArr.add(jsonObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonArr;
	}
	
	/**
	 * 区服邮件
	 * @author chsterccw  
	 * @date 2018年8月21日
	 */
	public static JSONArray getCsv(String sidPid, String context,String str) {
		// 处理服务器id、角色id
		String[] sidPidArr = sidPid.split(":");
		List<Long> sidList = new ArrayList<Long>();
		List<Long> pidList = new ArrayList<Long>();
		
		// 处理context
		String[] contextArr = sidPid.split(":");
		List<Long> typeList = new ArrayList<Long>();
		List<Long> idList = new ArrayList<Long>();
		List<Long> countList = new ArrayList<Long>();
		JSONArray jsonArr = new JSONArray();
		String filePath = "src/main/resources/xls/";
		try {
			JSONObject jsonObj = new JSONObject();
			ArrayList<String[]> csvList = new ArrayList<String[]>();
			Map<String, Object> xnpc = new HashMap<String,Object>();
			CsvReader reader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
			while (reader.readRecord()) {
				csvList.add(reader.getValues());
			}
			reader.close();
			for (int row = 1; row < csvList.size(); row++) {
				xnpc.put("id", ((String[]) csvList.get(row))[0]);
				xnpc.put("name", ((String[]) csvList.get(row))[1]);
				jsonObj = JSONObject.fromObject(xnpc);
				jsonArr.add(jsonObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonArr;
	}
	
	public static void main(String[] args) {
		String filePath = "src/main/resources/xls/";
		JSONArray xnpc = getCsv(filePath,"XNpc.csv");
		System.out.println(xnpc.toString());
	}

}

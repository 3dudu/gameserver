package com.icegame.framework.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.csvreader.CsvReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class XlsUtils {

	public static JSONArray getCsv(String filePath) {
		JSONArray jsonArr = new JSONArray();
		try {
			JSONObject jsonObj = new JSONObject();
			ArrayList<String[]> csvList = new ArrayList<String[]>();
			Map<String, Object> xnpc = new HashMap<String, Object>();
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

	public static JSONArray getCsvJingJie(String filePath) {
		JSONArray jsonArr = new JSONArray();
		try {
			JSONObject jsonObj = new JSONObject();
			ArrayList<String[]> csvList = new ArrayList<String[]>();
			Map<String, Object> xnpc = new HashMap<String, Object>();
			CsvReader reader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
			while (reader.readRecord()) {
				csvList.add(reader.getValues());
			}
			reader.close();
			for (int row = 1; row < csvList.size(); row++) {
				xnpc.put("id", ((String[]) csvList.get(row))[0]);
				xnpc.put("name", ((String[]) csvList.get(row))[4]);
				jsonObj = JSONObject.fromObject(xnpc);
				jsonArr.add(jsonObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonArr;
	}

    public static JSONArray getCsvEvaluate(String filePath) {
        JSONArray jsonArr = new JSONArray();
        try {
            JSONObject jsonObj = new JSONObject();
            ArrayList<String[]> csvList = new ArrayList<String[]>();
            Map<String, Object> xnpc = new HashMap<String, Object>();
            CsvReader reader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
            while (reader.readRecord()) {
                csvList.add(reader.getValues());
            }
            reader.close();
            for (int row = 1; row < csvList.size(); row++) {
                xnpc.put("id", ((String[]) csvList.get(row))[0]);
                xnpc.put("opt1", ((String[]) csvList.get(row))[2]);
                xnpc.put("opt2", ((String[]) csvList.get(row))[3]);
                xnpc.put("opt3", ((String[]) csvList.get(row))[4]);
                xnpc.put("opt4", ((String[]) csvList.get(row))[5]);
                jsonObj = JSONObject.fromObject(xnpc);
                jsonArr.add(jsonObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArr;
    }


	private static JSONArray getCsvStrange(String filePath , int type) {
		JSONArray jsonArr = new JSONArray();
		try {
			JSONObject jsonObj;
			ArrayList<String[]> csvList = new ArrayList<String[]>();
			Map<String, Object> xStrangeLevel = new HashMap<String, Object>();
			CsvReader reader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
			while (reader.readRecord()) {
				csvList.add(reader.getValues());
			}
			reader.close();
			for (int row = 1; row < csvList.size(); row++) {
				if (((String)csvList.get(row)[1]).trim().equals(new String(""+type))){
					xStrangeLevel.put("id", ((String[]) csvList.get(row))[0]);
					xStrangeLevel.put("name", ((String[]) csvList.get(row))[3]);
					jsonObj = JSONObject.fromObject(xStrangeLevel);
					jsonArr.add(jsonObj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonArr;
	}
	
	public static JSONArray getGoods(String filePath){
		//String filePath = "src/main/resources/xls/";
		filePath += "/WEB-INF/classes/xls/";
		//System.out.println(filePath);
	    JSONArray xnpc = getCsv(filePath + "XNpc.csv");
	    JSONArray xitem = getCsv(filePath + "XItem.csv");
	    JSONArray xequip = getCsv(filePath + "XEquip.csv");
	    JSONArray xhorse = getCsv(filePath + "XHorse.csv");
	    JSONArray retArr = new JSONArray();
//	    for(int i = 0 ; i < xnpc.size() ; i++ ){
//	    	retArr.add(xnpc.get(i));
//	    }
//	    for(int i = 0 ; i < xitem.size() ; i++ ){
//	    	retArr.add(xitem.get(i));
//	    }
//	    for(int i = 0 ; i < xequip.size() ; i++ ){
//	    	retArr.add(xequip.get(i));
//	    }
//	    for(int i = 0 ; i < xhorse.size() ; i++ ){
//	    	retArr.add(xhorse.get(i));
//	    }
	    retArr.add(xnpc.toString());
	    retArr.add(xequip.toString());
	    retArr.add(xitem.toString());
	    retArr.add(xhorse.toString());
		return retArr;
	}

	//加载副本关卡
	public static JSONArray getLevel(String filePath){
		filePath += "/WEB-INF/classes/xls/";
	    JSONArray xlevel = getCsv(filePath + "XLevel.csv");
	    JSONArray retArr = new JSONArray();
	    retArr.add(xlevel.toString());
		return retArr;
	}

	public static JSONArray getEvaluate(String filePath){
		filePath += "/WEB-INF/classes/xls/";
		JSONArray xEvaluate = getCsvEvaluate(filePath + "XEvaluate.csv");
		JSONArray retArr = new JSONArray();
		retArr.add(xEvaluate.toString());
		return retArr;
	}
	
	public static void main(String[] args) {
		String filePath = "src/main/resources/xls/";
	    JSONArray xnpc = getCsv(filePath + "XNpc.csv");
	    JSONArray xitem = getCsv(filePath + "XItem.csv");
	    JSONArray xequip = getCsv(filePath + "XEquip.csv");
	    JSONArray xhorse = getCsv(filePath + "XHorse.csv");
	    JSONArray retArr = new JSONArray();
	    for(int i = 0 ; i < xnpc.size() ; i++ ){
	    	retArr.add(xnpc.get(i));
	    }
	    for(int i = 0 ; i < xitem.size() ; i++ ){
	    	retArr.add(xitem.get(i));
	    }
	    for(int i = 0 ; i < xequip.size() ; i++ ){
	    	retArr.add(xequip.get(i));
	    }
	    for(int i = 0 ; i < xhorse.size() ; i++ ){
	    	retArr.add(xhorse.get(i));
	    }
		System.out.println(retArr.toString());
	}

	//加载修仙副本进度
	public static JSONArray getHanglevel(String filePath) {

		filePath += "/WEB-INF/classes/xls/";
		JSONArray xlevel = getCsv(filePath + "XiuXianLevel.csv");
		JSONArray retArr = new JSONArray();
		retArr.add(xlevel.toString());
		return retArr;
	}

	//加载修仙境界表
	public static JSONArray getLevelId(String filePath) {

		filePath += "/WEB-INF/classes/xls/";
		JSONArray xlevel = getCsvJingJie(filePath + "XiuXianJingJie.csv");
		JSONArray retArr = new JSONArray();
		retArr.add(xlevel.toString());
		return retArr;
	}

	//加载修仙仙官表
	public static JSONArray getGodId(String filePath) {

		filePath += "/WEB-INF/classes/xls/";
		JSONArray xlevel = getCsv(filePath + "XiuXianGodInfo.csv");
		JSONArray retArr = new JSONArray();
		retArr.add(xlevel.toString());
		return retArr;
	}

	//加载奇物关卡表--宠物关卡
	public static JSONArray getPetLevelId(String filePath) {
		filePath += "/WEB-INF/classes/xls/";
		JSONArray xlevel = getCsvStrange(filePath + "XStrangeLevel.csv",1);
		JSONArray retArr = new JSONArray();
		retArr.add(xlevel.toString());
		return retArr;
	}


	//加载奇物关卡表--兵书关卡
	public static JSONArray getBookLevelId(String filePath) {
		filePath += "/WEB-INF/classes/xls/";
		JSONArray xlevel = getCsvStrange(filePath + "XStrangeLevel.csv",2);
		JSONArray retArr = new JSONArray();
		retArr.add(xlevel.toString());
		return retArr;
	}

	//加载奇物关卡表--仙阶关卡
	public static JSONArray getCampaignLevelId(String filePath) {
		filePath += "/WEB-INF/classes/xls/";
		JSONArray xlevel = getCsvStrange(filePath + "XStrangeLevel.csv",3);
		JSONArray retArr = new JSONArray();
		retArr.add(xlevel.toString());
		return retArr;
	}

	//加载奇物关卡表--命格关卡
	public static JSONArray getDestinyLevelId(String filePath) {
		filePath += "/WEB-INF/classes/xls/";
		JSONArray xlevel = getCsvStrange(filePath + "XStrangeLevel.csv",7);
		JSONArray retArr = new JSONArray();
		retArr.add(xlevel.toString());
		return retArr;
	}
}

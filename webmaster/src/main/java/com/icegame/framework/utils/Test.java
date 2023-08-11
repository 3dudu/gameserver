package com.icegame.framework.utils;

import com.csvreader.CsvReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test{
	
  
  public static JSONArray getCsv(String filePath){
    JSONArray jsonArr = new JSONArray();
    try{
      JSONObject jsonObj = new JSONObject();
      ArrayList<String[]> csvList = new ArrayList();
      Map<String, Object> xnpc = new HashMap();
      CsvReader reader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
      while (reader.readRecord()) {
        csvList.add(reader.getValues());
      }
      reader.close();
      for (int row = 1; row < csvList.size(); row++){
        xnpc.put("id", ((String[])csvList.get(row))[0]);
        xnpc.put("name", ((String[])csvList.get(row))[1]);
        jsonObj = JSONObject.fromObject(xnpc);
        jsonArr.add(jsonObj);
      }
    }
    catch (Exception e){
      e.printStackTrace();
    }
    return jsonArr;
  }
  
  public static void main(String[] args){
	  String filePath = "src/main/resources/xls/";
    JSONArray xnpc = getCsv(filePath + "XNpc.csv");
    JSONArray xitem = getCsv(filePath + "XItem.csv");
    JSONArray xequip = getCsv(filePath + "XEquip.csv");
    JSONArray xhorse = getCsv(filePath + "XHorse.csv");
    JSONArray retArr = new JSONArray();
    System.out.println(xnpc.toString());
    System.out.println("----------------");
    System.out.println(xitem.toString());
    System.out.println("----------------");
    System.out.println(xequip.toString());
    System.out.println("----------------");
    System.out.println(xhorse.toString());
    System.out.println("----------------");
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
	
	  
	/*String str = "7:40002:5000\r\n7:40001:100000\r\n7:40005:100003" ;
	System.out.println(str.replaceAll("\\r\\n", ","));*/
	  
	/*char c = '；';
	char d = ';';
	System.out.println((byte)c);
	System.out.println((byte)d);*/
	  
	  /*String str = "41002:2";
	  String[] arr = str.split("\\:",3);
	  System.out.println(arr.length);
	  System.out.println(arr[0]);
	  System.out.println(arr[1]);*/
  }
  
  public static String[] tr(String str) {
	  String[] awardStr = str.split("\r\n");
	  return awardStr;
  }
}
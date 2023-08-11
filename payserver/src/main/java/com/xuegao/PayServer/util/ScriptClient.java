package com.xuegao.PayServer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xuegao.core.util.ClassPathUtil;
import com.xuegao.core.util.FileUtil;
import com.xuegao.core.util.HttpRequester;
import com.xuegao.core.util.HttpRespons;
import com.xuegao.core.util.Misc;

public class ScriptClient {
	public static void main(String[] args) throws Exception {
//		String url="http://127.0.0.1:9020/script/run";
		String url="http://121.40.193.31:9020/script/run";
		
		long a=System.currentTimeMillis();
		HttpRequester requester=new HttpRequester();
		requester.setDefaultContentEncoding("UTF-8");
		Map<String, String> params=new HashMap<String, String>();
		String timestamp=""+System.currentTimeMillis();
		String sign = Misc.md5("youneverknow" + timestamp);
		String script="";
		List<String> list=FileUtil.readFile(ClassPathUtil.findFile("ScriptTemp.java").getPath(), new ArrayList<String>(), "UTF-8");
		for(String s:list){
			script+=s.replace("ScriptTemp", "Script")+"\n";
		}
		byte[] bs=script.getBytes("UTF-8");
		List<Byte> list2=new ArrayList<Byte>();
		for(byte b:bs){
			list2.add(b);
		}
		params.put("sign", sign);
		params.put("timestamp", timestamp);
		params.put("scriptbytes", list2.toString());
		
		HttpRespons respons=requester.sendPost(url, params);
		
		String content=respons.getContent();
		long b=System.currentTimeMillis();
		System.out.println("返回结果:\r\n"+content+"cost:"+(b-a)+"ms");
	}
}

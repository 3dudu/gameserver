package com.xuegao.core.util.dfa;

import java.io.File;

import org.apache.log4j.Logger;

import com.xuegao.core.util.ClassPathUtil;
import com.xuegao.core.util.FileUtil;

public class DFAManager {
	
	static Logger logger=Logger.getLogger(DFAManager.class);
	private static DFA dfa=new DFA();
	
	private DFAManager(){
		reloadSensitiveWord();
	}
	
	public void reloadSensitiveWord(){
		try {
			//加载敏感词
			File file=ClassPathUtil.findFile("sensitive.txt");
			String path=file.getPath();
			logger.info("-----------------开始加载敏感词--------------------");
			String[] arr=FileUtil.readFileSafely(path, new String[0]);
			logger.info("------------------敏感词加载完毕，共"+arr.length+"个---------------first:"+(arr.length>0?arr[0]:"null"));
			dfa.reloadKeyWords(arr);
//			dfa.printDFAState();
		} catch (Exception e) {
			logger.error("敏感词加载异常",e);
		}
	}
	
	
	private static DFAManager dfaManager=new DFAManager();
	public static DFAManager getInstance(){
		return dfaManager;
	}
	
	public  boolean containsSensitiveWord(String str){
		return dfa.containsSensitiveWord(str);
	}
	
	public  String replaceSensitiveWord(String str){
		return dfa.replaceSensitiveWord(str, "***");
	}
	
	public static void main(String[] args) {
		DFAManager dfa=getInstance();
		int count=10000;
		String text="这是待检测的文字.asd6合彩eyioahsSF传世jkdnambxcxhasiuhd@(*vip公主^#@*%#akshd";
		System.out.println(dfa.replaceSensitiveWord(text));
		long a=System.currentTimeMillis();
		for(int i=0;i<count;i++){
			dfa.replaceSensitiveWord(text);
		}
		long b=System.currentTimeMillis();
		System.out.println("dfa检测消耗:"+(b-a)+"毫秒");
		
		File file=ClassPathUtil.findFile("sensitive.txt");
		String path=file.getPath();
		String[] arr=FileUtil.readFileSafely(path, new String[0]);
		
		long c=System.currentTimeMillis();
		for(int i=0;i<count;i++){
			for(int j=0;j<arr.length;j++){
				int index=text.indexOf(arr[j]);
				if(index!=-1){
					text=new StringBuffer().append(text.substring(0,index))
					.append("***")
					.append(text.substring(index+arr[j].length())).toString();
				}
			}
		}
		long d=System.currentTimeMillis();
		
		System.out.println("原检测消耗:"+(d-c)+"毫秒");
		
		
		
	}
}

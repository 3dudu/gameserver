package com.xuegao.core.util.dfa;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class DFA {
	static Logger logger=Logger.getLogger(DFA.class);
	private DFAState rootDfAState=new DFAState();
	
	ReadWriteLock lock = new ReentrantReadWriteLock(); 
	
	public void reloadKeyWords(String[] keywords){
		
			DFAState state=new DFAState();
			if(keywords==null||keywords.length==0){
				return;
			}
			for(String s:keywords){
				if(null==s||s.length()==0){
					continue;
				}
				s=s.toLowerCase();
				DFAState tempDFAState=state;
				char[] cs=s.toCharArray();
				for(int i=0;i<cs.length;i++){
					DFAState next=tempDFAState.getMap().get(cs[i]);
					if(next==null){
						next=new DFAState();
						next.setDeep(i+1);
						next.setPreDFAState(tempDFAState);
						next.setWord(cs[i]);
						char c=cs[i];
						tempDFAState.getMap().put(c, next);
					}
					if(i==cs.length-1){
						next.setEnd(true);
					}
					tempDFAState=next;
				}
			}
			lock.writeLock().lock();//上写锁
			rootDfAState=state;
			lock.writeLock().unlock();//释放写锁
		
	}
	
	private JSONObject DFAStateToJson(DFAState dfaState){
		JSONObject object=new JSONObject();
		Map<Character, DFAState> map=dfaState.getMap();
		JSONArray array=new JSONArray();
		for(Object key:map.keySet()){
			array.add( DFAStateToJson(map.get(key)));
		}
		object.put(""+dfaState.getDeep()+"_"+dfaState.getWord()+"_"+dfaState.isEnd(), array);
		return object;
	}
	
	
	public DFAState getRootDfAState() {
		return rootDfAState;
	}

	public void printDFAState(){
		JSONObject object=DFAStateToJson(rootDfAState);
		System.out.println(JSON.toJSONString(object, true));
	}
	
	public static void main(String[] args) {
		DFA dfa=new DFA();
		String[] words=new String[]{
			"FuCk","cd","efg"	,"fgsa","fasdasfasd","asdasfd","asdsdfsdf"
			,"中华人民共和国aaaaaaaa","国家","中国a"
		};
		dfa.reloadKeyWords(words);
		dfa.printDFAState();
		System.out.println("结果:"+dfa.replaceSensitiveWord("你好fUckfUc", "***"));
	}
	
	public String search(String str){
		char[] cs=str.toCharArray();
		StringBuffer stringBuffer=new StringBuffer();
		lock.readLock().lock();//上读锁
		for(int i=0;i<cs.length;i++){
			int index=find(cs,i,rootDfAState);
			if(index==-1){
				stringBuffer.append(cs[i]);
				//该字节非敏感词
				continue;
			}else {
				//是敏感词,长度为 index-i+1
				stringBuffer.append("[敏感词:");
				for(int j=i;j<=index;j++){
					stringBuffer.append(cs[j]);
				}
				stringBuffer.append("]");
				i=index;
			}
		}
		lock.readLock().unlock();//释放读锁
		
		return stringBuffer.toString();
		
	}
	
	public boolean containsSensitiveWord(String str){
		char[] cs=str.toLowerCase().toCharArray();
		
		for(int i=0;i<cs.length;i++){
			int index=find(cs,i,rootDfAState);
			if(index==-1){
				//该字节非敏感词
				continue;
			}else {
				//是敏感词,长度为 index-i+1
				return true;
			}
		}
		return false;
	}
	/**
	 * 替换敏感词，如果没有发现敏感词，返回null
	 * @param str
	 * @param placement
	 * @return
	 */
	public String replaceSensitiveWord(String str,String placement){
		char[] cs=str.toCharArray();
		char[] lowercs=str.toLowerCase().toCharArray();
		
		boolean containsSenstiveWord=false;
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<lowercs.length;i++){
			int index=find(lowercs,i,rootDfAState);
			if(index==-1){
				//该字节非敏感词
				sb.append(cs[i]);
				continue;
			}else {
				//是敏感词,长度为 index-i+1
				sb.append(placement);
				containsSenstiveWord=true;
				i=index;
			}
		}
		if(containsSenstiveWord){
			return sb.toString();
		}else{
			return null;
		}
	}
	
	public int find(char[] cs,int beginIndex,DFAState dfaState){
		for(int i=beginIndex;i<cs.length;i++){
			char c=cs[i];
			dfaState=dfaState.getMap().get(c);
			if(null==dfaState){
				//非敏感词
				return -1;
			}else {
				if(dfaState.isEnd()){
					//是敏感词
					return i;
				}else {
					//继续
					continue;
				}
			}
		}
		//不可能达到的地方
		return -1;
	}
	
	
}
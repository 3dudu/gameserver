package com.xuegao.core.util.dfa;

import java.util.HashMap;
import java.util.Map;

public class DFAState {
	int deep;
	DFAState preDFAState;
	char word='*';
	Map<Character, DFAState> map=new HashMap<Character, DFAState>(); 
	boolean isEnd=false;
	public int getDeep() {
		return deep;
	}
	public void setDeep(int deep) {
		this.deep = deep;
	}
	public DFAState getPreDFAState() {
		return preDFAState;
	}
	public void setPreDFAState(DFAState preDFAState) {
		this.preDFAState = preDFAState;
	}
	
	public char getWord() {
		return word;
	}
	public void setWord(char word) {
		this.word = word;
	}
	
	public Map<Character, DFAState> getMap() {
		return map;
	}
	public void setMap(Map<Character, DFAState> map) {
		this.map = map;
	}
	public boolean isEnd() {
		return isEnd;
	}
	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
	
	
	
}

package com.xuegao.core.datastruct;

import java.util.Collection;
import java.util.List;

public interface ListData<T> {
	public void set(int index,T t);
	public long add(T t);
	public void add(int index,T t);
	public void addAll(Collection<T> collection);
	public void remove(T t);
	public void remove(int index);
	public T popFromHead();
	public T popFromTail();
	public void trim(int start,int end);
	
	public T get(int index);
	public List<T> getAll();
	public List<T> getRange(int startIndex,int size);
	public long size();	
}
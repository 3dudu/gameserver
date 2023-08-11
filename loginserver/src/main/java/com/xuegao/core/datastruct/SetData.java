package com.xuegao.core.datastruct;

import java.util.Set;

public interface SetData<T> {
	public void add(T t);
	public void remove(T t);
	
	public Set<T> getAll();
	public boolean exists(T t);
	public long size();	
}
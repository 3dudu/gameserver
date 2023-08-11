package com.xuegao.core.datastruct;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public interface ZSetData<T> {
	void add(T t,double score);
	void add(Map<T, Double> scores);
	double incrby(T t,double score);
	void remove(T t);
	/**
	 * 删除指定排名的元素
	 * @param start 包含
	 * @param end 包含 -1表示最后一个
	 */
	long removeByRank(int start, int end);
	/**
	 * 删除权重在min和max之间的元素
	 * @param min 包含
	 * @param max 包含
	 * @return
	 */
	long removeByScore(double min, double max);
	
	
	long size();
	/**
	 * 权重在min<=x<=max之间的数量
	 * @param min
	 * @param max
	 */
	long size(double min,double max);
	
	/**
	 * 返回指定位置的集合元素（从低到高)
	 * @param start 包含
	 * @param end 包含
	 * @return
	 */
	Set<T> getRangeOrderByScore(int start,int end);
	/**
	 * 返回指定位置的集合元素（从低到高)
	 */
	LinkedHashMap<T, Double> getRangeMapOrderByScore(int start,int end);
	
	/**
	 * 返回权重在min与max之间的元素
	 * @param min
	 * @param max
	 * @return
	 */
	Set<T> getRange(double min, double max);
	/**
	 * 返回权重在min与max之间的元素
	 */
	LinkedHashMap<T, Double> getRangeMap(double min, double max);
	/**
	 * 返回指定位置的集合元素（从高到低)
	 * @param start 包含
	 * @param end 包含
	 * @return
	 */
	Set<T> getRangeOrderByScoreDesc(int start,int end);
	/**
	 * 返回指定位置的集合元素（从高到低)
	 */
	LinkedHashMap<T, Double> getRangeMapOrderByScoreDesc(int start,int end);
	/**
	 * 获取排名(从低到高)
	 * @param t
	 * @return
	 */
	Long rank(T t);
	/**
	 * 获取排名(从高到低)
	 * @param t
	 * @return
	 */
	Long revrank(T t);
	
	/**
	 * 获取元素的权重
	 * @param t
	 * @return
	 */
	double score(T t);
}
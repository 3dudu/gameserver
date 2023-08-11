package com.xuegao.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.common.base.Optional;

public class RandomUtil {
	public static interface Weightable<T>{
		public int getWeight(T t);
	};
	
	public static <T>Optional<T> random(Collection<T> collection){
		return random(collection, new Weightable<T>() {
			@Override
			public int getWeight(T t) {
				return 1;
			}
			
		});
	}
	/**
	 * 根据权重 从集合中随机一个
	 * @param collection
	 * @param weightable
	 * @return
	 */
	public static <T>Optional<T> random(Collection<T> collection,Weightable<T> weightable){
		if(collection==null){
			return Optional.absent();
		}
		int total=0;
		for(T t:collection){
			total+=weightable.getWeight(t);
		}
		if(total<=0){
			return Optional.absent();
		}
		int r=new Random().nextInt(total);
		int temp=0;
		for(T t:collection){
			temp+=weightable.getWeight(t);
			if(r<temp){
				return Optional.fromNullable(t);
			}
		}
		return null;
	}
	
	/**
	 * 根据权重 从集合中随机多个
	 * @param collection
	 * @param weightable
	 * @return
	 */
	public static <T>Optional<List<T>> random(Collection<T> collection,Weightable<T> weightable,int count){
		if(collection==null){
			return Optional.absent();
		}
		List<T> list=new ArrayList<T>();
		List<T> fixList=new ArrayList<T>();
		for(T t:collection){
			int w=weightable.getWeight(t);
			if(w>0){
				fixList.add(t);
			}
		}
		if(fixList.size()<count){
			return Optional.absent();
		}
		for(int i=0;i<count;i++){
			Optional<T> choosed=random(fixList, weightable);
			if(!choosed.isPresent()){
				return Optional.absent();
			}else{
				T t=choosed.get();
				fixList.remove(t);
				list.add(t);
			}
		}
		return Optional.fromNullable(list);
	}
	/**
	 * 从数组中随机一个，返回索引
	 * @param weights 权重数组
	 * @return
	 */
	public static Optional<Integer> random(int[] weights){
		if(weights==null||weights.length<1){
			return Optional.absent();
		}
		int total=0;
		for(int w:weights){
			total+=w;
		}
		if(total<=0){
			return Optional.absent();
		}
		int r=new Random().nextInt(total);
		int temp=0;
		for(int i=0;i<weights.length;i++){
			int w=weights[i];
			temp+=w;
			if(r<temp){
				return Optional.fromNullable(i);
			}
		}
		return Optional.absent();
	}
	/**
	 * 从数组中随机一个，返回索引
	 * @param weights 权重数组
	 * @return
	 */
	public static Optional<Integer> random(List<Integer> weights){
		if(weights==null||weights.size()<1){
			return Optional.absent();
		}
		int total=0;
		for(int w:weights){
			total+=w;
		}
		if(total<=0){
			return Optional.absent();
		}
		int r=new Random().nextInt(total);
		int temp=0;
		for(int i=0;i<weights.size();i++){
			int w=weights.get(i);
			temp+=w;
			if(r<temp){
				return Optional.fromNullable(i);
			}
		}
		return Optional.absent();
	}
	
}

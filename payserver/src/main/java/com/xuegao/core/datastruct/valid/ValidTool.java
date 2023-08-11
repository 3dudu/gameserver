package com.xuegao.core.datastruct.valid;

import java.lang.reflect.Method;

import com.xuegao.core.datastruct.BeanData;
import com.xuegao.core.datastruct.ListData;
import com.xuegao.core.datastruct.MapData;
import com.xuegao.core.datastruct.SetData;
import com.xuegao.core.datastruct.ZSetData;
import com.xuegao.core.datastruct.cacheinitial.AbsRedisInitialCacheBeanData;
import com.xuegao.core.datastruct.cacheinitial.AbsRedisInitialCacheListData;
import com.xuegao.core.datastruct.cacheinitial.AbsRedisInitialCacheMapData;
import com.xuegao.core.datastruct.cacheinitial.AbsRedisInitialCacheSetData;
import com.xuegao.core.datastruct.cacheinitial.AbsRedisInitialCacheZSetData;

/**
 * 检查initial类中的方法是否完整
 * @author ccx
 */
public class ValidTool {
	public static void checkCacheInitialJavaFile(){
		Class[] cs1={BeanData.class,ListData.class,MapData.class,SetData.class,ZSetData.class};
		Class[] cs2={AbsRedisInitialCacheBeanData.class,AbsRedisInitialCacheListData.class,AbsRedisInitialCacheMapData.class,AbsRedisInitialCacheSetData.class,AbsRedisInitialCacheZSetData.class};
		for(int i=0;i<cs1.length;i++){
			Class class1=cs1[i];
			Method[] methods1=class1.getDeclaredMethods();
			Class class2=cs2[i];
			Method[] methods2=class2.getDeclaredMethods();
			for(Method m1:methods1){
				
				boolean isExist=false;
				for(Method m2:methods2){
					
					if(m1.getName().equals(m2.getName())){
						
						//检查参数类型
						Class<?>[] types1=m1.getParameterTypes();
						Class<?>[] types2=m2.getParameterTypes();
						boolean typeMatched=true;
						if(types1.length!=types2.length){
							typeMatched=false;
						}else {
							for(Class c1:types1){
								boolean has=false;
								for(Class c2:types2){
									if(c2.equals(c1)){
										has=true;
										break;
									}
								}
								if(!has){
									typeMatched=false;
									break;
								}
							}
						}
						
						if(typeMatched){
							isExist=true;
							break;
						}
						
					}
					
				}
				if(!isExist){
					System.err.println("检查类"+class2.getSimpleName()+"是否存在"+m1.getName()+"()方法-----------不存在");
				}else {
//					System.out.println("检查类"+class2.getSimpleName()+"是否存在"+m1.getName()+"()方法-----------存在");
				}
			}
		}
		
	}
	public static void main(String[] args) {
		checkCacheInitialJavaFile();
	}
}

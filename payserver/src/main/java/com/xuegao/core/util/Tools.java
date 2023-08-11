package com.xuegao.core.util;

import java.lang.reflect.Method;

import com.xuegao.core.netty.Cmd;

public class Tools {
	/**
	 * 懒人使用,打印出handler的cmds
	 * @param classes Handlers数组
	 */
	public static void printCmds(Class<?>... classes){
		if(classes==null||classes.length==0)return;
		for(Class<?> class1:classes){
			Method[] methods=class1.getDeclaredMethods();
			for(int i=0;i<methods.length;i++){
				Method method=methods[i];
				Cmd cmd=method.getAnnotation(Cmd.class);
				if(cmd!=null){
					System.out.println("addRequestHandler(\""+cmd.value()+"\","+class1.getSimpleName()+".class);");
				}
			}
			System.out.println();
		}
	}
	
	/**
	 * 元素数组中有null，则返回true.没有null，返回false
	 */
	public static boolean hasNULL(Object... objects){
		if(objects==null)return false;
		for(Object obj:objects){
			if(null==obj){
				return true;
			}
		}
		return false;
	}
	
	
}

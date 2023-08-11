package com.xuegao.core.netty;

import io.netty.channel.ChannelHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.xuegao.core.db.DBWrapper;
import com.xuegao.core.db.Transaction;

public abstract class CmdHandler {
	static Logger logger=Logger.getLogger(CmdHandler.class);
	
	public CmdHandler() {
		init();
	}
	
	public abstract void init();
	private final  Map<String, Class<?>> cmdMap=new HashMap<String, Class<?>>();
	private final  Map<String, Method> methodMap=new HashMap<String,Method>();
	private final  Map<Class<?>, Object> objMap=new HashMap<Class<?>, Object>();
	private List<ICmdInterceptor> headInterceptors=new ArrayList<ICmdInterceptor>();
	private Map<String, Cmd> cmdInfoMap=new HashMap<String, Cmd>();
	
	public void addRequestHandler(String cmdName,Class<?> handleClass){
		try {
			if(cmdMap.containsKey(cmdName)||methodMap.containsKey(cmdName)){
				logger.warn("Same cmd name:"+cmdName+",the first one worked!");
				return;
			}
			if(!objMap.containsKey(handleClass)){
				objMap.put(handleClass, handleClass.newInstance());
			}
			//找出cmd对应的method
			Method[] methods=handleClass.getDeclaredMethods();
			for(int i=0;i<methods.length;i++){
				Method method=methods[i];
				Cmd cmd=method.getAnnotation(Cmd.class);
				if(cmd!=null&&cmdName.equals(cmd.value())){
					//找到相应的方法
					if(method.getParameterTypes().length!=2||!method.getParameterTypes()[0].equals(User.class)){
						logger.error("Cmd method params type not suitable:"+cmd.value());
						continue;
					}
					cmdInfoMap.put(cmdName, cmd);
					cmdMap.put(cmdName, handleClass);
					methodMap.put(cmd.value(), method);
					logger.info("CMD:\t["+cmd.value()+"]\t-->\t["+handleClass.getSimpleName()+"."+method.getName()+"]");
				}
			}
			if(!cmdMap.containsKey(cmdName)||!methodMap.containsKey(cmdName)){
				//如果未找到对应的注解
				logger.error("Cmd Annotation not found:"+cmdName);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public Cmd fetCmdInfo(String cmdName){
		return this.cmdInfoMap.get(cmdName);
	}
	/**
	 * 消息入口
	 * @param sender
	 * @param cmd
	 * @param params
	 */
	public abstract void handleRequest(User sender,String cmd,JSONObject params);
	
	/**
	 * 执行cmd消息
	 * @param sender
	 * @param cmd
	 * @param params
	 */
	public void exec(User sender,String cmd,JSONObject params){
		SessionManager.setCurrentUser(sender);
		String[] proxoolnames=null;
		try {
			List<ICmdInterceptor> headInterceptors=getHeadInterceptors();
			int hsize=headInterceptors.size();
			for(int i=0;i<hsize;i++){
				int status=headInterceptors.get(i).intercept(sender, cmd, params);
				if(status==0)return;
			}
			if(!hasCmd(cmd)){
				throw new Exception("cmd "+cmd+" not found!");
			}
			Class<?> hClass=cmdMap.get(cmd);
			Object object=objMap.get(hClass);
			Method method=methodMap.get(cmd);
			
			Transaction transaction=method.getAnnotation(Transaction.class);
			if(transaction!=null){
				proxoolnames=transaction.proxoolName().split(",");
				for(String proxoolName:proxoolnames){
					DBWrapper.getInstance(proxoolName).beginTransaction();
				}
			}
			
			method.invoke(object,sender, params);
			if(proxoolnames!=null){
				for(String proxoolName:proxoolnames){
					DBWrapper.getInstance(proxoolName).endTransaction();
				}
			}
		} catch (InvocationTargetException e) {
			if(proxoolnames!=null){
				for(String proxoolName:proxoolnames){
					DBWrapper.getInstance(proxoolName).rollbackFailureTransaction();
				}
			}
			caughtLogicException(sender, cmd, params, e.getCause());
		}catch (Throwable e) {
			if(proxoolnames!=null){
				for(String proxoolName:proxoolnames){
					DBWrapper.getInstance(proxoolName).rollbackFailureTransaction();
				}
			}
			caughtLogicException(sender, cmd, params, e);
		}finally{
			//内存回收
			if(params instanceof Releasable){
				((Releasable)params).release();
			}
		}
	}
	
	/**
	 * 编解码异常，或者连接异常
	 * @param sender
	 * @param cmd
	 * @param params
	 * @param e
	 */
	public void caughtDecodeException(User sender,Throwable cause){
		logger.error("编解码异常,连接关闭:"+cause.getMessage(),cause);
		sender.getChannel().close();
	}
	
	/**
	 * 执行handler中的逻辑时，遇到异常时的处理
	 * @param sender
	 * @param cmd
	 * @param params
	 * @param e
	 */
	public abstract void caughtLogicException(User sender,String cmd,JSONObject params,Throwable e);
	
	public boolean hasCmd(String cmd){
		return cmdMap.containsKey(cmd);
	}
	public List<ICmdInterceptor> getHeadInterceptors() {
		return headInterceptors;
	}
	public void setHeadInterceptors(List<ICmdInterceptor> headInterceptors) {
		this.headInterceptors = headInterceptors;
	}
	
	public abstract void sessionCreated(User sender,ChannelHandlerAdapter channelHandlerAdapter);
	
	public abstract void sessionRemoved(User sender,ChannelHandlerAdapter channelHandlerAdapter);
}

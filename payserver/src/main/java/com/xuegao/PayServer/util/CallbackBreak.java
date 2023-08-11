package com.xuegao.PayServer.util;
/**
 * @author absir
 * 
 * @param <T>
 */
public  interface CallbackBreak<T> {

	/**
	 * @param template
	 * @throws BreakException
	 */
	void doWith(T template) throws BreakException;
	/**
	 * @author absir
	 * 
	 */
	@SuppressWarnings("serial")
	public static class BreakException extends Exception {

	}
}
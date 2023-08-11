package com.xuegao.LoginServer.util;

import com.xuegao.LoginServer.data.Constants.PlatformOption;
import com.xuegao.LoginServer.data.Constants.PlatformOption.XGSDK;
import com.xuegao.LoginServer.handler.UserHandler;
import com.xuegao.core.util.Tools;

public class Test {

	public static void main(String[] args) throws ClassNotFoundException {
		Tools.printCmds(UserHandler.class);
		System.out.println(XGSDK.class);
		String name=PlatformOption.class.getName()+"$XGSDK";
		Class<?> class1=Class.forName(name);
		System.out.println(class1.getName());
	}
}

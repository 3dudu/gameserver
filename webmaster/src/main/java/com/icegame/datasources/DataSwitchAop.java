package com.icegame.datasources;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class DataSwitchAop {
		
	@Pointcut("execution(* com.icegame.sysmanage.service.impl.PayListService.*(..)) || execution(* com.icegame.sysmanage.service.impl.AutoOpenServerService.*(..))" )
	public void execute() {
		
	}
	
	/**
	 * 切换到另一种数据库进行查询
	 * @author chsterccw  
	 * @date 2018年3月8日
	 */
	@Before("execute()")
	public void dataSwitch(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();

		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		DataSwitch data = null;
		if (method != null) {
			data = method.getAnnotation(DataSwitch.class);
		}
		String dataSource = data.dataSource();
		if (dataSource != null) {
			MultipleDataSource.clearDataSourceType();
			MultipleDataSource.setDataSourceType(dataSource);
		}
	}
	
	/**
	 * 执行完查询操作之后还原到默认数据库
	 * @author chsterccw  
	 * @date 2018年3月8日
	 */
	@AfterReturning("execute()")
	public void closeDataSource() {
		MultipleDataSource.clearDataSourceType();
	}
}
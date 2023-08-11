package com.icegame.log.frameworks;

import com.alibaba.fastjson.JSON;
import com.icegame.framework.utils.HttpContextUtils;
import com.icegame.framework.utils.IPUtils;
import com.icegame.framework.utils.TimeUtils;
import com.icegame.framework.utils.UserUtils;
import com.icegame.log.entity.SysLog;
import com.icegame.log.service.impl.SysLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.URLDecoder;

/**
 * 系统日志：切面处理类
 */
@Aspect
@Component
public class SysLogAspect {

    @Autowired
    private SysLogService sysLogService;

    //定义切点 @Pointcut
    //在注解的位置切入代码
    @Pointcut("@annotation( com.icegame.log.frameworks.SysLogging)")
    public void logPoinCut() {
    }

    //切面 配置通知
    @AfterReturning("logPoinCut()")
    public void saveSysLog(JoinPoint joinPoint) {
        //保存日志
        SysLog sysLog = new SysLog();

        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();

        //获取操作
        SysLogging sysLogging = method.getAnnotation(SysLogging.class);
        if (sysLogging != null) {
            String value = sysLogging.value();
            sysLog.setOperation(value);//保存获取的操作
        }

        //获取请求的类名
        String className = joinPoint.getTarget().getClass().getName();
        //获取请求的方法名
        String methodName = method.getName();
        sysLog.setMethod(className + "." + methodName);

        //请求的参数
        Object[] args = joinPoint.getArgs();
        //将参数所在的数组转换成json
        //String json = URLDecoder.decode(args.toString(), "UTF-8");
        String params = JSON.toJSONString(args);
        sysLog.setParams(params);

        sysLog.setCreateTime(TimeUtils.getCurTime());
        //获取用户名
        sysLog.setUsername(UserUtils.getCurrrentUserName());
        //获取用户ip地址
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        sysLog.setIp(IPUtils.getIpAddr(request));

        //调用service保存SysLog实体类到数据库
        sysLogService.save(sysLog);
    }

}
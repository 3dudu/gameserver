package com.icegame.framework.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpContextUtils {

    private static ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    private static HttpServletRequest request = requestAttributes.getRequest();

    private static HttpServletResponse response = requestAttributes.getResponse();


    public static HttpServletRequest getHttpServletRequest (){
        return request;
    }

    public static HttpServletResponse getHttpServletResponse (){
        return response;
    }

}

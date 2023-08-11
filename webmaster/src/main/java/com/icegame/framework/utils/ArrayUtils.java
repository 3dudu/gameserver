package com.icegame.framework.utils;

import org.apache.poi.ss.formula.functions.T;

/**
 * @author wuzhijian
 * @date 2019-06-27
 */
public class ArrayUtils {

    public static String toStr(String[] arr){

        StringBuffer sb = new StringBuffer();
        for(String str :arr){
            sb.append(str).append(",");
        }
        String result = sb.toString();
        return result.substring(0,result.length() - 1);
    }

}

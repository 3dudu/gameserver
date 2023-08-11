package com.xuegao.LoginServer.util;

import java.util.Arrays;
import java.util.Map;

public class YueWanUtils {
    /**
     *  a-z排序key 并去除空value
     * @param params
     * @return
     */
    public static String getSortQueryString(Map<String, String> params){
        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);
        StringBuffer sb = new StringBuffer();
        for (Object key : keys) {
            if (isEmpty(params.get(String.valueOf(key)))) {
                sb.append(key).append("=").append(params.get(String.valueOf(key))).append("&");
            }
        }
        String text = sb.toString();
        if (text.endsWith("&")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    /**
     * 悦玩专属的验空方式
     * @param object
     * @return
     */
    public static boolean  isEmpty(Object object) {
        if (object == null) {
            return false;
        }
        String str =  object.toString();
        if ("".equals(str) || "0".equals(str) || "0.0".equals(str) || "false".equals(str)) {
            return false;
        }
        return true;
    }
}

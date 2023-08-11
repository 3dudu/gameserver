package com.xuegao.PayServer.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.Map;

public class YueWanUtils {
    public static JSONObject getErrorMsg(String msg, int ret, String content) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ret", ret);
        jsonObject.put("msg", msg);
        jsonObject.put("content", content);
        return jsonObject;
    }

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

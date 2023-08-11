package com.icegame.framework.utils;

import com.icegame.sysmanage.entity.Status;

import net.sf.json.JSONObject;
/**
 * 
 * 0：验证码有误  1：输入为空  2：用户名或者密码有误   3：验证成功	 
 * @author chsterccw
 *
 */
public class JsonUtils {
	
	public static String verifyerror(){
		Status status  = new Status();
		status.setStatus("0");
		return JSONObject.fromObject(status).toString();
	}
	
	public static String emptyerror(){
		Status status  = new Status();
		status.setStatus("1");
		return JSONObject.fromObject(status).toString();
	}
	
	public static String autherror(){
		Status status  = new Status();
		status.setStatus("2");
		return JSONObject.fromObject(status).toString();
	}
	
	public static String success(){
		Status status  = new Status();
		status.setStatus("3");
		return JSONObject.fromObject(status).toString();
	}
	
	public static String success(String str){
		Status status  = new Status();
		status.setStatus(str);
		return JSONObject.fromObject(status).toString();
	}
	
	public static String empty(){
		Status status  = new Status();
		status.setStatus("empty");
		return JSONObject.fromObject(status).toString();
	}
	
	
	public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * 添加space
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }

}

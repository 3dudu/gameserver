package com.xuegao.PayServer.util.wxpay;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class NumUtil {

	
	
	public static String getRandNum() {
		Random random = new Random();
		String result = "";
		for (int i = 0; i < 6; i++) {
			result += random.nextInt(10);
		}
		return result;
	}
	
	/**
	 * @param 生成字符串的长度
	 * @param 字符组成 提供5种形式
	 * */
	public static  String getCode(int passLength, int type)  
    {  
        StringBuffer buffer = null;  
        StringBuffer sb = new StringBuffer();  
        Random r = new Random();  
        r.setSeed(new Date().getTime()+ new Random().nextInt(99999));  
        switch (type)  
        {  
        case 0:  
            buffer = new StringBuffer("0123456789");  
            break;  
        case 1:  
            buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyz");  
            break;  
        case 2:  
            buffer = new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ");  
            break;  
        case 3:  
            buffer = new StringBuffer(  
                    "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");  
            break;  
        case 4:  
            buffer = new StringBuffer(  
                    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");  
            sb.append(buffer.charAt(r.nextInt(buffer.length() - 10)));  
            passLength -= 1;  
            break;  
        case 5:  
            String s = UUID.randomUUID().toString();  
            sb.append(s.substring(0, 8) + s.substring(9, 13)  
                    + s.substring(14, 18) + s.substring(19, 23)  
                    + s.substring(24));  
        }  
  
        if (type != 5)  
        {  
            int range = buffer.length();  
            for (int i = 0; i < passLength; ++i)  
            {  
                sb.append(buffer.charAt(r.nextInt(range)));  
            }  
        }  
        return sb.toString();  
    }
	/** 四舍五入
	 * @param oriNum 待处理的数字
	 * @param scale  保留小数后几位
	 * */
	public static BigDecimal formatBigDecimal(BigDecimal oriNum, int scale){
		BigDecimal one = new BigDecimal("1");  
		return oriNum.divide(one, scale, BigDecimal.ROUND_HALF_UP);
	}
	
	
	
	
	/** 
	* 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 
	* 定精度，以后的数字四舍五入。 
	* @param v1 被除数 
	* @param v2 除数 
	* @param scale 表示表示需要精确到小数点以后几位。 
	* @return 两个参数的商 
	*/  
	public static double div(double v1, double v2, int scale) {  
	   if (scale < 0) {  
	    throw new IllegalArgumentException(  
	      "The scale must be a positive integer or zero");  
	   }  
	   BigDecimal b1 = new BigDecimal(Double.toString(v1));  
	   BigDecimal b2 = new BigDecimal(Double.toString(v2));  
	   return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
	}  
	  
	/** 
	* 提供精确的小数位四舍五入处理。 
	* @param v 需要四舍五入的数字 
	* @param scale 小数点后保留几位 
	* @return 四舍五入后的结果 
	*/  
	public static double round(double v, int scale) {  
	   if (scale < 0) {  
	    throw new IllegalArgumentException(  
	      "The scale must be a positive integer or zero");  
	   }  
	   BigDecimal b = new BigDecimal(Double.toString(v));  
	   BigDecimal one = new BigDecimal("1");  
	   return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
	}  
	
	
	/** 
     * 获得一个UUID 
     * @return String UUID 
     */ 
    public static String getUUID(){ 
        String s = UUID.randomUUID().toString(); 
        //去掉“-”符号 
        return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
    } 
    
    /**  
     *   
     * 功能描述：去除字符串首部为"0"字符  
        
     * @param str 传入需要转换的字符串  
     * @return 转换后的字符串  
     */  
    public static String removeZero(String str){     
        char  ch;    
        String result = "";  
        if(str != null && str.trim().length()>0 && !str.trim().equalsIgnoreCase("null")){                  
            try{              
                for(int i=0;i<str.length();i++){  
                    ch = str.charAt(i);  
                    if(ch != '0'){                        
                        result = str.substring(i);  
                        break;  
                    }  
                }  
            }catch(Exception e){  
                result = "";  
            }     
        }else{  
            result = "";  
        }  
        return result;  
              
    }  
    /**  
     *   
     * 功能描述：金额字符串转换：单位元转成单分  
       
     * @param str 传入需要转换的金额字符串  
     * @return 转换后的金额字符串  
     */       
    public static String yuanToFen(Object o) {  
        if(o == null)  
            return "0";  
        String s = o.toString();  
        int posIndex = -1;  
        String str = "";  
        StringBuilder sb = new StringBuilder();  
        if (s != null && s.trim().length()>0 && !s.equalsIgnoreCase("null")){  
            posIndex = s.indexOf(".");  
            if(posIndex>0){  
                int len = s.length();  
                if(len == posIndex+1){  
                    str = s.substring(0,posIndex);  
                    if(str == "0"){  
                        str = "";  
                    }  
                    sb.append(str).append("00");  
                }else if(len == posIndex+2){  
                    str = s.substring(0,posIndex);  
                    if(str == "0"){  
                        str = "";  
                    }  
                    sb.append(str).append(s.substring(posIndex+1,posIndex+2)).append("0");  
                }else if(len == posIndex+3){  
                    str = s.substring(0,posIndex);  
                    if(str == "0"){  
                        str = "";  
                    }  
                    sb.append(str).append(s.substring(posIndex+1,posIndex+3));  
                }else{  
                    str = s.substring(0,posIndex);  
                    if(str == "0"){  
                        str = "";  
                    }  
                    sb.append(str).append(s.substring(posIndex+1,posIndex+3));  
                }  
            }else{  
                sb.append(s).append("00");  
            }  
        }else{  
            sb.append("0");  
        }  
        str = removeZero(sb.toString());  
        if(str != null && str.trim().length()>0 && !str.trim().equalsIgnoreCase("null")){  
            return str;  
        }else{  
            return "0";  
        }  
    }  
    
}

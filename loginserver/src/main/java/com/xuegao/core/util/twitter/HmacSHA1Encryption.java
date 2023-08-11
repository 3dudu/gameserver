package com.xuegao.core.util.twitter;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;

/**
 * 推特加密工具类
 */
public class HmacSHA1Encryption {

    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";

    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptText 被签名的字符串
     * @param encryptKey  密钥
     * @return 返回被加密后的字符串
     * @throws Exception
     */
    public static String hmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        // 完成 Mac 操作
        byte[] digest = mac.doFinal(text);
        return Base64.encodeBase64String(digest);
    }

    /**
     * 转换成Hex
     *
     * @param bytesArray
     */
    public static StringBuilder bytesToHexString(byte[] bytesArray) {
        if (bytesArray == null) {
            return null;
        }
        StringBuilder sBuilder = new StringBuilder();
        for (byte b : bytesArray) {
            String hv = String.format("%02x", b);
//			String hv = Integer.toHexString( b & 0xFF );
            sBuilder.append(hv);
        }
        return sBuilder;
    }

    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptData 被签名的字符串
     * @param encryptKey  密钥
     * @return 返回被加密后的字符串
     * @throws Exception
     */
    public static String hmacSHA1Encrypt(byte[] encryptData, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        // 完成 Mac 操作
        byte[] digest = mac.doFinal(encryptData);
        StringBuilder sBuilder = bytesToHexString(digest);
        return sBuilder.toString();
    }

    /**
     * 生成签名
     *
     * @param params
     * @param secretKey
     * @return
     * @throws Exception
     */
    public static String getSign(Map<String, String> params, String secretKey,String methodName) throws Exception {
        String signString = getSortQueryString(params,methodName);
        return URLEncoder.encode(HmacSHA1Encryption.hmacSHA1Encrypt(signString, secretKey),ENCODING);
    }

    /**
     * 获取得到排序好的查询字符串
     *
     * @param params
     * @return
     */
    public static String getSortQueryString(Map<String, String> params,String methodName) throws UnsupportedEncodingException {
        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);
        StringBuffer sb = new StringBuffer();
        for (Object key : keys) {
            sb.append(key).append("=").append(params.get(String.valueOf(key))).append("&");
        }
        String text = sb.toString();
        if (text.endsWith("&")) {
            text = text.substring(0, text.length() - 1);
        }
        return methodName+"&"+URLEncoder.encode("https://api.twitter.com/oauth/request_token", ENCODING)+"&"+URLEncoder.encode(text, ENCODING);
    }
}

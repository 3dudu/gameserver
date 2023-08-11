package com.xuegao.PayServer.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HmacSHA1Encryption {
    private static final String MAC_NAME = "HmacSHA1";
    private static final String MAC_NAME256 = "HmacSHA256";
    private static final String ENCODING = "UTF-8";

    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     * @param encryptText 被签名的字符串
     * @param encryptKey 密钥
     * @return 返回被加密后的字符串
     * @throws Exception
     */
    public static String HmacSHA1Encrypt( String encryptText, String encryptKey ){
        StringBuilder sBuilder =null;
        try {
            byte[] data = encryptKey.getBytes( ENCODING );
            // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKey secretKey = new SecretKeySpec( data, MAC_NAME );
            // 生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance( MAC_NAME );
            // 用给定密钥初始化 Mac 对象
            mac.init( secretKey );
            byte[] text = encryptText.getBytes( ENCODING );
            // 完成 Mac 操作
            byte[] digest = mac.doFinal( text );
            sBuilder = bytesToHexString( digest );
        }catch (Exception e) {
            e.printStackTrace();
        }
        return sBuilder.toString();
    }

    /**
     * 生成 HMACSHA256
     * @param data 待处理数据
     * @param key 密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String HMACSHA256Encrypt (String data, String key)  {
        StringBuilder sb = null ;

        try {
            sb = new StringBuilder();
            Mac mac = Mac.getInstance(MAC_NAME256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(ENCODING), MAC_NAME256);
            mac.init(secretKeySpec);
            byte[] array = mac.doFinal(data.getBytes(ENCODING));

            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
        }catch (Exception e) {
             e.printStackTrace();               
        }
           return sb.toString().toUpperCase();
    }

    /**
     * 转换成Hex
     *
     * @param bytesArray
     */
    public static StringBuilder bytesToHexString( byte[] bytesArray ){
        if ( bytesArray == null ){
            return null;
        }
        StringBuilder sBuilder = new StringBuilder();
        for ( byte b : bytesArray ){
            String hv = String.format("%02x", b);
            sBuilder.append( hv );
        }
        return sBuilder;
    }

    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptData 被签名的字符串
     * @param encryptKey 密钥
     * @return 返回被加密后的字符串
     * @throws Exception
     */
    public static String hmacSHA1Encrypt( byte[] encryptData, String encryptKey ) throws Exception{
        byte[] data = encryptKey.getBytes( ENCODING );
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec( data, MAC_NAME );
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance( MAC_NAME );
        // 用给定密钥初始化 Mac 对象
        mac.init( secretKey );
        // 完成 Mac 操作
        byte[] digest = mac.doFinal( encryptData );
        StringBuilder sBuilder = bytesToHexString( digest );
        return sBuilder.toString();
    }

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
}

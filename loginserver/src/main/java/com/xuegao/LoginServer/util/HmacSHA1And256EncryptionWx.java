package com.xuegao.LoginServer.util;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HmacSHA1And256EncryptionWx {
    private static final String MAC_SHA1_NAME = "HmacSHA1";
    private static final String MAC_SHA256_NAME = "HmacSHA256";
    private static final String ENCODING = "UTF-8";

    public static void main(String[] args) throws Exception {
       String secretKey = "2836e95fcd10e04b0069bb1ee659955b";
       String appId ="test-appId";
       String bizId ="test-bizId";
       String timestamps ="1584949895758";

    }
    /**
     * 生成 HMACSHA256
     * @param data 待处理数据
     * @param key 密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String GENHMACSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance(MAC_SHA256_NAME);
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(ENCODING), MAC_SHA256_NAME);
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes(ENCODING));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    /**
     * 使用 HMAC-SHA1 签名方法对对 encryptText 进行签名
     *
     * @param encryptData
     *            被签名的字符串
     * @param encryptKey
     *            密钥
     * @return 返回被加密后的字符串
     * @throws Exception
     */
    public static String HmacSHA1Encrypt(byte[] encryptData, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        SecretKey secretKey = new SecretKeySpec(data, MAC_SHA1_NAME);
        Mac mac = Mac.getInstance(MAC_SHA1_NAME);
        mac.init(secretKey);
        byte[] digest = mac.doFinal(encryptData);
        StringBuilder sBuilder = bytesToHexString(digest);
        return sBuilder.toString();
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
            sBuilder.append(hv);
        }
        return sBuilder;
    }
}
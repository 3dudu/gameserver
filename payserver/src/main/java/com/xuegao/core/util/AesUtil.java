package com.xuegao.core.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
/*
01 算法/模式/填充                16字节加密后数据长度        不满16字节加密后长度
02 AES/CBC/NoPadding             16                          不支持
03 AES/CBC/PKCS5Padding          32                          16
04 AES/CBC/ISO10126Padding       32                          16
05 AES/CFB/NoPadding             16                          原始数据长度
06 AES/CFB/PKCS5Padding          32                          16
07 AES/CFB/ISO10126Padding       32                          16
08 AES/ECB/NoPadding             16                          不支持
09 AES/ECB/PKCS5Padding          32                          16
10 AES/ECB/ISO10126Padding       32                          16
11 AES/OFB/NoPadding             16                          原始数据长度
12 AES/OFB/PKCS5Padding          32                          16
13 AES/OFB/ISO10126Padding       32                          16
14 AES/PCBC/NoPadding            16                          不支持
15 AES/PCBC/PKCS5Padding         32                          16
16 AES/PCBC/ISO10126Padding      32                          16
 
 
 
CryptoJS supports the following padding schemes:
 
    Pkcs7 (the default)
    Iso97971
    AnsiX923
    Iso10126
    ZeroPadding
    NoPadding 
*/
public class AesUtil {
	static Logger logger=Logger.getLogger(AesUtil.class);
	public static final String PREMARK0="/Dota0";
	public static final String KEY="mtheforcebewithu";
	
	public static String encrypt(String input, String key){
        byte[] crypted = null;
        String mi=null;
        try{
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/ISO10126Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
            mi=new String(Base64.encodeBase64(crypted));
        }catch(Exception e){
        	logger.error("---aes加密失败:"+e.getMessage()+",input="+input+",key="+key);
		}
		return mi;
	}
 
	public static String decrypt(String input, String key){
        byte[] output = null;
        String data=null;
        try{
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/ISO10126Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            byte[] bs=Base64.decodeBase64(input);
            output = cipher.doFinal(bs);
            data=new String(output);
        }catch(Exception e){
        	logger.error("---aes解密失败:"+e.getMessage()+",input="+input+",key="+key);
        }
        return data;
    }
	
	public static void main(String[] args) {
		String key="mtheforcebewithu";
		System.out.println(encrypt("你好，欢迎来到开源中国在线工具，这是一个AES加密测试1111（*&@#&……@￥%@￥##阿达#123123", "1234567812345678"));
		String str="Dota02pz4f4oAYDQeywlRAV2YaePF9iVMvwMbFGOewELUkCsSruTanBJxT2jJU8SOt7D6wSzKro75Cb4HkoSSoN5M4S8UwvJkcf9bn+99CGGfUe00njpxrlae0g18w3jWIEW17xZZ20JyovYO+x+P55f7kiJqxb0vVoLdMgJ/CPcm71bEZZqG/uj8oxSwjFc45FKjXpsHLSh2cp3GOOf18rxbPA==";
		String str2="bPMrP+H1BCE9TFq//2pLo71+fS7kMPp29dFdYZSbEjXzG8FZh21r9jSghBWIWhi6Su4g8lN9m2EuEQyM+w2CrjYZmQtlSViKYz2GUEZuC9mQ6R9T4GEFhTSsGn0dVGdwaz63/Bgjkz4E5fbH9UKLcwmP551agK3XMcKky9HnfuI=";
		String str3="bPMrP+H1BCE9TFq/2pLo71+fS7kMPp29dFdYZSbEjXzG8FZh21r9jSghBWIWhi6Su4g8lN9m2EuEQyM+w2CrjYZmQtlSViKYz2GUEZuC9mQ6R9T4GEFhTSsGn0dVGdwaz63/Bgjkz4E5fbH9UKLcwmP551agK3XMcKky9HnfuI=";
;		String data=AesUtil.decrypt(str2, key);
		System.out.println("解密:"+data);
	}
}

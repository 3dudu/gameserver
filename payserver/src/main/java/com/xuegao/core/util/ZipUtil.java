package com.xuegao.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class ZipUtil {
	static Logger logger=Logger.getLogger(ZipUtil.class);
	
	/**
	 * zip压缩字符串
	 * @param str
	 * @return
	 * @throws ZipException
	 * @throws IOException
	 */
	public static byte[] zip(String str){
		try {
			ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
			ZipEntry entry = new ZipEntry("data");
			ZipOutputStream zipOutputStream=new ZipOutputStream(byteArrayOutputStream);
			zipOutputStream.putNextEntry(entry);
			zipOutputStream.write(str.getBytes("UTF-8"));
			zipOutputStream.close();
			byte[] rs=byteArrayOutputStream.toByteArray();
			return rs;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}
	
	/**
	 * zip解压出第一个文件
	 * @param bs
	 * @return
	 */
	public static byte[] unzip(byte[] bs){
		byte[] rs=null;
		try {
			ZipInputStream zipInputStream=new ZipInputStream(new ByteArrayInputStream(bs));
			ZipEntry zipEntry = null;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
				int len = 0;
				byte[] buff=new byte[1024];
				while ((len = zipInputStream.read(buff,0,buff.length)) != -1){
					byteArrayOutputStream.write(buff,0,len);
				}
				byteArrayOutputStream.close();
				rs=byteArrayOutputStream.toByteArray();
				//取zip中第一个文件
				break;
			}
			zipInputStream.close();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return rs;
	}
	
	public static byte[] unGZip(byte[] data) {  
    	byte[] b = null;  
    	try {  
    	ByteArrayInputStream bis = new ByteArrayInputStream(data);  
    	ZipInputStream gzip = new ZipInputStream(bis);  
    	byte[] buf = new byte[1024];  
    	int num = -1;  
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    	while ((num = gzip.read(buf, 0, buf.length)) != -1) {  
    	baos.write(buf, 0, num);  
    	}  
    	b = baos.toByteArray();  
    	baos.flush();  
    	baos.close();  
    	gzip.close();  
    	bis.close();  
    	} catch (Exception ex) {  
    	ex.printStackTrace();  
    	}  
    	return b;  
    }  
}

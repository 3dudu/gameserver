/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icegame.gm.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;


/**
 *
 * @author timsa
 */
public class Misc {

	public static String getStackTraceAsString(Exception exception) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.print(" [ ");
		pw.print(exception.getClass().getName());
		pw.print(" ] ");
		pw.print(exception.getMessage());
		exception.printStackTrace(pw);
		return sw.toString();
	}
	
	public static String implode( String[] ary, String delim )
	{
		StringBuilder out = new StringBuilder();
		for ( int i = 0; i < ary.length; i++ )
		{
			if ( i != 0 )
			{
				out.append(delim );
			}
			out.append( ary[i] );
		}
		return out.toString();
	}
	
	public static String implode( Long[] ary, String delim )
	{
		StringBuilder out = new StringBuilder();
		for ( int i = 0; i < ary.length; i++ )
		{
			if ( i != 0 )
			{
				out.append(delim );
			}
			out.append( ary[i] );
		}
		return out.toString();
	}
	
	//--------------------------------
	// calculate md5 hash of a string
	//--------------------------------
	public static String md5(String s)
	{
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
				h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	public static String md5(byte[] bs)
	{
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(bs);
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
				h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static String URLEncode(String s)
	{
		if (s == null || s.isEmpty())
			return "";
		
		try {
			return java.net.URLEncoder.encode(s, "UTF-8");
		}catch (java.io.UnsupportedEncodingException e) {
			return "";
		}
	}
	
	public static byte[] compress(String s, int deflater) throws Exception
	{
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final DeflaterOutputStream dos = new DeflaterOutputStream(os, new Deflater(deflater));
		final PrintStream ps = new PrintStream(dos, true, "UTF-8");
		ps.print(s);
		dos.close();
		return os.toByteArray();
	}
	
	public static byte[] uncompress(byte[] b) throws Exception
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		InflaterOutputStream ios = new InflaterOutputStream(os);
		ios.write(b, 0, b.length);
		ios.close();
		return os.toByteArray();
	}

	/**
	 * 获取一个文件的md5值(可处理大文件)
	 * @return md5 value
	 */
	public static String getMD5(File file) {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[8192];
			int length;
			while ((length = fileInputStream.read(buffer)) != -1) {
				md.update(buffer, 0, length);
			}
			return new String(Hex.encodeHex(md.digest()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fileInputStream != null){
					fileInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getFileMD5(File file) {
		FileInputStream fileInputStream = null;
		String md5 = "";
		try {
			fileInputStream = new FileInputStream(file);
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = fileInputStream.read(buffer, 0, 1024)) != -1) {
				md.update(buffer, 0, length);
			}
			BigInteger bigInt = new BigInteger(1, md.digest());
			md5 = bigInt.toString(16);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return  md5;
	}

}

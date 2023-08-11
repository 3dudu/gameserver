/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xuegao.core.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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
		
}

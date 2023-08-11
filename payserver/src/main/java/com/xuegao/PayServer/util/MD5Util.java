package com.xuegao.PayServer.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

   private static String byteArrayToHexString(byte b[]) {
      StringBuffer resultSb = new StringBuffer();
      for (int i = 0; i < b.length; i++)
         resultSb.append(byteToHexString(b[i]));

      return resultSb.toString();
   }

   private static String byteToHexString(byte b) {
      int n = b;
      if (n < 0)
         n += 256;
      int d1 = n / 16;
      int d2 = n % 16;
      return hexDigits[d1] + hexDigits[d2];
   }

   public static String MD5Encode(String origin, String charsetname) {
      String resultString = null;
      try {
         resultString = new String(origin);
         MessageDigest md = MessageDigest.getInstance("MD5");
         if (charsetname == null || "".equals(charsetname))
            resultString = byteArrayToHexString(md.digest(resultString
                  .getBytes()));
         else
            resultString = byteArrayToHexString(md.digest(resultString
                  .getBytes(charsetname)));
      } catch (Exception exception) {
      }
      return resultString;
   }
   public static String md5(String text) {
		MessageDigest msgDigest = null;
		try {
			msgDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(
					"System doesn't support MD5 algorithm.");
		}

		msgDigest.update(text.getBytes());

		byte[] bytes = msgDigest.digest();

		byte tb;
		char low;
		char high;
		char tmpChar;

		String md5Str = new String();

		for (int i = 0; i < bytes.length; i++) {
			tb = bytes[i];

			tmpChar = (char) ((tb >>> 4) & 0x000f);

			if (tmpChar >= 10) {
				high = (char) (('a' + tmpChar) - 10);
			} else {
				high = (char) ('0' + tmpChar);
			}

			md5Str += high;
			tmpChar = (char) (tb & 0x000f);

			if (tmpChar >= 10) {
				low = (char) (('a' + tmpChar) - 10);
			} else {
				low = (char) ('0' + tmpChar);
			}

			md5Str += low;
		}
		return md5Str;
	}
   private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
         "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

}
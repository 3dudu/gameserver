package com.xuegao.PayServer.util;

import java.nio.charset.Charset;

/**
 * @author absir
 * 
 */
public class KernelCharset {

	/** UTF8 */
	public static final Charset UTF8 = Charset.forName("UTF-8");

	/** DEFAULT */
	public static final Charset DEFAULT = UTF8;

	/** DEFAULT_ENCODER */
	public static final Object DEFAULT_ENCODER = DEFAULT.newEncoder();

	/** DEFAULT_DECODER */
	public static final Object DEFAULT_DECODER = DEFAULT.newDecoder();
}

package com.xuegao.PayServer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.xuegao.PayServer.util.CallbackBreak.BreakException;

/**
 * @author absir
 * 
 */
public class HelperIO extends IOUtils {

	/**
	 * @param input
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(InputStream input, CallbackBreak<String> callback) throws IOException {
		doWithReadLine(input, Charset.defaultCharset(), callback);
	}

	/**
	 * @param input
	 * @param encoding
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(InputStream input, String encoding, CallbackBreak<String> callback) throws IOException {
		doWithReadLine(input, Charset.forName(encoding), callback);
	}

	/**
	 * @param input
	 * @param encoding
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(InputStream input, Charset encoding, CallbackBreak<String> callback) throws IOException {
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(input, encoding);
			doWithReadLine(reader, callback);

		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * @param reader
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(Reader reader, CallbackBreak<String> callback) throws IOException {
		if (reader instanceof BufferedReader) {
			doWithReadLine((BufferedReader) reader, callback);

		} else {
			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(reader);
				doWithReadLine(bufferedReader, callback);

			} finally {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
		}
	}

	/**
	 * @param reader
	 * @param callback
	 * @throws IOException
	 */
	public static void doWithReadLine(BufferedReader reader, CallbackBreak<String> callback) throws IOException {
		while (true) {
			try {
				String line = reader.readLine();
				if (line == null) {
					break;
				}

				callback.doWith(line);

			} catch (BreakException e) {
				break;
			}
		}
	}
}

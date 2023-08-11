package com.xuegao.PayServer.util;

import java.net.Socket;

/**
 * @author absir
 *
 */
public class SocketAdapter {


	/**
	 * @author absir
	 *
	 */
	public static interface CallbackAdapte {

		/**
		 * @param adapter
		 * @param buffer
		 */
		public void doWith(SocketAdapter adapter, byte[] buffer);
	}

	/** bit */
	public static final byte[] bit = "b".getBytes();

	/** ok */
	public static final byte[] ok = "ok".getBytes();

	/** failed */
	public static final byte[] failed = "failed".getBytes();

	/** socket */
	private Socket socket;

	/** beats */
	private byte[] beats = bit;

	/** beatLifeTime */
	private long beatLifeTime;

	/** callbackConnect */
	private CallbackAdapte callbackConnect;

	/** callbackDisconnect */
	private CallbackAdapte callbackDisconnect;

	/** acceptCallback */
	private CallbackAdapte acceptCallback;

	/** registered */
	private boolean registered;

	/** registerCallback */
	private CallbackAdapte registerCallback;


	/**
	 * @return
	 */
	public static int getMinCallbackIndex() {
		return 256;
	}

	/**
	 * @return
	 */
	public int getMaxBufferLength() {
		return 20480000;
	}


	

	/** DEBUG_FLAG */
	public static final byte DEBUG_FLAG = (byte) (0x01 << 7);

	/** CALLBACK_FLAG */
	public static final byte CALLBACK_FLAG = 0x01 << 6;

	/** POST_FLAG */
	public static final byte POST_FLAG = 0x01 << 5;

	/** ERROR_FLAG */
	public static final byte ERROR_FLAG = 0x01 << 4;

	/** RESPONSE_FLAG */
	public static final byte RESPONSE_FLAG = 0x01 << 3;
	/** callbackIndex */
	private  static int callbackIndex;
	/**
	 * @return
	 */
	public synchronized static int generateCallbackIndex() {
		int minCallbackIndex = getMinCallbackIndex();
		while (true) {
			if (++callbackIndex < minCallbackIndex || callbackIndex >= Integer.MAX_VALUE) {
				callbackIndex = minCallbackIndex + 1;
				break;
			}
		}

		return callbackIndex;
	}

	/**
	 * 生成发送数据包
	 * 
	 * @param off
	 * @param dataBytes
	 * @param head
	 * @param debug
	 * @param callbackIndex
	 * @param postData
	 * @return
	 */
	public static byte[] sendDataBytes(int off, byte[] dataBytes, boolean head, boolean debug, int callbackIndex, byte[] postData) {
		byte headFlag = 0x00;
		int headLength = off + (callbackIndex == 0 ? 4 : 8);
		if (head) {
			headLength++;
		} else if (callbackIndex != 0) {
			head = true;
			headLength++;
		}

		int dataLength = dataBytes.length;
		byte[] sendDataBytes;
		if (postData == null) {
			// no post
			dataLength += headLength;
			sendDataBytes = new byte[dataLength];
			System.arraycopy(dataBytes, 0, sendDataBytes, headLength, dataLength - headLength);

		} else {
			// post head
			if (!head) {
				head = true;
				headLength++;
			}

			headFlag |= POST_FLAG;
			headLength += 4;
			int postLength = postData.length;
			dataLength += headLength + postLength;
			sendDataBytes = new byte[dataLength];
			System.arraycopy(dataBytes, 0, sendDataBytes, headLength, dataLength - headLength - postLength);
			System.arraycopy(postData, 0, sendDataBytes, dataLength - postLength, postLength);
			sendDataBytes[headLength - 4] = (byte) postLength;
			sendDataBytes[headLength - 3] = (byte) (postLength >> 8);
			sendDataBytes[headLength - 2] = (byte) (postLength >> 16);
			sendDataBytes[headLength - 1] = (byte) (postLength >> 24);
		}

		// headFlag
		if (head) {
			if (debug) {
				headFlag |= DEBUG_FLAG;
			}

			if (callbackIndex != 0) {
				headFlag |= CALLBACK_FLAG;
				sendDataBytes[off + 5] = (byte) callbackIndex;
				sendDataBytes[off + 6] = (byte) (callbackIndex >> 8);
				sendDataBytes[off + 7] = (byte) (callbackIndex >> 16);
				sendDataBytes[off + 8] = (byte) (callbackIndex >> 24);
			}

			sendDataBytes[off + 4] = headFlag;
		}

		// send data bytes length
		dataLength -= 4;
		sendDataBytes[0] = (byte) dataLength;
		sendDataBytes[1] = (byte) (dataLength >> 8);
		sendDataBytes[2] = (byte) (dataLength >> 16);
		sendDataBytes[3] = (byte) (dataLength >> 24);
		return sendDataBytes;
	}

	
}

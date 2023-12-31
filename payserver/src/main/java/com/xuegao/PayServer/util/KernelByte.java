package com.xuegao.PayServer.util;

/**
 * @author absir
 * 
 */
public class KernelByte {

	/**
	 * @param destination
	 * @param destionationIndex
	 * @return
	 */
	public static int getLength(byte[] destination, int destionationIndex) {
		int length = destination[destionationIndex] & 0xFF;
		length += (destination[++destionationIndex] & 0xFF) << 8;
		length += (destination[++destionationIndex] & 0xFF) << 16;
		length += (destination[++destionationIndex] & 0xFF) << 24;
		return length;
	}

	/**
	 * @param destination
	 * @param destionationIndex
	 * @param length
	 */
	public static void setLength(byte[] destination, int destionationIndex, int length) {
		destination[destionationIndex] = (byte) (length);
		destination[++destionationIndex] = (byte) (length >> 8);
		destination[++destionationIndex] = (byte) (length >> 16);
		destination[++destionationIndex] = (byte) (length >> 24);
	}

	/**
	 * @param length
	 * @return
	 */
	public static byte[] getLengthBytes(int length) {
		byte[] destination = new byte[4];
		setLength(destination, 0, length);
		return destination;
	}

	/**
	 * @param source
	 * @param destination
	 * @param sourceIndex
	 * @param destionationIndex
	 * @param length
	 */
	public static void copy(byte[] source, byte[] destination, int sourceIndex, int destionationIndex, int length) {
		for (; length > 0; length--) {
			destination[destionationIndex++] = source[sourceIndex++];
		}
	}
}

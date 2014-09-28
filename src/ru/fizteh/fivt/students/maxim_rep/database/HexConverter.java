package ru.fizteh.fivt.students.maxim_rep.database;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class HexConverter {
	public static String hexToString(String HexCode) {
		ByteBuffer buff = ByteBuffer.allocate(HexCode.length() / 2);
		for (int i = 0; i < HexCode.length(); i += 2) {
			buff.put((byte) Integer.parseInt(HexCode.substring(i, i + 2), 16));
		}
		buff.rewind();
		Charset cs = Charset.forName("UTF-8");
		CharBuffer cb = cs.decode(buff);
		return cb.toString();

	}

	public static String stringToHex(String text) {
		char arr[] = text.toCharArray();
		String HexString = "";
		for (int i = 0; i < text.length(); i++) {
			HexString = HexString + Integer.toHexString(arr[i]);
		}
		return HexString.toUpperCase();
	}

	public static int getHexSize(String text) {
		int res = 0;
		for (int i = 0; i < text.length(); i++) {
			res++;
		}
		return res;
	}
	
	/*
	 * public static byte[] intToByteArray(int value) { byte[] b = new byte[4];
	 * for (int i = 0; i < 4; i++) { int offset = (b.length - 1 - i) * 8; b[i] =
	 * (byte) ((value >>> offset) & 0xFF); } return b; }
	 */

}

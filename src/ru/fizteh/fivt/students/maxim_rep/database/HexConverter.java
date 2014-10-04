package ru.fizteh.fivt.students.maxim_rep.database;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class HexConverter {
    public static String hexToString(String hexCode) {
        hexCode = hexCode.toUpperCase();
        ByteBuffer buff = ByteBuffer.allocate(hexCode.length() / 2);
        for (int i = 0; i < hexCode.length(); i += 2) {
            buff.put((byte) Integer.parseInt(hexCode.substring(i, i + 2), 16));
        }
        buff.rewind();
        // Charset cs = Charset.defaultCharset();
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = cs.decode(buff);
        return cb.toString();

    }

    public static String stringToHex(String text) {
        char[] arr = text.toCharArray();
        String hexString = "";
        for (int i = 0; i < text.length(); i++) {
            if (arr[i] == '\0') {
                hexString = hexString + "00";
            } else {
                hexString = hexString + Integer.toHexString(arr[i]);
            }
        }
        return hexString.toUpperCase();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}

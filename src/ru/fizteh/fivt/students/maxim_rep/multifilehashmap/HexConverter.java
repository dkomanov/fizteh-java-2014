package ru.fizteh.fivt.students.maxim_rep.multifilehashmap;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class HexConverter {
    public static String hexToString(String hexCode) {
        hexCode = hexCode.toUpperCase();
        if (hexCode.length() % 2 != 0) {
            System.out.println("[Warning] Database: Filesystem damaged!");
            return "";
        }
        ByteBuffer buff = ByteBuffer.allocate(hexCode.length() / 2);
        for (int i = 0; i < hexCode.length(); i += 2) {
            buff.put((byte) Integer.parseInt(hexCode.substring(i, i + 2), 16));
        }
        buff.rewind();
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = cs.decode(buff);
        return cb.toString();

    }

    public static String stringToHex(String text) {
        char[] arr = text.toCharArray();
        StringBuilder hexString = new StringBuilder("");
        for (int i = 0; i < text.length(); i++) {
            if (arr[i] == '\0') {
                hexString.append("00");
            } else {
                hexString.append(Integer.toHexString(arr[i]));
            }
        }
        return hexString.toString().toUpperCase();
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

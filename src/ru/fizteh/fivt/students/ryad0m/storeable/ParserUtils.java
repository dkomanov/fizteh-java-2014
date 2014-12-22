package ru.fizteh.fivt.students.ryad0m.storeable;

public class ParserUtils {
    public static String getFirst(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            if (Character.isSpaceChar(s.charAt(i)) && stringBuilder.length() > 0) {
                break;
            }
            if (!Character.isSpaceChar(s.charAt(i))) {
                stringBuilder.append(s.charAt(i));
            }
        }
        return stringBuilder.toString();
    }

    public static String getWithoutFirst(String s) {
        int i;
        boolean flag = false;
        for (i = 0; i < s.length(); ++i) {
            if (Character.isSpaceChar(s.charAt(i)) && flag) {
                break;
            }
            if (!Character.isSpaceChar(s.charAt(i))) {
                flag = true;
            }
        }
        return s.substring(i, s.length());
    }
}

package ru.fizteh.fivt.students.maxim_rep.database;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class IoLib {
    public static boolean writeToDB(String filePath, String text,
            boolean isAppend) {
        File f = null;
        try {
            f = new File(filePath);
        } catch (Exception e) {
            System.out.println("Database Error: Couldn't connect to DB file");
            return false;
        }
        if (!f.exists() || !f.isFile()) {
            System.out.println("Database Error: Couldn't connect to DB file");
            return false;
        }
        OutputStream writer;
        try {
            writer = new BufferedOutputStream(new FileOutputStream(filePath,
                    isAppend));
            writer.write(HexConverter.hexStringToByteArray(text));
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage() + "\"");
            return false;
        }

        try {
            writer.close();
        } catch (IOException e) {
            System.out.println("[Warning] Database: Couldn't close DB file");
        }

        return true;
    }

    public static String getFileText(String filePath) {
        File f = new File(filePath);
        if (!f.exists() || !f.isFile()) {
            System.out
                    .println("Database Error: " + filePath + ": No such file");
            return null;
        }
        FileInputStream in;
        try {
            in = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            System.out.println("Database Error: " + e.getMessage() + "\"");
            return null;
        }

        int len;
        String data = "";
        byte[] bytes = new byte[16];
        try {
            do {
                len = in.read(bytes);
                for (int j = 0; j < len; j++) {
                    data = data + String.format("%02X", bytes[j]);
                }
            } while (len != -1);
        } catch (IOException e) {
            System.out.println("Database Error: " + e.getMessage() + "\"");
            return null;
        }

        try {
            in.close();
        } catch (IOException e) {
            System.out.println("[Warning] Database: Couldn't close DB file");
        }

        return data;
    }

    public static String getData(String filePath) {
        String[] result = getKeyList(filePath, true, true);

        try {
            return result[result.length - 1];
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * <p>
     * Получить массив с ключами, их смещением и положением в ДБ.
     * </p>
     * <p>
     * Формат: xxxxxxxxXXXXXXXX..., x- смещение int, в 16-й системе; X - сам
     * ключ (HEX)
     * </p>
     * 
     * @param unFormatted
     *            - если true - возвращает в виде
     *            "Ключ 1, \0, смещение значения 1"
     * @return null если ошибка или ключи\данные отстутсвуют;
     */
    public static String[] getKeyList(String filePath, boolean unFormatted,
            boolean getData) {
        ArrayList<String> keysTable = new ArrayList<String>();
        String data = IoLib.getFileText(filePath);

        if (data == null) {
            return null;
        }

        char[] hexCharTable = data.toCharArray();
        ArrayList<String> hexTable = new ArrayList<String>();
        for (int i = 0; i < data.length() - 1; i = i + 2) {
            hexTable.add(i / 2, "" + hexCharTable[i] + hexCharTable[i + 1]);
        }

        StringBuilder key = new StringBuilder("");
        for (int i = 0; i < hexTable.size(); i++) {
            String currentChar = hexTable.get(i);
            if (unFormatted) {
                if (!currentChar.equals("00")) {
                    key = key.append(currentChar);
                } else {
                    key = key.append(currentChar);
                    for (int j = 1; j <= 4; j++) {
                        key = key.append(hexTable.get(i + j));

                    }
                    i = i + 4;
                    keysTable.add(key.toString());
                    key = new StringBuilder("");
                }
            } else {
                if (!currentChar.equals("00")) {
                    key = key.append(currentChar);
                } else {
                    for (int j = 4; j >= 1; j--) {
                        key = key.insert(0, hexTable.get(i + j));
                    }
                    i = i + 4;
                    keysTable.add(key.toString());
                    key = new StringBuilder("");
                }
            }

        }

        if (!key.equals("")) {
            keysTable.add(key.toString());
        }

        if (!getData) {
            try {
                keysTable.remove(keysTable.size() - 1);
            } catch (Exception e) {
                return null;
            }
        }
        String[] result = new String[keysTable.size()];
        result = keysTable.toArray(result);

        return result;
    }
}

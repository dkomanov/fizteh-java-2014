package ru.fizteh.fivt.students.maxim_rep.multifilehashmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class IoLib {

    public static boolean createTable(String tableName) {
        File f = null;
        f = new File(DbMain.filePath + System.getProperty("file.separator")
                + tableName);
        if (!f.exists() && !f.isFile()) {
            try {
                f.mkdir();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static String[] getDataFilePath(String keyName) {
        byte bytes = keyName.getBytes()[0];
        int ndirectory = Math.abs(bytes % 16);
        int nfile = Math.abs(bytes / 16 % 16);
        String[] result = new String[3];
        result[0] = DbMain.filePath + System.getProperty("file.separator")
                + DbMain.currentTable + System.getProperty("file.separator")
                + ndirectory + ".dir" + System.getProperty("file.separator")
                + nfile + ".dat";
        result[1] = ndirectory + ".dir";
        result[2] = nfile + ".dat";
        return result;
    }

    public static void recursiveRm(File f) {
        String[] files = f.list();
        for (int i = 0; i < files.length; ++i) {
            File currentFile = new File(f.getPath()
                    + System.getProperty("file.separator") + files[i]);
            if (currentFile.isDirectory()) {
                recursiveRm(currentFile);
            }
            currentFile.delete();
        }

        f.delete();
    }

    public static boolean dropTable(String tableName) {
        if (!IoLib.tableExists(tableName)) {
            return false;
        }

        File f = null;
        f = new File(DbMain.filePath + System.getProperty("file.separator")
                + DbMain.currentTable);
        if (f.exists() && f.isDirectory()) {
            try {
                IoLib.recursiveRm(f);
            } catch (Exception e) {
                return false;
            }

        }
        return true;
    }

    public static boolean checkFolderAfterRemove(String keyName) {
        String[] filePath = IoLib.getDataFilePath(keyName);
        if (!IoLib.tableExists(DbMain.currentTable)) {
            System.out.println(DbMain.currentTable + " not exists");
            return false;
        }

        File f = null;
        f = new File(DbMain.filePath + System.getProperty("file.separator")
                + DbMain.currentTable + System.getProperty("file.separator")
                + filePath[1]);
        if (f.exists() && f.isDirectory()) {
            if (f.list().length == 0) {
                try {
                    f.delete();
                } catch (Exception e) {
                    System.out
                            .println("Database Error: Couldn't delete DB files!");
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean createDBStructure(String keyName) {
        String[] filePath = IoLib.getDataFilePath(keyName);
        if (!IoLib.tableExists(DbMain.currentTable)) {
            System.out.println(DbMain.currentTable + " not exists");
            return false;
        }

        File f = null;
        f = new File(DbMain.filePath + System.getProperty("file.separator")
                + DbMain.currentTable + System.getProperty("file.separator")
                + filePath[1]);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                System.out
                        .println("Database Error: Couldn't create DB folders!");
                return false;
            }
        }
        f = new File(DbMain.filePath + System.getProperty("file.separator")
                + DbMain.currentTable + System.getProperty("file.separator")
                + filePath[1] + System.getProperty("file.separator")
                + filePath[2]);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (Exception e) {
                System.out.println("Database Error: Couldn't create DB files!");
                return false;
            }
        }
        return true;
    }

    public static boolean tableExists(String tableName) {
        File f = null;
        try {
            f = new File(DbMain.filePath + System.getProperty("file.separator")
                    + tableName);
        } catch (Exception e) {
            return false;
        }
        if (!f.exists() || f.isFile()) {
            return false;
        }
        return true;
    }

    public static boolean databaseExists(String filePath) {
        File f = null;
        try {
            f = new File(filePath);
        } catch (Exception e) {
            System.out.println("Database Error: Couldn't connect to DB");
            return false;
        }
        if (!f.exists() || f.isFile()) {
            System.out.println("Database Error: Couldn't connect to DB");
            return false;
        }
        return true;
    }

    public static boolean writeToDB(String filePath, String text,
            boolean isAppend, String keyName) {

        if (!IoLib.tableExists(DbMain.currentTable)) {
            System.out.println(DbMain.currentTable + " not exists");
            return false;
        }
        if (!IoLib.createDBStructure(keyName)) {
            System.out.println(DbMain.currentTable + " not exists");
            return false;
        }

        File f = null;
        try {
            f = new File(filePath);
        } catch (Exception e) {
            System.out.println("Database Error: Couldn't connect to DB file");
            return false;
        }

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println("Database Error: Couldn't create DB files");
            }
        }

        if (!f.exists() || !f.isFile()) {
            System.out.println("Database Error: Couldn't connect to DB file");
            return false;
        }

        if (text == null || text.equals("")) {
            f.delete();
            if (!IoLib.checkFolderAfterRemove(keyName)) {
                return false;
            }
        } else {

            OutputStream writer;
            try {
                writer = new BufferedOutputStream(new FileOutputStream(
                        filePath, isAppend));
                writer.write(HexConverter.hexStringToByteArray(text));
            } catch (Exception e) {
                System.out.println("Database Error: " + e.getMessage() + "\"");
                return false;
            }

            try {
                writer.close();
            } catch (IOException e) {
                System.out
                        .println("[Warning] Database: Couldn't close DB file");
            }
        }
        return true;
    }

    public static String getFileText(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            return null;
        }
        if (!f.isFile()) {
            System.out.println("[Warning] Database: Filesystem damaged: "
                    + filePath);
            return null;
        }

        FileInputStream in;
        try {
            in = new FileInputStream(f);
        } catch (FileNotFoundException e) {
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

    public static String[] getTables() {
        ArrayList<String> tablesArray = new ArrayList<String>();

        if (!IoLib.databaseExists(DbMain.filePath)) {
            return null;
        }

        File f = new File(DbMain.filePath);
        for (File current : f.listFiles()) {
            if (current.isDirectory()) {
                tablesArray
                        .add(IoLib.getTableKeyList(current.getName(), false).length
                                + "\n" + current.getName());
            }
        }

        String[] result = new String[tablesArray.size()];
        result = tablesArray.toArray(result);

        return result;

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

    public static String[] getTableKeyList(String tableName, boolean unFormatted) {
        if (!IoLib.tableExists(tableName)) {
            System.out.println(tableName + " not exists");
            return null;
        }

        ArrayList<String> keysTable = new ArrayList<String>();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                String filePath = DbMain.filePath
                        + System.getProperty("file.separator") + tableName
                        + System.getProperty("file.separator") + i + ".dir";
                if (new File(filePath).isFile()) {
                    System.out
                            .println("[Warning] Database: Filesystem damaged: "
                                    + filePath);
                    return null;
                }

                String data = IoLib.getFileText(filePath
                        + System.getProperty("file.separator") + j + ".dat");
                if (data == null) {
                    continue;
                }
                char[] hexCharTable = data.toCharArray();
                ArrayList<String> hexTable = new ArrayList<String>();
                for (int i2 = 0; i2 < data.length() - 1; i2 = i2 + 2) {
                    hexTable.add(i2 / 2, "" + hexCharTable[i2]
                            + hexCharTable[i2 + 1]);
                }

                StringBuilder key = new StringBuilder("");
                for (int i2 = 0; i2 < hexTable.size(); i2++) {
                    String currentChar = hexTable.get(i2);
                    if (unFormatted) {
                        if (!currentChar.equals("00")) {
                            key = key.append(currentChar);
                        } else {
                            key = key.append(currentChar);
                            for (int j2 = 1; j2 <= 4; j2++) {
                                key = key.append(hexTable.get(i2 + j2));

                            }
                            i2 = i2 + 4;
                            keysTable.add(key.toString());
                            key = new StringBuilder("");
                        }
                    } else {
                        if (!currentChar.equals("00")) {
                            key = key.append(currentChar);
                        } else {
                            for (int j2 = 4; j2 >= 1; j2--) {
                                key = key.insert(0, hexTable.get(i2 + j2));
                            }
                            i2 = i2 + 4;
                            keysTable.add(key.toString());
                            key = new StringBuilder("");
                        }
                    }

                }
            }
        }
        String[] result = new String[keysTable.size()];
        result = keysTable.toArray(result);
        return result;
    }
}
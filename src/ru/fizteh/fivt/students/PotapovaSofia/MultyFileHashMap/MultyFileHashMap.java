package ru.fizteh.fivt.students.PoatpovaSofia.MultyFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.nio.ByteBuffer;

public class MultyFileHashMap {
    public Map<String, Integer> tableNames = null;
    public String fmPath;

    public MultyFileHashMap(String path) throws IOException {
        fmPath = path;
        if (fmPath == null) {
            System.exit(1);
        }
        File tableDir = new File(fmPath);
        if (!tableDir.exists() || !tableDir.isDirectory()) {
            System.exit(1);
        }
        tableNames = new HashMap<>();
        File[] tables = tableDir.listFiles();
        for (File table : tables) {
            if (!checkTable(table.getName())) {
                continue;
            }
            int value = 0;
            value = readDataFromFile(table.getAbsolutePath()).size();
            tableNames.put(table.getName(), value);
        }
    }

    private boolean checkTable(String name) {
        File dircheck = new File(fmPath + File.separator + name);
        if (!dircheck.exists() || !dircheck.isDirectory()) {
            return false;
        }
        for (int i = 0; i < 16; i++) {
            File subdircheck = new File(dircheck.getAbsolutePath() + File.separator + Integer.toString(i) + ".dir");
            if (!subdircheck.exists() || !subdircheck.isDirectory()) {
                return false;
            }
        }
        return true;
    }
/*
    public static void main(String[] args) {
        MultyFileHashMap multiFileHashMap = new MultyFileHashMap();
        try {
            MultyLauncher launcher = new MultyLauncher(multiFileHashMap.tableNames, multiFileHashMap.fileMapPath);
            if (!launcher.launch(args)) {
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.print(e.getMessage());
            System.exit(1);
        }
    }
*/
    public static Map<String, String> readDataFromFile(String currentPath) throws IOException {
        Map<String, String> dataBase = new HashMap<>();
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(currentPath));
        while (true) {
            try {
                String key = readWord(dataInputStream);
                String value = readWord(dataInputStream);
                dataBase.put(key, value);
            } catch (EOFException e) {
                break;
            }
        }
        return dataBase;
    }

    public static void writeDataToFile(Map<String, String> dataBase, String currentPath) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(currentPath));
        for (Map.Entry<String, String> db : dataBase.entrySet()) {
            writeWord(dataOutputStream, db.getKey());
            writeWord(dataOutputStream, db.getValue());
        }
    }

    private static void writeWord(DataOutputStream dataOutputStream, String word) throws IOException {
        byte[] byteWord = word.getBytes("UTF-8");
        dataOutputStream.writeInt(byteWord.length);
        dataOutputStream.write(byteWord);
    }

    private static String readWord(DataInputStream dataInputStream) throws IOException {
        byte[] bytes = new byte[4];
        bytes[0] = dataInputStream.readByte();
        try {
            dataInputStream.readFully(bytes, 1, 3);
        } catch (EOFException e) {
            throw new IOException("Something goes wrong");
        }
        int length = ByteBuffer.wrap(bytes).getInt();
        if (length < 0) {
            throw new IOException("Something goes wrong");
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < length; i++) {
            try {
                byteArrayOutputStream.write(dataInputStream.readByte());
            } catch (EOFException e) {
                throw new IOException("Something goes wrong");
            }
        }
        return new String(byteArrayOutputStream.toByteArray(), "UTF-8");
    }
}


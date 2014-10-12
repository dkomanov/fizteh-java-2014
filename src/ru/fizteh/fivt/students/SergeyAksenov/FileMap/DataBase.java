package ru.fizteh.fivt.students.SergeyAksenov.FileMap;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class DataBase {


    public DataBase() throws FileMapExitException {
        dataBase = new HashMap<>();
        try {
            dataBasePath = Paths.get(System.getProperty("db.file"));
            try (RandomAccessFile dataBaseFile = new RandomAccessFile(
                    dataBasePath.toString(), "r")) {
                if (dataBaseFile.length() > 0) {
                    inStream = new DataInputStream(
                            new FileInputStream(dataBasePath.toString()));
                    read();
                }
            } catch (FileNotFoundException e) {
                dataBasePath.toFile().createNewFile();
            }
        } catch (Exception e) {
            System.err.println("Cannot create a database file");
            throw new FileMapExitException();
        }
    }
//
    private String readUtil() throws IOException {
        int length = inStream.readInt();
        byte[] word = new byte[length];
        inStream.readFully(word);
        return new String(word, "UTF-8");
    }

    private void read() throws Exception {
        boolean end = false;
        while (!end) {
            try {
                String key = readUtil();
                String value = readUtil();
                dataBase.put(key, value);
            } catch (IOException e) {
                end = true;
            }
        }
    }

    public void write(RandomAccessFile file) throws Exception {
        if (file.length() > 0) {
            outStream = new DataOutputStream(new FileOutputStream(toString()));
            for (Map.Entry<String, String> entry: dataBase.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                try {
                    writeUtil(key);
                    writeUtil(value);
                } catch (Exception e) {
                    throw new Exception("Error in writing");
                }

            }
        }
    }

    private void writeUtil(String str) throws IOException {
        byte[] byteStr = str.getBytes("UTF-8");
        outStream.writeInt(str.length());
        outStream.write(byteStr);
        outStream.flush();
    }

    public static HashMap<String, String> getDataBase() {
        return dataBase;
    }

    public void close() {
        try  (RandomAccessFile file = new RandomAccessFile(dataBasePath.toString(), "rw")) {
            write(file);
            inStream.close();
            outStream.close();
        }
        catch (Exception e) {
            System.err.println("Cannot write database to file");
        }
    }

    private static HashMap<String, String> dataBase;
    private static Path dataBasePath;
    public static DataInputStream inStream;
    public static DataOutputStream outStream;
}

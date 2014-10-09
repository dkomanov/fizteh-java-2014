package ru.fizteh.fivt.students.Bulat_Galiev.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Data {
    private static Path filePathdb;
    private static HashMap<String, String> fileMap;
    private static RandomAccessFile inputStream;
    private static RandomAccessFile outputStream;

    public Data() throws Exception {
        fileMap = new HashMap<>();
        try {
            filePathdb = Paths.get(System.getProperty("db.file"));
            try (RandomAccessFile filedb = new RandomAccessFile(
                    filePathdb.toString(), "r")) {
                if (filedb.length() > 0) {
                    Data.getData(filedb);
                }
            } catch (Exception e) {
                filePathdb.toFile().createNewFile();
            }
        } catch (Exception e) {
            System.err.println("Cannot create database file");
            System.exit(-1);
            throw new Exception();
        }
    }

    protected static void getData(final RandomAccessFile filedb) throws IOException {
        inputStream = new RandomAccessFile(filePathdb.toString(), "r");
        long bytesLeft = inputStream.length();
        while (bytesLeft > 0) {
            int keyLength = inputStream.readInt();
            int valueLength = inputStream.readInt();

            bytesLeft -= 8;

            byte[] bytekey = new byte[keyLength];
            int read = inputStream.read(bytekey);
            if (read < 0 || read != keyLength) {
                throw new IllegalArgumentException("bad file format.");
            }
            String key = new String(bytekey, "UTF-8");

            byte[] bytevalue = new byte[valueLength];
            read = inputStream.read(bytevalue);
            if (read < 0 || read != valueLength) {
                throw new IllegalArgumentException("bad file format.");
            }
            String value = new String(bytevalue, "UTF-8");
            fileMap.put(key, value);

            bytesLeft -= keyLength + valueLength;
        }

    }

    protected static void putData(final RandomAccessFile filedb) throws IOException {
        outputStream = new RandomAccessFile(filePathdb.toString(), "rw");
        filedb.setLength(0);
        Set<Map.Entry<String, String>> rows = fileMap.entrySet();
        for (Map.Entry<String, String> row : rows) {
            outputStream.writeInt(row.getKey().getBytes("UTF-8").length);
            outputStream.writeInt(row.getValue().getBytes("UTF-8").length);
            outputStream.write(row.getKey().getBytes("UTF-8"));
            outputStream.write(row.getValue().getBytes("UTF-8"));
        }
    }

    public final void close() {
        try (RandomAccessFile filedb = new RandomAccessFile(
                    filePathdb.toString(), "rw")) {
            Data.putData(filedb);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public final HashMap<String, String> getDataBase() {
        return fileMap;
    }
}

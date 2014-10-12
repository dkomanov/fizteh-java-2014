package ru.fizteh.fivt.students.Bulat_Galiev.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DatabaseSerializer {
    private static Path filePathdb;
    private static Map<String, String> fileMap;
    private static RandomAccessFile inputStream;
    private static RandomAccessFile outputStream;

    public DatabaseSerializer() throws Exception {
        fileMap = new HashMap<>();
        try {
            filePathdb = Paths.get(System.getProperty("db.file"));
            try (RandomAccessFile filedb = new RandomAccessFile(
                    filePathdb.toString(), "r")) {
                if (filedb.length() > 0) {
                    DatabaseSerializer.getData(filedb);
                }
            } catch (Exception e) {
                filePathdb.toFile().createNewFile();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static String getSomeData(final int dataLength) throws IOException {
        byte[] byteData = new byte[dataLength];
        int read = inputStream.read(byteData);
        if (read < 0 || read != dataLength) {
            throw new IllegalArgumentException("Bad file format.");
        }
        String data = new String(byteData, "UTF-8");
        return data;
    }

    protected static void getData(final RandomAccessFile filedb)
            throws IOException {
        inputStream = new RandomAccessFile(filePathdb.toString(), "r");
        long bytesLeft = inputStream.length();
        while (bytesLeft > 0) {
            int keyLength = inputStream.readInt();
            int valueLength = inputStream.readInt();

            bytesLeft -= 8;

            String key = getSomeData(keyLength);
            String value = getSomeData(valueLength);
            fileMap.put(key, value);

            bytesLeft -= keyLength + valueLength;
        }

    }

    protected static void putData(final RandomAccessFile filedb)
            throws IOException {
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
            DatabaseSerializer.putData(filedb);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public final Map<String, String> getDataBase() {
        return fileMap;
    }
}

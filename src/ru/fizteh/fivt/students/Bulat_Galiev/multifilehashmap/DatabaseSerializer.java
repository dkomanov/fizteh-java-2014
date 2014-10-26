package ru.fizteh.fivt.students.Bulat_Galiev.multifilehashmap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DatabaseSerializer {
    private final int BYTESNUMBER = 8;
    private Path filePathdb;
    private Map<String, String> fileMap;
    private RandomAccessFile inputStream;
    private RandomAccessFile outputStream;
    private int nrecords;

    public DatabaseSerializer(final Path databasePath, final int dirName, final int fileName)
            throws IOException {
        fileMap = new HashMap<>();
        String dirString = Integer.toString(dirName) + ".dir";
        String fileString = Integer.toString(fileName) + ".dat";
        filePathdb = databasePath.resolve(dirString);
        filePathdb = filePathdb.resolve(fileString);
        if (!Files.exists(filePathdb)) {
            filePathdb.getParent().toFile().mkdir();
            filePathdb.toFile().createNewFile();
        }
        try (RandomAccessFile filedb = new RandomAccessFile(
                filePathdb.toString(), "r")) {
            if (filedb.length() > 0) {
                this.getData(filedb);
            }
        } catch (FileNotFoundException e) {
            filePathdb.toFile().createNewFile();
        }
    }

    public final String readUTF8String(final int dataLength) throws IOException {
        byte[] byteData = new byte[dataLength];
        int read = inputStream.read(byteData);
        if (read < 0 || read != dataLength) {
            throw new IllegalArgumentException("Bad file format.");
        }
        String data = new String(byteData, "UTF-8");
        return data;
    }

    protected final void getData(final RandomAccessFile filedb) throws IOException {
        inputStream = new RandomAccessFile(filePathdb.toString(), "r");
        long bytesLeft = inputStream.length();
        while (bytesLeft > 0) {
            int keyLength = inputStream.readInt();
            int valueLength = inputStream.readInt();

            bytesLeft -= BYTESNUMBER;

            String key = readUTF8String(keyLength);
            String value = readUTF8String(valueLength);
            fileMap.put(key, value);

            bytesLeft -= keyLength + valueLength;
            nrecords++;
        }
        inputStream.close();
    }

    protected final void putData(final RandomAccessFile filedb) throws IOException {
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

    public final void disconnect() {
        if (nrecords == 0) {
            filePathdb.toFile().delete();
            filePathdb.getParent().toFile().delete();
        } else {
            try (RandomAccessFile filedb = new RandomAccessFile(
                    filePathdb.toString(), "rw")) {
                this.putData(filedb);
                outputStream.close();
            } catch (IOException e) {
                System.err.print(e.getMessage());
                System.exit(-1);
            }
        }
    }

    public final Map<String, String> getDataBase() {
        return fileMap;
    }

    public final void put(final String key, final String value) {
        if ((!key.equals("")) && (!value.equals(""))) {
            String putValue = fileMap.put(key, value);
            if (putValue == null) {
                System.out.println("new");
                nrecords++;
            } else {
                System.out.println("overwrite");
                System.out.println(putValue);
            }
        } else {
            throw new IllegalArgumentException("Incorrect key.");
        }

    }

    public final void get(final String key) {
        if (!key.equals("")) {
            String getValue = fileMap.get(key);
            if (getValue == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(getValue);
            }
        } else {
            throw new IllegalArgumentException("Incorrect key.");
        }
    }

    public final void remove(final String arg1) {
        if (!arg1.equals("")) {
            String getValue = fileMap.remove(arg1);
            if (getValue != null) {
                System.out.println("removed");
                nrecords--;
            } else {
                System.out.println("not found");
            }
        } else {
            throw new IllegalArgumentException("Incorrect arguments.");
        }
    }

    public final Set<String> list() {
        return fileMap.keySet();
    }

    public final int getnrecords() {
        return nrecords;
    }
}

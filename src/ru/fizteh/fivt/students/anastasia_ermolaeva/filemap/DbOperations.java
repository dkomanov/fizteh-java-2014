package ru.fizteh.fivt.students.anastasia_ermolaeva.filemap;

import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class DbOperations implements AutoCloseable {
    public DbOperations() {
        //
    }
    public static void main() {

    }
    public final void create() throws ExitException {
        database = new TreeMap<>();
        try {
            dbPath = Paths.get(System.getProperty("db.file"));
            try (RandomAccessFile dbFile
                         = new RandomAccessFile(dbPath.toString(), "r")) {
                if (dbFile.length() > 0) {
                    inStream = new DataInputStream(
                            new FileInputStream(dbPath.toString()));
                    read(dbFile);
                }
            } catch (FileNotFoundException e) {
                dbPath.toFile().createNewFile();
            }
        } catch (Exception e) {
            System.err.println("Can't create a database file");
            throw new ExitException(-1);
        }
    }
    private String readUtil() throws IOException {
        int length = inStream.readInt();
        byte[] wordArray = new byte[length];
        inStream.readFully(wordArray);
        return new String(wordArray, "UTF-8");
    }
    private void read(final RandomAccessFile dbFile) throws Exception {
        boolean end = false;
        while (!end) {
            try {
                String key = readUtil();
                String value = readUtil();
                //if (getDataBase().containsKey(key)) {
                  //  throw new Exception("ERROR: Two same keys");
                //}
                database.put(key, value);
            } catch (IOException e) {
                end = true;
            }
        }
    }

    private void write(final RandomAccessFile dbFile) throws Exception {
        if (dbFile.length() > 0) {
            outStream = new DataOutputStream(
                    new FileOutputStream(dbFile.toString()));
            for (Map.Entry<String, String> entry: getDataBase().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                try {
                    writeUtil(key);
                    writeUtil(value);
                } catch (Exception e) {
                    throw new Exception("Error with writing");
                }
            }
        }
    }
    public final void close() {
        try (RandomAccessFile dbFile
                     = new RandomAccessFile(dbPath.toString(), "rw")) {
            write(dbFile);
            inStream.close();
            outStream.close();
        } catch (Exception e) {
            System.err.println("Error writing database to file");
        }
    }

    public final TreeMap<String, String> getDataBase() {
        return database;
    }

    private void writeUtil(final String word) throws IOException {
        byte[] byteWord = word.getBytes("UTF-8");
        outStream.writeInt(byteWord.length);
        outStream.write(byteWord);
        outStream.flush();
    }

    private static TreeMap<String, String> database;
    private static Path dbPath;
    private static DataInputStream inStream;
    private static DataOutputStream outStream;
}

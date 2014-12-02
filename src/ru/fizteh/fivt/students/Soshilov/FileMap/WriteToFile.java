package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 09 October 2014
 * Time: 21:44
 */
public class WriteToFile {
    /**
     * Write whole information into a table.
     * @param currentTable The table, we use in program.
     */
    public static void writeToFile(final Map<String, String> currentTable) {
        if (System.getProperty("db.file") == null) {
            System.err.println("file was not pointed");
            System.exit(1);
        }
        File f = new File(System.getProperty("db.file"));
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            System.exit(1);
        }
//        way of pair: we put everything into a set and use set to get key and value separately
        Set<String> set = currentTable.keySet();
        for (String s : set) {
            writeObject(s, output);
            String tmp = currentTable.get(s).toString();
            writeObject(tmp, output);
        }
    }

    /**
     * Write object to a file.
     * @param data The object we write.
     * @param output The file we write in.
     */
    private static void writeObject(final String data, final FileOutputStream output) {

        String key = ((Integer) data.length()).toString();
        writeBytes(key, output);
        writeBytes(data, output);
    }

    /**
     * Write bytes: first we make bytes from a string, then write in into a file.
     * @param data The object we write.
     * @param output The file we write in.
     */
    private static void writeBytes(final String data, final FileOutputStream output) {
        try {
            byte[] bytes = ByteBuffer.allocate(4).putInt(data.length()).array();
            output.write(bytes);
        } catch (NullPointerException e) {
            System.err.println("cannot write size: null pointer exception");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("cannot write size");
            System.exit(1);
        }
        try {
            output.write(data.getBytes("UTF-8"));
        } catch (IOException e2) {
            System.err.println("cannot write data");
            System.exit(1);
        }
    }
}

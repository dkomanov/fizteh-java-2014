package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 09 October 2014
 * Time: 20:47
 */
public class Initialization {
    /**
     * By this class we initialize our map - the table - by reading info from file.
     * @param currentTable The table we use.
     * @param filePath The path to a file, which is used.
     */
    public static void initialization(Map<String, String> currentTable, final String filePath) {
        if (filePath == null) {
            System.err.println("a file was not pointed");
            System.exit(1);
        }

        if (!Files.exists(Paths.get(filePath))) {
            try {
                Files.createFile(Paths.get(filePath));
            } catch (IOException exc) {
                System.err.println("cannot create file");
                System.exit(1);
            }
        }

        File f = new File(filePath);
        DataInputStream currentFile = null;

        try {
            currentFile = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            System.err.println(filePath + ": file not found");
            System.exit(1);
        }
        try {
            while (currentFile.available() > 0) {
                //size of key
                int length = bytesToInt(currentFile);
                //the key
                String key = bytesToString(currentFile, length);
                //again size of key
                length = bytesToInt(currentFile);
                //again key
                key = bytesToString(currentFile, length);
                //size of value
                length = bytesToInt(currentFile);
                //the value
                String value = bytesToString(currentFile, length);
                //again size of value
                length = bytesToInt(currentFile);
                //again value
                value = bytesToString(currentFile, length);
                if (currentTable.containsKey(key)) {
                    System.err.println("existing keys");
                    System.exit(1);
                }
                currentTable.put(key, value);
            }
        } catch (IOException e) {
            System.err.println("cannot read from file");
            System.exit(3);
        }
        try {
            currentFile.close();
        } catch (IOException e) {
            System.err.println("cannot close file");
            System.exit(1);
        }
    }

    /**
     * Read function, that gets bytes from file.
     * @param input DataInputStream.
     * @param length Size of needed memory.
     * @return Buffer of bytes, that were read.
     * @throws IOException Can be got from read function.
     */
    private static byte[] readBytes(final DataInputStream input, final int length) throws IOException {
        if (length < 0) {
            System.err.println("cannot work with file: incorrect data base");
            System.exit(1);
        }
        ArrayList<Byte> wordBuilder = new ArrayList<>();
        try {
            for (int i = 0; i < length; i++) {
                byte symbol = input.readByte();
                wordBuilder.add(symbol);
            }
        } catch (EOFException e) {
            System.err.println("unexpected end of file");
            System.exit(1);
        } catch (OutOfMemoryError e) {
            System.err.println("not enough memory");
            System.exit(1);
        }
        byte[] buffer = new byte[length];
        for (int i = 0; i < length; ++i) {
            buffer[i] = wordBuilder.get(i);
        }
        return buffer;
    }

    /**
     * Convert bytes to int.
     * @param input The file, where read from.
     * @return Int: 4 bytes read from the file.
     * @throws IOException Can be got by readBytes.
     */
    public static int bytesToInt(final DataInputStream input) throws IOException {
        ByteBuffer wrapped = ByteBuffer.wrap(readBytes(input, 4));
        return wrapped.getInt();
    }

    /**
     * Convert bytes to string.
     * @param input The file, where read from.
     * @param size The size to read.
     * @return Int: size bytes read from the file.
     * @throws IOException Can be got by readBytes.
     */
    public static String bytesToString(final DataInputStream input, final int size) throws IOException {
        if (size <= 0) {
            throw new IOException("cannot work with file: incorrect data base");
        }
        byte[] buffer = readBytes(input, size);
        return new String(buffer, "UTF-8");
    }
}

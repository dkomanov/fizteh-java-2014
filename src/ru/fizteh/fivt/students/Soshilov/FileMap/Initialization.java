package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.io.*;
import java.nio.ByteBuffer;
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
    public static void initialization(Map currentTable, final String filePath) {
        if (filePath == null) {
            System.err.println("a file was not pointed");
            System.exit(1);
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
//                size of key
                int length = bytesToInt(currentFile);
//                the key
                String key = bytesToString(currentFile, length);
//                again size of key
//                length = bytesToInt(currentFile);
//                again key
//                key = bytesToString(currentFile, length);
//                size of value
                length = bytesToInt(currentFile);
//                the value
                String value = bytesToString(currentFile, length);
//                againg size of value
//                length = bytesToInt(currentFile);
//                again value
//                value = bytesToString(currentFile, length);
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

    /*private static byte[] read(final DataInputStream input, final int length) throws IOException {
        if (length < 0) {
            System.err.println("Incorrect data base file: negative length");
            System.exit(2);
        }
        ArrayList<Byte> wordBuilder = new ArrayList<Byte>();
        try {
            for (int i = 0; i < length; i++) {
                byte symbol = input.readByte();
                wordBuilder.add(symbol);
            }
        } catch (EOFException e) {
            System.err.println("unexpected end of file");
            System.exit(2);
        } catch (OutOfMemoryError e) {
            System.err.println("Not enough memory to store database");
            System.exit(2);
        }
        byte[] buffer = new byte[length];
        for (int i = 0; i < length; ++i) {
            buffer[i] = wordBuilder.get(i);
        }
        return buffer;
    }*/

    /**
     * Convert bytes to int.
     * @param input The file, where read from.
     * @return Int: 4 bytes read from the file.
     */
    public static int bytesToInt(final DataInputStream input) {
        byte[] data = new byte[4];
        try {
            input.read(data);
        }
        catch (IOException e) {
            System.err.println("cannot read argument");
            System.exit(1);
        }
        return ByteBuffer.wrap(data).getInt();
    }

    /**
     * Convert bytes to string.
     * @param input The file, where read from.
     * @param size The size to read.
     * @return Int: size bytes read from the file.
     */
    public static String bytesToString(final DataInputStream input, int size) {
        byte[] data = new byte[size];
        String returnValue = new String();
        try {
            input.read(data);
        }
        catch (IOException e) {
            System.err.println("cannot read argument");
            System.exit(1);
        }
        try {
            returnValue = new String(data, "UTF-8");
        }
        catch (IOException e) {
            System.err.println("cannot make string of bytes");
            System.exit(1);
        }
        return returnValue;
    }
}

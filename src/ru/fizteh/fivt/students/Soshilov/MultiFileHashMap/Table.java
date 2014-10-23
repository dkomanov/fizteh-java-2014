package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 22 October 2014
 * Time: 23:08
 */
public class Table extends HashMap<String, String> {
    /**
     * Quantity of directories and files in each directory.
     */
    public static final int F_AND_DIR_COUNT = 16;
    /**
     * A path to a table - our map.
     */
    private Path tablePath;

    /**
     * Constructor which assigns a private field tablePath.
     * @param path The Path which is assigned to the private field tablePath.
     */
    Table(final Path path) {
        tablePath = path;
    }

    /**
     * Reading key and value from file and putting it into a HashMap.
     * @param filePath The path to file read from.
     * @param dir Expected dir name.
     * @param file Expected file name.
     * @throws Exception All exceptions than could be thrown by different problems with reading from file.
     */
    private void readKeyAndValue(final Path filePath, final int dir, final int file) throws Exception {
        //If File(filePath) does not exist, get away from here.
        if (Files.exists(filePath)) {
            try (DataInputStream inputStream = new DataInputStream(Files.newInputStream(filePath))) {
                if (inputStream.available() == 0) {
                    throw new Exception("file '" + filePath + "' is empty");
                }
                while (inputStream.available() > 0) {
                    //available from FilterInputStream Returns an estimate of the number of bytes that can be read.
                    int keyLength = inputStream.readInt();
                    //readInt from DataInput Reads four input bytes and returns an int value.

                    if (inputStream.available() < keyLength) {
                        throw new Exception("wrong key size");
                    }

                    byte[] keyInBytes = new byte[keyLength];
                    inputStream.read(keyInBytes, 0, keyLength);
                    //read from FilterInputStream Read from keyInBytes[0] to keyInBytes[keyLength]

                    int valueLength = inputStream.readInt();
                    if (inputStream.available() < valueLength) {
                        throw new Exception("wrong value size");
                    }
                    byte[] valueInBytes = new byte[valueLength];
                    inputStream.read(valueInBytes, 0, valueLength);

                    String keyString = new String(keyInBytes, "UTF-8");
                    String valueString = new String(valueInBytes, "UTF-8");

                    int hashValue = keyString.hashCode();
                    if (hashValue % F_AND_DIR_COUNT != dir || hashValue / F_AND_DIR_COUNT % F_AND_DIR_COUNT != file) {
                        throw new Exception("wrong key file");
                    }

                    put(keyString, valueString);
                }

            } catch (IOException e) {
                throw new Exception("cannot read from file");
            }
        }
    }

    /**
     * Reading and filling the HashMap from every file.
     * @throws Exception All exceptions than could be thrown from previous method readKeyAndValue.
     */
    public void read() throws Exception {
        clear();
        //first clear the HashMap
        for (int dir = 0; dir < F_AND_DIR_COUNT; ++dir) {
            for (int file = 0; file < F_AND_DIR_COUNT; ++file) {
                Path filePath = tablePath.resolve(dir + ".dir").resolve(file + ".dat");
                try {
                    readKeyAndValue(filePath, dir, file);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    System.exit(1);
                }
            }
        }
    }

    /**
     * Whiting key and value to a file.
     * @param filePath A path to a File to write to.
     * @param keyStr A String presentment of key.
     * @param valueStr A String presentment of value.
     * @throws Exception All exceptions than could be writing to file.
     */
    private void writeKeyAndValue(final Path filePath, final String keyStr, final String valueStr) throws Exception {
        try (DataOutputStream outputStream = new DataOutputStream(Files.newOutputStream(filePath))) {
            byte[] keyInBytes = keyStr.getBytes("UTF-8");
            byte[] valueInBytes = valueStr.getBytes("UTF-8");
            outputStream.writeInt(keyInBytes.length);
            //writeInt from DataOutput Writes an keyInBytes.length to the outputStream as four bytes, high byte first.
            outputStream.write(keyInBytes);
            //write from FilterOutputStream Writes keyInBytes.length bytes to this output stream.
            outputStream.writeInt(valueInBytes.length);
            outputStream.write(valueInBytes);
        } catch (IOException ex) {
            throw new Exception("cannot read from file");
        }
    }

    /**
     * Write the content of HashMap (by using Nested Class).
     * @throws Exception All exceptions than could be thrown from previous method writeKeyAndValue.
     */
    public void write() throws Exception {
        if (Files.exists(tablePath)) {
            throw new Exception("directory exists: an empty directory must not exist");
        } else {
            try {
                Files.createDirectory(tablePath);
            } catch (IOException ex) {
                throw new Exception("cannot create a directory");
            }
        }

        for (HashMap.Entry<String, String> entry : entrySet()) {
            //Entry from Map (only Java 8, Nested Class of Map) - A map entry (key-value pair)
            //The Map.entrySet method returns a collection-view of the map, whose elements are of this class.
            String keyInString = entry.getKey();
            String valueInString = entry.getValue();
            int hashCode = keyInString.hashCode();
            int dir = hashCode % F_AND_DIR_COUNT;
            int file = hashCode / F_AND_DIR_COUNT % F_AND_DIR_COUNT;

            Path dirPath = tablePath.resolve(dir + ".dir");
            Path filePath = dirPath.resolve(file + ".dat");

            if (!Files.exists(dirPath)) {
                try {
                    Files.createDirectory(dirPath);
                } catch (IOException ex) {
                    throw new Exception("cannot create '" + dirPath + "'");
                }
            }
            if (!Files.exists(filePath)) {
                try {
                    Files.createFile(filePath);
                } catch (IOException ex) {
                    throw new Exception("cannot create '" + filePath + "'");
                }
            }
            writeKeyAndValue(filePath, keyInString, valueInString);
        }
    }
}

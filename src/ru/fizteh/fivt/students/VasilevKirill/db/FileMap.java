package ru.fizteh.fivt.students.VasilevKirill.db;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kirill on 11.10.2014.
 */
public class FileMap implements AutoCloseable{
    private String fileMapName;
    private Map<String, String> fileMap;

    public FileMap(String fileName) throws IOException {
        fileMapName = new File(fileName).getAbsolutePath();
        DataInputStream reader;
        try {
           File dbFile = new File(fileMapName);
           if (!dbFile.exists()) {
               if (!dbFile.createNewFile()) {
                   throw new IOException("File " + fileMapName + " was not created");
               }
           }
           reader = new DataInputStream(new FileInputStream(fileMapName));
        } catch (IOException e) {
            throw new IOException(fileMapName + ": error in opening: " + e.getMessage());
        }
        fileMap = new HashMap<String, String>();
        readFromFile(fileMap, reader);
    }

    @Override
    public void close() throws IOException {
        DataOutputStream writer = new DataOutputStream(new FileOutputStream(fileMapName));
        writeToFile(fileMap, writer);
    }

    private void readFromFile(Map<String, String> fileMap, DataInputStream reader) throws IOException {
        int keyLength = 0;
        int valueLength = 0;
        byte[] key;
        byte[] value;
        try {
            while (true) {
                keyLength = reader.readInt();
                try {
                    if (keyLength > reader.available()) {
                        throw new IOException("Broken file");
                    }
                    key = new byte[keyLength];
                    reader.read(key, 0, keyLength);
                    valueLength = reader.readInt();
                    if (valueLength > reader.available()) {
                        throw new IOException("Broken file");
                    }
                    value = new byte[valueLength];
                    reader.read(value, 0, valueLength);
                    fileMap.put(new String(key, "UTF8"), new String(value, "UTF8"));
                } catch (EOFException e) {
                    throw new IOException("Broken file");
                } catch (IOException e) {
                    throw new IOException("Error in reading: " + e.getMessage());
                }
            }
        } catch (EOFException e) {
            //Сюда мы попадаем, если не можем считать ключ, т.е. файл закончился
        }
    }

    private void writeToFile(Map<String, String> fileMap, DataOutputStream writer) throws IOException {
        int keyLength = 0;
        int valueLength = 0;
        byte[] key;
        byte[] value;
        for (Map.Entry entry : fileMap.entrySet()) {
            key = entry.getKey().toString().getBytes("UTF8");
            value = entry.getValue().toString().getBytes("UTF8");
            keyLength = key.length;
            valueLength = value.length;
            try {
                writer.writeInt(keyLength);
                writer.write(key, 0, keyLength);
                writer.writeInt(valueLength);
                writer.write(value, 0, valueLength);
            } catch (IOException e) {
                throw new IOException("Error in writing: " + e.getMessage());
            }
        }
        try {
            writer.flush();
        } catch (IOException e) {
            throw new IOException("Error in flushing: " + e.getMessage());
        }
    }

    public Map<String, String> getMap() {
        return fileMap;
    }

    public void setMap(Map<String, String> map) {
        fileMap = map;
    }
}

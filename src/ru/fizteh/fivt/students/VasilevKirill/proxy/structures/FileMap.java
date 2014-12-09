package ru.fizteh.fivt.students.VasilevKirill.proxy.structures;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kirill on 11.10.2014.
 */
public class FileMap implements Closeable{
    private String fileMapName;
    private Map<String, String> fileMap;
    private Class[] typeList;

    public FileMap(String fileName, Class[] typeList) throws IOException {
        fileMapName = new File(fileName).getAbsolutePath();
        FileReader reader;
        try {
           File dbFile = new File(fileMapName);
           if (!dbFile.exists()) {
               if (!dbFile.createNewFile()) {
                   throw new IOException("File " + fileMapName + " was not created");
               }
           }
            reader = new FileReader(fileMapName);
        } catch (IOException e) {
            throw new IOException(fileMapName + ": error in opening: " + e.getMessage());
        }
        fileMap = new HashMap<String, String>();
        readFromFile(fileMap, reader);
        reader.close();
        this.typeList = typeList;
    }

    @Override
    public void close() throws IOException {
        FileWriter writer = new FileWriter(fileMapName);
        writeToFile(fileMap, writer);
        writer.close();
    }

    private void readFromFile(Map<String, String> fileMap, FileReader reader) throws IOException {
        StringBuilder readDataBuilder = new StringBuilder("");
        while (reader.ready()) {
            int readChar = reader.read();
            readDataBuilder.append((char) readChar);
        }
        String readData = "";
        if (!readDataBuilder.toString().equals("")) {
            readData = new String(readDataBuilder);
            JSONObject obj = new JSONObject(readData);
            /*Set<String> keySet = obj.keySet();
            for (String it : keySet) {
                fileMap.put(it, obj.getJSONArray(it).toString());
            }*/
            for (Iterator iterator = obj.keys(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                fileMap.put(key, obj.getJSONArray(key).toString());
            }
        }
    }

    private void writeToFile(Map<String, String> fileMap, FileWriter writer) throws IOException {
        String key = null;
        String value = null;
        JSONObject obj = new JSONObject();
        for (Map.Entry entry : fileMap.entrySet()) {
            key = entry.getKey().toString();
            value = entry.getValue().toString();
            JSONArray input = new JSONArray(value);
            obj.put(key, input);
        }
        if (obj.length() != 0) {
            writer.write(obj.toString());
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

    public Set<String> getKeys() {
        return fileMap.keySet();
    }
}

package ru.fizteh.fivt.students.Volodin_Denis.MultiFileMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class HelpMap {
    HashMap<String, String> map;
    HelpMap() {
        map = new HashMap<String, String>();
    }
}

public class DataBase implements Map<String, String>, AutoCloseable {

    private String databasePath;
    private Map<String, String> database;

    public DataBase(final String dbPath) throws Exception {
        databasePath = dbPath;
        database = new HashMap<String, String>();
        if (Paths.get(databasePath).toFile().exists()) {
            readFromDisk();
        } else {
            Files.createDirectory(Paths.get(databasePath).getFileName());
        }
    }

    public String[] list() throws Exception {
        try {
            if (database.isEmpty()) {
                return new String[0];
            }
            Set<String> keysList = database.keySet();
            String[] listKeys = new String[database.size()];
            int i = 0;
            for (String key : keysList) {
                listKeys[i] = key;
                ++i;
            }
            return listKeys;
        } catch (Exception e) {
            filemapSmthWrong("list", e.getMessage());
        }
        return new String[0]; //warning
    }

    public String getPath() throws Exception {
        return Paths.get(databasePath).getFileName().toString();
    }

    public void readFromDisk() throws Exception {
        String key;
        String value;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                Path helpPath =  Paths.get(databasePath, i + ".dir", j + ".dat").normalize();
                if (helpPath.toFile().exists()) {
                    try (FileInputStream input = new FileInputStream(helpPath.toString())) {
                        FileChannel channel = input.getChannel();
                        ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()); 
                        while (buffer.hasRemaining()) {
                            byte[] word = new byte[buffer.getInt()];
                            buffer.get(word, 0, word.length);
                            key = new String(word, "UTF-8");
                            
                            word = new byte[buffer.getInt()];
                            buffer.get(word, 0, word.length);
                            value = new String(word, "UTF-8");
                            
                            if ((key.hashCode() % 16 != i) || (key.hashCode() % 16 / 16 != j)) {
                                throw new Exception("wrong input");
                            }
                            database.put(key, value);
                        }
                    } catch (Exception e) {
                        filemapErrorRead("read");
                    }
                }
            }
        }   
    }

    public void writeOnDisk() throws Exception {
        HelpMap[][] helpMap = new HelpMap[16][16];
        Set<String> keys = database.keySet();
        for (String key : keys) {
            helpMap[key.hashCode() % 16][(key.hashCode() % 16) / 16].map.put(key, database.get(key));
        }
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                Set<String> keyList = helpMap[i][j].map.keySet();
                Path helpPath =  Paths.get(databasePath, i + ".dir", j + ".dat").normalize();
                if (helpPath.toFile().exists()) {
                    try (FileOutputStream output = new FileOutputStream(databasePath)) {
                        for (String key : keyList) {
                            ByteBuffer buffer1 = ByteBuffer.allocate(4);
                            byte[] keyByte = buffer1.putInt(key.getBytes("UTF-8").length).array();
                            output.write(keyByte);
                            output.write(key.getBytes("UTF-8"));
                                                     
                            ByteBuffer buffer2 = ByteBuffer.allocate(4);
                            byte[] valueByte = buffer2.putInt(database.get(key).getBytes("UTF-8").length).array();
                            output.write(valueByte);
                            output.write(database.get(key).getBytes("UTF-8"));
                        }
                    } catch (Exception e) {
                        filemapErrorWrite("write");
                    }
                }
            }
        }
    }

    public void deleteEmptyFiles() throws Exception {
        File helpPath;
        try {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {       
                    helpPath =  Paths.get(databasePath, i + ".dir", j + ".dat").normalize().toFile();
                    if (helpPath.exists()) {
                        if (helpPath.length() == 0) {
                            helpPath.delete();
                        }
                    }
                }
                helpPath =  Paths.get(databasePath, i + ".dir").normalize().toFile();
                if (helpPath.exists()) {
                    if (helpPath.list().length == 0) {
                        helpPath.delete();
                    }
                }
            }
        } catch (Exception exception) {
            filemapSmthWrong("delete empty files", exception.getMessage());
        }
    }
  
    @Override
    public void close() throws Exception {
        writeOnDisk();        
    }

    @Override
    public void clear() {
        database.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return database.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return database.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        return database.entrySet();
    }

    @Override
    public String get(Object key) {
        return database.get(key);
    }

    @Override
    public boolean isEmpty() {
        return database.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return database.keySet();
    }

    @Override
    public String put(String key, String value) {
        return database.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        database.putAll(m);
    }

    @Override
    public String remove(Object key) {
        return database.remove(key);
    }

    @Override
    public int size() {
        return database.size();
    }

    @Override
    public Collection<String> values() {
        return database.values();
    }
    
    private void filemapErrorRead(final String commandName) throws Exception {
        throw new Exception(commandName + " : error reading from file");
    }
    
    private void filemapErrorWrite(final String commandName) throws Exception {
        throw new Exception(commandName + " : error writing to file");
    }
    
    private void filemapSmthWrong(final String commandName, final String message) throws Exception {
        throw new Exception(commandName + " :" + message);
    }
}

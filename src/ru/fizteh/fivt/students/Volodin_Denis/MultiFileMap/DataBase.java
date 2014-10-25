package ru.fizteh.fivt.students.Volodin_Denis.MultiFileMap;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class HelpMap {
    public HashMap<String, String> map;
    public HelpMap() {
        map = new HashMap<String, String>();
    }
}

public class DataBase implements Map<String, String>, AutoCloseable {

    private static final int FOLDERS = 16;
    private static final int FILES = 16;

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
        for (int i = 0; i < FOLDERS; ++i) {
            for (int j = 0; j < FILES; ++j) {
                Path helpPath =  Paths.get(databasePath, Integer.toString(i) + ".dir", Integer.toString(j) + ".dat").normalize();
                if (helpPath.toFile().exists()) {
                    try (DataInputStream input = new DataInputStream(new FileInputStream(helpPath.toString()))) {
                        while (true) {
                            try {
                                byte[] word = new byte[input.readInt()];
                                input.readFully(word);
                                key = new String(word, "UTF-8");
                                
                                word = new byte[input.readInt()];
                                input.readFully(word);
                                value = new String(word, "UTF-8");
                                
                                if ((Math.abs(key.hashCode()) % FOLDERS != i) || (Math.abs(key.hashCode()) / FOLDERS % FILES != j)) {
                                    throw new Exception("wrong input");
                                }
                                database.put(key, value);
                            } catch (EOFException e) {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        filemapErrorRead("read");
                    }
                }
            }
        }   
    }

    public void writeOnDisk() throws Exception {
        HelpMap[][] helpMap = new HelpMap[FOLDERS][FILES];
        for (int i = 0; i < FOLDERS; ++i) {
            for (int j = 0; j < FILES; ++j) {
                helpMap[i][j] = new HelpMap();
            }
        }
        Set<String> keys = database.keySet();
        for (String key : keys) {
            helpMap[Math.abs(key.hashCode()) % FOLDERS][Math.abs(key.hashCode()) / FOLDERS % FILES].map.put(key, database.get(key));
        }
        for (int i = 0; i < FOLDERS; ++i) {
            for (int j = 0; j < FILES; ++j) {
                Path helpPath =  Paths.get(databasePath, Integer.toString(i) + ".dir", Integer.toString(j) + ".dat").normalize();
                if (helpPath.toFile().exists()) {
                    if (!helpPath.toFile().delete()) {
                        filemapSmthWrong("write", "file is not deleted");
                    }
                }
            }
            Path helpPath =  Paths.get(databasePath, Integer.toString(i) + ".dir").normalize();
            if (helpPath.toFile().exists()) {
                if (!helpPath.toFile().delete()) {
                    filemapSmthWrong("write", "folder is not deleted");
                };
            }
        }
        for (int i = 0; i < FOLDERS; ++i) {
            for (int j = 0; j < FILES; ++j) {
                Set<String> keyList = helpMap[i][j].map.keySet();
                if (!keyList.isEmpty()) {
                    Path helpPath =  Paths.get(databasePath, Integer.toString(i) + ".dir").normalize();
                    if (!helpPath.toFile().exists()) {
                        Files.createDirectory(helpPath);
                    }
                    helpPath =  Paths.get(helpPath.toString(), j + ".dat").normalize();
                    Files.createFile(helpPath);
                    
                    try (FileOutputStream output = new FileOutputStream(helpPath.toString())) {
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
            for (int i = 0; i < FOLDERS; ++i) {
                for (int j = 0; j < FILES; ++j) {       
                    helpPath =  Paths.get(databasePath, Integer.toString(i) + ".dir", Integer.toString(j) + ".dat").normalize().toFile();
                    if (helpPath.exists()) {
                        if (helpPath.length() == 0) {
                            helpPath.delete();
                        }
                    }
                }
                helpPath =  Paths.get(databasePath, Integer.toString(i) + ".dir").normalize().toFile();
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

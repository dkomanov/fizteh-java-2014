package ru.fizteh.fivt.students.Volodin_Denis.MultiFileMap;

import java.io.DataInputStream;
import java.io.EOFException;
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
            ErrorFunctions.smthWrong("list", e.getMessage());
        }
        return new String[0]; // Unreachable code, add return to ignore Eclipse warning.
    }

    public String getPath() throws Exception {
        return Paths.get(databasePath).getFileName().toString();
    }

    public void readFromDisk() throws Exception {
        String key;
        String value;
        for (int i = 0; i < FOLDERS; ++i) {
            for (int j = 0; j < FILES; ++j) {
                Path helpPath =  Paths.get(databasePath, Integer.toString(i) + ".dir", Integer.toString(j)
                                                                             + ".dat").normalize();
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
                                
                                if ((Math.abs(key.hashCode()) % FOLDERS != i)
                                 || (Math.abs(key.hashCode()) / FOLDERS % FILES != j)) {
                                    throw new Exception("wrong input");
                                }
                                database.put(key, value);
                            } catch (EOFException e) {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        ErrorFunctions.errorRead("read");
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
            helpMap[Math.abs(key.hashCode()) % FOLDERS][Math.abs(key.hashCode()) / FOLDERS % FILES].map
                    .put(key, database.get(key));
        }
        for (int i = 0; i < FOLDERS; ++i) {
            for (int j = 0; j < FILES; ++j) {
                Path helpPath =  Paths.get(databasePath, Integer.toString(i) + ".dir", Integer.toString(j)
                                                                             + ".dat").normalize();
                if (helpPath.toFile().exists()) {
                    if (!helpPath.toFile().delete()) {
                        ErrorFunctions.smthWrong("write", "file is not deleted");
                    }
                }
            }
            Path helpPath =  Paths.get(databasePath, Integer.toString(i) + ".dir").normalize();
            if (helpPath.toFile().exists()) {
                if (!helpPath.toFile().delete()) {
                    ErrorFunctions.smthWrong("write", "folder is not deleted");
                }
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
                            //
                            writeOneWordOnDisk(key, output);
                            writeOneWordOnDisk(database.get(key), output);
                        }
                    } catch (Exception e) {
                        ErrorFunctions.errorWrite("write");
                    }
                }
            }
        }
    }

    private void writeOneWordOnDisk(final String word, FileOutputStream output) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        byte[] wordByte = buffer.putInt(word.getBytes("UTF-8").length).array();
        output.write(wordByte);
        output.write(word.getBytes("UTF-8"));
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
}

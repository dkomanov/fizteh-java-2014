package ru.fizteh.fivt.students.Volodin_Denis.JUnit.database;

import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.ErrorFunctions;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.Table;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TableByVolodden implements Table {

    private String dbPath;
    private FileMap fileMap;
    
    public TableByVolodden(final String path) throws Exception {
        dbPath = WorkWithFile.toAbsolutePath(path);
        fileMap = new FileMap(dbPath);
    }

    //---
    class HelpMap {
        public HashMap<String, String> map;
        public HelpMap() {
            map = new HashMap<String, String>();
        }
    }
    
    class FileMap implements Map<String, String>, AutoCloseable {

        private final int FOLDERS = 16;
        private final int FILES = 16;
        private final String ENCODING = "UTF-8";
        private final String dir = ".dir";
        private final String dat = ".dat";

        private String databasePath;
        private Map<String, String> database;
        private Map<String, String> diff;

        public FileMap(final String dbPath) throws Exception {
            databasePath = dbPath;
            database = new HashMap<String, String>();
            diff = new HashMap<String, String>();
            if (WorkWithFile.exists(databasePath)) {
                readFromDisk();
            } else {
                WorkWithFile.createDirectory(databasePath);
            }
        }

        private void readFromDisk() throws Exception {
            for (int i = 0; i < FOLDERS; ++i) {
                for (int j = 0; j < FILES; ++j) {
                    Path somePath =  Paths.get(databasePath, Integer.toString(i) + dir, Integer.toString(j)
                                                                                 + dat).normalize();
                    if (somePath.toFile().exists()) {
                        try (DataInputStream input = new DataInputStream(new FileInputStream(somePath.toString()))) {
                            while (true) {
                                try {
                                    String key = readOneWordFromDisk(input);
                                    String value = readOneWordFromDisk(input);
                                    
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
                            ErrorFunctions.errorRead();
                        }
                    }
                }
            }   
        }
      
        private String readOneWordFromDisk(DataInputStream input) throws Exception {
            byte[] word = new byte[input.readInt()];
            input.readFully(word);
            return new String(word, ENCODING);
        }

        private void writeOnDisk() throws Exception {
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
                    Path somePath =  Paths.get(databasePath, Integer.toString(i) + dir, Integer.toString(j)
                                                                                 + dat).normalize();
                    if (somePath.toFile().exists()) {
                        if (!somePath.toFile().delete()) {
                            ErrorFunctions.smthWrong("write", "file is not deleted");
                        }
                    }
                }
                Path somePath =  Paths.get(databasePath, Integer.toString(i) + dir).normalize();
                if (somePath.toFile().exists()) {
                    if (!somePath.toFile().delete()) {
                        ErrorFunctions.smthWrong("write", "folder is not deleted");
                    }
                }
            }
            for (int i = 0; i < FOLDERS; ++i) {
                for (int j = 0; j < FILES; ++j) {
                    Set<String> keyList = helpMap[i][j].map.keySet();
                    if (!keyList.isEmpty()) {
                        Path somePath =  Paths.get(databasePath, Integer.toString(i) + dir).normalize();
                        if (!somePath.toFile().exists()) {
                            Files.createDirectory(somePath);
                        }
                        somePath =  Paths.get(somePath.toString(), j + dat).normalize();
                        Files.createFile(somePath);
                        
                        try (FileOutputStream output = new FileOutputStream(somePath.toString())) {
                            for (String key : keyList) {
                                writeOneWordOnDisk(key, output);
                                writeOneWordOnDisk(database.get(key), output);
                            }
                        } catch (Exception e) {
                            ErrorFunctions.errorWrite();
                        }
                    }
                }
            }
        }

        private void writeOneWordOnDisk(final String word, FileOutputStream output) throws Exception {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            byte[] wordByte = buffer.putInt(word.getBytes(ENCODING).length).array();
            output.write(wordByte);
            output.write(word.getBytes(ENCODING));
        }

        private void upgrade() throws Exception {
            for (String key : diff.keySet()) {
                if (diff.get(key) == null) {
                    database.remove(key);
                } else {
                    database.put(key, diff.get(key));
                }
            }
            diff.clear();
            writeOnDisk();
        }

        private void downgrade() {
            diff.clear();
        }
        
        public int numUncommitedChanges() {
            return diff.size();
        }
        
        @Override
        public void close() throws Exception {
        }

        //Not used.
        @Override
        public void clear() {
            database.clear();
            diff.clear();
        }

        //Not used.
        @Override
        public boolean containsKey(Object key) {
            if (database.containsKey(key)) {
                if (diff.containsKey(key)) {
                    if (diff.get(key) != null) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return true;
            } else {
                return diff.containsKey(key);
            }
        }

        //Not used.
        @Override
        public boolean containsValue(Object value) {
            if (diff.containsValue(value)) {
                return true;
            } else {
                for (String key : database.keySet()) {
                    if (database.get(key).equals(value)) {
                        if (!diff.containsKey(key)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
        
        // Not used.
        @Override
        public Set<java.util.Map.Entry<String, String>> entrySet() {
            return null;
        }

        @Override
        public String get(Object key) {
            if (diff.containsKey(key)) {
                return diff.get(key);
            } else {
                return database.get(key);
            }
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public Set<String> keySet() {

            Set<String> keysList = new HashSet<String>();
            if (isEmpty()) {
                return keysList;
            }

            for (String key : database.keySet()) {
                if (diff.containsKey(key)) {
                    if (diff.get(key) != null) {
                        keysList.add(key);
                    }
                } else {
                    keysList.add(key);
                }
            }
            for (String key : diff.keySet()) {
                if (!keysList.contains(key)) {
                    keysList.add(key);
                }
            }
            return keysList;
        }

        @Override
        public String put(String key, String value) {
            if (diff.containsKey(key)) {
                if (database.containsKey(key)) {
                    if (database.get(key).equals(value)) {
                        String oldValue = diff.get(key);
                        diff.remove(key);
                        return oldValue;
                    } else {
                        return diff.put(key, value);
                    }
                } else {
                    return diff.put(key, value);
                }
            } else {
                if (database.containsKey(key)) {
                    if (database.get(key).equals(value)) {
                        return database.get(key);
                    } else {
                        diff.put(key, value);
                        return database.get(key);
                    }
                } else {
                    return diff.put(key, value);
                }
            }
        }

        // Not used.
        @Override
        public void putAll(Map<? extends String, ? extends String> m) {
            return;
        }

        @Override
        public String remove(Object key) {
            if (diff.containsKey(key)) {
                if (diff.get(key) == null) {
                    return null;
                } else {
                    return diff.put((String) key, null);
                }
            } else {
                if (database.containsKey(key)) {
                    diff.put((String) key, null);
                }
                return database.get(key);
            }
        }

        @Override
        public int size() {
            int result = database.size();
            for (String key : database.keySet()) {
                if (diff.containsKey(key)) {
                    if (diff.get(key) == null) {
                        --result;
                    }
                }
            }
            for (String key : diff.keySet()) {
                if (!database.containsKey(key)) {
                    if (diff.get(key) != null) {
                        ++result;
                    }
                }
            }
            return result;
        }
        
        // Not used.
        @Override
        public Collection<String> values() {
            return null;
        }
    }
    
    //---

    @Override
    public String getName() {
        return WorkWithFile.getFileName(dbPath);
    }

    @Override
    public String get(final String key) {
        return fileMap.get(key);
    }

    @Override
    public String put(String key, String value) {
        return fileMap.put(key, value);
    }

    @Override
    public String remove(String key) {
        return fileMap.remove(key);
    }

    @Override
    public int size() {
       return fileMap.size();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return fileMap.numUncommitedChanges();
    }
    
    @Override
    public int commit() {
        int changes = 0;
        try {
            fileMap.upgrade();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return changes;
    }

    @Override
    public int rollback() {
        int changes = fileMap.numUncommitedChanges();
        fileMap.downgrade();
        return changes;
    }

    @Override
    public List<String> list() {

        List<String> keys = new ArrayList<String>();
        for (String key : fileMap.keySet()) {
            keys.add(key);
        }
        return keys;
    }
}

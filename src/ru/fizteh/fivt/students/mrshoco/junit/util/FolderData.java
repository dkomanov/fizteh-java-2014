package junit.util;

import java.io.File;
<<<<<<< HEAD:src/ru/fizteh/fivt/students/mrshoco/multifilehashmap/util/FolderData.java
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
=======
>>>>>>> junit:src/ru/fizteh/fivt/students/mrshoco/junit/util/FolderData.java
import java.util.HashMap;

public class FolderData extends Data {

<<<<<<< HEAD:src/ru/fizteh/fivt/students/mrshoco/multifilehashmap/util/FolderData.java
    private static Map<String, String> checkFile(Map<String, String> hashMap, 
                                                    int fileNumber) throws Exception {
=======
    private static HashMap<String, String> checkFile(HashMap<String, String> hashMap, 
                                                    int fileNumber) throws IllegalArgumentException {
>>>>>>> junit:src/ru/fizteh/fivt/students/mrshoco/junit/util/FolderData.java
        for (String key : hashMap.keySet()) {
            if (key.hashCode() / 16 % 16 != fileNumber) {
                throw new IllegalArgumentException("Key was in wrong place !");
            }
        }
        return hashMap;
    }
    
<<<<<<< HEAD:src/ru/fizteh/fivt/students/mrshoco/multifilehashmap/util/FolderData.java
    private static Map<String, String> checkFolder(Map<String, String> hashMap, 
                                                    int fileNumber) throws Exception {
=======
    private static HashMap<String, String> checkFolder(HashMap<String, String> hashMap, 
                                                    int fileNumber) throws IllegalArgumentException {
>>>>>>> junit:src/ru/fizteh/fivt/students/mrshoco/junit/util/FolderData.java
        for (String key : hashMap.keySet()) {
            if (key.hashCode() % 16 != fileNumber) {
                throw new IllegalArgumentException("Key was in wrong place !");
            }
        }
        return hashMap;
    }

<<<<<<< HEAD:src/ru/fizteh/fivt/students/mrshoco/multifilehashmap/util/FolderData.java
    public static Map<String, String> loadDb(File file) throws Exception {
=======
    public static HashMap<String, String> loadDb(File file) throws IllegalArgumentException {
>>>>>>> junit:src/ru/fizteh/fivt/students/mrshoco/junit/util/FolderData.java
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Db isn't a directory !");
        }
        Map<String, String> data = new HashMap<String, String>();
        for (int i = 0; i < 16; i++) {
            data.putAll(checkFolder(loadFolder(new File(file, i + ".dir")), i));
        }
        return data;
    }

<<<<<<< HEAD:src/ru/fizteh/fivt/students/mrshoco/multifilehashmap/util/FolderData.java
    private static Map<String, String> loadFolder(File file) throws Exception {
        Map<String, String> dataFromFolder = new HashMap<String, String>();
=======
    private static HashMap<String, String> loadFolder(File file) throws IllegalArgumentException {
        HashMap<String, String> dataFromFolder = new HashMap<String, String>();
>>>>>>> junit:src/ru/fizteh/fivt/students/mrshoco/junit/util/FolderData.java
        if (!file.exists()) {
            return dataFromFolder;
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("One of .dir files isn't a directory !");
        }
        for (int i = 0; i < 16; i++) {
            dataFromFolder.putAll(checkFile(loadFile(new File(file, i + ".dat")), i));
        }
        return dataFromFolder;
    }

<<<<<<< HEAD:src/ru/fizteh/fivt/students/mrshoco/multifilehashmap/util/FolderData.java
    private static Map<String, String> loadFile(File file) throws Exception {
        Map<String, String> dataFromFile = new HashMap<String, String>();
=======
    private static HashMap<String, String> loadFile(File file) throws IllegalArgumentException {
        HashMap<String, String> dataFromFile = new HashMap<String, String>();
>>>>>>> junit:src/ru/fizteh/fivt/students/mrshoco/junit/util/FolderData.java
        if (!file.exists()) {
            return dataFromFile;
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("One of .dat files isn't a file");
        }
        dataFromFile = Data.load(file);

        return  dataFromFile;
    }

<<<<<<< HEAD:src/ru/fizteh/fivt/students/mrshoco/multifilehashmap/util/FolderData.java
    public static void saveDb(Map<String, String> data, File file) throws Exception {
=======
    public static void saveDb(HashMap<String, String> data, File file) throws IllegalArgumentException {
>>>>>>> junit:src/ru/fizteh/fivt/students/mrshoco/junit/util/FolderData.java
        @SuppressWarnings("unchecked")
        Map<String, String>[][] sortedData = new HashMap[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                sortedData[i][j] = new HashMap<String, String>();
            }
        }
        for (String key : data.keySet()) {
            int hashcode = key.hashCode();
            sortedData[hashcode % 16][hashcode / 16 % 16].put(key, data.get(key));
        }

        for (int i = 0; i < 16; i++) {
            File fileToCreate = new File(file, i + ".dir");
            saveFolder(sortedData[i], fileToCreate);
        }
    }

<<<<<<< HEAD:src/ru/fizteh/fivt/students/mrshoco/multifilehashmap/util/FolderData.java
    private static void saveFolder(Map<String, String>[] dataToFolder, File file) 
                                                            throws Exception {
=======
    private static void saveFolder(HashMap<String, String>[] dataToFolder, File file) 
                                                            throws IllegalArgumentException {
>>>>>>> junit:src/ru/fizteh/fivt/students/mrshoco/junit/util/FolderData.java
        if (!file.exists()) {
            file.mkdir();
        } else if (!file.isDirectory()) {
            throw new IllegalArgumentException("Can't create a directory");
        }
        boolean isSomeFiles = false;
        for (int i = 0; i < 16; i++) {
            File fileToCreate = new File(file, i + ".dat");
            isSomeFiles |= saveFile(dataToFolder[i], fileToCreate);
        }
        if (!isSomeFiles) {
            file.delete();
        }
    }

<<<<<<< HEAD:src/ru/fizteh/fivt/students/mrshoco/multifilehashmap/util/FolderData.java
    private static boolean saveFile(Map<String, String> dataToFile, File file) 
                                                            throws Exception {
=======
    private static boolean saveFile(HashMap<String, String> dataToFile, File file) 
                                                            throws IllegalArgumentException {
>>>>>>> junit:src/ru/fizteh/fivt/students/mrshoco/junit/util/FolderData.java
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                throw new IllegalArgumentException("Can't create a file");
            }
        } else if (!file.isFile()) {
            throw new IllegalArgumentException("Can't create a file");
        }

        save(file, dataToFile);
        if (dataToFile.size() == 0) {
            file.delete();
            return false;
        } else {
            return true;
        }
    }
}

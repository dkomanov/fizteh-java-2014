package multifilehashmap.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class FolderData extends Data {

    private static Map<String, String> checkFile(Map<String, String> hashMap, 
                                                    int fileNumber) throws Exception {
        for (String key : hashMap.keySet()) {
            if (key.hashCode() / 16 % 16 != fileNumber) {
                throw new Exception("Key was in wrong place !");
            }
        }
        return hashMap;
    }
    
    private static Map<String, String> checkFolder(Map<String, String> hashMap, 
                                                    int fileNumber) throws Exception {
        for (String key : hashMap.keySet()) {
            if (key.hashCode() % 16 != fileNumber) {
                throw new Exception("Key was in wrong place !");
            }
        }
        return hashMap;
    }

    public static Map<String, String> loadDb(File file) throws Exception {
        if (!file.isDirectory()) {
            throw new Exception("Db isn't a directory !");
        }
        Map<String, String> data = new HashMap<String, String>();
        for (int i = 0; i < 16; i++) {
            data.putAll(checkFolder(loadFolder(new File(file, i + ".dir")), i));
        }
        return data;
    }

    private static Map<String, String> loadFolder(File file) throws Exception {
        Map<String, String> dataFromFolder = new HashMap<String, String>();
        if (!file.exists()) {
            return dataFromFolder;
        }
        if (!file.isDirectory()) {
            throw new Exception("One of .dir files isn't a directory !");
        }
        for (int i = 0; i < 16; i++) {
            dataFromFolder.putAll(checkFile(loadFile(new File(file, i + ".dat")), i));
        }
        return dataFromFolder;
    }

    private static Map<String, String> loadFile(File file) throws Exception {
        Map<String, String> dataFromFile = new HashMap<String, String>();
        if (!file.exists()) {
            return dataFromFile;
        }
        if (!file.isFile()) {
            throw new Exception("One of .dat files isn't a file");
        }
        try {
            dataFromFile = Data.load(file);
        } catch (FileNotFoundException e) {
            throw new Exception("File not found");
        } catch (IOException e) {
            throw new Exception("Error while reading file");
        }
        return  dataFromFile;
    }

    public static void saveDb(Map<String, String> data, File file) throws Exception {
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

    private static void saveFolder(Map<String, String>[] dataToFolder, File file) 
                                                            throws Exception {
        if (!file.exists()) {
            file.mkdir();
        } else if (!file.isDirectory()) {
            throw new Exception("Can't create a directory");
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

    private static boolean saveFile(Map<String, String> dataToFile, File file) 
                                                            throws Exception {
        if (!file.exists()) {
            file.createNewFile();
        } else if (!file.isFile()) {
            throw new Exception("Can't create a file");
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

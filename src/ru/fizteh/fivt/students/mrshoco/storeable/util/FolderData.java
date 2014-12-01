package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class FolderData extends Data {

    private static HashMap<String, String> checkFile(HashMap<String, String> hashMap, 
                                                    int fileNumber) throws IllegalArgumentException {
        for (String key : hashMap.keySet()) {
            if (key.hashCode() / 16 % 16 != fileNumber) {
                throw new IllegalArgumentException("Key was in wrong place !");
            }
        }
        return hashMap;
    }
    
    private static HashMap<String, String> checkFolder(HashMap<String, String> hashMap, 
                                                    int fileNumber) throws IllegalArgumentException {
        for (String key : hashMap.keySet()) {
            if (key.hashCode() % 16 != fileNumber) {
                throw new IllegalArgumentException("Key was in wrong place !");
            }
        }
        return hashMap;
    }

    public static List<Class<?>> loadSignature(File file) 
                                throws IOException {
        File signatureFile = new File(file, "signature.tsv");
        List<Class<?>> signatures = new ArrayList<Class<?>>();
        String str = new String();
        try {
            Scanner scanner = new Scanner(signatureFile);
            while (scanner.hasNext()) {
                str = scanner.next();

                switch (str) {
                    case "int": 
                        signatures.add(Integer.class);
                        break;
                    case "long": 
                        signatures.add(Long.class);
                        break;
                    case "byte": 
                        signatures.add(Byte.class);
                        break;
                    case "float": 
                        signatures.add(Float.class);
                        break;
                    case "double": 
                        signatures.add(Double.class);
                        break;
                    case "boolean": 
                        signatures.add(Boolean.class);
                        break;
                    case "String": 
                        signatures.add(String.class);
                        break;
                    default:
                        throw new IllegalArgumentException("Error with signature.tsv file");
                }
            }
            scanner.close();
        } catch (IOException e) {
            throw new IOException("Error while reading signature.tsv");
        }
        return signatures;
    }

    public static HashMap<String, String> loadDb(File file) throws IllegalArgumentException {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Db isn't a directory !");
        }
        HashMap<String, String> data = new HashMap<String, String>();
        for (int i = 0; i < 16; i++) {
            data.putAll(checkFolder(loadFolder(new File(file, i + ".dir")), i));
        }
        return data;
    }

    private static HashMap<String, String> loadFolder(File file) throws IllegalArgumentException {
        HashMap<String, String> dataFromFolder = new HashMap<String, String>();
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

    private static HashMap<String, String> loadFile(File file) throws IllegalArgumentException {
        HashMap<String, String> dataFromFile = new HashMap<String, String>();
        if (!file.exists()) {
            return dataFromFile;
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("One of .dat files isn't a file");
        }
        dataFromFile = Data.load(file);

        return  dataFromFile;
    }

    public static void saveDb(HashMap<String, String> data, File file) throws IllegalArgumentException {
        @SuppressWarnings("unchecked")
        HashMap<String, String>[][] sortedData = new HashMap[16][16];
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

    private static void saveFolder(HashMap<String, String>[] dataToFolder, File file) 
                                                            throws IllegalArgumentException {
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

    private static boolean saveFile(HashMap<String, String> dataToFile, File file) 
                                                            throws IllegalArgumentException {
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

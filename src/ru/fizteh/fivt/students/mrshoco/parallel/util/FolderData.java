package storeable.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FolderData extends Data {

    private static Map<String, String> checkFile(Map<String, String> hashMap, 
                                                    int fileNumber) throws IllegalArgumentException {
        for (String key : hashMap.keySet()) {
            if (key.hashCode() / 16 % 16 != fileNumber) {
                throw new IllegalArgumentException("Key was in wrong place !");
            }
        }
        return hashMap;
    }
    
    private static Map<String, String> checkFolder(Map<String, String> hashMap, 
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

                if (TypesTransformer.toType(str) == null) {
                    scanner.close();
                    throw new IllegalArgumentException("Error with signature.tsv file");
                }
                signatures.add(TypesTransformer.toType(str));
            }
            scanner.close();
        } catch (IOException e) {
            throw new IOException("Error while reading signature.tsv");
        }
        return signatures;
    }

    public static void saveSignature(File file, List<Class<?>> types) {
        File signatureFile = new File(file, "signature.tsv");
        PrintWriter writer;
        try {
            writer = new PrintWriter(signatureFile);
            for (int i = 0; i < types.size(); i++) {
                writer.println(TypesTransformer.toString(types.get(i)));
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error while reading signature");
        }
    }

    public static Map<String, String> loadDb(File file) throws IllegalArgumentException {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Db isn't a directory !");
        }
        Map<String, String> data = new HashMap<String, String>();
        for (int i = 0; i < 16; i++) {
            data.putAll(checkFolder(loadFolder(new File(file, i + ".dir")), i));
        }
        return data;
    }

    private static Map<String, String> loadFolder(File file) throws IllegalArgumentException {
        Map<String, String> dataFromFolder = new HashMap<String, String>();
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

    private static Map<String, String> loadFile(File file) throws IllegalArgumentException {
        Map<String, String> dataFromFile = new HashMap<String, String>();
        if (!file.exists()) {
            return dataFromFile;
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("One of .dat files isn't a file");
        }
        dataFromFile = Data.load(file);

        return  dataFromFile;
    }

    public static void saveDb(Map<String, String> data, File file) throws IllegalArgumentException {
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

    private static boolean saveFile(Map<String, String> dataToFile, File file) 
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

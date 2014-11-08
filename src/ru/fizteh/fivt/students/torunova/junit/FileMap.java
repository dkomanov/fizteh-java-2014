package ru.fizteh.fivt.students.torunova.junit;

import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by nastya on 19.10.14.
 */
public class FileMap {
    private static final int NUMBER_OF_PARTITIONS = 16;
    private Map<String, String> savedCopy = new HashMap<>();
    private Map<String, String> workingCopy = new HashMap<>();
    private String file;

    public FileMap(final String f) throws IncorrectFileException, IOException {
        file = f;
        readFile();
    }

    public String put(String key, String value) {
       return workingCopy.put(key, value);
    }

    public String get(String key) {
        return workingCopy.get(key);
    }

    public String remove(String key) {
          return  workingCopy.remove(key);
    }

    public Set<String> list() {
        return workingCopy.keySet();
    }

    public boolean isEmpty() {
        return workingCopy.isEmpty();
    }

    public int size() {
        return workingCopy.size();
    }

    public int rollback() {
        int numberOfRevertedChanges = countChangedEntries();
        workingCopy = new HashMap<>();
        workingCopy.putAll(savedCopy);
        return numberOfRevertedChanges;
    }
    public int commit() throws  IOException {
        DataOutputStream fos = new DataOutputStream(new FileOutputStream(file));
        Set<String> keys = workingCopy.keySet();

        for (String key : keys) {
            fos.writeInt(key.getBytes("UTF-8").length);
            fos.write(key.getBytes("UTF-8"));
            fos.writeInt(workingCopy.get(key).getBytes("UTF-8").length);
            fos.write(workingCopy.get(key).getBytes("UTF-8"));
        }
        int numberOfChangedEntries = countChangedEntries();
        savedCopy = new HashMap<>();
        savedCopy.putAll(workingCopy);
        return  numberOfChangedEntries;
    }
    public int countChangedEntries() {
        int numberOfChangedEntries = 0;
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(workingCopy.keySet());
        allKeys.addAll(savedCopy.keySet());
        for (String key: allKeys) {
            if (savedCopy.get(key) == null || workingCopy.get(key) == null) {
                numberOfChangedEntries++;
                continue;
            }
            if (!savedCopy.get(key).equals(workingCopy.get(key))) {
                numberOfChangedEntries++;
            }
        }
        return numberOfChangedEntries;
    }
    private void readFile() throws IncorrectFileException, IOException {
        DataInputStream fis = new DataInputStream(new FileInputStream(file));
        int  length;
        while (fis.available() > 0) {
            length = fis.readInt();
            if(length >= Runtime.getRuntime().freeMemory())
                throw new IncorrectFileException("Cannot load file " + file +". Ran out of memory.");
            byte[] key = new byte[length];
            if (fis.read(key) != length) {
                throw new IncorrectFileException("File " + file + " has wrong structure.");
            }
            if(!checkKey(new String(key,"UTF-8"))) {
                throw new IncorrectFileException("File " + file + " contains illegal key.");
            }
            length = fis.readInt();
            if(length >= Runtime.getRuntime().freeMemory())
                throw new IncorrectFileException("Cannot load file " + file + ". Ran out of memory.");
            byte[] value = new byte[length];
            if (fis.read(value) != length) {
                throw new IncorrectFileException("File " + file + " has wrong structure.");
            }
            savedCopy.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
            workingCopy.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
        }
    }
    private int getIndexOfFile() {
        int indexOfFile = Integer.parseInt(file.substring(file.lastIndexOf(File.separatorChar)+1, file.lastIndexOf('.')));
        return indexOfFile;
    }
    private int getIndexOfDir() {
        File f = new File(file).getAbsoluteFile();
        String dirName = f.getParentFile().getName();
        int indexOfDir = Integer.parseInt(dirName.substring(0,dirName.indexOf('.')));
        return indexOfDir;
    }
    private boolean checkKey(String key) {
        int hashcode = Math.abs(key.hashCode());
        int indexOfKeyFile = hashcode / NUMBER_OF_PARTITIONS % NUMBER_OF_PARTITIONS;
        int indexOfKeyDir = hashcode % NUMBER_OF_PARTITIONS;
        return indexOfKeyDir == getIndexOfDir() && indexOfKeyFile == getIndexOfFile();
    }

}

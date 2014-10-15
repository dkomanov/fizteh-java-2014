package ru.fizteh.fivt.students.IvanShafran.multifilehashmap;


import java.io.*;
import java.util.HashMap;

public class DBFile {
    private File workingFile;
    private HashMap<String, String> hashMap;

    public File getWorkingFile() throws Exception {
        return workingFile;
    }

    public void setWorkingFile(File file) {
        workingFile = file;
    }

    public HashMap<String, String> getHashMap() {
            return hashMap;
    }

    DBFile(File file) {
        setWorkingFile(file);
    }

    public void readFile() throws Exception {
        HashMap readingHashMap = new HashMap();

        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(workingFile))) {

            while (dataInputStream.available() != 0) {
                String key = dataInputStream.readUTF();
                String value = dataInputStream.readUTF();

                readingHashMap.put(key, value);
            }
        }
        catch (Exception e) {
            throw new Exception("file " + workingFile.toString() + " didn't read");
        }

        hashMap = readingHashMap;
    }

    public void writeHashMapToFile() throws Exception {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(workingFile))) {
            for (String key : hashMap.keySet()) {
                dataOutputStream.writeUTF(key);
                dataOutputStream.writeUTF(hashMap.get(key));
            }
        }
        catch (Exception e) {
            throw new Exception("error during writing string");
        }
    }

}

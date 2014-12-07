package ru.fizteh.fivt.students.kuzmichevdima.FileMap;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
public class DB {
    private static String dbPath;
    private static Map<String, String> db;
    public DB(String path) throws Exception {
        dbPath = path;
        File file = new File(path).getAbsoluteFile();
        if (file.exists()) {
            db = readDataFromFile();
        } else {
            file = new File(dbPath);
            file.createNewFile();
            db = new HashMap<String, String>();
        }
    }
    private String readWord(DataInputStream dataInputStream) throws IOException {
        int length = dataInputStream.readInt();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(dataInputStream.readChar());
        }
        return stringBuilder.toString();
    }
    public Map<String, String> readDataFromFile() throws IOException {
        Map<String, String> resultFileMap = new HashMap<String, String>();
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(dbPath));
        while (true) {
            try {
                String key = readWord(dataInputStream);
                String value = readWord(dataInputStream);
                resultFileMap.put(key, value);
            } catch (IOException e) {
                break;
            }
        }
        return resultFileMap;
    }
    public final Map<String, String> getDataBase() {
        return db;
    }
    private void writeWord(DataOutputStream dataOutputStream, String str) throws IOException {
        dataOutputStream.writeInt(str.length());
        dataOutputStream.writeChars(str);
    }
    public void writeDataToFile() throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(dbPath));
        Set<String> keySet = db.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            writeWord(dataOutputStream, key);
            writeWord(dataOutputStream, db.get(key));
        }
    }
}

package ru.fizteh.fivt.students.RadimZulkarneev.DataBase;

import java.io.*;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileMap implements Map<String, String>, AutoCloseable {
    private final Path pathName;
    private Map<String, String> map;
    private static final String CODING = "UTF-8";
    private static final String NUMBER = "[0-9]";
    private static final int SIXTEEN = 16;
    private static final int TEN = 10;
    private static final int FOUR = 4;

    public FileMap(Path pathName) throws IOException {
        this.pathName = pathName;
        loadFromFile();
        saveToFile();
    }

    private void saveToFile() throws IOException {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(pathName.toString()));
        for (Entry<String, String> entry : map.entrySet()) {
            isValid(entry.getKey());
            writeString(out, entry.getKey());
            writeString(out, entry.getValue());
        }
        out.close();
    }

    public Path getPath() {
        return pathName;
    }
    private void writeString(DataOutputStream out, String s) throws IOException {
        byte[] bytes = s.getBytes(CODING);
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    public void loadFromFile() throws IOException {
        map = new HashMap<>();
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(pathName.toString()));
            while (in.available() > 0) {
                String key = readString(in);
                String value = readString(in);
                isValid(key);
                map.put(key, value);
            }
            in.close();
        } catch (FileNotFoundException e) {
            // That's normal: will be created in saveToFile().
        }
    }

    private String readString(DataInputStream in) throws IOException {
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.read(bytes);
        return new String(bytes, CODING);
    }

    private void isValid(String key) {
        String dest = keyDestination(key);
        String nameDir = getDirName(dest);
        String nameFile = getFileName(dest);
        if (!(nameFile.equals(pathName.getFileName().toString())
                && nameDir.equals(pathName.getParent().getFileName().toString()))) {
            throw new RuntimeException("Key hash does not matches with table");
        }
    }

    private String getDirName(String dest) {
        return new String(dest.substring(0, 2) + ".dir");
    }

    private String getFileName(String dest) {
            return new String(dest.substring(2, FOUR) + ".dat");
    }
    private String keyDestination(String key) {
        int hashcode = Math.abs(key.hashCode());
        int nDir = hashcode % SIXTEEN;
        int nFile = hashcode / SIXTEEN % SIXTEEN;
        String retVal = "";
        if (nDir < TEN) {
            retVal += ("0" + nDir);
        } else {
            retVal += nDir;
        }
        if (nFile < TEN) {
            retVal += ("0" + nFile);
        } else {
            retVal += nFile;
        }
        return retVal;
    }

    /**
     * @return название fileMap в формате [ddff], где dd - номер директории, ff - номер файла. Все в диапазоне от 00 до 15.
     */
    public String fileMapCode() {
        String code = "";
        try {
            String fileMapParentName = pathName.getParent().getFileName().toString();
            String current = fileMapParentName.substring(0, fileMapParentName.length() - FOUR);
            if (current.matches(NUMBER)) {
                code = (0 + current);
            } else {
                code = (current);
            }
            String fileMapName = pathName.getFileName().toString();
            current = fileMapName.substring(0, fileMapName.length() - FOUR);
            if (current.matches(NUMBER)) {
                code += (0 + current);
            } else {
                code += (current);
            }
        } catch (Exception ex) {
            throw ex;
        }
        return code;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String get(Object key) {
        return map.get(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public String put(String key, String value) {
        isValid(key);
        return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        map.putAll(m);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public String remove(Object key) {
        return map.remove(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public void close() throws Exception {
        saveToFile();
        if (map.size() == 0) {
            pathName.toFile().delete();
        }
    }

    @Override
    public Collection<String> values() {
        // TODO Auto-generated method stub
        return null;
    }
}

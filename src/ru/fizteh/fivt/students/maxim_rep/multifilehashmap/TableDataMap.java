package ru.fizteh.fivt.students.maxim_rep.multifilehashmap;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TableDataMap implements Map<String, String>, AutoCloseable {
    private final String dbPath;
    private final String currentTableName;
    protected Map<String, String> map;
    private final String separator = System.getProperty("file.separator");

    private String fullTablePath() {
        return dbPath + separator + currentTableName + separator;
    }

    public TableDataMap(String dbPath, String currentTableName)
            throws IOException {
        this.dbPath = dbPath;
        this.currentTableName = currentTableName;
        loadFromTable();
        saveToTable();
    }

    private void removeFolder(File f) {
        if (f.exists()) {
            String[] files = f.list();
            for (int i = 0; i < files.length; ++i) {
                File currentFile = new File(f.getAbsolutePath() + separator
                        + files[i]);
                if (currentFile.isDirectory()) {
                    removeFolder(currentFile);
                }
                currentFile.delete();
            }
            f.delete();
        }
    }

    private void makeFolders(String keyName) throws IOException {
        String[] filePath = generateFilePath(keyName);

        File f = null;
        f = new File(fullTablePath() + filePath[1] + separator);
        if (!f.exists()) {
            f.mkdir();
            f = new File(fullTablePath() + filePath[1] + separator
                    + filePath[2]);
        }
    }

    private String[] generateFilePath(String keyName) {
        byte bytes = keyName.getBytes()[0];
        int ndirectory = Math.abs(bytes % 16);
        int nfile = Math.abs(bytes / 16 % 16);
        String[] result = new String[3];
        result[0] = fullTablePath() + ndirectory + ".dir" + separator + nfile
                + ".dat";
        result[1] = ndirectory + ".dir";
        result[2] = nfile + ".dat";
        return result;
    }

    private void saveToTable() throws IOException {
        for (int i = 0; i <= 15; i++) {
            removeFolder(new File(fullTablePath() + i + ".dir"));
        }

        for (Entry<String, String> entry : map.entrySet()) {
            String savePath = generateFilePath(entry.getKey())[0];
            makeFolders(entry.getKey());
            DataOutputStream out = new DataOutputStream(new FileOutputStream(
                    savePath));

            writeString(out, entry.getKey());
            writeString(out, entry.getValue());

        }
    }

    private void writeString(DataOutputStream out, String s) throws IOException {
        byte[] bytes = s.getBytes("UTF-8");
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    public void loadFromTable() throws IOException {
        map = new HashMap<>();
        try {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    String filePath = fullTablePath() + i + ".dir" + separator
                            + j + ".dat";
                    File file = new File(filePath);
                    if (file.exists() && file.isFile()) {
                        DataInputStream in = new DataInputStream(
                                new FileInputStream(filePath));
                        while (in.available() > 0) {
                            String key = readString(in);
                            String value = readString(in);
                            map.put(key, value);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // That's normal: will be created in saveToFile().
        }
    }

    private String readString(DataInputStream in) throws IOException {
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.read(bytes);
        return new String(bytes, "UTF-8");
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public String compute(
            String key,
            BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return map.compute(key, remappingFunction);
    }

    @Override
    public String computeIfPresent(
            String key,
            BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction);
    }

    @Override
    public String merge(
            String key,
            String value,
            BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return map.merge(key, value, remappingFunction);
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return map.entrySet();
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super String> action) {
        map.forEach(action);
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
    public String putIfAbsent(String key, String value) {
        return map.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public void replaceAll(
            BiFunction<? super String, ? super String, ? extends String> function) {
        map.replaceAll(function);
    }

    @Override
    public boolean replace(String key, String oldValue, String newValue) {
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public String put(String key, String value) {
        return map.put(key, value);
    }

    @Override
    public String getOrDefault(Object key, String defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public Collection<String> values() {
        return map.values();
    }

    @Override
    public String replace(String key, String value) {
        return map.replace(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        map.putAll(m);
    }

    @Override
    public String computeIfAbsent(String key,
            Function<? super String, ? extends String> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
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
        saveToTable();
    }

    public void drop(String tableName) throws IOException {
        removeFolder(new File(fullTablePath()));
    }
}

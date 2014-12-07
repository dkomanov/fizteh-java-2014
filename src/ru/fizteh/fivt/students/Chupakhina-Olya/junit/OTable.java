package ru.fizteh.fivt.students.olga_chupakhina.junit;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.*;
import java.util.*;

public class OTable implements Table {
    public String tableName;
    public String path;
    public File table;
    public Map<Integer, TableState> tableStates;
    public Map<String, String> map;
    public int numberOfElements;
    private static final int N = 16;
    public int unsavedChanges;
    public int numberOfState;

    public OTable(String name, String pathname) {
        map = new TreeMap<String, String>();
        tableStates = new TreeMap<Integer, TableState>();
        File dir = new File(pathname);
        path = pathname + File.separator + name;
        table = new File(path);
        tableName = name;
        unsavedChanges = 0;
        numberOfState = 0;
        TableState ts = new TableState(map, unsavedChanges, numberOfElements);
        tableStates.put(numberOfState, ts);
    }

    @Override
    public String getName() {
        return tableName;
    };

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        String s = map.get(key);
        if (s != null) {
            return s;
        } else {
            return null;
        }
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is a null-string");
        }
        String s = map.put(key, value);
        if (s != null) {
            return s;
        } else {
            unsavedChanges++;
            numberOfElements++;
            return null;
        }
    };

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key or value is a null-string");
        }
        String s = map.remove(key);
        if (s != null) {
            unsavedChanges++;
            numberOfElements--;
            return s;
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        return numberOfElements;
    }

    @Override
    public int commit() {
        String key;
        String value;
        rm();
        try {
            for (Map.Entry<String, String> i : map.entrySet()) {
                key = i.getKey();
                value = i.getValue();
                Integer ndirectory = Math.abs(key.getBytes("UTF-8")[0] % N);
                Integer nfile = Math.abs((key.getBytes("UTF-8")[0] / N) % N);
                String pathToDir = path + File.separator + ndirectory.toString()
                        + ".dir";
                File file = new File(pathToDir);
                if (!file.exists()) {
                    file.mkdir();
                }
                String pathToFile = path + File.separator + ndirectory.toString()
                        + ".dir" + File.separator + nfile.toString() + ".dat";
                file = new File(pathToFile);
                if (!file.exists()) {
                    file.createNewFile();
                }
                DataOutputStream outStream = new DataOutputStream(
                        new FileOutputStream(pathToFile, true));
                byte[] byteWord = key.getBytes("UTF-8");
                outStream.writeInt(byteWord.length);
                outStream.write(byteWord);
                outStream.flush();
                byteWord = value.getBytes("UTF-8");
                outStream.writeInt(byteWord.length);
                outStream.write(byteWord);
                outStream.flush();
                outStream.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        TableState ts = new TableState(map, unsavedChanges, numberOfElements);
        tableStates.put(++numberOfState, ts);
        int n = unsavedChanges;
        unsavedChanges = 0;
        return n;
    }

    public void rm() {

        File[] dirs = this.table.listFiles();
        if (dirs != null) {
            for (File dir : dirs) {
                if (!dir.isDirectory()) {
                    System.err.println(dir.getName()
                            + " is not directory");
                    System.exit(-1);
                }
                File[] dats = dir.listFiles();
                if (dats.length == 0) {
                    System.err.println("Empty folders found");
                    System.exit(-1);
                }
                for (File dat : dats) {
                    if (!dat.delete()) {
                        System.out.println("Error while reading table " + tableName);
                    }


                }
                if (!dir.delete()) {
                    System.out.println("Error while reading table " + tableName);
                }

            }
        }
    }

    @Override
    public int rollback() {
        TableState state = tableStates.get(numberOfState);
        map = new HashMap<String, String>(state.map);
        numberOfElements = state.numberOfElements;
        int n = unsavedChanges;
        unsavedChanges = state.unsavedChanges;
        return n;
    }

    @Override
    public List<String> list() {
        Set<String> keySet = map.keySet();
        List<String> list = new LinkedList();
        for (String current : keySet) {
            list.add(current);
        }
        return list;
    }
}

package util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import structured.Storeable;
import structured.Table;
import structured.TableProvider;

public class MyTable implements Table {
    TableProvider myTableProvider;
    File tableRoot;
    HashMap<String, Storeable> data;
    List<Class<?>> types;

    public MyTable(File tableFile, TableProvider tableProvider) {
        myTableProvider = tableProvider;
        try {
            types = FolderData.loadSignature(tableFile);

            HashMap<String, String> hashMap = FolderData.loadDb(tableFile);
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                data.put(entry.getKey(), myTableProvider.deserialize(this, entry.getValue()));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to load signature");
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error while parsing values");
        }
        tableRoot = tableFile;
    }

    @Override
    public String getName() {
        return tableRoot.getName();
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key value");
        }

        if (data.containsKey(key)) {
            System.out.println("found\n" + data.get(key));
            return data.get(key);
        } else {
            System.out.println("not found");
            return null;
        }
    }

    @Override
    public String put(String key, Storeable value) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key value");
        }

        if (data.containsKey(key)) {
            System.out.println("overwrite\nold value");
            String oldValue = data.get(key);
            data.put(key, value);
            return oldValue;
        } else {
            System.out.println("new");
            data.put(key, value);
            return null;
        }
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key value");
        }

        if (data.containsKey(key)) {
            System.out.println("removed");
            String oldValue = data.get(key);
            data.remove(key);
            return oldValue;
        } else {
            System.out.println("not found");
            return null;
        }
    }

    @Override
    public int size() {
        List<String> keyList = new ArrayList<String>();
        for (String key : data.keySet()) {
            keyList.add(key);
        }
        System.out.println(keyList.size());
        return keyList.size();
    }
    
    @Override
    public int commit() {
        int diffSize = diff();
        FolderData.saveDb(data, tableRoot);
        System.out.println(diffSize);
        return diffSize;
    }

    @Override
    public int rollback() {
        int diffSize = diff();
        data = FolderData.loadDb(tableRoot);
        System.out.println(diffSize);
        return diffSize;
    }

    private int diff() {
        HashMap<String, String> oldData = FolderData.loadDb(tableRoot);
        Set<String> allKeys = new HashSet<String>(oldData.keySet());
        allKeys.addAll(data.keySet());
        
        int size = 0;

        for (String k : allKeys) {
            if ((data.containsKey(k) && !oldData.containsKey(k))
                    || (!data.containsKey(k) && oldData.containsKey(k))
                        || (!data.get(k).equals(oldData.get(k)))) {
                System.out.println(" " + data.get(k) + " " + oldData.get(k));
                size++;
            }
        }
        return size;
    }

    @Override
    public List<String> list() {
        List<String> keyList = new ArrayList<String>();
        for (String key : data.keySet()) {
            System.out.println(key);
            keyList.add(key);
        }
        return keyList;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return diff();
    }

    @Override
    public int getColumnsCount() {
        return types.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= types.size() || columnIndex < 0) {
            throw new IndexOutOfBoundsException("Index is out of bound");
        }
        return types.get(columnIndex);
    }
}

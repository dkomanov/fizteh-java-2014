package storeable.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import storeable.structured.ColumnFormatException;
import storeable.structured.Storeable;
import storeable.structured.Table;
import storeable.structured.TableProvider;

public class MyTable implements Table {
    TableProvider myTableProvider;
    File tableRoot;
    Map<String, Storeable> data;
    List<Class<?>> types;

    public MyTable(File tableFile, TableProvider tableProvider) {
        myTableProvider = tableProvider;
        try {
            types = FolderData.loadSignature(tableFile);

            data = deserializeMap(FolderData.loadDb(tableFile));
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to load signature");
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error while parsing values");
        }
        tableRoot = tableFile;
    }
    
    private Storeable copy(Storeable storeable) {
        try {
            return myTableProvider.deserialize(this,
                    myTableProvider.serialize(this, storeable));
        } catch (ParseException e) {
            throw new ColumnFormatException(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return tableRoot.getName();
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key value");
        }

        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            return null;
        }
    }

    @Override
    public Storeable put(String key, Storeable value) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key value");
        }

        if (data.containsKey(key)) {
                Storeable oldValue = copy(data.get(key));
                data.put(key, copy(value));
                return oldValue;
        } else {
            data.put(key, copy(value));
            return null;
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key value");
        }

        if (data.containsKey(key)) {
            Storeable oldValue = data.get(key);
            data.remove(key);
            return oldValue;
        } else {
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
        FolderData.saveDb(serializeMap(data), tableRoot);
        System.out.println(diffSize);
        return diffSize;
    }

    private Map<String, String> serializeMap(Map<String, Storeable> map) {
        Map<String, String> hashMap = new HashMap<String, String>();
        for (Map.Entry<String, Storeable> entry : map.entrySet()) {
            hashMap.put(entry.getKey(), myTableProvider.serialize(this, entry.getValue()));
        }

        return hashMap;
    }

    @Override
    public int rollback() {
        int diffSize = diff();
        try {
            data = deserializeMap(FolderData.loadDb(tableRoot));
        } catch (ParseException e) {
            System.err.println("failed to load old data");
        }
        System.out.println(diffSize);
        return diffSize;
    }


    private Map<String, Storeable> deserializeMap(Map<String, String> map) 
                                                            throws ParseException {
        Map<String, Storeable> hashMap = new HashMap<String, Storeable>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            hashMap.put(entry.getKey(), myTableProvider.deserialize(this, entry.getValue()));
        }

        return hashMap;
    }

    protected int diff() {
        Map<String, String> oldData = FolderData.loadDb(tableRoot);
        Set<String> allKeys = new HashSet<String>(oldData.keySet());
        Map<String, String> sData = serializeMap(data);
        allKeys.addAll(sData.keySet());
        
        int size = 0;

        for (String k : allKeys) {
            if ((sData.containsKey(k) && !oldData.containsKey(k))
                    || (!sData.containsKey(k) && oldData.containsKey(k))
                        || (!sData.get(k).equals(oldData.get(k)))) {
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

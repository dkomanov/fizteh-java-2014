package storeableparallel.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import storeableparallel.structured.*;

public class MyTable implements Table {
    TableProvider myTableProvider;
    File tableRoot;
    Map<String, Storeable> data;
    List<Class<?>> types;
    ReentrantReadWriteLock lock;
    ThreadLocal<Diff> diff;

    public MyTable(File tableFile, TableProvider tableProvider, 
                                                ReentrantReadWriteLock passedLock) {
        lock = passedLock;
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
        
        final MyTable thisTable = this;
        diff = new ThreadLocal<Diff>() {
            @Override
            protected Diff initialValue() {
                return new Diff(thisTable, data, lock);
            }
        };
    }
    
    private Storeable copy(Storeable storeable) {
        if (storeable == null) {
            return null;
        }
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
        Storeable value = copy(diff.get().get(key));
        return value;
    }

    @Override
    public Storeable put(String key, Storeable value) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key value");
        }

        Storeable oldValue = get(key);
        diff.get().put(key, copy(value));
        return oldValue;
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key value");
        }

        Storeable oldValue = get(key);
        diff.get().remove(key);
        return oldValue;
    }

    @Override
    public int size() {
        return diff.get().size();
    }
    
    @Override
    public int commit() throws IOException {
        int diffSize = diff.get().diff();
        diff.get().commit();
        diff.get().clear();
        return diffSize;
    }

    protected Map<String, String> serializeMap(Map<String, Storeable> map) {
        Map<String, String> hashMap = new HashMap<String, String>();
        for (Map.Entry<String, Storeable> entry : map.entrySet()) {
            hashMap.put(entry.getKey(), myTableProvider.serialize(this, entry.getValue()));
        }

        return hashMap;
    }

    @Override
    public int rollback() {
        int diffSize = diff.get().diff();
        diff.get().clear();
        return diffSize;
    }


    protected Map<String, Storeable> deserializeMap(Map<String, String> map) 
                                                            throws ParseException {
        Map<String, Storeable> hashMap = new HashMap<String, Storeable>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            hashMap.put(entry.getKey(), myTableProvider.deserialize(this, entry.getValue()));
        }

        return hashMap;
    }

    protected File getTableRoot() {
        return tableRoot;
    }

    @Override
    public List<String> list() {
        return diff.get().list();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return diff.get().diff();
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

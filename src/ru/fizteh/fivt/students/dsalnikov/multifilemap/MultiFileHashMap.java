package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dsalnikov.filemap.SingleFileTable;
import ru.fizteh.fivt.students.dsalnikov.utils.NoTableException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultiFileHashMap implements MultiTable {
    private Map<String, Integer> tableInfo;
    private MultiFileTable currTable;
    private File dbFolder;
    private TableProvider manager;


    //traverse over all existing tables and count amount of keys
    public MultiFileHashMap() {
        try {
            String path = System.getProperty("fizteh.db.dir");
            dbFolder = new File(path);
            tableInfo = new HashMap<>();
            manager = new DBTableProvider(path);
            getSizeOfTable();
        } catch (Throwable thr) {
            thr.printStackTrace();
            System.err.println(thr.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void create(List<String> name) {
        if (!tableInfo.containsKey(name)) {
            tableInfo.put(name.get(1), 0);
        }
        manager.createTable(name.get(1));
    }


    @Override
    public void drop(String name) {
        //erase directory from disk, remove tableInfo entry
        if (currTable != null) {
            if (currTable.getName().equals(name)) {
                currTable = null;
            }
        }
        manager.removeTable(name);

        tableInfo.remove(name);
    }

    @Override
    public void use(String name) {
        currTable = (MultiFileTable) manager.getTable(name);
        tableInfo.put(name, currTable.size());
    }

    @Override
    public List<String> showTables() {
        return tableInfo.entrySet().stream().map(v -> String.format("%s\t%s", v.getKey(), v.getValue())).collect(Collectors.toList());
    }

    @Override
    public String getDbPath() {
        return dbFolder.toString();
    }

    @Override
    public File getDbFile() {
        return dbFolder;
    }

    @Override
    public int getAmountOfChanges() {
        if (currTable == null) {
            return 0;
        } else {
            return currTable.getAmountOfChanges();
        }
    }

    @Override
    public int getTableDimensions() {
        return 1;
    }

    @Override
    public String getName() {
        return currTable.getName();
    }

    @Override
    public String get(String key) {
        checkTableUsed();
        String result = currTable.get(key);
        tableInfo.put(currTable.getName(), currTable.size());
        return result;
    }

    @Override
    public String put(String key, String value) {
        checkTableUsed();
        String result = currTable.put(key, value);
        tableInfo.put(currTable.getName(), currTable.size());
        return result;
    }


    @Override
    public List<String> list() {
        checkTableUsed();
        return currTable.list();
    }

    @Override
    public String remove(String key) {
        checkTableUsed();
        String result = currTable.remove(key);
        tableInfo.put(currTable.getName(), currTable.size());
        return result;
    }

    @Override
    public int size() {
        checkTableUsed();
        return currTable.size();
    }

    @Override
    public int commit() {
        checkTableUsed();
        int result = currTable.commit();
        tableInfo.put(currTable.getName(), currTable.size());
        return result;
    }

    @Override
    public int rollback() {
        checkTableUsed();
        int result = currTable.rollback();
        tableInfo.put(currTable.getName(), currTable.size());
        return result;
    }


    @Override
    public int exit() {
        return 0;
    }

    private void getSizeOfTable() {
        int counter;
        for (File table : dbFolder.listFiles()) {
            counter = 0;
            try {
                for (File subfolders : table.listFiles()) {
                    for (File dbfile : subfolders.listFiles()) {
                        counter += new SingleFileTable(dbfile).size();
                    }
                }
            } catch (NullPointerException exc) {
                exc.getMessage();
            }
            tableInfo.put(table.getName(), counter);
        }
    }

    private void checkTableUsed() {
        if (currTable == null) {
            throw new NoTableException();
        }
    }

}
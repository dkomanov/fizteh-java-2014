package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dsalnikov.filemap.SingleFileTable;
import ru.fizteh.fivt.students.dsalnikov.utils.NoTableException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMap implements MultiTable {
    private Map<String, Integer> tableinfo;
    private MultiFileTable currtable;
    private File dbfolder;
    private TableProvider manager;


    //traverse over all existing tables and count amount of keys
    public MultiFileHashMap() {
        try {
            String path = System.getProperty("fizteh.table.dir");
            dbfolder = new File(path);
            tableinfo = new HashMap<>();
            manager = new DBTableProvider(path);
            getSizeOfTable();
        } catch (Throwable thr) {
            thr.printStackTrace();
            System.err.println(thr.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void create(String name) {
        if (!tableinfo.containsKey(name)) {
            tableinfo.put(name, 0);
        }
        manager.createTable(name);
    }


    @Override
    public void drop(String name) {
        //erase directory from disk, remove tableinfo entry
        if (currtable != null) {
            if (currtable.getName().equals(name)) {
                currtable = null;
            }
        }
        manager.removeTable(name);

        tableinfo.remove(name);
    }

    @Override
    public void use(String name) {
        if (currtable != null) {
            int changes = currtable.getAmountOfCnages();
            if (changes > 0) {
                System.out.println("Error: there are " + changes + " unsaved changes");
            }
        }
        if (!tableinfo.containsKey(name)) {
            System.out.println(String.format("%s not exists", name));
        } else {
            currtable = (MultiFileTable) manager.getTable(name);
            tableinfo.put(name, currtable.size());
        }

    }

    @Override
    public void showTables() {
        System.out.println(String.format("table_name row_count"));
        for (String s : tableinfo.keySet()) {
            System.out.println(String.format("%s\t\t%s", s, tableinfo.get(s)));
        }
    }

    @Override
    public String getName() {
        return currtable.getName();
    }

    @Override
    public String get(String key) {
        checkTableUsed();
        String result = currtable.get(key);
        tableinfo.put(currtable.getName(), currtable.size());
        return result;
    }

    @Override
    public String put(String key, String value) {
        checkTableUsed();
        String result = currtable.put(key, value);
        tableinfo.put(currtable.getName(), currtable.size());
        return result;
    }


    @Override
    public void list() {
        checkTableUsed();
        currtable.list();
    }

    @Override
    public String remove(String key) {
        checkTableUsed();
        String result = currtable.remove(key);
        tableinfo.put(currtable.getName(), currtable.size());
        return result;
    }

    @Override
    public int size() {
        checkTableUsed();
        return currtable.size();
    }

    @Override
    public int commit() {
        checkTableUsed();
        int result = currtable.commit();
        tableinfo.put(currtable.getName(), currtable.size());
        return result;
    }

    @Override
    public int rollback() {
        checkTableUsed();
        int result = currtable.rollback();
        tableinfo.put(currtable.getName(), currtable.size());
        return result;
    }


    @Override
    public int exit() {
        return 0;
    }

    private void getSizeOfTable() {
        int counter;
        for (File table : dbfolder.listFiles()) {
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
            tableinfo.put(table.getName(), counter);
        }
    }

    private void checkTableUsed() {
        if (currtable == null) {
            throw new NoTableException();
        }
    }

}
package ru.fizteh.fivt.students.kotsurba.multifilehashmap;

import ru.fizteh.fivt.students.kotsurba.filemap.database.DataBaseException;

import java.io.File;
import java.io.IOException;

public final class DataBaseTable {
    private String tableDir;
    private File tableDirFile;
    private String currentTable;
    private MultiDataBase dataBase = null;

    public DataBaseTable(String newTableDir) {
        tableDir = newTableDir;
        currentTable = "";

        tableDirFile = new File(tableDir);
        if (!tableDirFile.exists() || !tableDirFile.isDirectory()) {
            throw new DataBaseException(tableDir + " not exists!");
        }
    }

    public boolean createTable(final String tableName) {
        File file = new File(tableDir + File.separator + tableName);
        if (file.exists()) {
            return false;
        }
        file.mkdir();
        return true;
    }

    public boolean dropTable(final String tableName) throws IOException {
        File file = new File(tableDir + File.separator + tableName);
        if (!file.exists()) {
            return false;
        }
        MultiDataBase base = new MultiDataBase(tableDir + File.separator + tableName);
        base.drop();
        file.delete();
        if (tableName.equals(currentTable)) {
            dataBase = null;
        }
        return true;
    }

    public boolean useTable(String tableName) throws IOException {
        if (!new File(tableDir + File.separator + tableName).exists()) {
            return false;
        }

        save();

        dataBase = null;
        currentTable = tableName;
        dataBase = new MultiDataBase(tableDir + File.separator + tableName);
        return true;
    }

    public String put(final String keyStr, final String valueStr) {
        return dataBase.put(keyStr, valueStr);
    }

    public String get(final String keyStr) {
        return dataBase.get(keyStr);
    }

    public boolean remove(final String keyStr) {
        return dataBase.remove(keyStr);
    }

    public String list() {
        return dataBase.list();
    }

    public String show() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        MultiDataBase mdb;

        for (String str : new File(tableDir).list()) {
            mdb = new MultiDataBase(tableDir + File.separator + str);
            stringBuilder.append(str).append(" ").append(mdb.getCount()).append("\n");
        }
        return stringBuilder.toString();
    }

    public void save() {
        if (exist()) {
            dataBase.save();
        }
    }

    public boolean exist() {
        return (dataBase != null);
    }
}


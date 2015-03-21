package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import ru.fizteh.fivt.storage.strings.Table;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class DBaseTable implements Table {

    static final int SIZE = 16;

    private BaseTable table;

    public DBaseTable(BaseTable baseTable) {
        table = baseTable;
    }


    @Override
    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("No key, null");
        }

        if (table.keys.containsKey(key)) {
            return (table.keys.get(key));
        }
        return null;
    }

    @Override
    public int size() throws Exception {
        return table.keys.size();
    }

    @Override
    public String put(String key, String value) throws Exception {
        if (key == null) {
            throw new IllegalArgumentException("No key, null");
        }
        table.puted.put(key, value);
        return table.keys.get(key);
    }

    @Override
    public String getName() {
        return table.tableName;
    }

    @Override
    public String remove(String key) throws IllegalArgumentException {
        if (!table.keys.containsKey(key)) {
            return null;
        }
        table.removed.add(key);
        return table.keys.get(key);
    }

    @Override
    public int commit() throws Exception {
        if (table.puted.size() == 0 && table.removed.size() == 0) {
            return 0;
        }
        byte b;
        int nDirectory;
        int nFile;
        for (Entry<String, String> pair : table.puted.entrySet()) {
            b = pair.getKey().getBytes()[0];
            nDirectory = b % SIZE;
            nFile = b / SIZE % SIZE;

            if (table.tableDateBase[nDirectory][nFile] == null) {
                String s;
                s = String.valueOf(nDirectory);
                s = s.concat(".dir");
                Path pathDir = table.path;
                pathDir = pathDir.resolve(s);
                try {
                    if (!pathDir.toFile().exists()) {
                        try {
                            pathDir.toFile().mkdir();
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }

                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

                s = String.valueOf(nFile);
                s = s.concat(".dat");
                Path pathFile = pathDir.resolve(s);
                try {
                    if (!pathFile.toFile().exists()) {
                        try {
                            pathFile.toFile().createNewFile();
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                table.tableDateBase[nDirectory][nFile] = new DataBase(pathFile.toString());
            }
            table.tableDateBase[nDirectory][nFile].put(pair.getKey(), pair.getValue());
            if (table.keys.containsKey(pair.getKey())) {
                table.keys.remove(pair.getKey());
            }
            table.keys.put(pair.getKey(), pair.getValue());
        }
        int size = table.puted.size();
        table.puted.clear();
        for (String key : table.removed) {
            b = key.getBytes()[0];
            nDirectory = b % SIZE;
            nFile = b / SIZE % SIZE;
            table.tableDateBase[nDirectory][nFile].remove(key);
            table.keys.remove(key);
        }
        table.removed.clear();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (table.tableDateBase[i][j] != null) {
                    table.tableDateBase[i][j].close();
                }
            }
        }
        return size;
    }

    @Override
    public int rollback() throws Exception {
        int size = table.puted.size();
        table.removed.clear();
        table.puted.clear();
        return size;
    }

    @Override
    public List<String> list() {
        List<String> list = new ArrayList<>();
        for (String key : table.keys.keySet()) {
            list.add(key);
        }
        return list;
    }

}
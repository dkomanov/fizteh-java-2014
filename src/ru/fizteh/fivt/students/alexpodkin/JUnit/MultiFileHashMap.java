package ru.fizteh.fivt.students.alexpodkin.JUnit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MultiFileHashMap {

    public HashMap<String, Integer> tableNames = null;
    public String fileMapPath;

    private boolean checkTable(String name) {
        File dircheck = new File(fileMapPath + File.separator + name);
        if (!dircheck.exists() || !dircheck.isDirectory()) {
            return false;
        }
        for (int i = 0; i < 16; i++) {
            File subdircheck = new File(dircheck.getAbsolutePath() + File.separator + Integer.toString(i) + ".dir");
            if (!subdircheck.exists() || !subdircheck.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    private boolean initBase() {
        Reader reader;
        tableNames = new HashMap<>();
        File tableDir = new File(fileMapPath);
        if (!tableDir.isDirectory()) {
            return false;
        }
        File[] tables = tableDir.listFiles();
        for (File table : tables) {
            if (!checkTable(table.getName())) {
                continue;
            }
            reader = new Reader(table.getAbsolutePath());
            int value = 0;
            try {
                value = reader.readDataFromFile().size();
            } catch (IOException e) {
                System.err.println("bad reader");
            }
            tableNames.put(table.getName(), value);
        }
        return true;
    }

    public MultiFileHashMap() {
        fileMapPath = System.getProperty("fizteh.db.dir");
        if (fileMapPath == null) {
            System.exit(1);
        }
        File parentDir = new File(fileMapPath);
        if (!parentDir.exists() || !parentDir.isDirectory()) {
            System.exit(1);
        }
        initBase();
    }

    public static void main(String[] args) {
        MultiFileHashMap multiFileHashMap = new MultiFileHashMap();
        try {
            MultiLauncher launcher = new MultiLauncher(multiFileHashMap.tableNames, multiFileHashMap.fileMapPath);
            if (!launcher.launch(args)) {
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.print(e.getMessage());
            System.exit(1);
        }
    }
}

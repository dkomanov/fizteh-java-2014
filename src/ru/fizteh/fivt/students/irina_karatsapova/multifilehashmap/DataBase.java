package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.LoadTable;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.Table;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.utils.DataBaseException;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.utils.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DataBase {
    public static File root;
    public static Map<String, Integer> tables = new HashMap<String, Integer>();

    public static void init() {
        try {
            if (System.getProperty(Main.mainDir) == null) {
                throw new DataBaseException("Path to the database is not set up. Use -D" + Main.mainDir + "=...");
            }
            root = new File(Utils.mainDir());
            check();
            load();
        } catch (DataBaseException e) {
            System.err.println("Incorrect database: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void check() throws DataBaseException {
        if (!root.exists()) {
            throw new DataBaseException("Main directory does not exist");
        }

        for (File file: root.listFiles()) {
            if (!file.isDirectory()) {
                throw new DataBaseException("Contains files instead of directories");
            }
        }
    }

    private static void load() throws DataBaseException {
        try {
            for (File file : root.listFiles()) {
                String tableName = file.getName().toString();
                File inf = file.toPath().resolve("inf.txt").toFile();
                if (!inf.exists()) {
                    LoadTable.start(Utils.makePathAbsolute(tableName));
                    int valuesNumber = Table.countValues();
                    initInf(tableName, valuesNumber);
                }
                int valuesNumber;
                try (DataInputStream inStream = new DataInputStream(new FileInputStream(inf))) {
                    valuesNumber = inStream.readInt();
                }
                tables.put(tableName, valuesNumber);
            }
        } catch (Exception e) {
            throw new DataBaseException("Error while loading the number of values");
        }
    }

    public static void initInf(String tableName, int valuesNumber) throws Exception {
        File inf = Utils.makePathAbsolute(tableName + "/inf.txt");
        inf.createNewFile();
        try (DataOutputStream outStream = new DataOutputStream(new FileOutputStream(inf))) {
            outStream.writeInt(valuesNumber);
            outStream.flush();
        }
    }

    public static void saveInf(File file) throws IOException {
        File inf = file.toPath().resolve("inf.txt").toFile();
        try (DataOutputStream outStream = new DataOutputStream(new FileOutputStream(inf))) {
            Integer valuesNumber = tables.get(file.getName());
            outStream.writeInt(valuesNumber);
        }
    }

    public static void increaseValuesNumber(String file) {
        Integer prevValue = tables.get(file);
        tables.put(file, prevValue + 1);
    }

    public static void decreaseValuesNumber(String file) {
        Integer prevValue = tables.get(file);
        tables.put(file, prevValue - 1);
    }
}

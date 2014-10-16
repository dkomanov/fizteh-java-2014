package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.LoadTable;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.Table;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DataBase {
    public static File root;
    public static Map<String, Integer> tables = new HashMap<String, Integer>();

    public static void init(File rootFile) {
        root = rootFile;
        try {
            check();
            load();
        } catch (Exception e) {
            System.err.println("Incorrect database: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void check() throws Exception {
        if (!root.exists()) {
            throw new Exception("Main directory does not exist");
        }
        for (File file: root.listFiles()) {
            if (!file.isDirectory()) {
                throw new Exception("Contains files instead of directories");
            }
        }
    }

    private static void load() throws Exception {
        try {
            for (File file : root.listFiles()) {
                String tableName = file.getName().toString();
                File inf = file.toPath().resolve("inf.txt").toFile();
                if (!inf.exists()) {
                    LoadTable.start(Utils.makePathAbsolute(tableName));
                    int valuesNumber = Table.countValues();
                    initInf(tableName, valuesNumber);
                }
                DataInputStream inStream = new DataInputStream(new FileInputStream(inf));
                Integer valuesNumber = inStream.readInt();
                inStream.close();
                tables.put(tableName, valuesNumber);
            }
        } catch (Exception e) {
            throw new Exception("Error while loading the number of values");
        }
    }

    public static void initInf(String tableName, int valuesNumber) throws Exception {
        File inf = Utils.makePathAbsolute(tableName + "/inf.txt");
        inf.createNewFile();
        DataOutputStream outStream = new DataOutputStream(new FileOutputStream(inf));
        outStream.writeInt(valuesNumber);
        outStream.flush();
        outStream.close();
    }

    public static void saveInf(File file) throws Exception {
        File inf = file.toPath().resolve("inf.txt").toFile();
        DataOutputStream outStream = new DataOutputStream(new FileOutputStream(inf));
        Integer valuesNumber = tables.get(file.getName());
        outStream.writeInt(valuesNumber);
        outStream.close();
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

package ru.fizteh.fivt.students.isalysultan.MultiFileHashMap;

import java.io.IOException;

public class Command {

    public static void create(RootDirectory direct, String tableName)
            throws IOException {
        if (!direct.tableList.containsKey(tableName)) {
            System.out.println("tablename exists");
            return;
        }
        Table newTable = new Table(direct, tableName);
        direct.TableInizial(newTable, tableName);
    }

    public static void use(RootDirectory direct, String tableName)
            throws IOException {
        if (!direct.tableList.containsKey(tableName)) {
            System.out.println("tablename not exists");
            return;
        }
        direct.use(tableName);
    }

    public static void drop(RootDirectory direct, String tableName) {
        direct.drop(tableName);
    }

    public static void show(RootDirectory direct) {
        direct.showTables();
    }
}

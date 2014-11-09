package ru.fizteh.fivt.students.isalysultan.MultiFileHashMap;

import java.io.IOException;

public class Command {

    public static void create(RootDirectory direct, String tableName)
            throws IOException {
        if (direct.checkTableExist(tableName)) {
            System.out.println("tablename exists");
            return;
        }
        Table newTable = new Table(direct, tableName);
        direct.tableInitialization(newTable, tableName);
    }

    public static void use(RootDirectory direct, String tableName,
            boolean notForShowTables) throws IOException {
        if (!direct.checkTableExist(tableName)) {
            if (notForShowTables) {
                System.out.println("tablename not exists");
            }
            return;
        }
        direct.use(tableName, notForShowTables);
    }

    public static void drop(RootDirectory direct, String tableName) {
        direct.drop(tableName);
    }

    public static void show(RootDirectory direct) throws IOException {
        direct.showTables();
    }
}

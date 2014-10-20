package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class DropCommand extends DbCommand {
    public DropCommand(final String[] args) {
        super("drop", args);
        NUM_OF_ARGS = 1;
        checkArgs();
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException {
        String tableToDrop = args[0];
        Set<String> tableNames = dbManager.getTableNames();
        if (!tableNames.contains(tableToDrop)) {
            msg = new String(tableToDrop + " not exists");
        } else {
            tableNames.remove(tableToDrop);
            TableManager currentTable = dbManager.getCurrentTable();
            if (currentTable != null && currentTable.getTableName().equals(tableToDrop)) {
                dbManager.forgetCurrentTable();
            }
            drop(dbManager.getTablePath(tableToDrop));
            msg = new String("dropped");
        }
    }

    public void drop(final Path tablePath) throws IOException {
        File[] dirs = new File(tablePath.toString()).listFiles();
        for (File dir : dirs) {
            deleteDir(dir);
        }
        Files.delete(tablePath);
    }

    public void deleteDir(final File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            file.delete();
        }
        dir.delete();
    }
}

package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.newJUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Дмитрий on 02.11.2014.
 */
public class CurrentTableProvider implements  TableProvider{

    public Path dbDirectory = new File(System.getProperty("fizteh.db.dir")).toPath();
    public Map<String, CurrentTable> tables = new HashMap<>();

    private void checkTableName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name is null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Table name is empty");
        }
    }

    @Override
    public Table createTable(String name) {
        checkTableName(name);
        String directory = dbDirectory + File.separator + name;
        File tableDirectory = new File(directory);
        if (tableDirectory.exists() || tables.containsKey(name)) {
            System.out.println(name + " exists");
            return null;
        }
        try {
            Files.createDirectory(tableDirectory.toPath());
        } catch(IOException e) {
            System.err.println("Can't create directory: " + directory);
            return null;
        }
        tables.put(name, new CurrentTable(directory));
        System.out.println("created");
        return tables.get(name);
    }

    @Override
    public Table getTable(String name) {
        checkTableName(name);
        if (!tables.containsKey(name)) {
            return null;
        }
        return tables.get(name);
    }

    @Override
    public void removeTable(String name) {
        checkTableName(name);
        String directory = dbDirectory + File.separator + name;
        File tableDirectory = new File(directory);
        if (!tableDirectory.exists() || !tables.containsKey(name)) {
            throw new IllegalArgumentException("table doesn't exist");
        }
        CurrentTable table = tables.remove(name);
        table.deleteFiles(name, true);
        //table.changedFiles.put(table.whereToSave(args[0]).getKey(), 0);
    }

}

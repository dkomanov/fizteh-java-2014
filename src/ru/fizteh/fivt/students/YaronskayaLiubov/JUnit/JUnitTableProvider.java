package ru.fizteh.fivt.students.YaronskayaLiubov.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created by luba_yaronskaya on 26.10.14.
 */
public class JUnitTableProvider implements TableProvider {
    public String dbDir;
    protected HashMap<String, JUnitTable> tables;

    protected JUnitTableProvider(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("directory name is null");
        }
        this.dbDir = dir;
        if (!Files.exists(Paths.get(dbDir))) {
            try {
                Files.createDirectory(Paths.get(dbDir));
            } catch (IOException e) {
                throw new IllegalArgumentException(dbDir + " illegal name of directory");
            }
        }

        tables = new HashMap<String, JUnitTable>();
        String[] tableNames = new File(dbDir).list();
        for (String s : tableNames) {
            Path tableName = Paths.get(dbDir).resolve(s);
            if (Files.isDirectory(tableName)) {
                tables.put(s, new JUnitTable(tableName.toString()));
            }
        }
    }

    @Override
    public Table getTable(String name) {
        CheckParameters.checkTableName(name);
        return tables.get(name);
    }

    @Override
    public Table createTable(String name) throws IllegalArgumentException {
        CheckParameters.checkTableName(name);
        JUnitTable newTable = new JUnitTable(dbDir + File.separator + name);
        if (tables.get(name) == null) {
            tables.put(name, newTable);
            return newTable;
        }
        return null;
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        CheckParameters.checkTableName(name);
        if (tables.remove(name) == null) {
            throw new IllegalStateException("table '" + name + "' does not exist");
        }
        try {
            FileMap.fileDelete(new File(Paths.get(dbDir).resolve(name).toString()));
        } catch (NullPointerException e) {
            //do something?
        }
    }

    public HashMap<String, JUnitTable> getTables() {
        return tables;
    }
}

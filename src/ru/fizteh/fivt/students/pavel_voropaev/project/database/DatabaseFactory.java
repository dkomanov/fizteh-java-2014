package ru.fizteh.fivt.students.pavel_voropaev.project.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProviderFactory;

public class DatabaseFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        TableProvider retVal = null;

        Path dirPath = Paths.get(dir);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("Cannot create database "
                        + dirPath.toString() + e.getMessage(), e);
            }
        }
        retVal = new Database(dir);
        return retVal;
    }
}

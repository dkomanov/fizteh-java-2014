package ru.fizteh.fivt.students.AliakseiSemchankau.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by Aliaksei Semchankau on 09.11.2014.
 */
public class DatabaseProvider implements TableProvider {

    Path pathDatabase;
    HashMap<String, DatabaseTable> referenceToTableInfo;
    public Path currentTable;
    public String currentTableName;

    public DatabaseProvider(String dir) {

        currentTable = null;
        currentTableName = null;
        pathDatabase = Paths.get(System.getProperty("user.dir")).resolve(dir);

        if (!Files.exists(pathDatabase)) {
            try {
                Files.createDirectory(pathDatabase);
            } catch (IOException ioexc) {
                throw new DatabaseException("can't create " + pathDatabase.toString());
            }
        }

        if (!Files.isDirectory(pathDatabase)) {
            throw new DatabaseException(pathDatabase + " isn't a direction");
        }

        DirectoryStream<Path> listOfDirs;

        try {
            listOfDirs = Files.newDirectoryStream(pathDatabase);
        } catch (IOException ioexc) {
            throw new DatabaseException(pathDatabase + ": can't make a list of directories");
        }

        referenceToTableInfo = new HashMap<String, DatabaseTable>();

        for (Path innerTable : listOfDirs) {

            if (!Files.isDirectory(innerTable)) {
                throw new DatabaseException(innerTable + ": isn't a directiion");
            }
            String tableName = Difference.difference(pathDatabase.toString(), innerTable.toString());
            referenceToTableInfo.put(tableName, new DatabaseTable(innerTable, pathDatabase));
            referenceToTableInfo.get(tableName).tableName = tableName;
            referenceToTableInfo.get(tableName).openTable();

        }
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name of table cannot be a null");
        }
        if (referenceToTableInfo.get(name) == null) {
           return null;
        }

        currentTable = referenceToTableInfo.get(name).pathToTable;
        currentTableName = name;

        return referenceToTableInfo.get(name);
    }

    @Override
    public Table createTable(String name) {

        if (name == null) {
            throw new IllegalArgumentException("you can't create table without name");
        }

        if (referenceToTableInfo.get(name) != null) {
            return null;
        }

        try {
            Path pathToTable = Paths.get(pathDatabase.toString()).resolve(name);
            referenceToTableInfo.put(name, new DatabaseTable(pathToTable, pathDatabase));
            referenceToTableInfo.get(name).tableName = name;
        } catch (IllegalArgumentException iaexc) {
            throw new IllegalArgumentException(name + " is impossible name for creating a table");
        }

        referenceToTableInfo.get(name).writeTable();

        return referenceToTableInfo.get(name);

    }

    @Override
    public void removeTable(String name) {

        if (name == null) {
            throw new IllegalArgumentException("name of removing table can't be a null");
        }

        if (currentTableName == name) {
            currentTable = null;
            currentTableName = null;
        }

        if (referenceToTableInfo.get(name) == null) {
            throw new IllegalStateException(name + " not exists");
        }

        if (currentTableName.equals(name)) {
            currentTable = null;
            currentTableName = null;
        }

        Path pathToRemovingTable = referenceToTableInfo.get(name).pathToTable;

        if (Files.exists(pathToRemovingTable)) {
            DeleteFunctions.deleteTable(pathToRemovingTable);
        }
        referenceToTableInfo.remove(name);
    }

}

package ru.fizteh.fivt.students.AliakseiSemchankau.parallel2;

import ru.fizteh.fivt.storage.structured.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Aliaksei Semchankau on 09.11.2014.
 */
public class DatabaseProvider implements TableProvider {

    Path pathDatabase;
    HashMap<String, DatabaseTable> referenceToTableInfo;
    public Path currentTable;
    public String currentTableName;
    SerializeFunctions serializer = new SerializeFunctions();
    ReentrantReadWriteLock providerLock = new ReentrantReadWriteLock(true);

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

        providerLock.readLock().lock();
        try {
            if (name == null) {
                throw new IllegalArgumentException("name of table cannot be a null");
            }
            if (!referenceToTableInfo.containsKey(name)) {
                return null;
            }

            currentTable = referenceToTableInfo.get(name).pathToTable;
            currentTableName = name;

            return referenceToTableInfo.get(name);
        } finally {
            providerLock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {

        if (name == null) {
            throw new IllegalArgumentException("you can't create table without name");
        }

        providerLock.writeLock().lock();

        try {

            if (referenceToTableInfo.containsKey(name)) {
                return null;
            }

            try {
                Path pathToTable = Paths.get(pathDatabase.toString()).resolve(name);
                referenceToTableInfo.put(name, new DatabaseTable(pathToTable, pathDatabase, columnTypes));
                referenceToTableInfo.get(name).tableName = name;
            } catch (IllegalArgumentException iaexc) {
                throw new IllegalArgumentException(name + " is impossible name for creating a table");
            }

            referenceToTableInfo.get(name).writeTable();

            return referenceToTableInfo.get(name);
        } finally {
            providerLock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) {

        if (name == null) {
            throw new IllegalArgumentException("name of removing table can't be a null");
        }

        providerLock.writeLock().lock();

        try {

            if (referenceToTableInfo.get(name) == null) {
                throw new IllegalStateException(name + " not exists");
            }

            if (currentTableName != null && currentTableName.equals(name)) {
                currentTable = null;
                currentTableName = null;
            }

            Path pathToRemovingTable = referenceToTableInfo.get(name).pathToTable;

            if (Files.exists(pathToRemovingTable)) {
                DeleteFunctions.deleteTable(pathToRemovingTable);
            }
            referenceToTableInfo.remove(name);
        } finally {
            providerLock.writeLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return serializer.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {

        if (table == null) {
            throw new ColumnFormatException("table for serialize is null");
        }

        if (value == null) {
            throw new ColumnFormatException("value for serialize is null");
        }

        try {
            return serializer.serialize(table, value);
        } catch (ParseException pexc) {
            throw new DatabaseException("failed to serialize storeable");
        }
    }

    @Override
    public Storeable createFor(Table table) {
        List<Object> valuesForStoreable = new ArrayList();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            valuesForStoreable.add(null);
        }
        return new DatabaseStoreable(valuesForStoreable);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        List<Object> objectValues = new ArrayList<>(values);
        if (objectValues.size() != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("values.size isn't equal to signature.size of " + table.getName());
        }
        for (int i = 0; i < values.size(); ++i) {
            if (objectValues.get(i) == null) {
                continue;
            }
            if (objectValues.get(i).getClass() != table.getColumnType(i)) {
                throw new ColumnFormatException(objectValues.get(i).getClass() + " != " + table.getColumnType(i));
            }
        }
        return new DatabaseStoreable(objectValues);
    }

    @Override
    public List<String> getTableNames() {
        List<String> tableNames = new LinkedList<>();
        for (String curTable : referenceToTableInfo.keySet()) {
            tableNames.add(curTable);
        }
        return tableNames;
    }

}

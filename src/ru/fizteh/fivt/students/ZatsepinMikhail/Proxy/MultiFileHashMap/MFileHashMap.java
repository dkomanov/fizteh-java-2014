package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage.Serializator;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.AbstractStoreable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.shell.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MFileHashMap implements TableProvider, AutoCloseable {
    private String dataBaseDirectory;
    private HashMap<String, FileMap> tables;
    private FileMap currentTable;
    private ReentrantReadWriteLock lockForCreateAndGet;
    private boolean isClosed;

    public MFileHashMap(String newDirectory) {
        dataBaseDirectory = newDirectory;
        tables = new HashMap<>();
        lockForCreateAndGet = new ReentrantReadWriteLock();
        isClosed = false;
        init();
    }

    private void assertNotClosed() throws IllegalStateException {
        if (isClosed) {
            throw new IllegalStateException("table provider is closed");
        }
    }

    @Override
    public void close() throws Exception {
        assertNotClosed();
        isClosed = true;
        for (FileMap oneTable : tables.values()) {
            oneTable.close();
        }
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        assertNotClosed();
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        lockForCreateAndGet.readLock().lock();
        FileMap returnValue;
        if (tables.containsKey(name)) {
            returnValue = tables.get(name);
        } else {
            returnValue = null;
        }
        if (returnValue.checkIfClosed()) {
            returnValue = new FileMap(returnValue);

        }
        lockForCreateAndGet.readLock().unlock();
        return returnValue;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException, IllegalArgumentException {
        assertNotClosed();
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("null argument");
        }
        TypesUtils.checkTypes(columnTypes);

        lockForCreateAndGet.writeLock().lock();
        Table returnValue;
        if (tables.containsKey(name)) {
            returnValue = null;
        } else {
            Path pathOfNewTable = Paths.get(dataBaseDirectory, name);
            Path pathOfNewTableSignatureFile = Paths.get(dataBaseDirectory, name, "signature.tsv");
            if (Files.exists(pathOfNewTable) & Files.isDirectory(pathOfNewTable)) {
                lockForCreateAndGet.writeLock().unlock();
                throw new IllegalArgumentException("this directory already exists");
            }
            try {
                Files.createDirectory(pathOfNewTable);
                Files.createFile(pathOfNewTableSignatureFile);
                try (FileWriter fileOut = new FileWriter(pathOfNewTableSignatureFile.toString())) {
                    fileOut.write(TypesUtils.toFileSignature(columnTypes));
                }
                FileMap newTable = new FileMap(pathOfNewTable.toString(), columnTypes, this);
                tables.put(name, newTable);
                returnValue = newTable;
            } catch (IOException e) {
                lockForCreateAndGet.writeLock().unlock();
                throw new IOException();
            }
        }
        lockForCreateAndGet.writeLock().unlock();
        return returnValue;
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException, IOException {
        assertNotClosed();
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        lockForCreateAndGet.writeLock().lock();
        if (tables.containsKey(name)) {
            Path pathForRemoveTable = Paths.get(dataBaseDirectory, name);
            tables.remove(name);
            currentTable = null;
            FileUtils.rmdir(pathForRemoveTable);
            lockForCreateAndGet.writeLock().unlock();
        } else {
            lockForCreateAndGet.writeLock().unlock();
            throw new IllegalStateException("table \'" + name + "\' doesn't exist");
        }
    }

    @Override
    public Storeable createFor(Table table) {
        assertNotClosed();
        Object[] startValues = new Object[table.getColumnsCount()];
        return new AbstractStoreable(startValues, table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        assertNotClosed();
        if (table.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException("number of types");
        }
        List<Object> objValues = new ArrayList<>(values);
        List<Class<?>> typeList = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (objValues.get(i).getClass() != (table.getColumnType(i))) {
                throw new ColumnFormatException("mismatch column type");
            }
            typeList.add(table.getColumnType(i));
        }
        TypesUtils.checkNewStorableValue(typeList, objValues);
        return new AbstractStoreable(objValues.toArray(), table);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        assertNotClosed();
        if (table.getColumnsCount() != TypesUtils.getSizeOfStoreable(value)) {
            throw new ColumnFormatException("wrong size");
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (!table.getColumnType(i).equals(value.getColumnAt(i).getClass())) {
                throw new ColumnFormatException("need: " + table.getColumnType(i)
                    + ", but got:" + value.getColumnAt(i).getClass());
            }
        }
        return Serializator.serialize(table, value);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        assertNotClosed();
        return Serializator.deserialize(table, value);
    }

    @Override
    public List<String> getTableNames() {
        assertNotClosed();
        List<String> result = new ArrayList<>();
        Collection<FileMap> filemaps = tables.values();
        for (FileMap oneTable : filemaps) {
            result.add(oneTable.getName());
        }
        return result;
    }


    public void showTables() {
        assertNotClosed();
        Set<Entry<String, FileMap>> pairSet = tables.entrySet();
        for (Entry<String, FileMap> oneTable: pairSet) {
            System.out.println(oneTable.getKey() + " "
                + oneTable.getValue().size());
        }
    }

    public void setCurrentTable(FileMap newCurrentTable) {
        currentTable = newCurrentTable;
    }

    public FileMap getCurrentTable() {
        return currentTable;
    }

    public boolean init() {
        String[] listOfFiles = new File(dataBaseDirectory).list();
        for (String oneFile: listOfFiles) {
            Path oneTablePath = Paths.get(dataBaseDirectory, oneFile);
            Path oneTableSignaturePath = Paths.get(dataBaseDirectory, oneFile, "signature.tsv");
            if (Files.isDirectory(oneTablePath) & Files.exists(oneTableSignaturePath)) {
                try (Scanner input = new Scanner(oneTableSignaturePath)) {
                    String[] types;
                    if (input.hasNext()) {
                        types = input.nextLine().trim().split("\\s+");
                        List<Class<?>> newTypeList = TypesUtils.toTypeList(types);
                        if (newTypeList != null) {
                            tables.put(oneFile, new FileMap(oneTablePath.toString(), newTypeList, this));
                        }
                    }
                } catch (FileNotFoundException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
            }
        }
        boolean allRight = true;
        Set<Entry<String, FileMap>> pairSet = tables.entrySet();
        for (Entry<String, FileMap> oneFileMap: pairSet) {
            if  (!oneFileMap.getValue().init()) {
                allRight = false;
            }
        }
        return allRight;
    }
}

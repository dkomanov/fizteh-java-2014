package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage.AbstractStoreable;
import ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage.TypesUtils;
import ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage.Serializator;
import ru.fizteh.fivt.students.ZatsepinMikhail.shell.FileUtils;

public class MFileHashMap implements TableProvider {
    private String dataBaseDirectory;
    private HashMap<String, FileMap> tables;
    private FileMap currentTable;
    public MFileHashMap(String newDirectory) {
        dataBaseDirectory = newDirectory;
        tables = new HashMap<>();
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (tables.containsKey(name)) {
            return tables.get(name);
        } else {
            return null;
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException, IllegalArgumentException {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (!TypesUtils.checkTypes(columnTypes)) {
            throw new IllegalArgumentException("wrong type columns");
        }
        if (tables.containsKey(name)) {
            return null;
        } else {
            Path pathOfNewTable = Paths.get(dataBaseDirectory, name);
            Path pathOfNewTableSignatureFile = Paths.get(dataBaseDirectory, name, "signature.tsv");
            try {
                Files.createDirectory(pathOfNewTable);
                Files.createFile(pathOfNewTableSignatureFile);
                try (FileWriter fout = new FileWriter(pathOfNewTableSignatureFile.toString())) {
                    fout.write(TypesUtils.toFileSignature(columnTypes));
                }
                FileMap newTable = new FileMap(pathOfNewTable.toString(), columnTypes, this);
                tables.put(name, newTable);
                return newTable;
            } catch (IOException e) {
                throw new IOException();
            }
        }
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException, IOException {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (tables.containsKey(name)) {
            Path pathForRemoveTable = Paths.get(dataBaseDirectory, name);
            tables.remove(name);
            FileUtils.rmdir(pathForRemoveTable);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Storeable createFor(Table table) {
        Object[] startValues = new Object[table.getColumnsCount()];
        return new AbstractStoreable(startValues, table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (TypesUtils.canonicalTypes.size() != values.size()) {
            throw new IndexOutOfBoundsException("number of types");
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (!table.getColumnType(i).equals(values.get(i).getClass())) {
                throw new ColumnFormatException("types mismatch");
            }
        }
        return new AbstractStoreable(values.toArray(), table);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (!table.getColumnType(i).getClass().equals(value.getColumnAt(i).getClass())) {
                throw new ColumnFormatException("need: " + table.getColumnType(i).getClass()
                    + ", but got:" + value.getColumnAt(i).getClass());
            }
        }
        return Serializator.serialize(table, value);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return Serializator.deserialize(table, value);
    }

    public void showTables() {
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

    public FileMap findTableByName(String requiredFileMapName) {
        return tables.get(requiredFileMapName);
    }
}

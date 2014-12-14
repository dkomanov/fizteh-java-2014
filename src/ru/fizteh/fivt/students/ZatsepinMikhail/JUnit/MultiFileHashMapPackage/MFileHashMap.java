package ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.MultiFileHashMapPackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.FileMapPackage.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.ShellPackage.FileUtils;

public class MFileHashMap implements TableProvider {
    private String dataBaseDirectory;
    private HashMap<String, FileMap> tables;
    private FileMap currentTable;

    public MFileHashMap(String newDirectory) throws IOException {
        dataBaseDirectory = newDirectory;
        tables = new HashMap<>();
        init();
    }

    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return tables.get(name);
        } else {
            return null;
        }
    }

    public Table createTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return null;
        } else {
            Path pathOfNewTable = Paths.get(dataBaseDirectory, name);
            try {
                Files.createDirectory(pathOfNewTable);
                FileMap newTable = new FileMap(pathOfNewTable.toString());
                tables.put(name, newTable);
                return newTable;
            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
        }
    }

    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            Path pathForRemoveTable = Paths.get(dataBaseDirectory, name);
            tables.remove(name);
            if (!FileUtils.rmdir(pathForRemoveTable)) {
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalStateException();
        }
    }


    public void addTable(String tableName, FileMap newFileMap) {
        tables.put(tableName, newFileMap);
    }

    public void dropTable(String tableName) {
        if (tables.get(tableName).equals(currentTable)) {
            currentTable = null;
        }
        tables.remove(tableName);
    }

    public String getDataBaseDirectory() {
        return dataBaseDirectory;
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

    public boolean init() throws IOException {
        String[] listOfFiles = new File(dataBaseDirectory).list();
        for (String oneFile: listOfFiles) {
            Path oneTablePath = Paths.get(dataBaseDirectory, oneFile);
            if (Files.isDirectory(oneTablePath)) {
                tables.put(oneFile, new FileMap(oneTablePath.toString()));
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

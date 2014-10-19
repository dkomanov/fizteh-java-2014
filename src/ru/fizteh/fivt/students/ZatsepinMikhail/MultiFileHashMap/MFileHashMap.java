package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.FileMap;

/**
 * Created by mikhail on 16.10.14.
 */
public class MFileHashMap {
    private String dataBaseDirectory;
    private HashMap<String, FileMap> tables;
    private FileMap currentTable;
    public MFileHashMap(String newDirectory) {
        dataBaseDirectory = newDirectory;
        tables = new HashMap<>();
    }

    public String getDataBaseDirectory() {
        return dataBaseDirectory;
    }

    public int getNumberOfTables() {
        return tables.size();
    }

    public void printTables() {
        Set<Entry<String, FileMap>> pairSet = tables.entrySet();
        for (Entry<String, FileMap> oneTable: pairSet) {
            System.out.println(oneTable.getValue().getDirectoryOfTable());
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

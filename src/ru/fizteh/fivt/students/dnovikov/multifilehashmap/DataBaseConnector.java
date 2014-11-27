package ru.fizteh.fivt.students.dnovikov.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class DataBaseConnector {
    private Table currentTable = null;
    private Path rootDirectory;

    private ArrayList<Table> tables = new ArrayList<>();
    private Map<String, Table> tableNames = new TreeMap<>();

    public DataBaseConnector() throws NullPointerException, LoadOrSaveException {
        String directoryPath = System.getProperty("fizteh.db.dir");
        if (directoryPath == null) {
            throw new NullPointerException("database directory not set");
        } else {
            rootDirectory = new File(directoryPath).toPath();
        }
        loadTables();
    }

    public Path getRootDirectory() {
        return rootDirectory;
    }

    public Table getCurrentTable() {
        return currentTable;
    }

    public Table createTable(String name) throws IOException, LoadOrSaveException {

        if (tableNames.get(name) != null) {
            System.out.println(name + " exists");
            return null;
        } else {
            Path newDir = new File(rootDirectory + File.separator + name).toPath();
            try {
                Files.createDirectory(newDir);
            } catch (IOException e) {
                throw new IOException("can't create directory :" + newDir.toAbsolutePath());
            }
            Table table = new Table(name, this);
            tableNames.put(name, table);
            tables.add(table);
            System.out.println("created");
            return table;
        }

    }

    public void removeTable(String name) throws IOException, LoadOrSaveException {
        Table table = tableNames.get(name);
        if (table == null) {
            System.out.println(name + " not exists");
        } else {
            if (table.equals(currentTable)) {
                currentTable = null;
            }
            tableNames.remove(name);
            table.drop();
            tables.remove(table);
            System.out.println("dropped");
        }
    }

    public void showTable() {
        String[] tableNames = new String[tables.size()];
        int[] sizes = new int[tables.size()];
        for (int i = 0; i < tableNames.length; i++) {
            Table table = tables.get(i);
            tableNames[i] = table.getTableName();
            sizes[i] = table.size();
        }
        for (int i = 0; i < tableNames.length; i++) {
            System.out.println(tableNames[i] + " " + sizes[i]);
        }
    }

    public void useTable(String name) throws IOException, LoadOrSaveException {
        if (currentTable != null) {
            currentTable.save();
        }
        if (tableNames.get(name) != null) {
            currentTable = tableNames.get(name);
            System.out.println("using " + name);
        } else {
            System.out.println(name + " not exists");
        }
    }

    public void loadTables() throws LoadOrSaveException {
        if (rootDirectory.toFile().exists() && rootDirectory.toFile().isDirectory()) {
            File[] foldersInRoot = rootDirectory.toFile().listFiles();
            for (File folder : foldersInRoot) {
                if (!folder.isDirectory()) {
                    throw new LoadOrSaveException("file '" + folder.getName() + "' in root directory");
                }
            }
            for (File folder : foldersInRoot) {
                Table currTable = new Table(folder.getName(), this);
                tables.add(currTable);
                tableNames.put(folder.getName(), currTable);
            }
        } else if (!rootDirectory.toFile().exists()) {
            throw new LoadOrSaveException("root directory '" + rootDirectory.getFileName() + "' not found");
        } else {
            throw new LoadOrSaveException("root directory '" + rootDirectory.getFileName() + "' is not directory");
        }
    }

    public void saveTable() throws IOException, LoadOrSaveException {
        if (currentTable != null) {
            currentTable.save();
        }
    }
}

package ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Database {
    private Map<String, Integer> database;
    private final Path directory;
    private MultiFileTable workingTable = null;

    public Database(String directory) throws IOException {
        database = new HashMap<>();

        this.directory = Paths.get(directory);
        if (!Files.exists(this.directory)) {
            Files.createDirectory(this.directory);
        }
        load();
    }
    
    public MultiFileTable getWorkingTable() {
        return workingTable;
    }
    
    private void load() throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path pathToTable : directoryStream) {
                String tableName = pathToTable.getFileName().toString();
                if (Files.isDirectory(pathToTable)) {
                    workingTable = new MultiFileTable(directory, tableName);
                    this.close();
                } else {
                    throw new IOException(directory + "contains improper files");
                }
            }
        }
    }

    public boolean createTable(String tableName)
            throws IllegalArgumentException, IOException {
        if (tableName == null) {
            throw new IllegalArgumentException("Null cannot be an argument");
        }
        String[] reservedCharacters = {"\\", "/", ":", "*", "?", "\"", "<", ">", "|", "%"};
        for (String character : reservedCharacters) {
            if (tableName.contains(character)) {
                throw new IllegalArgumentException("Table name contains invalid characters");
            }
        }

        if (database.containsKey(tableName)) {
            return false;
        }
        
        if (Files.exists(directory.resolve(tableName))) {
            throw new IllegalArgumentException("Directory " + tableName + " is already exists.");
        }
        Files.createDirectory(directory.resolve(tableName));
        database.put(tableName, 0);    

        return true;
    }

    public boolean dropTable(String tableName) throws IllegalArgumentException,
            IllegalStateException, IOException {
        if (!database.containsKey(tableName)) {
            return false;
        }
        if (workingTable != null && workingTable.name().equals(tableName)) {
            workingTable = null;
        }

        Utils.rm(directory.resolve(tableName));
        database.remove(tableName);
        return true;
    }

    public MultiFileTable setTable(String tableName) throws IOException {
        if (!database.containsKey(tableName)) {
            return null;
        }
        MultiFileTable retVal = new MultiFileTable(directory, tableName);
        if (retVal != null) {
            this.close();
            workingTable = retVal;
            

        }
        return retVal;
    }

    public String[] list() {
        String[] retVal = new String[database.size()];
        Set<String> tables = database.keySet();
        
        if (workingTable != null) {
            database.put(workingTable.name(), workingTable.size());
        }
       
        Iterator<String> it = tables.iterator();
        for (int i = 0; it.hasNext(); ++i) {
            String key = it.next();
            
            retVal[i] = new StringBuilder().append(key)
                    .append(" ").append(Integer.toString(database.get(key)))
                    .toString();
        }

        return retVal;
    }

    public void close() throws IOException {
        if (workingTable != null) {
            database.put(workingTable.name(), workingTable.size());
            workingTable.close();
        }
        workingTable = null;
    }
    
}

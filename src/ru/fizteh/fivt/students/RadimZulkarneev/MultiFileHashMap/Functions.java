package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Functions {
    private Functions() {
        //
    }

    public static void makeDbFile(final String fileName) throws MapExcept {
        File ctFile = new File(fileName);
        if (ctFile.isDirectory()) {
            throw new MapExcept("It is a directory");
        }
        if (ctFile.exists()) {
            throw new MapExcept("MakeDbFile: File already exist");
        } else {
            try {
                Files.createFile(ctFile.toPath());
            } catch (IOException e) {
                throw new MapExcept("MakeDbFile: some errors");
            }
        }
    }
    public static void makeDbFileHard(final String
            fileName) throws MapExcept {
        File ctFile = new File(fileName);
        try {
            Files.deleteIfExists(ctFile.toPath());
            Files.createFile(ctFile.toPath());
        } catch (IOException e) {
            throw new MapExcept(e.getMessage());
        }
    }
    
    public static Path openDir() throws FileNotFoundException {
        File ctFile = new File(System.getProperty("fizteh.db.dir"));
        if (System.getProperty("fizteh.db.dir").equals(null)) {
            throw new NullPointerException();
        }
        if (!ctFile.exists() || !ctFile.isDirectory()) {
            throw new FileNotFoundException();
        }
        return ctFile.toPath();
    }
    
    public static void rmDir(Path tableName) throws MapExcept, IOException {
        File ct = tableName.toFile();
        if (!ct.exists()) {
            throw new MapExcept(tableName.getFileName() + ": tablename not exists");
        }
        String[] dirs = ct.list();
        for (String currentDir : dirs) {
            String[] curFiles = tableName.resolve(currentDir).toFile().list();
            for (String curFile : curFiles) {
                Files.delete(tableName.resolve(currentDir).resolve(curFile));
            }
            Files.delete(tableName.resolve(currentDir));
        }
        Files.delete(tableName);
        
    }
}

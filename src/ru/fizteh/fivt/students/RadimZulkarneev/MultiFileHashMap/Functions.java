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

    public static void makeDbFile(final String fileName) throws MapException {
        File ctFile = new File(fileName);
        if (ctFile.isDirectory()) {
            throw new MapException("It is a directory");
        }
        if (ctFile.exists()) {
            throw new CreateFileException("");
        } else {
            try {
                Files.createFile(ctFile.toPath());
            } catch (IOException e) {
                throw new MapException("MakeDbFile: some errors");
            }
        }
    }
    
    public static void makeDbFileHard(final String
            fileName) throws MapException {
        File ctFile = new File(fileName);
        try {
            Files.deleteIfExists(ctFile.toPath());
            Files.createFile(ctFile.toPath());
        } catch (IOException e) {
            throw new MapException(e.getMessage());
        }
    }
    
    public static Path openDir() throws IOException {
        File ctFile = new File(System.getProperty("fizteh.db.dir"));
        if (System.getProperty("fizteh.db.dir") == null) {
            throw new NullPointerException();
        }
        if (!ctFile.isDirectory() && ctFile.exists()) {
            throw new FileNotFoundException();
        } 
        if (!ctFile.exists()) {
            Files.createDirectory(ctFile.toPath());
        }
        return ctFile.toPath();
    }
    
    public static void rmDir(Path tableName) throws MapException, IOException {
        File ct = tableName.toFile();
        if (!ct.exists()) {
            throw new MapException(tableName.getFileName() + ": tablename not exists");
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

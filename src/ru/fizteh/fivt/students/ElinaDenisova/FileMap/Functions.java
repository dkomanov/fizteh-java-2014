package ru.fizteh.fivt.students.ElinaDenisova.FileMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public  class Functions {
    public static void makeDbFile( String fileName) throws DataBaseException {
        File ctFile = new File(fileName);
        if (ctFile.isDirectory()) {
            throw new DataBaseException("It is a directory");
        }
        if (ctFile.exists()) {
            throw new DataBaseException("MakeDbFile: File already exist");
        } else {
            try {
                Files.createFile(ctFile.toPath());
            } catch (IOException e) {
                throw new DataBaseException("MakeDbFile: some errors");
            }
        }
    }
    public static void makeDbFileHard( String
            fileName) throws DataBaseException {
        File ctFile = new File(fileName);
        try {
            Files.deleteIfExists(ctFile.toPath());
            Files.createFile(ctFile.toPath());
        } catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}

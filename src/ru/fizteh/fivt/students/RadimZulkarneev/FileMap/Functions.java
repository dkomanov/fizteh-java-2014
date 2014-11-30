package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class Functions {
    private Functions() {
        //
    }

    public static void makeDbFile(final String fileName) throws MapExcept {
        File ctFile = new File(fileName);
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
}

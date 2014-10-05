package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

final public class Functions {
    private Functions() {
        //
    }

    public static void MakeDbFile(final String fileName) throws MapExcept {
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
    public static void MakeDbFileHard(final String
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

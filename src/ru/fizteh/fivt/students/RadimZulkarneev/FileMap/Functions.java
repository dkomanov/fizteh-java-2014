package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

<<<<<<< HEAD
public final class Functions {
=======
final public class Functions {
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
    private Functions() {
        //
    }

<<<<<<< HEAD
    public static void makeDbFile(final String fileName) throws MapExcept {
        File ctFile = new File(fileName);
        if (ctFile.isDirectory()) {
            throw new MapExcept("It is a directory");
        }
=======
    public static void MakeDbFile(final String fileName) throws MapExcept {
        File ctFile = new File(fileName);
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
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
<<<<<<< HEAD
    public static void makeDbFileHard(final String
=======
    public static void MakeDbFileHard(final String
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
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

package ru.fizteh.fivt.students.deserg.junit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by deserg on 01.12.14.
 */
public class Shell {

    public static boolean checkName(String name) {

        return (!name.contains("\\") && !name.contains("/000"));

    }

    public static void delete(Path path) {

        if (!Files.isDirectory(path)) {
            try {
                Files.delete(path);
            } catch (IOException ex) {
                throw new MyException("Error while deleting " + path.toString());
            }
        } else {
            deleteFinal(path);
        }

    }

    private static void deleteFinal(Path path) {

        File curDir = new File(path.toString());
        File[] content = curDir.listFiles();

        if (content != null) {
            for (File item: content) {
                if (item.isFile()) {
                    try {
                        Files.delete(item.toPath());
                    } catch (IOException ex) {
                        throw new MyException("I/O error occurs while removing " + item.toPath().toString());
                    }
                } else {
                    deleteFinal(item.toPath());
                }
            }
        }

        try {
            Files.delete(path);
        } catch (IOException ex) {
            throw new MyException("I/O error occurs while removing " + path.toString());
        }

    }

}

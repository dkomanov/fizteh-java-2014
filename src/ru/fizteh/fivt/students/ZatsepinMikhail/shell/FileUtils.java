package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static boolean mkdir(Path newDirectory) {
        CommandMkdir command = new CommandMkdir();
        String[] args = {"mkdir",
                         newDirectory.toString()
        };
        return command.run(args);
    }

    public static void rmdir(Path newDirectory) throws IOException {
        if (!Files.isDirectory(newDirectory) | !Files.exists(newDirectory)) {
            throw new IllegalArgumentException();
        }
        FileVisitorDelete myFileVisitorDelete = new FileVisitorDelete();
        Files.walkFileTree(newDirectory, myFileVisitorDelete);
    }
}

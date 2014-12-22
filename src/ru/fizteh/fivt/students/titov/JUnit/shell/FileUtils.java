package ru.fizteh.fivt.students.titov.JUnit.shell;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static boolean mkdir(Path newDirectory) {
        MkdirCommand command = new MkdirCommand();
        String[] args = {"mkdir",
                newDirectory.toString()
        };
        return command.run(args);
    }

    public static boolean rmdir(Path newDirectory) {
        RmCommand command = new RmCommand();
        if (!Files.isDirectory(newDirectory) | !Files.exists(newDirectory)) {
            return false;
        }
        String[] args = {"rm",
                "-r",
                newDirectory.toString()
        };
        return command.run(args);
    }
}

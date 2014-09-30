package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandMkDir implements Command {
    private static Path presentWorkingDirectory = Paths.get("").toAbsolutePath().normalize();

    /*private static void makeDirectory(String dirName) throws IOException {
        Path dirAbsolutePath = Paths.get(presentWorkingDirectory.toString(), dirName).toAbsolutePath().normalize();
        System.out.println(dirAbsolutePath.toString());
        try {
            Files.createDirectory(dirAbsolutePath);
        } catch (IOException exception) {
            throw new IOException("cannot create directory \'" + dirName + "\': No such file or directory");
        }
    }*/

    @Override
    public void execute(String... args) throws RuntimeException, IOException {
        String dirName;
        if (args.length < 2) {
            throw new IllegalArgumentException(toString() + ": missing operand");
        } else {
            dirName = args[1];
        }
        Path dirAbsolutePath = Paths.get(presentWorkingDirectory.toString(), dirName).toAbsolutePath().normalize();
        /**/System.out.println(dirAbsolutePath.toString());
        try {
            Files.createDirectory(dirAbsolutePath);
        } catch (IOException e) {
            throw new IOException(toString() + ": cannot create directory \'" + dirName + "\': " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "mkdir";
    }
}

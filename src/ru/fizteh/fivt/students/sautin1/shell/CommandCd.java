package ru.fizteh.fivt.students.sautin1.shell;

import org.omg.SendingContext.RunTime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandCd implements Command {
    private static Path presentWorkingDirectory = Paths.get("").toAbsolutePath().normalize();

    /*private static void changeDirectory(String dirName) throws IOException {
        Path dirAbsolutePath = Paths.get(dirName);
        if (!dirAbsolutePath.isAbsolute()) {
            dirAbsolutePath = Paths.get(presentWorkingDirectory.toString(), dirName).toAbsolutePath().normalize();
        }
        System.out.println(dirAbsolutePath.toString());
        if (Files.exists(dirAbsolutePath) && Files.isDirectory(dirAbsolutePath)) {
            presentWorkingDirectory = dirAbsolutePath;
            System.out.println("Made changes to pwd!");
        } else {
            throw new NoSuchFileException("\'" + dirName + "\': No such file or directory");
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
        Path dirAbsolutePath = Paths.get(dirName);
        if (!dirAbsolutePath.isAbsolute()) {
            dirAbsolutePath = Paths.get(presentWorkingDirectory.toString(), dirName).toAbsolutePath().normalize();
        }
        /**/System.out.println(dirAbsolutePath.toString());
        if (Files.exists(dirAbsolutePath) && Files.isDirectory(dirAbsolutePath)) {
            presentWorkingDirectory = dirAbsolutePath;
            /**/System.out.println("Made changes to pwd!");
        } else {
            throw new NoSuchFileException(toString() + ": \'" + dirName + "\': No such file or directory");
        }
    }

    @Override
    public String toString() {
        return "cd";
    }
}

package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandCd extends Command {

    public CommandCd() {
        minArgNumber = 1;
        commandName = "cd";
    }

    @Override
    public void execute(String... args) throws RuntimeException, IOException {
        if (!enoughArguments(args)) {
            throw new IllegalArgumentException(toString() + ": missing operand");
        }

        String dirName;
        dirName = args[1];

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

}

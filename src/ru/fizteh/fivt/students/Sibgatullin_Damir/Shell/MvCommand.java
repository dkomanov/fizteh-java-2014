package ru.fizteh.fivt.students.Sibgatullin_Damir.Shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class MvCommand implements Commands {
    public void execute(String[] args) throws MyException {
        if (args.length < 3) {
            throw new MyException("mv: not enough arguments");
        }
        if (args.length > 3) {
            throw new MyException("mv: too many arguments");
        }

        Path sourcePath = Shell.currentPath.resolve(args[1]);
        Path destinationPath = Shell.currentPath.resolve(args[2]);

        if (!Files.exists(sourcePath)) {
            throw new MyException("mv: file '" + args[1] + "' does not exist");
        }

        if (Files.exists(destinationPath) && Files.isDirectory(destinationPath)) {
            destinationPath = Paths.get(destinationPath.toString(), sourcePath.getFileName().toString());
        }
        try {
            Files.move(sourcePath, destinationPath);
        } catch (IOException e) {
            throw new MyException("mv: I/O error occurs");
        }
    }

    public String getName() {
        return "mv";
    }
}

package ru.fizteh.fivt.students.Sibgatullin_Damir.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class RmCommand implements Commands {
    void removeNonrecursive(String path) throws MyException {
        try {
            Path newPath = Shell.currentPath.resolve(path).toRealPath();
            if (Files.isDirectory(newPath)) {
                throw new MyException("rm: cannot remove '" + path + "': No such file or directory");
            } else {
                Files.delete(newPath);
            }
        } catch (IOException e) {
            throw new MyException("rm: " + path + ": the file does not exist");
        }
    }

    void removeRecursive(String path) throws MyException {
        try {
            Path newPath = Shell.currentPath.resolve(path).toRealPath();
            if (Files.isDirectory(newPath)) {
                removeRecursiveFinal(newPath);
            } else {
                Files.delete(newPath);
            }
        } catch (IOException e) {
            throw new MyException("rm: cannot remove " + path + ": No such file or directory");
        }
    }

    void removeRecursiveFinal(Path path) throws MyException {
        if (!Files.exists(path)) {
            throw new MyException("rm: cannot remove '" + path.toString() + "': No such file or directory");
        }

        File dir = new File(path.toString());
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new MyException("I/O error occurs while removing " + file.toPath().toString());
                }
            } else {
                removeRecursiveFinal(file.toPath());
            }
        }

        try {
            Files.delete(path);
        } catch (IOException ex) {
            throw new MyException("I/O error occurs while removing " + path.toString());
        }
    }

    public void execute(String[] args) throws MyException {
        if (args.length < 2) {
            throw new MyException("rm: not enough arguments");
        }
        if (args.length > 3) {
            throw new MyException("rm: too many arguments");
        }
        if (args[1].equals("-r") && args.length == 3) {
            removeRecursive(args[2]);
        } else if (args.length == 2) {
            removeNonrecursive(args[1]);
        } else {
            throw new MyException("rm: invalid arguments");
        }
    }

    public String getName() {
        return "rm";
    }
}

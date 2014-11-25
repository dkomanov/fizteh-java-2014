package ru.fizteh.fivt.students.SibgatullinDamir.Shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileAlreadyExistsException;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class MkdirCommand implements Commands {
    public void execute(String[] args) throws MyException {
        try {
            if (args.length == 2) {
                Files.createDirectory(Shell.currentPath.resolve(args[1]));
            } else if (args.length < 2) {
                throw new MyException("mkdir: not enough arguments");
            } else {
                throw new MyException("mkdir: too many arguments");
            }
        } catch (FileAlreadyExistsException e) {
            throw new MyException("mkdir: directory or file already exists");
        } catch (IOException e) {
            throw new MyException("mkdir: directory doesn't exist");
        }

    }

    public String getName() {
        return "mkdir";
    }
}

package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class ChangeDirectoryCommand implements Command {
    private Shell link;

    public ChangeDirectoryCommand(Shell shell) {
        link = shell;
    }

    @Override
    public void executeCommand(final ArrayList<String> arguments) throws Exception {
        if (arguments.isEmpty()) {
            return;
        }
        if (arguments.size() > 1) {
            throw new IllegalArgumentException("too many arguments");
        }
        File targetDirectory = null;
        if (Paths.get(arguments.get(0)).isAbsolute()) {
            targetDirectory = new File(arguments.get(0));
        } else {
            targetDirectory = new File(link.getWorkDirectory(), arguments.get(0));
        }

        if (!targetDirectory.exists()) {
            throw new Exception(arguments.get(0) + ": no such file or directory");
        } else if (!targetDirectory.isDirectory()) {
            throw new Exception(arguments.get(0) + ": not a directory");
        } else {
            link.setWorkDirectory(new File(targetDirectory.getAbsolutePath()));
            try {
                link.setWorkDirectory(new File(targetDirectory.getCanonicalPath()));
            } catch (IOException ioe) {
                throw new Exception("broken path");
            }
        }

    }

    @Override
    public String getName() {
        return "cd";
    }
}

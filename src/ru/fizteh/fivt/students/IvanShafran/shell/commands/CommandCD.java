package ru.fizteh.fivt.students.IvanShafran.shell.commands;

import ru.fizteh.fivt.students.IvanShafran.shell.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class CommandCD extends Command {
    public Shell shellLink;

    public CommandCD(Shell shell) {
        shellLink = shell;
    }

    @Override
    public void execute(ArrayList<String> args) throws Exception {
        if (args.size() == 0) {
            return;
        }

        File nextDirectory;
        nextDirectory
                = new File(getAbsolutePath(shellLink.getWorkingDirectory().getAbsolutePath(), args.get(0)));

        if (!nextDirectory.isDirectory()) {
            throw new Exception(args.get(0) + ": Not a directory");
        } else {
            try {
                shellLink.setWorkingDirectory(new File(nextDirectory.getCanonicalPath()));
            } catch (IOException e) {
                throw new Exception("i/o error");
            }
        }
    }

}

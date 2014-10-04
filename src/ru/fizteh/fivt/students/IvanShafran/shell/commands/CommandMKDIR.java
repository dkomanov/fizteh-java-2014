package ru.fizteh.fivt.students.IvanShafran.shell.commands;

import ru.fizteh.fivt.students.IvanShafran.shell.Shell;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CommandMKDIR extends Command {
    public Shell shellLink;

    public CommandMKDIR(Shell shell) {
        shellLink = shell;
    }

    @Override
    public void checkArgs(ArrayList<String> args) throws Exception {
        if (args.size() == 0) {
            throw new Exception("missing operand");
        }
    }

    @Override
    public void execute(ArrayList<String> args) throws Exception {
        checkArgs(args);

        for (String newDirName : args) {
            Path newDirPath;
            newDirPath =
                    Paths.get(getAbsolutePath(shellLink.getWorkingDirectory().getAbsolutePath(),
                            newDirName, false));

            try {
                Files.createDirectory(newDirPath);
            } catch (FileAlreadyExistsException e) {
                throw new Exception(newDirName + " already exists");
            } catch (SecurityException e) {
                throw new Exception("you haven't got permission to write into directory");
            } catch (IOException e) {
                throw new Exception("you haven't got permission to write into directory");
            }
        }
    }

}

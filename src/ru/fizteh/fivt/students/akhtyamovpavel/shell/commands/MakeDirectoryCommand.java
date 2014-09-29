package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.shell.Shell;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class MakeDirectoryCommand implements Command {
    public Shell link;

    public MakeDirectoryCommand(Shell shell) {
        link = shell;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.isEmpty()) {
            throw new Exception("usage: mkdir directory");
        }
        for (String folderName : arguments) {
            try {
                Path newDirectoryPath = Paths.get(link.getWorkDirectory().getAbsolutePath(), folderName);
                Files.createDirectory(newDirectoryPath);
            } catch (FileAlreadyExistsException e) {
                throw new Exception("mkdir: " + folderName + "already exists");
            } catch (SecurityException se) {
                throw new Exception("mkdir: you haven't got permission to write into directory");
            } catch (IOException ioe) {
                throw new Exception("mkdir: you haven't got permission to write into directory");
            }
        }
    }

    @Override
    public String getName() {
        return "mkdir";
    }
}

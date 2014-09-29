package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class MoveCommand extends FileCommand {
    public MoveCommand(Shell shell) {
        link = shell;
    }

    @Override
    protected void checkArgumentNumberCorrection(ArrayList<String> arguments) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("mv: usage <source> <destination>");
        }
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        checkArgumentNumberCorrection(arguments);
        File sourceFile = getResolvedFile(arguments.get(0));
        File targetFile = getResolvedFile(arguments.get(1));
        if (!targetFile.canWrite()) {
            throw new Exception("mv: permission denied");
        }
        targetFile = Paths.get(targetFile.getAbsolutePath(), sourceFile.getName()).toFile();

        try {
            Files.move(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (DirectoryNotEmptyException de) {
            extraMove(arguments);
        } catch (SecurityException se) {
            throw new Exception("mv: permission denied");
        } catch (IOException ioe) {
            extraMove(arguments);
        }
    }

    @Override
    public String getName() {
        return "mv";
    }

    private void extraMove(ArrayList<String> arguments) throws Exception {
        ArrayList<String> newArguments = new ArrayList<String>();
        newArguments.add("-r");
        newArguments.add(arguments.get(0));
        newArguments.add(arguments.get(1));

        (new CopyCommand(link)).executeCommand(newArguments);

        newArguments.remove(1);
        (new CopyCommand(link)).executeCommand(newArguments);
    }
}

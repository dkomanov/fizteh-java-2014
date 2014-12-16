package ru.fizteh.fivt.students.elina_denisova.shell.commands;

import ru.fizteh.fivt.students.elina_denisova.shell.Shell;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;


public abstract class FileCommand implements Command {
    protected Shell link;

    protected File getResolvedFile(String filePath, boolean checkExistence) throws Exception {
        File targetFile = null;
        if (Paths.get(filePath).isAbsolute()) {
            targetFile = new File(filePath);
        } else {
            targetFile = new File(link.getWorkDirectory(), filePath);
        }
        if (!targetFile.exists() && checkExistence) {
            throw new Exception(filePath + "': No such file or directory");
        }
        return targetFile;
    }

    protected abstract void checkArgumentNumberCorrection(ArrayList<String> arguments) throws Exception;
}

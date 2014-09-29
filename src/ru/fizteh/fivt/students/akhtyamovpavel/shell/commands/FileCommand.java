package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.shell.Shell;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by user1 on 29.09.2014.
 */
public abstract class FileCommand implements Command{
    protected Shell link;

    protected File getResolvedFile(String filePath) throws Exception {
        File targetFile = null;
        if (Paths.get(filePath).isAbsolute()) {
            targetFile = new File(filePath);
        } else {
            targetFile = new File(link.getWorkDirectory(), filePath);
        }
        if (!targetFile.exists()) {
            throw new Exception(getName() + ": '" + filePath + "': No such file or directory");
        }
        return targetFile;
    }

    protected abstract void checkArgumentNumberCorrection(ArrayList<String> arguments);
}

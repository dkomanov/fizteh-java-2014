package ru.fizteh.fivt.students.IvanShafran.shell.commands;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public abstract class Command {
    public abstract void execute(ArrayList<String> args) throws Exception;

    public void checkArgs(ArrayList<String> args) throws Exception {
    }

    public static String getAbsolutePath(String workingDirectory, String path,
                                         boolean checkExisting) throws Exception {
        String resultPath;
        if (!Paths.get(path).isAbsolute()) {
            resultPath = Paths.get(workingDirectory, path).toString();
        } else {
            resultPath = Paths.get(path).toString();
        }

        File resultFile = new File(resultPath);
        if (checkExisting && !resultFile.exists()) {
            throw new Exception(path + ": No such file or directory");
        }
        return resultPath;
    }

    public static String getAbsolutePath(String workingDirectory, String path) throws Exception {
        return getAbsolutePath(workingDirectory, path, true);
    }
}

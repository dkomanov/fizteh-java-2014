package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

import java.io.File;

public class CdCommand extends Command {
    boolean execute(String[] args) {
        if (args.length != maxNumberOfArguements) {
            System.out.println(name + ": Invalid number of arguements");
            return false;
        }
        String path = args[1];
        File newDir = (path.charAt(0) == '/') ? new File(path) : new File(
                Shell.curDir, path);
        if (!newDir.exists()) {
            System.out.println("cd: '" + path + "': No such file or directory");
            return false;
        }
        if (!newDir.isDirectory()) {
            System.out.println(name + ": '" + path + "' is not directory");
            return false;
        }
        Shell.curDir = newDir;
        return true;
    }

    CdCommand() {
        name = "cd";
        maxNumberOfArguements = 2;
    }
}

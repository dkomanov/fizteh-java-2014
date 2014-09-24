package ru.fizteh.fivt.students.VasilevKirill.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kirill on 23.09.2014.
 */
public class CdCommand implements Command {
    @Override
    public void execute(String[] args) throws IOException {
        String path = Shell.currentPath;
        if (args.length < 2) return;
        else if (args[1].equals(".")) return;
        else if (args[1].equals("..")) {
            if (path.equals("C:")) return;
            int lastIndex = path.lastIndexOf(File.separator);
            if (lastIndex < 0) return;
            Shell.currentPath = path.substring(0, lastIndex);
        } else {
            if (args[1].substring(0, 2).equals("C:")) {
                File directory = new File(args[1]);
                if (!directory.exists() || !directory.isDirectory()) {
                    System.out.println("cd: '" + args[1] + "': No such file or directory");
                    return;
                }
                Shell.currentPath = directory.getPath();
            } else {
                File directory = new File(Shell.currentPath + File.separator + args[1]);
                if (!directory.exists() || !directory.isDirectory()) {
                    System.out.println("cd: '" + args[1] + "': No such file or directory");
                    return;
                }
                Shell.currentPath = Shell.currentPath + File.separator + args[1];
            }
        }
    }

    @Override
    public String toString() {
        return "cd";
    }
}

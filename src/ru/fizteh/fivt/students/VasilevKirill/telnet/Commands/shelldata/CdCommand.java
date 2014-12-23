package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kirill on 23.09.2014.
 */
public class CdCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        String path = Shell.currentPath;
        if (args.length != 2) {
            return 1;
        } else if (args[1].equals(".")) {
            return 0;
        } else if (args[1].equals("..")) {
            if (path.equals("C:")) {
                return 0;
            }
            int lastIndex = path.lastIndexOf(File.separator);
            if (lastIndex < 0) {
                return 1;
            }
            Shell.currentPath = path.substring(0, lastIndex);
        } else {
            if (args[1].length() > 2 && args[1].substring(0, 2).equals("C:")) {
                File directory = new File(args[1]);
                if (!directory.exists() || !directory.isDirectory()) {
                    System.out.println("cd: '" + args[1] + "': No such file or directory");
                    return 1;
                }
                Shell.currentPath = directory.getPath();
            } else {
                File directory = new File(Shell.currentPath + File.separator + args[1]);
                if (!directory.exists() || !directory.isDirectory()) {
                    System.out.println("cd: '" + args[1] + "': No such file or directory");
                    return 1;
                }
                Shell.currentPath = Shell.currentPath + File.separator + args[1];
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "cd";
    }

    @Override
    public boolean checkArgs(String[] args) {
        return false;
    }
}

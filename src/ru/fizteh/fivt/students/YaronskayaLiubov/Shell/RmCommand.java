package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

import java.io.File;

public class RmCommand extends Command {
    boolean execute(String[] args) {
        if (args.length > maxNumberOfArguements) {
            System.out.println(name + ": Invalid number of arguements");
            return false;
        }
        int option = (args[1].equals("-r")) ? 1 : 0;
        String fileName = args[1 + option];
        File newFile = (fileName.charAt(0) == '/') ? new File(fileName)
                : new File(Shell.curDir, fileName);
        if (!newFile.exists()) {
            System.out.println("rm: cannot remove '" + fileName
                    + "': No such file or directory");
            return false;
        }
        newFile.mkdirs();
        if (option == 0 && newFile.isDirectory()) {
            System.out.println(name + ": " + fileName + " is a directory");
            return false;
        }
        Shell.fileDelete(newFile);
        return true;
    }

    RmCommand() {
        name = "rm";
        maxNumberOfArguements = 3;
    }
}

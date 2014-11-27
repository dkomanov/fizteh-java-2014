package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CpCommand extends Command {
    CpCommand() {
        name = "cp";
        maxNumberOfArguements = 4;
    }

    boolean execute(String[] args) throws IOException {
        if (args.length != (maxNumberOfArguements - 1) && args.length != maxNumberOfArguements) {
            System.out.println(name + ": Invalid number of arguements");
            return false;
        }
        int option = (args[1].equals("-r")) ? 1 : 0;
        String source = args[1 + option];
        String destination = args[2 + option];

        File fileSource = (source.charAt(0) == '/') ? new File(source)
                : new File(Shell.curDir, source);
        if (!fileSource.exists()) {
            System.out.println("rm: cannot remove '" + source
                    + "': No such file or directory");
            return false;
        }
        File dirDestination = (destination.charAt(0) == '/') ? new File(
                destination) : new File(Shell.curDir, destination);
        if (!dirDestination.exists()) {
            Shell.mkdirs(dirDestination);

        }
        if (!fileSource.getCanonicalPath().equals(dirDestination.getCanonicalPath())) {
            if (option == 0 && fileSource.isDirectory()) {
                System.out.println(name + ": " + source
                        + " is a directory (not copied).");
                return false;
            }
            try {
                if (dirDestination.isDirectory()) {
                    Shell.dirCopy(fileSource, dirDestination);
                } else {
                    if (fileSource.isDirectory()) {
                        System.err.println(name + ": cann't copy directory. '" + destination + "' is file");
                        return false;
                    }
                    FileInputStream fis = new FileInputStream(fileSource);
                    FileOutputStream fos;
                    fos = new FileOutputStream(dirDestination);
                    Shell.fileCopy(fis, fos);
                    fis.close();
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                return false;
            }
        }
        return true;
    }

}

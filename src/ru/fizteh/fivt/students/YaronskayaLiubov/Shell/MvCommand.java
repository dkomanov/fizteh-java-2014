package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

import java.io.File;

public class MvCommand extends Command {
    boolean execute(String[] args) {
        String source = args[1];
        String destination = args[2];
        File fileSource = (source.charAt(0) == '/') ? new File(source)
                : new File(Shell.curDir, source);
        if (!fileSource.exists()) {
            System.out.println("mv: cannot remove '" + source
                    + "': No such file or directory");
            return false;
        }
        File fileDestination = (destination.charAt(0) == '/') ? new File(
                destination) : new File(Shell.curDir, destination);
        if (!fileDestination.exists()) {
            System.out.println("rm: cannot remove to '" + destination
                    + "': No such file or directory");
            return false;
        }
        fileDestination.mkdirs();
        if (fileSource.getParent().equals(fileDestination.getParent())) {
            if (!fileSource.renameTo(fileDestination)) {
                System.out.println("Rename failed");
                return false;
            }
        } else {
            try {
                Shell.dirCopy(fileSource, fileDestination);
                Shell.fileDelete(fileSource);
            } catch (Exception e) {
                System.out.println(e.toString());
                return false;
            }
        }
        return true;
    }

    MvCommand() {
        name = "mv";
    }
}

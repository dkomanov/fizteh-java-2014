package ru.fizteh.fivt.students.AlexanderKhalyapov.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Rm implements Command  {
    public final String getName() {
        return "rm";
    }
    public static void remove(final Path thePath) throws IOException {
        File theFile = thePath.toFile();
        if (theFile.isFile()) {
            if (!theFile.delete()) {
                throw new IOException("can't delete");
            }
        } else if (theFile.isDirectory()) {
            File[] listOfFiles = theFile.listFiles();
            if (listOfFiles != null) {
                for (File listOfFile : listOfFiles) {
                    remove(listOfFile.toPath());
                }
            }
            if (!theFile.delete()) {
                throw new IOException("can't delete");
            }
        }
    }
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {
        Path thePath = shell.getState().getPath().resolve(args[0]);
        try {
            if (1 == args.length) {
                if (thePath.toFile().exists()) {
                    remove(thePath);
                }
            } else {
                throw new IOException("Incorrect number of arguments");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            while (!thePath.toFile().isDirectory()) {
                thePath = thePath.getParent();
            }
            shell.setState(thePath);
        }
    }
}

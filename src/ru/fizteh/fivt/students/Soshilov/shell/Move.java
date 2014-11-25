package ru.fizteh.fivt.students.Soshilov.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 03 October 2014
 * Time: 14:17
 */
public class Move {
    /**
     * Move the file to a new directory or rename file to the second argument like UNIX mv
     * @param currentArgs Arguments, entered with command (operators, files)
     * @param cd Current directory where we are now
     */
    public static void moveRun(final String[] currentArgs, final CurrentDirectory cd) {
        if (currentArgs.length == 2) {
            System.err.println("mv: missing destination file operand after ‘" + currentArgs[1] + "’");
        } else if (currentArgs.length > 3) {
            //We move files into the directory which was entered as last word
            CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
            File dest = new File(currentArgs[currentArgs.length - 1]);
            if (!dest.isAbsolute()) {
                dest = new File(cd.getCurrentDirectory(), currentArgs[currentArgs.length - 1]);
            }
            if ((!dest.exists()) || (!dest.isDirectory())) {
                System.err.println("mv: target ‘" + dest.toString() + "’ is not a directory");
                System.exit(1);
            }
            for (int i = 0; i < currentArgs.length - 1; ++i) {
                File source = new File(currentArgs[i]);
                if (!source.isAbsolute()) {
                    source = new File(cd.getCurrentDirectory(), currentArgs[i]);
                }
                dest = new File(dest.getAbsolutePath(), source.getName());
                try {
                    Files.move(source.toPath(), dest.toPath(), options);
                } catch (IOException e) {
                    System.err.println("Error: cannot rename or move file");
                    System.exit(1);
                }
            }
        } else {
            CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
            File source = new File(currentArgs[1]);
            File dest = new File(currentArgs[2]);
            if (!source.isAbsolute()) {
                source = new File(cd.getCurrentDirectory(), currentArgs[1]);
            }
            if (!dest.isAbsolute()) {
                dest = new File(cd.getCurrentDirectory(), currentArgs[2]);
            }
            dest = new File(dest.getAbsolutePath(), source.getName());
            try {
                Files.move(source.toPath(), dest.toPath(), options);
            } catch (IOException e) {
                System.err.println("Error: cannot rename or move file");
                System.exit(1);
            }
        }
    }
}

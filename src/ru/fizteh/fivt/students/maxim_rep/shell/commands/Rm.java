package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import java.io.*;
import ru.fizteh.fivt.students.maxim_rep.shell.*;

public class Rm implements ShellCommand {

    String currentPath;
    String destination;
    boolean recursive;

    public Rm(String currentPath, String destination, boolean recursive) {
        this.currentPath = currentPath;
        this.destination = Parser.pathConverter(destination, currentPath);
        this.recursive = recursive;
    }

    public static void recursiveRm(File f, String path) {
        String[] files = f.list();
        for (int i = 0; i < files.length; ++i) {
            File currentFile = new File(f.getPath()
                    + System.getProperty("file.separator") + files[i]);
            if (currentFile.isDirectory()) {
                recursiveRm(currentFile, currentFile.getPath());
            }
            currentFile.delete();
        }

        f.delete();
    }

    @Override
    public boolean execute() {
        File f = new File(destination);
        if (f.isFile()) {
            f.delete();
            return true;
        } else if (f.isDirectory()) {
            if (recursive) {
                recursiveRm(f, destination);
            } else {
                System.out
                        .println("Rm: '" + destination + "': Is a directory!");
                return false;
            }
        } else {
            System.out.println("rm: cannot remove '" + destination
                    + "': No such file or directory");
            return false;
        }
        return true;
    }
}

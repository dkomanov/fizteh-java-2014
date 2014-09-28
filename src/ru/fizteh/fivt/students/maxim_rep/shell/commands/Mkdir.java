package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import java.io.*;

public class Mkdir implements ShellCommand {

    String currentPath;
    String fileName;

    public Mkdir(String currentPath, String fileName) {
        this.fileName = fileName;
        this.currentPath = currentPath;
    }

    @Override
    public boolean execute() {

        File f = new File(currentPath + System.getProperty("file.separator")
                + fileName);
        if (f.mkdir()) {
            System.out.println("Directory " + fileName + " created!");
        } else {
            System.out.println("Couldn't create new directory!");
            return false;
        }
        return true;
    }
}

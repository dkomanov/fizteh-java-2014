package ru.fizteh.fivt.students.ilivanov.shell;

import java.io.*;
import java.util.ArrayList;

public class CommandLs implements Command {

    CommandLs(final ArrayList<String> parameters) throws Exception {
        if (parameters.size() != 1) {
            throw new Exception("wrong number of parameters");
        }
    }

    @Override
    public int execute() {
        try {
            File currDir = Shell.currentDirectory;
            File[] content = currDir.listFiles();
            if (content == null) {
                System.err.println("ls: current directory error");
                return -1;
            }
            for (File f : content) {
                if (f.isDirectory()) {
                    System.out.print("dir   ");
                } else {
                    System.out.print("file  ");
                }
                System.out.println(f.getName());
            }
        } catch (Exception e) {
            System.err.println("ls: " + e.getMessage());
            return -1;
        }
        return 0;
    }
}

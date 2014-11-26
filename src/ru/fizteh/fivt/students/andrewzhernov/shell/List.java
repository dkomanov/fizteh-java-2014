package ru.fizteh.fivt.students.andrewzhernov.shell;

import java.io.File;

public class List {
    public static void execute(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Usage: ls");
        } else {
            File currentPath = new File(System.getProperty("user.dir"));
            String[] list = currentPath.list();
            for (String fileName : list) {
                if (fileName.charAt(0) != '.') {
                    System.out.println(fileName);
                }
            }
        }
    }
}

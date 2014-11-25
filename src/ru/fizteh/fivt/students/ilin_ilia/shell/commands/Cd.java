package ru.fiztech.fivt.students.theronsg.shell.commands;

import java.io.File;
import java.nio.file.Paths;

public final class Cd {
    public static void run(final String path) {
        try {
            File curPath = Paths.get(path).normalize().toFile();
            if (!curPath.isAbsolute()) {
                curPath = Paths.get(System.getProperty("user.dir"),
                        curPath.getPath()).normalize().toFile();
            }
            if (curPath.exists()) {
                if (curPath.isDirectory()) {
                    System.setProperty("user.dir", curPath.getPath());
                } else {
                    System.out.println("\"" + path + "\"" + "isn't a dir");
                }
            }
        } catch (Exception e) {
            System.out.println("Can't change current dir");
            System.exit(-1);
        }
    }

}

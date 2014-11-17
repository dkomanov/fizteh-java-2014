package ru.fiztech.fivt.students.theronsg.shell.commands;

import java.io.File;

public final class Ls {
    public static void run() {
        String curDir = new File(".").getAbsolutePath();
        File fileWithCurDir = new File(curDir);
        File [] curFiles = fileWithCurDir.listFiles();
        for (File f: curFiles) {
            System.out.println(f.getName());
        }
    }

}

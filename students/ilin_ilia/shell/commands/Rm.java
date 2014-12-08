package ru.fiztech.fivt.students.theronsg.shell.commands;

import java.io.File;

public final class Rm {
    public static void run(final String name, final boolean b) {
        File fileName = new File(name);
        if (b) {
            if (!fileName.exists()) {
                System.out.println("File doesn't exist");
                return;
            }
            if (fileName.isFile()) {
                fileName.delete();
                return;
            }
            if (fileName.isDirectory()) {
                if (fileName.list().length == 0) {
                    fileName.delete();
                    return;
                } else {
                    for (String s: fileName.list()) {
                        run(fileName + "\\" + s, b);
                    }
                    fileName.delete();
                    return;
                }
            }
        } else {
            if (!fileName.exists()) {
                System.out.println("File doesn't exist");
                return;
            }
            if (fileName.isFile()) {
                fileName.delete();
                return;
            }
            if (fileName.isDirectory()) {
                if (fileName.list().length == 0) {
                    fileName.delete();
                    return;
                } else {
                    System.out.println("Dir isn't empty. Can't delete.");
                    return;
                }
            }
        }
    }
}

package ru.fiztech.fivt.students.theronsg.shell.commands;

import java.io.File;

public final class MkDir {
    public static void run(final String dirName) {
        File dir = new File(System.getProperty("user.dir") + "\\" + dirName);
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            System.out.println("Dir has already existed.");
        }
    }

}

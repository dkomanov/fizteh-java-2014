package ru.fiztech.fivt.students.theronsg.shell.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Mv {
    public static void run(final String source, final String destination) {
        File s = new File(source);
        File d = new File(destination + "\\" + source);
        if (!s.exists()) {
            System.out.println(source + " doesn't exist.");
            System.exit(-1);
        }
        if (d.exists()) {
            System.out.println(destination + "has already existed.");
            System.exit(-1);
        } else {
            try {
                Files.move(Paths.get(source), Paths.get(destination
                            + "\\" + source));
                System.out.println(s.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package ru.fiztech.fivt.students.theronsg.shell.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class Cp {
    public static void cop(final File source, final File destination) {
        File to = null;
        if (destination.exists()) {
            if (!destination.isDirectory()) {
                System.out.println(destination.getName() + " isn't a dir");
                return;
            } else {
                try {
                    to = new File(destination.getCanonicalPath()
                            + File.separator + source.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            to = destination;
        }
        try {
            Files.copy(source.toPath(), to.toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (source.isDirectory()) {
            File [] list = source.listFiles();
            if (list == null) {
                System.out.println("Dir list is empty");
                return;
            }
            for (File f: list) {
                cop(f, to);
            }
        }
    }
    public static void run(final String source, final String destination, final boolean b) {
        File from = new File(new File(".").getAbsolutePath()
                        + File.separator + source);
        File to =  new File(new File(".").getAbsolutePath()
                        + File.separator + destination);
        if (!from.exists()) {
            System.out.println(from.getName() + " doesnt exist");
            return;
        }
        if (from.isDirectory() && !b) {
            System.out.println(from.getName() + " is a dir. Use -r.");
            return;
        }
        cop(from, to);
    }
}



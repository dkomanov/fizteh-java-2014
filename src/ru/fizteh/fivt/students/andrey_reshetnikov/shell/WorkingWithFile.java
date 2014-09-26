package ru.fizteh.fivt.students.andrey_reshetnikov.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.*;

public class WorkingWithFile {
	public static File ConcatPath(File pwdFolder, String s) throws IOException {
		File newElem = new File(s);
        if (!newElem.isAbsolute()) {
            newElem = new File(pwdFolder, s);
        }
        return newElem;
	}
	public static void delete(File deleting) throws IOException, FileWasNotDeleted {
        File[] listOfElements = deleting.listFiles();
        if (listOfElements != null) {
            for (File i : listOfElements) {
                if (i.isDirectory()) {
                    delete(i);
                } else if (!i.delete()) {
                    throw new FileWasNotDeleted();
                }
            }
        }
        if (!deleting.delete()) {
            throw new FileWasNotDeleted();
        }
    }
	public static void copy(File source, File destination) throws IOException, WrongCommand {
        if (source.isDirectory()) {
            copyDirectory(source, new File(destination, source.getName()));
        } else {
            if (destination.exists() && destination.isDirectory()) {
                copyFile(source, new File(destination, source.getName()));
            } else {
                copyFile(source, destination);
            }
        }
    }
    protected static void copyDirectory(File source, File destination) throws IOException, WrongCommand {
        if (!destination.mkdirs()) {
            System.out.println("cannot make directory :'" + destination + "'");
        }
        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    copyDirectory(file, new File(destination, file.getName()));
                } else {
                    copyFile(file, new File(destination, file.getName()));
                }
            }
        }
    }
    protected static void copyFile(File source, File destination) throws IOException, WrongCommand {
        Files.copy(source.toPath(), Paths.get(destination.toPath().toString()), REPLACE_EXISTING);
    }
}

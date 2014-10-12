package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {
    private Utils() {
    }

    public static void copy(File source, File destination) throws IOException {
        try {
            FileChannel sourceChannel = new FileInputStream(source.getPath()).getChannel();
            FileChannel destinationChannel = new FileOutputStream(destination.getPath()).getChannel();
            destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            sourceChannel.close();
            destinationChannel.close();
        } catch (Exception e) {
            System.err.println("Couldn't close sources or write");
        }
    }

    public static void copyFileOrDirectory(File source, File destination, boolean flag) throws IOException {
        //try {
            if ((destination.isFile()) && (!(source.isFile()))) {
                throw new IOException("Impossible to copy  directory");
            }
            if ((source.isFile()) && (destination.isDirectory())) {
                copy(source, new File(destination.getPath(), source.getName()));
            } else if (source.isFile()) {
                try {
                    copy(source, destination);
                } catch (Exception e) {
                    System.err.println("cannot copy");
                }
            } else {
                File elementsDestination = new File(destination.getPath(), source.getName());

                if (!elementsDestination.exists()) {
                    if (!elementsDestination.mkdir()) {
                        throw new IOException();
                    }
                }
                for (String element : source.list()) {
                    copyFileOrDirectory(new File(source.getPath(), element), elementsDestination, flag);
                }
            }
        //} catch (Exception e) {
        //    System.err.println("A problem with reading from source file or writing to destination file");
        //}
    }

    public static void deleteFileOrDirectory(final File source) {
        if (source.isFile()) {
            Path path = source.toPath();
            try {
                Files.deleteIfExists(path);
            } catch (IOException | SecurityException e) {
                System.err.println(e);
            }
            return;
        }

        for (String element : source.list()) {
            deleteFileOrDirectory(new File(source.getPath(), element));
        }

        Path path = source.toPath();
        try {
            Files.deleteIfExists(path);
        } catch (IOException | SecurityException e) {
            System.err.println(e);
        }
    }

    public static File absoluteFileCreate(final String name) throws IOException {
        File file = new File(name);
        if (file.isAbsolute()) {
            return file;
        }
        file = new File(CurrentDir.getCurrentDirectory(), name).getCanonicalFile();
        return file;
    }
}

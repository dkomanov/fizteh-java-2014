package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Utils {
    private Utils() {
    }

    public static void copy(File source, File destination) throws IOException {
        if (destination.exists()) {
            System.out.print("file already exists");
        }

        @SuppressWarnings("resource")
        FileChannel sourceChannel = new FileInputStream(source.getPath()).getChannel();
        @SuppressWarnings("resource")
        FileChannel destinationChannel = new FileOutputStream(destination.getPath()).getChannel();

        destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

        sourceChannel.close();
        destinationChannel.close();
    }

    public static void copyFileOrDirectory(File source, File destination, boolean flag) throws IOException {
        if ((destination.isFile()) && (!(source.isFile()))) {
            System.out.print("Impossible to copy directory ");
        }
        if ((source.isFile()) && (destination.isDirectory())) {
            if (destination.exists()) {
                System.out.print("file already exists");
            }
            copy(source, new File(destination.getPath(), source.getName()));
        } else if (source.isFile()) {
            copy(source, destination);
        } else {
            File elementsDestination = new File(destination.getPath(), source.getName());

            if (!elementsDestination.exists()) {
                elementsDestination.mkdir();
            }
            for (String element : source.list()) {
                copyFileOrDirectory(new File(source.getPath(), element), elementsDestination, flag);
            }
        }
    }

    public static void deleteFileOrDirectory(final File source) {
        if (source.isFile()) {
            source.delete();
            return;
        }

        for (String element : source.list()) {
            deleteFileOrDirectory(new File(source.getPath(), element));
        }

        source.delete();
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

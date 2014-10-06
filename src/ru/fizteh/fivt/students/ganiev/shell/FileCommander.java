package ru.fizteh.fivt.students.ganiev.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileCommander {

    private FileCommander() {
    }

    public static File getFile(String fileName, Shell.MyShell shell) throws IOException {
        File currentFile = new File(fileName);

        if (currentFile.isAbsolute()) {
            return currentFile.getCanonicalFile();
        } else {
            return new File(shell.getCurrentDirectory(), fileName).getCanonicalFile();
        }
    }

    public static void copy(File source, File destination) throws IOException {
        if (destination.exists()) {
            throw new IOException("File " + destination.getPath() + " already exists");
        }

        FileChannel sourceChannel = new FileInputStream(source.getPath()).getChannel();
        FileChannel destinationChannel = new FileOutputStream(destination.getPath()).getChannel();

        destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

        sourceChannel.close();
        destinationChannel.close();
    }

    public static void copyFileOrDirectory(File source, File destination) throws IOException {
        if ((source.isDirectory()) && (destination.isFile())) {
            throw new IOException("Unable to copy directory " + source.getPath() + " to file " + destination.getPath());
        }

        if ((source.isFile()) && (destination.isDirectory())) {
            copy(source, new File(destination.getPath(), source.getName()));
        } else if (source.isFile()) {
            copy(source, destination);
        } else {
            File elementsDestination = new File(destination.getPath(), source.getName());

            if (!elementsDestination.exists()) {
                elementsDestination.mkdir();
            }

            for (String element : source.list()) {
                copyFileOrDirectory(new File(source.getPath(), element), elementsDestination);
            }
        }
    }

    public static void deleteFileOrDirectory(File source) {
        if (source.isFile()) {
            source.delete();
            return;
        }

        for (String element : source.list()) {
            deleteFileOrDirectory(new File(source.getPath(), element));
        }

        source.delete();
    }
}

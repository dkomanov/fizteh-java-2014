package ru.fizteh.fivt.students.dsalnikov.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static void closeStream(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            //так и задумано
            System.err.println("this is not an error");
        }
    }

    private static void recursiveDelete(File startPoint) throws IOException {
        File[] listOfFiles = startPoint.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                recursiveDelete(file);
            }
        }

        if (!startPoint.delete()) {
            throw new IOException("Can't delete directory, unknown error");
        }
    }

    public static void forceRemoveDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            throw new IOException("File or directory do not exist");
        }
        recursiveDelete(directory);
    }

}

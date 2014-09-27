package ru.fizteh.fivt.students.dsalnikov.utils;

import java.io.Closeable;
import java.io.IOException;

public class FileUtils {
    public static void closeStream(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
        }
    }
}

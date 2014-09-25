package ru.fizteh.fivt.students.dsalnikov.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public class FileMapUtils {
    public static void readToMap() throws FileNotFoundException {
        String dbpath = System.getProperty("db.file");
        File f = new File(dbpath);
        if(!f.isFile() || !f.exists()) {
            throw new FileNotFoundException("file path is incorrect");
        } else {

        }
    }

    public static void flush(File f, Map<String, String> storage) {

    }
}

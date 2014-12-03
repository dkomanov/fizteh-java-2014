package ru.fizteh.fivt.students.dsalnikov.utils;

import java.io.File;
import java.io.FileNotFoundException;


public class StringUtils {
    public static File processFile(String currdir, String s) throws FileNotFoundException {
        File fi = new File(s);
        if (!fi.isAbsolute()) {
            fi = new File(currdir, s);
        }
        if (!fi.exists()) {
            throw new FileNotFoundException("file doesn't exist");
        }
        return fi;
    }
}

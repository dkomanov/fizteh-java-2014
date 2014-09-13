package ru.fizteh.fivt.students.dsalnikov.Utils;

import java.io.File;
import java.io.FileNotFoundException;


public class StringUtils {
    public static File ProcessFile(String currdir, String s) throws FileNotFoundException {
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

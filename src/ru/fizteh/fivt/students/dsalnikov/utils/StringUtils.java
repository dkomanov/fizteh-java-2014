package ru.fizteh.fivt.students.dsalnikov.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;


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

    public static String cutTimeStamp(String proxyResult) {
        return proxyResult.replaceFirst(" timestamp=\"[0-9]+\"", "").trim();
    }

    public static String createString(String className, List<Object> storeableListOfObjects) {
        StringBuilder builder = new StringBuilder(className);
        builder.append("[");
        Object object;
        for (Integer i = 0; i < storeableListOfObjects.size(); ++i) {
            object = storeableListOfObjects.get(i);
            if (object != null) {
                builder.append(object.toString());
            } else {
                builder.append("");
            }
            if (i != storeableListOfObjects.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}

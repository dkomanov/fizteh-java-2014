package ru.fizteh.fivt.students.YaronskayaLiubov.JUnit;

import java.io.File;

/**
 * Created by luba_yaronskaya on 27.10.14.
 */
public class CheckParameters {
    public static void checkKey(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
    }

    public static void checkValue(String value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
    }

    public static void checkNumberOfArguments(String[] args, int legalNumber) {
        if (args.length != legalNumber) {
            throw new IllegalArgumentException("invalid number of arguments");
        }
    }

    public static void checkTableName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("table name is null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("table name is empty");
        }
        if (name.contains(File.separator)) {
            throw new IllegalStateException("table name contains separator");
        }
    }
}

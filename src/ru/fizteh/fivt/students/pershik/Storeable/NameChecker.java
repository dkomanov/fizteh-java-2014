package ru.fizteh.fivt.students.pershik.Storeable;

import java.io.File;

/**
 * Created by pershik on 11/8/14.
 */
public class NameChecker {
    static boolean checkName(String name) {
        return !(name == null || "..".equals(name) || ".".equals(name)
                || name.contains(File.separator));
    }
}

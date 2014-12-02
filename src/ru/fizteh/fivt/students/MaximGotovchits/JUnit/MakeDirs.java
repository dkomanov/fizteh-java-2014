package ru.fizteh.fivt.students.MaximGotovchits.JUnit;

import java.io.File;

public class MakeDirs extends CommandTools {
    void makeDirsFunction(/*Map<String, String> storage*/) throws Exception {
        File file = new File(dataBaseName);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            if (!file.isDirectory()) {
                System.err.println(dataBaseName + " is not a directory");
                System.exit(1);
            } else {
                for (File sub : file.listFiles()) {
                    if (!sub.isDirectory()) {
                        System.err.println(dataBaseName + "/"
                                + sub.getName() + " is not a directory");
                    }
                }
            }
        }
    }
}

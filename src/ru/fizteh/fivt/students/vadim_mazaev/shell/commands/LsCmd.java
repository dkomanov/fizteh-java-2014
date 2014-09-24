package ru.fizteh.fivt.students.vadim_mazaev.shell.commands;

import java.io.File;

public final class LsCmd {
    private LsCmd() {
        //not called
    }
    public static void run(final String[] cmdWithArgs) throws Exception {
        if (cmdWithArgs.length > 1) {
            throw new Exception(getName()
                + ": too much arguments");
        }
        try {
            String[] fileNamesList =
                    new File(System.getProperty("user.dir")).list();
            for (String fileName : fileNamesList) {
                System.out.println(fileName);
            }
        } catch (SecurityException e) {
            throw new Exception(getName()
                + ": cannot get the list of files: access denied");
        }
    }
    public static String getName() {
        return "ls";
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell;

import java.io.File;

/**
 *
 * @author shakarim
 */
public class RmCommand {

    public static void run(String[] args) throws Exception {
        boolean recFlag = false;
        String[] newArgs = new String[2];
        int index = 0;

        for (String opt : args) {
            if (opt.equals("-r")) {
                recFlag = true;
            } else {

                newArgs[index] = opt;
                ++index;
            }
        }

        if (index < 2) {
            throw new Exception(getName() + ": few arguments");
        }

        if (index > 2) {
            throw new Exception(getName() + ": too much arguments");
        }

        File toDelete = CdCommand.newPath(newArgs[1]);

        if (!recFlag && toDelete.isDirectory()) {
            throw new Exception(getName() + ": '"
                    + newArgs[1] + "' is Directory");
        }

        if (!toDelete.exists()) {
            throw new Exception(getName() + ": '"
                    + newArgs[1] + "' No such File or Directory");
        }

        rm(toDelete);

    }

    public static void rm(File file) throws Exception {
        boolean isDeleted = false;
        if (file.isFile()) {
            isDeleted = file.delete();
        } else {
            String[] list = file.list();
            for (String son : list) {
                rm(new File(file, son));
            }
            isDeleted = file.delete();
        }
        if (!isDeleted) {
            throw new Exception(getName() + ": Cannot delete '"
                    + file.getCanonicalPath() + "'");
        }
    }

    public static String getName() {
        return "rm";
    }
}

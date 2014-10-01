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
public class CdCommand {

    public static void run(String[] args) throws Exception {
        if (args.length < 2) {
            throw new Exception(getName() + ": few arguments");
        } else if (args.length > 2) {
            throw new Exception(getName() + ": too much arguments");
        } else {
            File dirToGo = newPath(args[1]);
            if (dirToGo.isDirectory()) {
                String newPath = dirToGo.getCanonicalPath();
                System.setProperty("user.dir", newPath);
            } else if (dirToGo.exists()) {
                throw new Exception(getName() + ": '"
                        + args[1] + "' is not a Directory");
            } else {
                throw new Exception(getName() + ": '"
                        + args[1] + "' No such File or Directory");
            }
        }
    }

    public static File newPath(String file) {
        if (file.charAt(0) == File.separatorChar) {

            return new File(file);
        } else {
            return new File(PwdCommand.getCurPath() + File.separator + file);
        }
    }

    public static String getName() {
        return "cd";
    }
}

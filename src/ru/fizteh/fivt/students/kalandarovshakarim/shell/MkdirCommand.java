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
public class MkdirCommand {

    public static void run(String[] args) throws Exception {
        if (args.length < 2) {
            throw new Exception(getName() + ": few arguments");
        } else if (args.length > 2) {
            throw new Exception(getName() + ": too much arguments");
        } else {
            File newDir = CdCommand.newPath(args[1]);
            if (newDir.mkdir() == false) {
                throw new Exception(getName() 
                        + ": Cannot create directory '"
                        + args[1] + "'");
            }
        }
    }
    
    private static String getName() {
        return "mkdir";
    }
}

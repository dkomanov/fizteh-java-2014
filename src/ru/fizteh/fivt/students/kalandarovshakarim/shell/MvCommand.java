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
public class MvCommand {

    public static void run(String[] args) throws Exception {
        if (args.length < 3) {
            throw new Exception(getName() + ": few arguments");
        } else if (args.length > 3) {
            throw new Exception(getName() + ": too much arguments");
        }
        
        String[] newArgs = new String[4];
        newArgs[0] = args[0];
        newArgs[1] = "-r";
        newArgs[2] = args[1];
        newArgs[3] = args[2];
       
        CpCommand.run(newArgs);
        
        newArgs = new String[3];
        newArgs[0] = args[0];
        newArgs[1] = "-r";
        newArgs[2] = args[1];
        
        RmCommand.run(newArgs);
    }
    
    public static String getName() {
        return "mv";
    }
}

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
public class LsCommand {

    public static void run(String[] args) throws Exception {
        if (args.length > 1) {
            throw new Exception(getName() + ": too much arguments");
        }
        File curDir = new File(PwdCommand.getCurPath());
        String[] list = curDir.list();

        for (String name : list) {
            if (name.charAt(0) != '.') {
                System.out.println(name);
            }
        }
    }

    public static String getName() {
        return "ls";
    }
}

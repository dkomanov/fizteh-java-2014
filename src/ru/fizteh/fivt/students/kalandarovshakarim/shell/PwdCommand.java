/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell;

/**
 *
 * @author shakarim
 */
public class PwdCommand {

    public static void run(String[] args) throws Exception {
        if (args.length > 1) {
            throw new Exception(getName() + ": too much arguments");
        }
        System.out.println(getCurPath());
    }

    public static String getCurPath() {
        return System.getProperty("user.dir");
    }

    public static String getName() {
        return "pwd";
    }
}

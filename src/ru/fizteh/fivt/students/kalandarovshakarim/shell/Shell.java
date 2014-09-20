/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell;

/**
 *
 * @author shakarim
 */
public class Shell {

    public static void main(String[] args) {
        if (args.length == 0) {
            ShellInterface.interMode();
        } else {
            ShellInterface.packMode(args);
        }
    }
}

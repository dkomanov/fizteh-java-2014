/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell;

import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.*;

/**
 *
 * @author Shakarim
 */
public class ShellMain {

    public static void main(String[] args) {

        ShellUtils mainContext = new ShellUtils();

        Command[] commands = new Command[]{
            new LsCommand(mainContext),
            new RmCommand(mainContext),
            new CdCommand(mainContext),
            new CpCommand(mainContext),
            new MvCommand(mainContext),
            new PwdCommand(mainContext),
            new CatCommand(mainContext),
            new ExitCommand(mainContext),
            new MkdirCommand(mainContext)
        };

        Shell shell = new Shell(args, commands);
        shell.exec();
    }
}

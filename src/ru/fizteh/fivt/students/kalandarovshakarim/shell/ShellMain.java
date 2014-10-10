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
        Command[] commands = new Command[]{
            (Command<ShellState>) new ExitCommand(),
            (Command<ShellState>) new LsCommand(),
            (Command<ShellState>) new RmCommand(),
            (Command<ShellState>) new CatCommand(),
            (Command<ShellState>) new CdCommand(),
            (Command<ShellState>) new CpCommand(),
            (Command<ShellState>) new MkdirCommand(),
            (Command<ShellState>) new MvCommand(),
            (Command<ShellState>) new PwdCommand()
        };


        Shell shell = new Shell(new ShellState(), args, commands);
        shell.exec();
    }
}

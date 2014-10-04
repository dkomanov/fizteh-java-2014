/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap;

import java.io.FileNotFoundException;
import java.io.IOException;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.commands.*;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.Shell;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.Command;

/**
 *
 * @author shakarim
 */
public class FileMapMain {

    public static void main(String[] args) {
        FileMapShellState state = null;

        try {
            state = new FileMapShellState();
        } catch (FileNotFoundException ex) {
            System.err.println("db.file was not found");
            System.exit(1);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        Command[] commands = new Command[]{
            (Command<FileMapShellState>) new ExitCommand(),
            (Command<FileMapShellState>) new PutCommand(),
            (Command<FileMapShellState>) new GetCommand(),
            (Command<FileMapShellState>) new RemoveCommand(),
            (Command<FileMapShellState>) new ListCommand()
        };

        Shell<FileMapShellState> shell = new Shell<>(state, args, commands);
        shell.exec();
    }
}
